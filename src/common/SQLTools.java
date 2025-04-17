package common;

import java.sql.*;
import java.util.List;
import java.util.Map;

public final class SQLTools {

    private final Connection conn;

    public SQLTools(String dbName) throws SQLException {
        this.conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/" + dbName, "postgres", "postgres"
        );
    }

    public void showTables() throws SQLException {
        String tablesSql = "SELECT table_name FROM information_schema.tables WHERE table_schema='public'";

        try (
                Statement tableStmt = conn.createStatement();
                ResultSet tablesRs = tableStmt.executeQuery(tablesSql)
        ) {
            while (tablesRs.next()) {
                String tableName = tablesRs.getString("table_name");
                System.out.println("\nТаблица: " + tableName);

                String columnsQuery =
                        "SELECT column_name, data_type " +
                                "FROM information_schema.columns " +
                                "WHERE table_schema='public' AND table_name = ?";

                try (
                        PreparedStatement columnsStmt = conn.prepareStatement(columnsQuery)
                ) {
                    columnsStmt.setString(1, tableName);
                    try (ResultSet columnsRs = columnsStmt.executeQuery()) {
                        while (columnsRs.next()) {
                            String columnName = columnsRs.getString("column_name");
                            String dataType = columnsRs.getString("data_type");
                            System.out.printf("   🔹 %s : %s%n", columnName, dataType);
                        }
                    }
                }
            }
        }
    }


    public void createTables(List<String> tableNames, List<Map<String, String>> columnsList) throws SQLException {
        for (int i = 0; i < tableNames.size(); i++) {
            String tableName = tableNames.get(i);
            Map<String, String> columns = columnsList.get(i);

            StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " (");

            int colIndex = 0;
            for (Map.Entry<String, String> entry : columns.entrySet()) {
                sb.append(entry.getKey()).append(" ").append(entry.getValue());
                if (colIndex < columns.size() - 1) sb.append(", ");
                colIndex++;
            }

            sb.append(")");

            String sql = sb.toString();

            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(sql);
            }

            System.out.println("Табличка(-и) в БД успешно создана");
        }
    }


    public void insertRowIntoDB(String tablename, Map<String, Object> data) throws SQLException {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Данные для вставки не могут быть пустыми");
        }

        String columns = String.join(", ", data.keySet());
        String placeholders = String.join(", ", data.keySet().stream().map(k -> "?").toList());

        String sql = "INSERT INTO " + tablename + " (" + columns + ") VALUES (" + placeholders + ")";

        try (var pstmt = conn.prepareStatement(sql)) {
            int index = 1;
            for (Object value : data.values()) {
                pstmt.setObject(index++, value);
            }
            pstmt.executeUpdate();
        }
    }


    public void saveToExcel() {

    }

    public void closeConnection() throws SQLException {

    }
}
