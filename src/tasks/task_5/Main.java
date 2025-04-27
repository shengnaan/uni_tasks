package tasks.task_5;

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
                2. Создать таблицу(-ы) в БД.
                3. Изменить порядок символов строки на обратный
                4. Добавить одну строку в середину другой
                5. Сохранить все данные (вышеполученные результаты) из БД в Excel и вывести на экран.
                6. Показать меню
                7. Остановить программу.
                """;
        BaseTask.tableSchemas = tableSchemas;
    }

    public static void main(String[] args) throws SQLException {
        final Map<String, Map<String, String>> tableSchemas = Map.of(
                "strings", Map.of(
                        "input", "VARCHAR(510)",
                        "result", "VARCHAR(510)"
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
                continue;
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
        main.closeConnection();
    }

    public void reverseString() throws SQLException {
        if (!sqlTools.hasTables()) {
            System.out.println("Ошибка: нет созданных таблиц. Сначала создайте хотя бы одну (пункт 2).");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        TableAndColumns tableAndCols = promptTableAndColumns(
                scanner,
                List.of(
                        "Укажите столбец для исходной строки (тип VARCHAR(510)):",
                        "Укажите столбец для результата (тип VARCHAR(510)):"
                )
        );
        if (tableAndCols == null) {
            return;
        }

        System.out.println("Введите строку: ");
        String input = scanner.nextLine();
        if (input.length() > 255) {
            System.out.println("Длинна строки не может быть больше 255 символов.");
            return;
        }

        String result = new StringBuilder(input).reverse().toString();
        System.out.println("Перевёрнутая строка: " + result);

        Map<String, Object> dataLogical = Map.of(
                "Укажите столбец для исходной строки (тип VARCHAR(510)):", input,
                "Укажите столбец для результата (тип VARCHAR(510)):", result
        );
        Map<String, Object> dataReal = tableAndCols.createInsertMap(dataLogical);

        insertRowIntoDB(
                tableAndCols.getTableName(),
                dataReal
        );
    }

    public void insertInto() throws SQLException {
        if (!sqlTools.hasTables()) {
            System.out.println("Ошибка: нет созданных таблиц. Сначала создайте хотя бы одну (пункт 2).");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        TableAndColumns tableAndCols = promptTableAndColumns(
                scanner,
                List.of(
                        "Укажите столбец для исходной(-ых) строки (тип VARCHAR(510)):",
                        "Укажите столбец для результата (тип VARCHAR(510)):"
                )
        );
        if (tableAndCols == null) {
            return;
        }

        String firstString;
        do {
            System.out.println("Введите первую строку (минимальная длина - 50 символов): ");
            firstString = scanner.nextLine().trim();
            if (firstString.length() < 50 || firstString.length() > 255) {
                System.out.println("Ошибка: длинна строки должна быть в диапазоне от 50 до 255 символов.");
            }
        } while (firstString.length() < 50 || firstString.length() > 255);

        String secondString;
        do {
            System.out.println("Введите вторую строку (минимальная длина - 50 символов): ");
            secondString = scanner.nextLine().trim();
            if (secondString.length() < 50 || secondString.length() > 255) {
                System.out.println("Ошибка: длинна строки должна быть в диапазоне от 50 до 255 символов.");
            }
        } while (secondString.length() < 50 || secondString.length() > 255);

        int position = -1;
        while (true) {
            System.out.println("Введите позицию для вставки (число от 0 до " + secondString.length() + "): ");
            String posInput = scanner.nextLine();
            try {
                position = Integer.parseInt(posInput);
                if (position < 0 || position > secondString.length()) {
                    System.out.println("Ошибка: позиция должна быть в пределах от 0 до " + secondString.length() + ".");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное число.");
            }
        }

        StringBuilder buffer = new StringBuilder(secondString);
        buffer.insert(position, firstString);
        String result = buffer.toString();

        System.out.println("Результат: " + result);
        Map<String, Object> dataLogical = Map.of(
                "Укажите столбец для исходной(-ых) строки (тип VARCHAR(510)):", firstString + " + " + secondString,
                "Укажите столбец для результата (тип VARCHAR(510)):", result
        );
        Map<String, Object> dataReal = tableAndCols.createInsertMap(dataLogical);

        insertRowIntoDB(
                tableAndCols.getTableName(),
                dataReal
        );
    }
}