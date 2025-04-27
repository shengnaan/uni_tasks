package tasks.task_7;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.BaseTask;
import common.SQLTools;
import org.postgresql.util.PGobject;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class Main extends BaseTask {
    private boolean isEnd = false;
    private Sort sort;
    private long lastInsertedId;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Main(String dbName, Map<String, Map<String, String>> tableSchemas) throws SQLException {
        super(new SQLTools(dbName, tableSchemas));
        menuText = """
                1. Вывести все таблицы из БД.
                2. Создать таблицу в БД.
                3. Ввести одномерный массив и сохранить его в БД с последующим выводом в консоль
                4. Отсортировать массив, сохранить результаты по возрастанию и убыванию в БД и вывести в консоль
                5. Сохранить результаты из БД в Excel и вывести их в консоль
                6. Показать меню
                7. Остановить программу.
                """;
        BaseTask.tableSchemas = tableSchemas;
    }

    public static void main(String[] args) throws SQLException {
        final Map<String, Map<String, String>> tableSchemas = Map.of(
                "arrays", Map.of(
                        "id", "SERIAL",
                        "initial_array", "JSON",
                        "asc_sorted_array", "JSON",
                        "desc_sorted_array", "JSON"
                )
        );
        final String dbName = "task_7";
        Main main = new Main(dbName, tableSchemas);

        System.out.println("Практическая работа 6");
        main.showMenu(menuText);

        String var = "";
        int menuPunkt = 0;
        Scanner sc = new Scanner(System.in);

        while (!"7".equals(var)) {
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
                case 3 -> main.inputArray();
                case 4 -> main.sortArray();
                case 5 -> main.saveToExcel();
                case 6 -> main.showMenu(menuText);
                case 7 -> System.out.println("Программа завершила работу");
                default -> System.out.println("Неверная опция");
            }
        }
        main.closeConnection();
    }

    private void inputArray() throws SQLException {
        if (!hasTables()) {
            System.out.println("Ошибка: нет созданных таблиц. Сначала создайте хотя бы одну (пункт 2).");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        TableAndColumns tableAndCols = promptTableAndColumns(scanner, List.of(
                "Укажите столбец для исходного массива"
        ));

        sort = new Sort();
        sort.fillArrayFromKeyboard();
        isEnd = true;

        PGobject arrayJson = new PGobject();
        try {
            arrayJson.setType("json");
            arrayJson.setValue(objectMapper.writeValueAsString(sort.array));
        } catch (Exception e) {
            System.out.println("Ошибка при преобразовании в JSON: " + e.getMessage());
            return;
        }

        Map<String, Object> logicalMap = Map.of(
                "Укажите столбец для исходного массива", arrayJson
        );

        Map<String, Object> dbMap = tableAndCols.createInsertMap(logicalMap);
        String table = tableAndCols.getTableName();

        insertRowIntoDB(table, dbMap);

        this.lastInsertedId = sqlTools.getLastInsertedId(table, sqlTools.findSerialColumn(table));
        System.out.println("Массив успешно сохранен в таблицу: " + tableAndCols.getTableName());
    }

    private void sortArray() throws SQLException {
        if (!isEnd) {
            System.out.println("Сначала введите массив (пункт 3).");
            return;
        }

        // Спросим у пользователя столбцы для возрастающей и убывающей сортировок
        Scanner scanner = new Scanner(System.in);
        TableAndColumns tableAndCols = promptTableAndColumns(
                scanner,
                List.of(
                        "Укажите столбец для массива, отсортированного по возрастанию",
                        "Укажите столбец для массива, отсортированного по убыванию"
                )
        );

        // Сортируем по возрастанию
        sort.bubbleSortAscending();
        PGobject ascJson = new PGobject();
        try {
            ascJson.setType("json");
            ascJson.setValue(objectMapper.writeValueAsString(sort.array));
        } catch (Exception e) {
            System.out.println("Ошибка при преобразовании результата (возрастание) в JSON: " + e.getMessage());
            return;
        }

        // Сортируем по убыванию
        sort.bubbleSortDescending();
        PGobject descJson = new PGobject();
        try {
            descJson.setType("json");
            descJson.setValue(objectMapper.writeValueAsString(sort.array));
        } catch (Exception e) {
            System.out.println("Ошибка при преобразовании результата (убывание) в JSON: " + e.getMessage());
            return;
        }

        // Сохраняем обе сортировки сразу
        Map<String, Object> logicalMap = Map.of(
                "Укажите столбец для массива, отсортированного по возрастанию", ascJson,
                "Укажите столбец для массива, отсортированного по убыванию", descJson
        );
        Map<String, Object> dbMap = tableAndCols.createInsertMap(logicalMap);

        updateRowInDB(
                tableAndCols.getTableName(),
                dbMap,
                lastInsertedId
        );

        System.out.println("Отсортированные массивы (по возрастанию и убыванию) сохранены в строку с ID = " + lastInsertedId);
        isEnd = false;
    }
}