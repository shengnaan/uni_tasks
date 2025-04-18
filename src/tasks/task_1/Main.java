package tasks.task_1;

import common.BaseTask;
import common.SQLTools;

import java.sql.SQLException;
import java.util.Map;
import java.util.Scanner;

public class Main extends BaseTask {

    public Main(String dbName, Map<String, Map<String, String>> tableSchemas) throws SQLException {
        super(new SQLTools(dbName, tableSchemas));
        menuText = """
                1. Вывести все таблицы из БД.
                2. Создать таблицу(-ы) в БД.
                3. Сложение чисел, результат сохранить в БД с последующим выводом в консоль.
                4. Вычитание чисел, результат сохранить в БД с последующим выводом в консоль.
                5. Умножение чисел, результат сохранить в БД с последующим выводом в консоль.
                6. Деление чисел, результат сохранить в БД с последующим выводом в консоль.
                7. Деление чисел по модулю (остаток), результат сохранить в БД с последующим выводом в консоль.
                8. Возведение числа в модуль, результат сохранить в БД с последующим выводом в консоль.
                9. Возведение числа в степень, результат сохранить в БД с последующим выводом в консоль.
                10. Сохранить все данные (вышеполученные результаты) из БД в Excel и вывести на экран.
                11. Показать меню
                12. Остановить программу.
                """;
    }

    public static void main(String[] args) throws SQLException {
        final Map<String, Map<String, String>> tableSchemas = Map.of(
                "expressions", Map.of(
                        "id", "SERIAL PRIMARY KEY",
                        "result", "FLOAT",
                        "operation", "VARCHAR(50)"
                ),
                "sfsdf", Map.of("sdf", "INTEGER",
                        "sdfsdfsdf", "INTEGER")
        );
        final String dbName = "task_1";
        Main main = new Main(dbName, tableSchemas);

        System.out.println("Практическая работа 1");
        main.showMenu(menuText);

        String var = "";
        int menuPunkt = 0;

        Scanner sc = new Scanner(System.in);

        while (!"12".equals(var)) {
            var = sc.next();

            try {
                menuPunkt = Integer.parseInt(var);
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат ввода!");
            }

            switch (menuPunkt) {
                case 1 -> main.showTables();
                case 2 -> main.createTables();
                case 3 -> main.count("+");
                case 4 -> main.count("-");
                case 5 -> main.count("*");
                case 6 -> main.count("/");
                case 7 -> main.count("%");
                case 8 -> main.count("abs");
                case 9 -> main.count("pow");
                case 10 -> main.saveToExcel();
                case 11 -> main.showMenu(menuText);
                case 12 -> System.out.println("Программа завершила работу");
                default -> System.out.println("Неверная опция");
            }
        }
        main.sqlTools.closeConnection();

    }

    public void count(String operation) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        double a, b = 0;
        double result;

        if (operation.equals("abs")) {
            a = getValidDouble(scanner, "Введите число: ");
            result = Math.abs(a);
        } else if (operation.equals("pow")) {
            a = getValidDouble(scanner, "Введите основание: ");
            b = getValidDouble(scanner, "Введите степень: ");
            result = Math.pow(a, b);
        } else {
            a = getValidDouble(scanner, "Введите первое число: ");
            b = getValidDouble(scanner, "Введите второе число: ");

            switch (operation) {
                case "+" -> result = a + b;
                case "-" -> result = a - b;
                case "*" -> result = a * b;
                case "/" -> {
                    if (b == 0) {
                        System.out.println("Ошибка: деление на ноль.");
                        return;
                    }
                    result = a / b;
                }
                case "%" -> {
                    if (b == 0) {
                        System.out.println("Ошибка: деление по модулю на ноль.");
                        return;
                    }
                    result = a % b;
                }
                default -> {
                    System.out.println("Неизвестная операция.");
                    return;
                }
            }
        }

        String fullOperation = switch (operation) {
            case "+", "-", "*", "/", "%" -> a + " " + operation + " " + b;
            case "abs" -> "|" + a + "|";
            case "pow" -> a + "^" + b;
            default -> "Неопределенно";
        };

        this.insertRowIntoDB(
                "expressions",
                Map.of(
                        "result", result,
                        "operation", fullOperation
                )
        );
        System.out.printf("✅ Операция: %s = %s%n", fullOperation, result);
    }

    private double getValidDouble(Scanner scanner, String prompt) {
        double value = 0;
        boolean valid = false;

        while (!valid) {
            System.out.print(prompt);
            String input = scanner.next();
            try {
                value = Double.parseDouble(input);
                valid = true;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное число (например, 12.3)");
            }
        }

        return value;
    }

}
