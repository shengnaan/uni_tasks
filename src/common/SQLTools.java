package common;

import java.io.FileOutputStream;
import java.sql.*;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public final class SQLTools {

    private final Connection conn;
    private final Map<String, Map<String, String>> tableSchemas;

    public SQLTools(String dbName, Map<String, Map<String, String>> tableSchemas) throws SQLException {
        this.conn = DriverManager.getConnection(Settings.getDatabaseUrl(dbName));
        this.tableSchemas = tableSchemas;
    }

    public void showTables() throws SQLException {
        String query = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            boolean hasTables = false;

            while (rs.next()) {
                hasTables = true;
                String tableName = rs.getString("table_name");
                System.out.println("\nТаблица: " + tableName);

                String columnsQuery = """
                            SELECT column_name, data_type
                            FROM information_schema.columns
                            WHERE table_schema = 'public' AND table_name = ?
                        """;
                try (PreparedStatement colStmt = conn.prepareStatement(columnsQuery)) {
                    colStmt.setString(1, tableName);
                    try (ResultSet colRs = colStmt.executeQuery()) {
                        while (colRs.next()) {
                            System.out.printf("   🔹 %s : %s%n", colRs.getString("column_name"), colRs.getString("data_type"));
                        }
                    }
                }
            }
            if (!hasTables) {
                System.out.println("Таблицы пока не созданы.");
            }
        }
    }

    public void createTables() throws SQLException {
        for (Map.Entry<String, Map<String, String>> entry : tableSchemas.entrySet()) {
            String tableName = entry.getKey();
            Map<String, String> columns = entry.getValue();

            if (!isTableExists(tableName)) {
                StringBuilder sb = new StringBuilder("CREATE TABLE " + tableName + " (");

                int colIndex = 0;
                for (Map.Entry<String, String> column : columns.entrySet()) {
                    sb.append(column.getKey()).append(" ").append(column.getValue());
                    if (colIndex < columns.size() - 1) sb.append(", ");
                    colIndex++;
                }

                sb.append(")");

                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(sb.toString());
                    System.out.println("Таблица создана: " + tableName);
                }
            } else {
                System.out.println("Таблица " + tableName + " уже существует");
            }
        }
    }

    public void insertRowIntoDB(String tablename, Map<String, Object> data) throws SQLException {
        if (!tableSchemas.containsKey(tablename)) {
            throw new SQLException("Попытка вставить данные в неизвестную таблицу: " + tablename);
        }

        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Данные для вставки не могут быть пустыми");
        }

        String columns = String.join(", ", data.keySet());
        String placeholders = String.join(", ", data.keySet().stream().map(k -> "?").toList());

        String sql = "INSERT INTO " + tablename + " (" + columns + ") VALUES (" + placeholders + ")";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int index = 1;
            for (Object value : data.values()) {
                pstmt.setObject(index++, value);
            }
            pstmt.executeUpdate();
        }
    }


    public void saveToExcel() {
        String fileName = "exported_data.xlsx";

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            for (String table : tableSchemas.keySet()) {
                System.out.println("Экспортируем таблицу: " + table);
                String query = "SELECT * FROM " + table;

                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    XSSFSheet sheet = workbook.createSheet(table);

                    Row titleRow = sheet.createRow(0);
                    titleRow.createCell(0).setCellValue("Таблица: " + table);

                    Row header = sheet.createRow(1);
                    for (int i = 1; i <= columnCount; i++) {
                        header.createCell(i - 1).setCellValue(metaData.getColumnName(i));
                    }

                    int rowIndex = 2;
                    while (rs.next()) {
                        Row row = sheet.createRow(rowIndex++);
                        for (int i = 1; i <= columnCount; i++) {
                            row.createCell(i - 1).setCellValue(rs.getString(i));
                        }
                    }

                } catch (SQLException e) {
                    System.out.println("Ошибка при экспорте таблицы " + table + ": " + e.getMessage());
                }
            }

            try (FileOutputStream fos = new FileOutputStream(fileName)) {
                workbook.write(fos);
                System.out.println("Все таблицы экспортированы в файл: " + fileName);
            }

        } catch (Exception e) {
            System.err.println("Ошибка при создании Excel-файла: " + e.getMessage());
        }
    }

    public void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    private boolean isTableExists(String tableName) throws SQLException {
        String checkSql = """
                    SELECT EXISTS (
                        SELECT FROM information_schema.tables
                        WHERE table_schema = 'public' AND table_name = ?
                    )
                """;

        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, tableName);
            try (ResultSet rs = checkStmt.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        }
    }

    public Map<String, String> getFromTable(String query) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                String str1 = rs.getString("first_string");
                String str2 = rs.getString("second_string");

                return Map.of("first_string", str1,
                        "second_string", str2);
            } else {
                System.out.println("Нет данных в таблице.");
                return Map.of();
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при подсчёте длины: " + e.getMessage());
        }
        return Map.of();
    }
}
