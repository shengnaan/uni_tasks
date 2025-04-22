package tasks.task_4;

import common.BaseTask;
import common.SQLTools;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main extends BaseTask {

    public Main(String dbName, Map<String, Map<String, String>> tableSchemas, Map<String, Map<String, String>> TEMPLATE_SCHEMA) throws SQLException {
        super(new SQLTools(dbName, tableSchemas, TEMPLATE_SCHEMA));
        menuText = """
                1. Вывести все таблицы из БД.
                2. Создать таблицу в БД.
                3. Возвращение подстроки по индексам.
                4. Перевод строк в верхний и нижний регистры.
                5. Поиск подстроки и определение окончания подстроки.
                6. Сохранить все данные из БД в Excel и вывести на экран.
                7. Показать меню
                8. Остановить программу.
                """;
    }

    public static void main(String[] args) throws SQLException {
        final Map<String, Map<String, String>> TEMPLATE_SCHEMA = Map.of(
                "strings", Map.of(
                        "id", "SERIAL PRIMARY KEY",
                        "operation", "VARCHAR(50)",
                        "input", "VARCHAR(150)",
                        "result", "VARCHAR(150)"
                )
        );

        Map<String, Map<String, String>> tableSchemas = new HashMap<>();



        final String dbName = "task_4";
        Main main = new Main(dbName, tableSchemas, TEMPLATE_SCHEMA);

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
        main.sqlTools.closeConnection();

    }

    public void substringByIndex() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите строку: ");
        String input = scanner.nextLine();

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
        System.out.println("Результат: " + result);

        try {
            sqlTools.insertRowIntoDB("strings", Map.of(
                    "operation", "substring",
                    "input", input + " (start=" + startIndex + ", end=" + endIndex + ")",
                    "result", result
            ));
        } catch (SQLException e) {
            System.out.println("Ошибка при сохранении в БД: " + e.getMessage());
        }
    }

    public void changeCase() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите строку: ");
        String input = scanner.nextLine();

        String upperCase = input.toUpperCase();
        String lowerCase = input.toLowerCase();

        System.out.println("Верхний регистр: " + upperCase);
        System.out.println("Нижний регистр: " + lowerCase);

        try {
            sqlTools.insertRowIntoDB("strings", Map.of(
                    "operation", "uppercase",
                    "input", input,
                    "result", upperCase
            ));
            sqlTools.insertRowIntoDB("strings", Map.of(
                    "operation", "lowercase",
                    "input", input,
                    "result", lowerCase
            ));
        } catch (SQLException e) {
            System.out.println("Ошибка при сохранении в БД: " + e.getMessage());
        }
    }

    public void searchSubstring() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите строку: ");
        String input = scanner.nextLine();

        System.out.println("Введите подстроку для поиска: ");
        String substring = scanner.nextLine();

        int index = input.indexOf(substring);
        boolean endsWithSubstring = input.endsWith(substring);

        String result = "Индекс: " + index + ", Заканчивается подстрокой: " + endsWithSubstring;
        System.out.println(result);

        try {
            sqlTools.insertRowIntoDB("strings", Map.of(
                    "operation", "search",
                    "input", input + " (искомое: " + substring + ")",
                    "result", result
            ));
        } catch (SQLException e) {
            System.out.println("Ошибка при сохранении в БД: " + e.getMessage());
        }
    }
}