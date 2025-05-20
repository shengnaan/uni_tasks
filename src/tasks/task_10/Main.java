package tasks.task_10;

import common.BaseTask;
import common.SQLTools;
import java.sql.*;
import java.util.*;

public class Main extends BaseTask {

    public Main(String dbName, Map<String, Map<String, String>> tableSchemas) throws SQLException {
        super(new SQLTools(dbName, tableSchemas));
        menuText = """
                1. Вывести все таблицы из БД.
                2. Создать таблицу в БД.
                3. Ввести данные о всех студентах и сохранить их с выводом в консоль.
                4. Вывести данные о студенте по ID.
                5. Удалить данные о студенте по ID.
                6. Сохранить итоговые результаты в Excel и вывести их в консоль.
                7. Показать меню.
                8. Остановить программу.
                """;
        BaseTask.tableSchemas = tableSchemas;
    }

    public static void main(String[] args) throws SQLException {
        final Map<String, Map<String, String>> tableSchemas = Map.of(
                "students", Map.of(
                        "id", "SERIAL PRIMARY KEY",
                        "full_name", "VARCHAR(255) NOT NULL",
                        "direction", "VARCHAR(255) NOT NULL",
                        "group_name", "VARCHAR(50) NOT NULL"
                )
        );
        final String dbName = "task_10";
        Main main = new Main(dbName, tableSchemas);

        System.out.println("Система управления студентами");
        main.showMenu(menuText);

        Scanner sc = new Scanner(System.in);
        String input = "";
        int menuItem = 0;

        while (!"8".equals(input)) {
            input = sc.next();

            try {
                menuItem = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат ввода!");
                continue;
            }

            switch (menuItem) {
                case 1 -> main.showTables();
                case 2 -> main.createTables();
                case 3 -> main.inputStudentsData();
                case 4 -> main.showStudentById();
                case 5 -> main.deleteStudentById();
                case 6 -> main.saveToExcel();
                case 7 -> main.showMenu(menuText);
                case 8 -> System.out.println("Программа завершила работу");
                default -> System.out.println("Неверная опция");
            }
        }
        main.closeConnection();
    }

    public void inputStudentsData() throws SQLException {

        if (!sqlTools.hasTables()) {
            System.out.println("Ошибка: в базе данных нет ни одной таблицы. Сначала создайте таблицы (пункт 2 меню).");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите количество студентов (минимум 5):");
        int count;
        while (true) {
            try {
                count = Integer.parseInt(scanner.nextLine());
                if (count < 5) {
                    System.out.println("Минимальное количество студентов - 5. Повторите ввод:");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат ввода. Введите число:");
            }
        }

        System.out.println("Введите данные студентов:");
        for (int i = 0; i < count; i++) {
            System.out.println("\nСтудент #" + (i + 1));
            System.out.print("ФИО: ");
            String fullName = scanner.nextLine();

            System.out.print("Направление подготовки: ");
            String direction = scanner.nextLine();

            System.out.print("Группа: ");
            String group = scanner.nextLine();


            sqlTools.executeUpdate("INSERT INTO students (full_name, direction, group_name) VALUES (?, ?, ?)",
                    Arrays.asList(fullName, direction, group));

            System.out.println("Студент добавлен!");
        }

        // Вывод всех студентов в табличном виде
        System.out.println("\nСписок всех студентов:");
        printStudentsTable();
    }

    private void printStudentsTable() throws SQLException {
        ResultSet rs = sqlTools.executeQuery("SELECT * FROM students ORDER BY id");

        // Вывод заголовков
        System.out.println("+" + "-".repeat(10) + "+" + "-".repeat(30) + "+" + "-".repeat(30) + "+" + "-".repeat(15) + "+");
        System.out.printf("| %-8s | %-28s | %-28s | %-13s |\n",
                "ID", "ФИО", "Направление", "Группа");
        System.out.println("+" + "-".repeat(10) + "+" + "-".repeat(30) + "+" + "-".repeat(30) + "+" + "-".repeat(15) + "+");

        // Вывод данных
        while (rs.next()) {
            System.out.printf("| %-8d | %-28s | %-28s | %-13s |\n",
                    rs.getInt("id"),
                    rs.getString("full_name"),
                    rs.getString("direction"),
                    rs.getString("group_name"));
        }
        System.out.println("+" + "-".repeat(10) + "+" + "-".repeat(30) + "+" + "-".repeat(30) + "+" + "-".repeat(15) + "+");
    }

    public void showStudentById() throws SQLException {
        if (!sqlTools.hasTables()) {
            System.out.println("Ошибка: в базе данных нет ни одной таблицы. Сначала создайте таблицы (пункт 2 меню).");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите ID студента:");
        int id;
        while (true) {
            try {
                id = Integer.parseInt(scanner.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат ввода. Введите число:");
            }
        }

        ResultSet rs = sqlTools.executeQuery("SELECT * FROM students WHERE id = ?", Collections.singletonList(id));
        if (!rs.next()) {
            System.out.println("Студент с ID " + id + " не найден.");
            return;
        }

        System.out.println("\nРезультат поиска:");
        System.out.println("+" + "-".repeat(10) + "+" + "-".repeat(30) + "+" + "-".repeat(30) + "+" + "-".repeat(15) + "+");
        System.out.printf("| %-8s | %-28s | %-28s | %-13s |\n",
                "ID", "ФИО", "Направление", "Группа");
        System.out.println("+" + "-".repeat(10) + "+" + "-".repeat(30) + "+" + "-".repeat(30) + "+" + "-".repeat(15) + "+");

        System.out.printf("| %-8d | %-28s | %-28s | %-13s |\n",
                rs.getInt("id"),
                rs.getString("full_name"),
                rs.getString("direction"),
                rs.getString("group_name"));

        System.out.println("+" + "-".repeat(10) + "+" + "-".repeat(30) + "+" + "-".repeat(30) + "+" + "-".repeat(15) + "+");
    }

    public void deleteStudentById() throws SQLException {
        if (!sqlTools.hasTables()) {
            System.out.println("Ошибка: в базе данных нет ни одной таблицы. Сначала создайте таблицы (пункт 2 меню).");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите ID студента для удаления:");
        int id;
        while (true) {
            try {
                id = Integer.parseInt(scanner.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат ввода. Введите число:");
            }
        }

        int affectedRows = sqlTools.executeUpdate("DELETE FROM students WHERE id = ?", Collections.singletonList(id));
        if (affectedRows > 0) {
            System.out.println("Студент с ID " + id + " успешно удален.");
        } else {
            System.out.println("Студент с ID " + id + " не найден.");
        }
    }
}