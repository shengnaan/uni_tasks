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
                        "first_str", "VARCHAR(255)",
                        "second_str", "VARCHAR(255)",
                        "result", "VARCHAR(520)"
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
        if (!sqlTools.hasTables()) {
            System.out.println("Ошибка: в базе данных нет ни одной таблицы. Сначала создайте таблицы (пункт 2 меню).");
            return;
        }
        Scanner scanner = new Scanner(System.in);

        TableAndColumns tableAndCols = promptTableAndColumns(
                scanner,
                List.of(
                        "Введите название столбца для хранения операции (тип VARCHAR(255)): ",
                        "Введите название столбца для хранения первой строки (тип VARCHAR(255)): ",
                        "Введите название столбца для хранения индекса (тип SERIAL): ",
                        "Введите название столбца для хранения результата операции (тип VARCHAR(520)): "
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
                "Введите название столбца для хранения первой строки (тип VARCHAR(255)): ", input,
                "Введите название столбца для хранения индекса (тип SERIAL): ", endIndex,
                "Введите название столбца для хранения результата операции (тип VARCHAR(520)): ", result
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
            System.out.println("Ошибка: в базе данных нет ни одной таблицы. Сначала создайте таблицы (пункт 2 меню).");
            return;
        }
        Scanner scanner = new Scanner(System.in);

        TableAndColumns tableAndCols = promptTableAndColumns(
                scanner,
                List.of(
                        "Введите название столбца для хранения операции (тип VARCHAR(255)): ",
                        "Введите название столбца для хранения входной строки (тип VARCHAR(255)): ",
                        "Введите название столбца для хранения результата операции (тип VARCHAR(520)): "
                )
        );

        if (tableAndCols == null) {
            return;
        }

        System.out.println("Введите строку: ");
        String input = scanner.nextLine();

        String upperCase = input.toUpperCase();
        String lowerCase = input.toLowerCase();

        System.out.println("Верхний регистр: " + upperCase);
        System.out.println("Нижний регистр: " + lowerCase);

        Map<String, Object> dataLogical = Map.of(
                "Введите название столбца для хранения операции (тип VARCHAR(255)): ", "Upper/Lower Case",
                "Введите название столбца для хранения входной строки (тип VARCHAR(255)): ", input,
                "Введите название столбца для хранения результата операции (тип VARCHAR(520)): ", upperCase + " + " + lowerCase
        );

        Map<String, Object> dataReal = tableAndCols.createInsertMap(dataLogical);
        String table = tableAndCols.getTableName();

        insertRowIntoDB(
                table,
                dataReal
        );
    }

    public void searchSubstring() throws SQLException {
        if (!sqlTools.hasTables()) {
            System.out.println("Ошибка: в базе данных нет ни одной таблицы. Сначала создайте таблицы (пункт 2 меню).");
            return;
        }
        Scanner scanner = new Scanner(System.in);

        TableAndColumns tableAndCols = promptTableAndColumns(
                scanner,
                List.of(
                        "Введите название столбца для хранения операции (тип VARCHAR(255)): ",
                        "Введите название столбца для хранения входной строки (тип VARCHAR(255)): ",
                        "Введите название столбца для хранения результата операции (тип VARCHAR(520)): "
                )
        );

        if (tableAndCols == null) {
            return;
        }
        System.out.println("Введите строку: ");
        String input = scanner.nextLine();

        System.out.println("Введите подстроку для поиска: ");
        String substring = scanner.nextLine();

        int index = input.indexOf(substring);
        boolean endsWithSubstring = input.endsWith(substring);

        String result = "Индекс: " + index + ", Заканчивается подстрокой: " + endsWithSubstring;
        System.out.println(result);

        Map<String, Object> dataLogical = Map.of(
                "Введите название столбца для хранения операции (тип VARCHAR(255)): ", "Найти индекс подстроки",
                "Введите название столбца для хранения входной строки (тип VARCHAR(510)): ", input,
                "Введите название столбца для хранения результата операции (тип VARCHAR(510)): ", result
        );

        Map<String, Object> dataReal = tableAndCols.createInsertMap(dataLogical);
        String table = tableAndCols.getTableName();

        insertRowIntoDB(
                table,
                dataReal
        );
    }
}