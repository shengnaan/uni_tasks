package tasks.task_4;

import common.BaseTask;
import common.SQLTools;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main extends BaseTask {

    public Main(String dbName, Map<String, Map<String, String>> tableSchemas) throws SQLException {
        super(new SQLTools(dbName, tableSchemas));
        menuText = """
                1. Вывести все таблицы из БД.
                2. Создать таблицу в БД.
                3. Возвращение подстроки по индексам, результат сохранить в MySQL с последующим выводом в консоль.
                4. Перевод строк в верхний и нижний регистры, результат сохранить в MySQL с последующим выводом в консоль.
                5. Поиск подстроки и определение окончания подстроки, результат сохранить в MySQL с последующим выводом в консоль.
                6. Сохранить все данные из БД в Excel и вывести на экран.
                7. Показать меню
                8. Остановить программу.
                """;
        BaseTask.tableSchemas = tableSchemas;
    }

    private static String lastString1 = "";
    private static String lastString2 = "";

    public static void main(String[] args) throws SQLException {
        final Map<String, Map<String, String>> tableSchemas = Map.of(
                "strings", Map.of(
                        "id", "SERIAL",
                        "operation", "VARCHAR(255)",
                        "input", "VARCHAR(510)",
                        "result", "VARCHAR(510)"
                )
        );
        final String dbName = "task_4";
        Main main = new Main(dbName, tableSchemas);

        System.out.println("Практическая работа 4");
        main.showMenu(menuText);

        String var = "";
        int menuPunkt = 0;

        Scanner sc = new Scanner(System.in);

        while (!"8".equals(var)) {
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
                case 3 -> main.substringByIndex();
                case 4 -> main.changeCase();
                case 5 -> main.searchSubstring();
                case 6 -> main.saveToExcel();
                case 7 -> main.showMenu(menuText);
                case 8 -> System.out.println("Программа завершила работу");
                default -> System.out.println("Неверная опция");
            }
        }
        main.closeConnection();
    }

    public void substringByIndex() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        TableAndColumns tableAndCols = promptTableAndColumns(
                scanner,
                List.of(
                        "Введите название столбца для хранения операции (тип VARCHAR(255)): ",
                        "Введите название столбца для хранения входной строки (тип VARCHAR(510)): ",
                        "Введите название столбца для хранения результата операции (тип VARCHAR(510)): "
                )
        );

        if (tableAndCols == null) {
            return;
        }

        String input = getNonEmptyInput(scanner, "Введите строку, из которой будет извлекаться подстрока: ");

        int startIndex = -1;
        while (startIndex == -1) {
            System.out.println("Введите начальный индекс (от 0 до " + (input.length() - 1) + "): ");
            try {
                startIndex = Integer.parseInt(scanner.nextLine());
                if (startIndex < 0 || startIndex >= input.length()) {
                    System.out.println("Ошибка: начальный индекс должен быть от 0 до " + (input.length() - 1));
                    startIndex = -1;
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное число");
            }
        }

        int endIndex = -1;
        while (endIndex == -1) {
            System.out.println("Введите конечный индекс (от " + (startIndex + 1) + " до " + input.length() + "): ");
            try {
                endIndex = Integer.parseInt(scanner.nextLine());
                if (endIndex <= startIndex || endIndex > input.length()) {
                    System.out.println("Ошибка: конечный индекс должен быть больше " + startIndex + " и не больше " + input.length());
                    endIndex = -1;
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное число");
            }
        }

        String result = input.substring(startIndex, endIndex);

        Map<String, Object> dataLogical = Map.of(
                "Введите название столбца для хранения операции (тип VARCHAR(255)): ", "Substring",
                "Введите название столбца для хранения входной строки (тип VARCHAR(510)): ", input,
                "Введите название столбца для хранения результата операции (тип VARCHAR(510)): ", result
        );


        Map<String, Object> dataReal = tableAndCols.createInsertMap(dataLogical);
        String table = tableAndCols.getTableName();

        insertRowIntoDB(
                table,
                dataReal
        );

        System.out.println("Результат: " + result);
    }

    public void changeCase() throws SQLException {
        if (!sqlTools.hasTables()) {
            System.out.println("Ошибка: В базе данных нет ни одной таблицы. Сначала создайте таблицу (пункт 2 меню).");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        String string1 = getNonEmptyInput(scanner, "Введите первую строку: ");
        String string2 = getNonEmptyInput(scanner, "Введите вторую строку: ");

        lastString1 = string1;
        lastString2 = string2;

        TableAndColumns tableAndCols = promptTableAndColumns(
                scanner,
                List.of(
                        "Введите название столбца для хранения операции (тип VARCHAR(255)): ",
                        "Введите название столбца для хранения строк (тип VARCHAR(510)): ",
                        "Введите название столбца для хранения результата операции (тип VARCHAR(510)): "
                )
        );

        if (tableAndCols == null) {
            return;
        }

        String string1Upper = string1.toUpperCase();
        String string1Lower = string1.toLowerCase();
        String string2Upper = string2.toUpperCase();
        String string2Lower = string2.toLowerCase();

        Map<String, Object> dataLogical = Map.of(
                "Введите название столбца для хранения операции (тип VARCHAR(255)): ", "Change Case",
                "Введите название столбца для хранения строк (тип VARCHAR(510)): ", string1 + "," + string2,
                "Введите название столбца для хранения результата операции (тип VARCHAR(510)): ", string1Upper + ", " + string1Lower + ", " + string2Upper + ", " + string2Lower
        );

        Map<String, Object> dataReal = tableAndCols.createInsertMap(dataLogical);
        String table = tableAndCols.getTableName();

        insertRowIntoDB(
                table,
                dataReal
        );

        System.out.println("Результаты преобразования:");
        System.out.println("Первая строка (верхний регистр): " + string1Upper);
        System.out.println("Первая строка (нижний регистр): " + string1Lower);
        System.out.println("Вторая строка (верхний регистр): " + string2Upper);
        System.out.println("Вторая строка (нижний регистр): " + string2Lower);
    }

    public void searchSubstring() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        TableAndColumns tableAndCols = promptTableAndColumns(
                scanner,
                List.of(
                        "Введите название столбца для хранения операции (тип VARCHAR(255)): ",
                        "Введите название столбца для хранения входной строки (тип VARCHAR(510)): ",
                        "Введите название столбца для хранения результата операции (тип VARCHAR(510)): "
                )
        );

        if (tableAndCols == null) {
            return;
        }

        String input = getNonEmptyInput(scanner, "Введите строку, в которой будет искаться подстрока: ");
        String substring = getNonEmptyInput(scanner, "Введите подстроку для поиска: ");

        int index = input.indexOf(substring);
        boolean endsWithSubstring = input.endsWith(substring);

        Map<String, Object> dataLogical = Map.of(
                "Введите название столбца для хранения операции (тип VARCHAR(255)): ", "Find_substring",
                "Введите название столбца для хранения входной строки (тип VARCHAR(510)): ", input + ", " + substring,
                "Введите название столбца для хранения результата операции (тип VARCHAR(510)): ", index + ", " + endsWithSubstring
        );

        Map<String, Object> dataReal = tableAndCols.createInsertMap(dataLogical);
        String table = tableAndCols.getTableName();

        insertRowIntoDB(
                table,
                dataReal
        );

        String result = "Индекс: " + index + ", Заканчивается подстрокой: " + endsWithSubstring;
        System.out.println(result);
    }
}