package tasks.task_2;

import common.BaseTask;
import common.SQLTools;

import java.sql.*;
import java.util.Map;
import java.util.Scanner;

public class Main extends BaseTask{

    public Main(String dbName, Map<String, Map<String, String>> tableSchemas) throws SQLException {
        super(new SQLTools(dbName, tableSchemas));
        menuText = """
                1. Вывести все таблицы из MySQL.
                2. Создать таблицу в MySQL.
                3. Ввести две строки с клавиатуры, результат сохранить в MySQL с последующим выводом в
                консоль.
                4. Подсчитать размер ранее введенных строк, результат сохранить в MySQL с последующим
                выводом в консоль.
                5. Объединить две строки в единое целое, результат сохранить в MySQL с последующим выводом
                в консоль.
                6. Сравнить две ранее введенные строки, результат сохранить в MySQL с последующим выводом
                в консоль.
                7. Сохранить все данные (вышеполученные результаты) из MySQL в Excel и вывести на экран.
                8. Показать меню.
                9. Остановить программу.
                """;
    }

    public static void main(String[] args) throws SQLException {
        final Map<String, Map<String, String>> tableSchemas = Map.of(
                "strings", Map.of(
                        "id", "SERIAL PRIMARY KEY",
                        "first_string", "TEXT",
                        "second_string", "TEXT",
                        "length_1", "INT",
                        "length_2", "INTEGER",
                        "is_equal", "VARCHAR(3)",  // YES или NO
                        "result", "TEXT"
                )
        );

        final String dbName = "task_2";
        Main main = new Main(dbName, tableSchemas);
        System.out.println("Практическая работа 2");
        main.showMenu(menuText);

        String var = "";
        int menuPunkt = 0;

        Scanner sc = new Scanner(System.in);

        while (!"9".equals(var)) {
            var = sc.next();

            try {
                menuPunkt = Integer.parseInt(var);
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат ввода!");
            }

            switch (menuPunkt) {
                case 1 -> main.showTables();
                case 2 -> main.createTables();
                case 3 -> main.input();
                case 4 -> main.countLength();
                case 5 -> main.concatStrings();
                case 6 -> main.compareStrings();
                case 7 -> main.saveToExcel();
                case 8 -> main.showMenu(menuText);
                case 9 -> System.out.println("Программа завершила работу");
                default -> System.out.println("Неверная опция");
            }
        }
        main.sqlTools.closeConnection();
    }

    public void input() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите первую строку: ");
        String string1 = scanner.nextLine();
        System.out.println("Введите вторую строку: ");
        String string2 = scanner.nextLine();

        try {
            sqlTools.insertRowIntoDB("strings", Map.of(
                    "first_string", string1,
                    "second_string", string2
            ));
        } catch (SQLException e) {
            System.out.println("Ошибка при сохранении в БД: " + e.getMessage());
        }
    }

    public void countLength() throws SQLException {
        String query = "SELECT first_string, second_string FROM strings ORDER BY id DESC LIMIT 1";
        Map<String, String> strings = sqlTools.getFromTable(query);

        String str1 = strings.get("first_string");
        String str2 = strings.get("second_string");

        int len1 = str1.length();
        int len2 = str2.length();

        sqlTools.insertRowIntoDB("strings", Map.of(
                "length_1", len1,
                "length_2", len2
        ));

        System.out.printf("Длина строк: %d и %d%n", len1, len2);
    }

    public void concatStrings() throws SQLException {
        String query = "SELECT first_string, second_string FROM strings ORDER BY id DESC LIMIT 1";
        Map<String, String> strings = sqlTools.getFromTable(query);

        String str1 = strings.get("first_string");
        String str2 = strings.get("second_string");
        String str3 = str1.concat(str2);

        try {
            sqlTools.insertRowIntoDB("strings", Map.of(
                    "result", str3
            ));
            System.out.println("Результат объединения строк: " + str3);
        } catch (SQLException e) {
            System.out.println("Ошибка при сохранении в БД: " + e.getMessage());
        }
    }

    public void compareStrings() throws SQLException {
        String query = "SELECT first_string, second_string FROM strings ORDER BY id DESC LIMIT 1";
        Map<String, String> strings = sqlTools.getFromTable(query);

        String str1 = strings.get("first_string");
        String str2 = strings.get("second_string");
        String isEqual = "";

        if (str1.equals(str2)){
            System.out.println("Строки равны.");
            isEqual = "YES";
        } else {
            System.out.println("Строки не равны.");
            isEqual = "NO";
        }

        sqlTools.insertRowIntoDB("strings",
                Map.of("is_equal", isEqual));
    }

}
