package tasks.task_1;

import common.BaseTask;
import common.SQLTools;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main extends BaseTask {
    private static String menuText = "";

    public Main(String dbName) throws SQLException {
        super(new SQLTools(dbName));
        menuText = """
                1. Вывести все таблицы из БД.
                2. Создать таблицу в БД.
                3. Сложение чисел, результат сохранить в БД с последующим выводом в консоль.
                4. Вычитание чисел, результат сохранить в БД с последующим выводом в консоль.
                5. Умножение чисел, результат сохранить в БД с последующим выводом в консоль.
                6. Деление чисел, результат сохранить в БД с последующим выводом в консоль.
                7. Деление чисел по модулю (остаток), результат сохранить в БД с последующим выводом в консоль.
                8. Возведение числа в модуль, результат сохранить в БД с последующим выводом в консоль.
                9. Возведение числа в степень, результат сохранить в БД с последующим выводом в консоль.
                10. Сохранить все данные (вышеполученные результаты) из БД в Excel и вывести на экран.
                11. Остановить программу.
                """;
    }

    public static void main(String[] args) throws SQLException {
        Main main = new Main("task_1");

        System.out.println("Практическая работа 1");
        System.out.println(menuText);

        String var = "";
        int menuPunkt = 0;

        Scanner sc = new Scanner(System.in);

        while (!"11".equals(var)) {
            var = sc.next();

            try {
                menuPunkt = Integer.parseInt(var);
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат ввода!");
            }

            switch (menuPunkt) {
                case 1 -> main.showTables();
                case 2 -> main.createTables(
                        List.of("expressions"),
                        List.of(
                                Map.of(
                                        "id", "SERIAL PRIMARY KEY",
                                        "result", "VARCHAR(255)",
                                        "operation", "VARCHAR(255)"
                                )
                        )
                );
                case 3 -> main.count();
                case 10 -> main.saveToExcel();
            }
        }
    }

    public void count() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Выберите операцию:");
        System.out.println(
                """
                        + : сложение
                        - : вычитание
                        * : умножение
                        / : деление
                        % : остаток от деления
                        abs : модуль числа
                        pow : возведение в степень
                        """
        );

        String operation = scanner.next();
        double a, b = 0;
        double result = 0;

        if (operation.equals("abs")) {
            System.out.print("Введите число: ");
            a = scanner.nextDouble();
            result = Math.abs(a);
        } else if (operation.equals("pow")) {
            System.out.print("Введите основание: ");
            a = scanner.nextDouble();
            System.out.print("Введите степень: ");
            b = scanner.nextDouble();
            result = Math.pow(a, b);
        } else {
            System.out.print("Введите первое число: ");
            a = scanner.nextDouble();
            System.out.print("Введите второе число: ");
            b = scanner.nextDouble();

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

        String resultStr = String.valueOf(result);
        String fullOperation = switch (operation) {
            case "+", "-", "*", "/", "%" -> a + " " + operation + " " + b;
            case "abs" -> "|" + a + "|";
            case "pow" -> a + "^" + b;
            default -> "Неопределено";
        };
//
//        String sql = "INSERT INTO expressions (result, operation) VALUES (?, ?)";
//        try (var pstmt = sqlTools.getConnection().prepareStatement(sql)) {
//            pstmt.setString(1, resultStr);
//            pstmt.setString(2, fullOperation);
//            pstmt.executeUpdate();
//        }

        System.out.printf("✅ Операция: %s = %s%n", fullOperation, resultStr);
    }

    public void insertRowIntoDB() throws SQLException {

    }

    @Override
    public void saveToExcel() {

    }
}
