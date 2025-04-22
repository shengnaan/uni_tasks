package tasks.task_3;

import common.BaseTask;
import common.SQLTools;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main extends BaseTask {

    public Main(String dbName, Map<String, Map<String, String>> tableSchemas) throws SQLException {
        super(new SQLTools(dbName, tableSchemas));
        menuText = """
                1. Вывести все таблицы из БД.
                2. Создать таблицу(-ы) в БД.
                3. Проверить числа на целостность и чётность
                4. Сохранить все данные (вышеполученные результаты) из БД в Excel и вывести на экран.
                5. Показать меню
                6. Остановить программу.
                """;
    }

    public static void main(String[] args) throws SQLException {
        final Map<String, Map<String, String>> TEMPLATE_SCHEMA = Map.of(
                "number_checks", Map.of(
                        "id", "SERIAL PRIMARY KEY",
                        "number", "VARCHAR(50)",
                        "is_integer", "BOOLEAN",
                        "is_even", "BOOLEAN"
                )
        );

        Map<String, Map<String, String>> tableSchemas = new HashMap<>();
        Scanner schemaScanner = new Scanner(System.in);

        System.out.println("Введите название таблицы:");
        String tableName = schemaScanner.nextLine();

        Map<String, String> columns = new HashMap<>();
        System.out.println("Введите столбцы и их типы (для завершения введите 'end'):");

        while (true) {
            System.out.print("Название столбца: ");
            String columnName = schemaScanner.nextLine();
            if (columnName.equals("end")) break;

            System.out.print("Тип данных: ");
            String columnType = schemaScanner.nextLine();
            columns.put(columnName, columnType);
        }

        tableSchemas.put(tableName, columns);

        if (!tableSchemas.equals(TEMPLATE_SCHEMA)) {
            System.out.println("Некорректная структура таблицы");
            tableSchemas = TEMPLATE_SCHEMA;
        }

        final String dbName = "task_3";
        tasks.task_3.Main main = new tasks.task_3.Main(dbName, tableSchemas);

        System.out.println("Практическая работа 3");
        main.showMenu(menuText);

        String var = "";
        int menuPunkt = 0;

        Scanner sc = new Scanner(System.in);
        while (!"6".equals(var)) {
            var = sc.next();

            try {
                menuPunkt = Integer.parseInt(var);
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат ввода!");
            }

            switch (menuPunkt) {
                case 1 -> main.showTables();
                case 2 -> main.createTables();
                case 3 -> main.checkNumbers();
                case 4 -> main.saveToExcel();
                case 5 -> main.showMenu(menuText);
                case 6 -> System.out.println("Программа завершила работу");
                default -> System.out.println("Неверная опция");
            }
        }
        main.sqlTools.closeConnection();
    }

    public void checkNumbers() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите числа через пробел:");
        String input = scanner.nextLine();
        String[] numbers = input.split("\\s+");

        for (String number : numbers) {
            boolean isInteger;
            boolean isEven = false;

            try {
                double value = Double.parseDouble(number);
                isInteger = value % 1 == 0;
                if (isInteger) {
                    isEven = ((int) value) % 2 == 0;
                }
            } catch (NumberFormatException e) {
                System.out.printf("Ошибка: '%s' не является числом.%n", number);
                continue;
            }

            this.insertRowIntoDB(
                    "number_checks",
                    Map.of(
                            "number", number,
                            "is_integer", isInteger,
                            "is_even", isEven
                    )
            );
            System.out.printf("Число: %s, Целое: %s, Чётное: %s%n", number, isInteger, isEven);
        }
    }
}
