package tasks.task_3;

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
                3. Проверить числа на целостность и чётность
                4. Сохранить все данные (вышеполученные результаты) из БД в Excel и вывести на экран.
                5. Показать меню
                6. Остановить программу.
                """;
    }

    public static void main(String[] args) throws SQLException {
        final Map<String, Map<String, String>> tableSchemas = Map.of(
                "number_checks", Map.of(
                        "id", "SERIAL PRIMARY KEY",
                        "number", "VARCHAR(50)",
                        "is_integer", "BOOLEAN",
                        "is_even", "BOOLEAN"
                )
        );
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

        // Проверяем наличие таблиц в БД
        if (!sqlTools.hasTables()) {
            System.out.println("Ошибка: В базе данных нет ни одной таблицы. Сначала создайте таблицы (пункт 2 меню).");
            return;
        }

        // Получаем ввод с проверкой
        String tableName = getNonEmptyInput(scanner, "Введите название таблицы для сохранения результатов:");

        if (!sqlTools.isTableExists(tableName)) {
            System.out.println("Ошибка: Таблица '" + tableName + "' не существует. Сначала создайте её (пункт 2 меню).");
            return;
        }

        String numberColumn = getNonEmptyInput(scanner, "Введите название столбца для числа (тип VARCHAR(50)):");
        String isIntegerColumn = getNonEmptyInput(scanner, "Введите название столбца для признака целого числа (тип BOOLEAN):");
        String isEvenColumn = getNonEmptyInput(scanner, "Введите название столбца для признака четности (тип BOOLEAN):");
        String numbersInput = getNonEmptyInput(scanner, "Введите числа через пробел:");

        // Обработка чисел
        String[] numbers = numbersInput.split("\\s+");
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
                    tableName,
                    Map.of(
                            numberColumn, number,
                            isIntegerColumn, isInteger,
                            isEvenColumn, isEven
                    )
            );
            System.out.printf("Число: %s, Целое: %s, Чётное: %s%n", number, isInteger, isEven);
        }
    }
    /**
     * Вспомогательный метод для получения непустого ввода от пользователя
     * @param scanner Объект Scanner для ввода
     * @param promptMessage Сообщение с подсказкой для пользователя
     * @return Непустая строка, введенная пользователем
     */
    private String getNonEmptyInput(Scanner scanner, String promptMessage) {
        String input;
        while (true) {
            System.out.println(promptMessage);
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Ошибка: ввод не может быть пустым. Попробуйте снова.");
        }
    }
}

