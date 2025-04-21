package tasks.task_5;

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
                3. Изменить порядок символов строки на обратный, результат сохранить в MySQL с последующим выводом в консоль.
                4. Добавить первую строку во вторую, результат сохранить в MySQL с последующим выводом в консоль
                5. Сохранить все данные (вышеполученные результаты) из БД в Excel и вывести на экран.
                6. Показать меню
                7. Остановить программу.
                """;
    }

    public static void main(String[] args) throws SQLException {
        final Map<String, Map<String, String>> tableSchemas = Map.of(
                "strings", Map.of(
                        "id", "SERIAL PRIMARY KEY",
                        "input", "VARCHAR(150)",
                        "result", "VARCHAR(150)"
                )
        );
        final String dbName = "task_5";
        Main main = new Main(dbName, tableSchemas);

        System.out.println("Практическая работа 5");
        main.showMenu(menuText);

        String var = "";
        int menuPunkt = 0;

        Scanner sc = new Scanner(System.in);

        while (!"7".equals(var)) {
            var = sc.next();

            try {
                menuPunkt = Integer.parseInt(var);
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат ввода!");
                return;
            }

            switch (menuPunkt) {
                case 1 -> main.showTables();
                case 2 -> main.createTables();
                case 3 -> main.reverseString();
                case 4 -> main.insertInto();
                case 5 -> main.saveToExcel();
                case 6 -> main.showMenu(menuText);
                case 7 -> System.out.println("Программа завершила работу");
                default -> System.out.println("Неверная опция");
            }
        }
        main.sqlTools.closeConnection();

    }

    public void reverseString() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите строку: ");
        String input = scanner.nextLine();
        String result = new StringBuffer(input).reverse().toString();
        System.out.println("Получившаяся строка: " + result);

        try {
            sqlTools.insertRowIntoDB("strings", Map.of(
                    "input", input,
                    "result", result
            ));
        } catch (SQLException e) {
            System.out.println("Ошибка при сохранении в БД: " + e.getMessage());
        }
    }

    public void insertInto() {
        Scanner scanner = new Scanner(System.in);

        String firstString;
        do {
            System.out.println("Введите первую строку (минимальная длина - 50 символов): ");
            firstString = scanner.nextLine().trim();
            if (firstString.length() < 50) {
                System.out.println("Ошибка: строка должна содержать не менее 50 символов.");
            }
        } while (firstString.length() < 50);

        String secondString;
        do {
            System.out.println("Введите вторую строку (минимальная длина - 50 символов): ");
            secondString = scanner.nextLine().trim();
            if (secondString.length() < 50) {
                System.out.println("Ошибка: строка должна содержать не менее 50 символов.");
            }
        } while (secondString.length() < 50);

        int position = -1;
        while (true) {
            System.out.println("Введите позицию для вставки (число от 0 до " + secondString.length() + "): ");
            String input = scanner.nextLine();
            try {
                position = Integer.parseInt(input);
                if (position < 0 || position > secondString.length()) {
                    System.out.println("Ошибка: позиция должна быть в пределах от 0 до " + secondString.length() + ".");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное число.");
            }
        }

        StringBuffer buffer = new StringBuffer(secondString);
        buffer.insert(position, firstString);
        String result = buffer.toString();

        if (result.length() > 150) {
            System.out.println("Ошибка: результирующая строка превышает 150 символов и не может быть сохранена в БД.");
            return;
        }

        System.out.println("Результат: " + result);

        try {
            sqlTools.insertRowIntoDB("strings", Map.of(
                    "input", firstString + ", " + secondString + ", pos=" + position,
                    "result", result
            ));
        } catch (SQLException e) {
            System.out.println("Ошибка при сохранении в БД: " + e.getMessage());
        }
    }
}