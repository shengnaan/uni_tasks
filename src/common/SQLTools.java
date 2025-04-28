package common;

import java.io.FileOutputStream;
import java.sql.*;
import java.util.*;

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

    public void createTables(Map<String, String> columns, String tableName) throws SQLException {
        StringBuilder sb = new StringBuilder("CREATE TABLE ");
        sb.append('"').append(tableName).append('"').append(" (");

        int i = 0;
        for (Map.Entry<String, String> column : columns.entrySet()) {
            sb.append('"').append(column.getKey()).append('"')
                    .append(" ").append(column.getValue());
            if (i < columns.size() - 1) sb.append(", ");
            i++;
        }

        sb.append(")");

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sb.toString());
            System.out.println("Таблица создана: " + tableName);
        }
    }


    public void insertRowIntoDB(String tablename, Map<String, Object> data) throws SQLException {
        System.out.println(tablename);
        if (!isTableExists(tablename)) {
            throw new SQLException("Не найдена схема для таблицы '" + tablename + "'");
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

        String tableName;
        while (true) {
            System.out.println("Введите название таблицы для сохранения:");
            tableName = sc.nextLine().trim();
            if (!isTableExists(tableName)) {
                System.out.println("Таблица '" + tableName + "' не существует! Попробуйте снова.");
            } else {
                break;
            }
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

                Row titleRow = sheet.createRow(0);
                titleRow.createCell(0).setCellValue("Таблица: " + tableName);

                Row header = sheet.createRow(1);
                for (int i = 1; i <= columnCount; i++) {
                    header.createCell(i - 1).setCellValue(metaData.getColumnName(i));
                }

                StringBuilder headerBuilder = new StringBuilder();
                headerBuilder.append("|");
                for (int i = 1; i <= columnCount; i++) {
                    headerBuilder.append(" ")
                            .append(metaData.getColumnName(i))
                            .append(" |");
                }
                System.out.println(headerBuilder);

                int rowIndex = 2;
                while (rs.next()) {
                    Row row = sheet.createRow(rowIndex++);
                    StringBuilder rowBuilder = new StringBuilder();
                    rowBuilder.append("|");

                    for (int i = 1; i <= columnCount; i++) {
                        Object value = rs.getObject(i);

                        String cellValue = (value != null) ? value.toString() : "";
                        row.createCell(i - 1).setCellValue(cellValue);

                        rowBuilder.append(" ")
                                .append(cellValue)
                                .append(" |");
                    }

                    System.out.println(rowBuilder);
                }

                for (int i = 0; i < columnCount; i++) {
                    sheet.autoSizeColumn(i);
                }

            } catch (SQLException e) {
                System.out.println("Ошибка при экспорте таблицы " + tableName + ": " + e.getMessage());
                return;
            }

            try (FileOutputStream fos = new FileOutputStream(fileName)) {
                workbook.write(fos);
                System.out.println("Таблица '" + tableName + "' успешно экспортирована в файл: " + fileName);
            }

        } catch (Exception e) {
            System.err.println("Ошибка при создании Excel-файла: " + e.getMessage());
        }
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
            return rs.next();
        }
    }

    public boolean isColumnExists(String tableName, String columnName) throws SQLException {
        String query = """
                    SELECT column_name 
                    FROM information_schema.columns 
                    WHERE table_schema = 'public'
                      AND table_name = ?
                      AND column_name = ?
                """;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, tableName);
            stmt.setString(2, columnName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public String getColumnType(String tableName, String columnName) throws SQLException {
        // Предположим, что у нас есть свойство connection типа java.sql.Connection
        // и что в конструкторе SQLTools это соединение уже устанавливается
        DatabaseMetaData metaData = conn.getMetaData();

        // В некоторых СУБД имя таблиц и столбцов может формироваться в разном регистре;
        // часто имеет смысл привести их к верхнему/нижнему регистру.
        try (ResultSet columns = metaData.getColumns(null, null, tableName, columnName)) {
            if (columns.next()) {
                // Получаем тип из метаданных. Поле "TYPE_NAME" содержит наименование типа
                // (например, "VARCHAR", "INT", "FLOAT", "CHAR" и т. д.)
                String typeName = columns.getString("TYPE_NAME");
                int size = columns.getInt("COLUMN_SIZE");

                // Если это строковый тип, обычно имеет смысл отобразить и размер в формате "VARCHAR(255)"
                // В реальной БД могут встречаться нюансы: например, "TEXT" не всегда имеет size.
                // Ниже — упрощённый пример формирования итоговой строки.
                if (typeName != null) {
                    typeName = typeName.trim().toUpperCase();
                    // Если это разновидность строкового типа, добавим размеры
                    if (typeName.contains("CHAR")) {
                        return typeName + "(" + size + ")";
                    } else {
                        // Возвращаем просто тип, например "INTEGER", "FLOAT"
                        return typeName;
                    }
                }
            }
        }

        // Если в метаданных нет информации (или не нашли столбец), вернём null или бросим исключение
        return null;
    }

    public long getLastInsertedId(String tableName, String columnName) throws SQLException {
        String sql = "SELECT MAX(" + columnName + ") FROM " + tableName;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getLong(1);
            } else {
                throw new SQLException("Не удалось получить последний ID");
            }
        }
    }

    public void updateRowInDB(String tableName, Map<String, Object> data, long id) throws SQLException {
        StringBuilder query = new StringBuilder("UPDATE " + tableName + " SET ");
        List<String> assignments = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            assignments.add(entry.getKey() + " = ?");
            values.add(entry.getValue());
        }

        query.append(String.join(", ", assignments));
        String serialColumn = findSerialColumn(tableName);
        query.append(" WHERE ").append(serialColumn).append(" = ?");

        values.add(id);

        try (PreparedStatement stmt = conn.prepareStatement(query.toString())) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }
            stmt.executeUpdate();
        }
    }

    public String findSerialColumn(String tableName) throws SQLException {
        final String query = """
                    SELECT column_name,
                           data_type,
                           column_default
                    FROM information_schema.columns
                    WHERE table_schema = 'public'
                      AND table_name = ?
                    ORDER BY ordinal_position
                """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, tableName);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String columnName = rs.getString("column_name");
                    String columnDefault = rs.getString("column_default");
                    if (columnDefault != null && columnDefault.toLowerCase().startsWith("nextval(")) {
                        return columnName;
                    }
                }
            }
        }
        throw new SQLException("Не удалось найти столбец SERIAL для таблицы " + tableName);
    }
}