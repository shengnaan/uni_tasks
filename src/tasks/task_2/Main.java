package tasks.task_2;

import common.BaseTask;
import common.SQLTools;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main extends BaseTask{

    public Main(String dbName, Map<String, Map<String, String>> tableSchemas) throws SQLException {
        super(new SQLTools(dbName, tableSchemas));
        menuText = """
                1. Вывести все таблицы из MySQL.
                2. Создать таблицу в MySQL.
                3. Ввести две строки с клавиатуры, результат сохранить в MySQL с последующим выводом в
                консоль.
                4. Подсчитать размер ранее введенных строк, результат сохранить в MySQL с последующим
                выводом в консоль.
                5. Объединить две строки в единое целое, результат сохранить в MySQL с последующим выводом
                в консоль.
                6. Сравнить две ранее введенные строки, результат сохранить в MySQL с последующим выводом
                в консоль.
                7. Сохранить все данные (вышеполученные результаты) из MySQL в Excel и вывести на экран.
                8. Показать меню.
                9. Остановить программу.
                """;
        BaseTask.tableSchemas = tableSchemas;
    }

    private static String lastString1 = "";
    private static String lastString2 = "";
    private static long lastInsertedId = -1;

    public static void main(String[] args) throws SQLException {
        final Map<String, Map<String, String>> tableSchemas = Map.of(
                "strings", Map.of(
                        "id", "SERIAL PRIMARY KEY",
                        "first_string", "VARCHAR(255)",
                        "second_string", "VARCHAR(255)",
                        "length_1", "INT4",
                        "length_2", "INT4",
                        "is_equal", "BOOL",
                        "result", "VARCHAR(510)"
                )
        );

        final String dbName = "task_2";
        Main main = new Main(dbName, tableSchemas);

        System.out.println("Практическая работа 2");
        main.showMenu(menuText);

        String var = "";
        int menuPunkt = 0;

        Scanner sc = new Scanner(System.in);

        while (!"9".equals(var)) {
            var = sc.next();

            try {
                menuPunkt = Integer.parseInt(var);
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат ввода!");
                continue;
            }

            switch (menuPunkt) {
                case 1 -> main.showTables();
                case 2 -> main.createTables();
                case 3 -> main.input();
                case 4 -> main.countLength();
                case 5 -> main.concatStrings();
                case 6 -> main.compareStrings();
                case 7 -> main.saveToExcel();
                case 8 -> main.showMenu(menuText);
                case 9 -> System.out.println("Программа завершила работу");
                default -> System.out.println("Неверная опция");
            }
        }
        main.closeConnection();
    }

    public void input() throws SQLException {

        if (!sqlTools.hasTables()) {
            System.out.println("Ошибка: В базе данных нет ни одной таблицы. Сначала создайте таблицу (пункт 2 меню).");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        TableAndColumns tableAndCols = promptTableAndColumns(
                scanner,
                List.of(
                        "Введите название столбца для хранения первой строки (тип VARCHAR(255)): ",
                        "Введите название столбца для хранения второй строки (тип VARCHAR(255)): "
                )
        );

        if (tableAndCols == null) {
            return;
        }

        String string1 = getNonEmptyInput(scanner, "Введите первую строку: ");
        String string2 = getNonEmptyInput(scanner, "Введите вторую строку: ");

        lastString1 = string1;
        lastString2 = string2;

        Map<String, Object> dataLogical = Map.of(
                "Введите название столбца для хранения первой строки (тип VARCHAR(255)): ", string1,
                "Введите название столбца для хранения второй строки (тип VARCHAR(255)): ", string2
        );

        Map<String, Object> dataReal = tableAndCols.createInsertMap(dataLogical);

        insertRowIntoDB(
                tableAndCols.getTableName(),
                dataReal
        );

        lastInsertedId = sqlTools.getLastInsertedId(tableAndCols.getTableName());

        System.out.println("Первая строка: " + string1 + ", Вторая строка: " + string2);
    }

    public void countLength() throws SQLException {
        if (lastString1.isEmpty() || lastString2.isEmpty()) {
            System.out.println("Ошибка: строки еще не введены. Сначала выполните пункт 3 меню.");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        TableAndColumns tableAndCols = promptTableAndColumns(
                scanner,
                List.of(
                        "Введите название столбца для хранения длины первой строки (тип INT4): ",
                        "Введите название столбца для хранения длины второй строки (тип INT4): "
                )
        );

        if (tableAndCols == null) {
            return;
        }

        int len1 = lastString1.length();
        int len2 = lastString2.length();

        Map<String, Object> dataLogical = Map.of(
                "Введите название столбца для хранения длины первой строки (тип INT4): ", len1,
                "Введите название столбца для хранения длины второй строки (тип INT4): ", len2
        );

        Map<String, Object> dataReal = tableAndCols.createInsertMap(dataLogical);

        sqlTools.updateRowInDB(
                tableAndCols.getTableName(),
                dataReal,
                lastInsertedId
        );


        System.out.printf("Длина строк: %d и %d%n", len1, len2);
    }

    public void concatStrings() throws SQLException {
        if (lastString1.isEmpty() || lastString2.isEmpty()) {
            System.out.println("Ошибка: строки еще не введены. Сначала выполните пункт 3 меню.");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        TableAndColumns tableAndCols = promptTableAndColumns(
                scanner,
                List.of(
                        "Введите название столбца для хранения результата объединения строк (тип VARCHAR(510)): "
                )
        );

        if (tableAndCols == null) {
            return;
        }

        String concat_result = lastString1.concat(lastString2);

        Map<String, Object> dataLogical = Map.of(
                "Введите название столбца для хранения результата объединения строк (тип VARCHAR(510)): ", concat_result
        );

        Map<String, Object> dataReal = tableAndCols.createInsertMap(dataLogical);

        sqlTools.updateRowInDB(
                tableAndCols.getTableName(),
                dataReal,
                lastInsertedId
        );

        System.out.printf("Результат объединения строк %s и %s: %s%n", lastString1, lastString2, concat_result);
    }

    public void compareStrings() throws SQLException {
        if (lastString1.isEmpty() || lastString2.isEmpty()) {
            System.out.println("Ошибка: строки еще не введены. Сначала выполните пункт 3 меню.");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        TableAndColumns tableAndCols = promptTableAndColumns(
                scanner,
                List.of(
                        "Введите название столбца для хранения результата сравнения строк (тип BOOL): "
                )
        );

        if (tableAndCols == null) {
            return;
        }

        boolean flag;

        flag = lastString1.equals(lastString2);

        Map<String, Object> dataLogical = Map.of(
                "Введите название столбца для хранения результата сравнения строк (тип BOOL): ", flag
        );

        Map<String, Object> dataReal = tableAndCols.createInsertMap(dataLogical);

        sqlTools.updateRowInDB(
                tableAndCols.getTableName(),
                dataReal,
                lastInsertedId
        );

        System.out.println("Строки равны? Ответ: " + flag);
    }

    protected String getNonEmptyInput(Scanner scanner, String promptMessage) {
        String input;
        while (true) {
            System.out.println(promptMessage);
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Ошибка: ввод не может быть пустым. Попробуйте снова.");
        }
    }

}
