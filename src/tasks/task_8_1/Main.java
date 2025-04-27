package tasks.task_8_1;

import common.BaseTask;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class Main extends Worker {

    public Main(String dbName, Map<String, Map<String, String>> tableSchemas) throws SQLException {
        super(dbName, tableSchemas);
        menuText = """
                1. Вывести все таблицы из БД.
                2. Создать таблицу(-ы) в БД.
                3. Ввести имя, возраст и зарплату и сохранить в БД с последующим выводом в консоль.
                4. Сохранить все данные (вышеполученные результаты) из БД в Excel и вывести на экран.
                5. Показать меню
                6. Остановить программу.
                """;
        BaseTask.tableSchemas = tableSchemas;
    }

    public static void main(String[] args) throws SQLException {
        final Map<String, Map<String, String>> tableSchemas = Map.of(
                "students", Map.of(
                        "name", "VARCHAR(255)",
                        "age", "INT4",
                        "salary", "FLOAT8"
                )
        );
        final String dbName = "task_8_1";
        Main main = new Main(dbName, tableSchemas);

        System.out.println("Практическая работа 8.1");
        main.showMenu(menuText);

        String var = "";
        int menuPunkt = 0;
        Scanner sc = new Scanner(System.in);

        while (!"6".equals(var)) {
            var = sc.next();

            try {
                menuPunkt = parseInt(var);
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат ввода!");
                continue;
            }

            switch (menuPunkt) {
                case 1 -> main.showTables();
                case 2 -> main.createTables();
                case 3 -> main.inputData(dbName, tableSchemas);
                case 4 -> main.saveToExcel();
                case 5 -> main.showMenu(menuText);
                case 6 -> System.out.println("Программа завершила работу");
                default -> System.out.println("Неверная опция");
            }
        }
        main.closeConnection();
    }

    private void inputData(String dbName, Map<String, Map<String, String>> tableSchemas) throws SQLException {
        if (!hasTables()) {
            System.out.println("Ошибка: нет созданных таблиц. Сначала создайте хотя бы одну (пункт 2).");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        TableAndColumns tableAndCols = promptTableAndColumns(
                scanner,
                List.of(
                        "Укажите столбец для имени (тип VARCHAR(255)):",
                        "Укажите столбец для возраста (тип INT4):",
                        "Укажите столбец для зарплаты (тип FLOAT8):"
                )
        );

        if (tableAndCols == null) {
            return;
        }

        Worker worker = new Worker(dbName, tableSchemas);
        String name;
        do {
            System.out.println("Введите имя:");
            name = scanner.nextLine().trim();
            if (name.length() > 255) {
                System.out.println("Ошибка: длинна имени должна быть до 255 символов.");
            }
        } while (name.length() > 255);
        worker.setName(name);

        int age = -1;
        do {
            System.out.println("Введите возраст:");
            try {
                age = Integer.parseInt(scanner.nextLine());
                if (age < 0 || age > 120) {
                    System.out.println("Ошибка: возраст должен быть целым числом от 0 до 120.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число.");
                age = -1;
            }
        } while (age < 0 || age > 120);
        worker.setAge(age);

        double salary = -1;
        do {
            System.out.println("Введите зарплату:");
            try {
                salary = Integer.parseInt(scanner.nextLine());
                if (salary < 0) {
                    System.out.println("Ошибка: зарплата должна быть числом от 0 до 10000000000.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число.");
                salary = -1;
            }
        } while (salary < 0);
        worker.setSalary(salary);

        Map<String, Object> dataLogical = Map.of(
                "Укажите столбец для имени (тип VARCHAR(255)):", worker.getName(),
                "Укажите столбец для возраста (тип INT4):", worker.getAge(),
                "Укажите столбец для зарплаты (тип FLOAT8):", worker.getSalary()
        );
        Map<String, Object> dataReal = tableAndCols.createInsertMap(dataLogical);

        insertRowIntoDB(
                tableAndCols.getTableName(),
                dataReal
        );
        System.out.println("Студент: " + worker.getName());
        System.out.println("Возраст: " + worker.getAge());
        System.out.println("ЗП: " + worker.getSalary());
    }
}
