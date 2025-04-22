package common;

import java.io.FileOutputStream;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

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
        Scanner scanner = new Scanner(System.in);

        for (Map.Entry<String, Map<String, String>> entry : tableSchemas.entrySet()) {
            String tableName;
            while (true) {
                System.out.println("Введите название таблицы для схемы:");
                tableName = scanner.nextLine().trim();
                if (!tableName.isEmpty()) {
                    break;
                }
                System.out.println("Ошибка: название таблицы не может быть пустым. Попробуйте снова.");
            }

            Map<String, String> columns = new LinkedHashMap<>();
            int colIndex = 0;
            for (Map.Entry<String, String> column : entry.getValue().entrySet()) {
                String columnName;
                while (true) {
                    System.out.println("Введите название столбца #" + (colIndex + 1) + " (тип: " + column.getValue() + "):");
                    columnName = scanner.nextLine().trim();
                    if (!columnName.isEmpty()) {
                        break;
                    }
                    System.out.println("Ошибка: название столбца не может быть пустым. Попробуйте снова.");
                }
                columns.put(columnName, column.getValue());
                colIndex++;
            }

            if (!isTableExists(tableName)) {
                StringBuilder sb = new StringBuilder("CREATE TABLE " + tableName + " (");

                int i = 0;
                for (Map.Entry<String, String> column : columns.entrySet()) {
                    sb.append(column.getKey()).append(" ").append(column.getValue());
                    if (i < columns.size() - 1) sb.append(", ");
                    i++;
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
        if (!tableSchemas.values().stream().anyMatch(schema ->
                schema.size() == data.size() + 1)) { // +1 для учета id
            throw new SQLException("Количество столбцов не соответствует ни одной схеме");
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



    public boolean isTableExists(String tableName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, tableName, new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    public void saveToExcel() throws SQLException {
        Scanner sc = new Scanner(System.in);

        // Запрашиваем название таблицы для сохранения
        System.out.println("Введите название таблицы для сохранения:");
        String tableName = sc.nextLine().trim();

        // Проверяем существование таблицы
        if (!isTableExists(tableName)) {
            System.out.println("Таблица '" + tableName + "' не существует!");
            return;
        }

        System.out.println("Введите название файла для сохранения:");
        String fileName = sc.nextLine().trim();
        if (!fileName.toLowerCase().endsWith(".xlsx")) {
            fileName += ".xlsx";
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            System.out.println("Экспортируем таблицу: " + tableName);
            String query = "SELECT * FROM " + tableName;

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                XSSFSheet sheet = workbook.createSheet(tableName);

                // Заголовок с названием таблицы
                Row titleRow = sheet.createRow(0);
                titleRow.createCell(0).setCellValue("Таблица: " + tableName);

                // Заголовки столбцов
                Row header = sheet.createRow(1);
                for (int i = 1; i <= columnCount; i++) {
                    header.createCell(i - 1).setCellValue(metaData.getColumnName(i));
                }

                // Данные таблицы
                int rowIndex = 2;
                while (rs.next()) {
                    Row row = sheet.createRow(rowIndex++);
                    for (int i = 1; i <= columnCount; i++) {
                        Object value = rs.getObject(i);
                        if (value != null) {
                            row.createCell(i - 1).setCellValue(value.toString());
                        } else {
                            row.createCell(i - 1).setCellValue("");
                        }
                    }
                }

                // Автоподбор ширины столбцов
                for (int i = 0; i < columnCount; i++) {
                    sheet.autoSizeColumn(i);
                }

            } catch (SQLException e) {
                System.out.println("Ошибка при экспорте таблицы " + tableName + ": " + e.getMessage());
                return;
            }

            // Сохранение файла
            try (FileOutputStream fos = new FileOutputStream(fileName)) {
                workbook.write(fos);
                System.out.println("Таблица '" + tableName + "' успешно экспортирована в файл: " + fileName);
            }

        } catch (Exception e) {
            System.err.println("Ошибка при создании Excel-файла: " + e.getMessage());
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

    public void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
            System.out.println("Соединение с базой данных закрыто");
        }
    }

    public boolean hasTables() throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, "%", new String[]{"TABLE"})) {
            return rs.next(); // Возвращает true, если есть хотя бы одна таблица
        }
    }
}
