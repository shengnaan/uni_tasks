package common;

import java.sql.SQLException;
import java.util.*;

public abstract class BaseTask {
    protected SQLTools sqlTools;
    protected static String menuText = "";
    protected static Map<String, Map<String, String>> tableSchemas;

    public BaseTask(SQLTools sqlTools) throws SQLException {
        this.sqlTools = sqlTools;
    }

    public static class TableAndColumns {
        private final String tableName;
        private final Map<String, String> columns;

        public TableAndColumns(String tableName, Map<String, String> columns) {
            this.tableName = tableName;
            this.columns = columns;
        }

        public String getTableName() {
            return tableName;
        }

        public String getColumn(String prompt) {
            return columns.get(prompt);
        }

        public Map<String, Object> createInsertMap(Map<String, Object> logicalToValue) {
            Map<String, Object> result = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : logicalToValue.entrySet()) {
                String realColumnName = columns.get(entry.getKey());
                if (realColumnName != null) {
                    result.put(realColumnName, entry.getValue());
                }
            }
            return result;
        }
    }

    public void showMenu(String menuText) {
        System.out.println("📋 Меню:");
        System.out.println(menuText);
    }

    public void showTables() throws SQLException {
        this.sqlTools.showTables();
    }

    public void createTables() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        for (Map.Entry<String, Map<String, String>> entry : tableSchemas.entrySet()) {
            String tableName;
            while (true) {
                System.out.println("Введите название таблицы для схемы (только латинские буквы, цифры и знак подчеркивания):");
                tableName = scanner.nextLine().trim();

                if (sqlTools.isTableExists(tableName)) {
                    System.out.println("Таблица с таким именем уже существует.");
                    return;
                }
                if (tableName.length() > 63) {
                    System.out.println("Предупреждение: название таблицы превышает 63 символа. Будет обрезано до: " +
                            tableName.substring(0, 63));
                    tableName = tableName.substring(0, 63);
                }
                if (tableName.matches("^[a-zA-Z][a-zA-Z0-9_]*$")) {
                    break;
                }
                System.out.println("Ошибка: недопустимое название таблицы. Название должно:\n" +
                        "- Начинаться с буквы\n" +
                        "- Содержать только буквы, цифры и знак подчеркивания\n" +
                        "Попробуйте снова.");
            }

            Map<String, String> columns = new LinkedHashMap<>();
            int colIndex = 0;
            for (Map.Entry<String, String> column : entry.getValue().entrySet()) {
                String columnName;
                while (true) {
                    System.out.println("Введите название столбца #" + (colIndex + 1) + " (тип: " + column.getValue() + "):");
                    columnName = scanner.nextLine().trim();
                    if (columnName.matches("^[a-zA-Z][a-zA-Z0-9_]*$") && columnName.length() <= 63) {
                        if (!columns.containsKey(columnName)) {
                            break;
                        } else {
                            System.out.println("Ошибка: столбец с таким именем уже существует.");
                        }
                    }
                    System.out.println("Ошибка: недопустимое название столбца. Название должно:\n" +
                            "- Начинаться с буквы\n" +
                            "- Содержать только буквы, цифры и знак подчеркивания\n" +
                            "- Быть не длиннее 63 символов\n" +
                            "Попробуйте снова.");
                }
                columns.put(columnName, column.getValue());
                colIndex++;
            }

            this.sqlTools.createTables(columns, tableName);
        }
    }

    public void insertRowIntoDB(String tablename, Map<String, Object> data) throws SQLException {
        this.sqlTools.insertRowIntoDB(tablename, data);
    }

    public void saveToExcel() throws SQLException {
        this.sqlTools.saveToExcel();
    }

    public boolean hasTables() throws SQLException {
        return this.sqlTools.hasTables();
    }

    public TableAndColumns promptTableAndColumns(
            Scanner scanner,
            List<String> columnsPrompts
    ) throws SQLException {

        String tableName = getNonEmptyInput(scanner, "Введите название таблицы для сохранения результатов:");
        if (!sqlTools.isTableExists(tableName)) {
            System.out.println("Ошибка: таблица '" + tableName + "' не существует.");
            return null;
        }

        Map<String, String> columnMapping = new LinkedHashMap<>();
        for (String prompt : columnsPrompts) {
            String userColumn;
            while (true) {
                userColumn = getNonEmptyInput(scanner, prompt);
                if (!sqlTools.isColumnExists(tableName, userColumn)) {
                    System.out.println("Ошибка: в таблице '" + tableName + "' нет столбца '" + userColumn + "'. Попробуйте снова.");
                } else {
                    break;
                }
            }
            columnMapping.put(prompt, userColumn);
        }

        return new TableAndColumns(tableName, columnMapping);
    }

    protected String getNonEmptyInput(Scanner scanner, String promptMessage) {
        while (true) {
            System.out.println(promptMessage);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Ошибка: ввод не может быть пустым. Попробуйте снова.");
        }
    }


}