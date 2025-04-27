package tasks.task_6;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.BaseTask;
import common.SQLTools;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.postgresql.util.PGobject;

import static java.lang.Integer.parseInt;

public class Main extends BaseTask {
    private boolean isEnd = false;
    private Matrix matrix;
    private long lastInsertedId;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Main(String dbName, Map<String, Map<String, String>> tableSchemas) throws SQLException {
        super(new SQLTools(dbName, tableSchemas));
        menuText = """
                1. Вывести все таблицы из БД.
                2. Создать таблицу в БД.
                3. Ввести две матрицы и сохранить их в БД с последующим выводом в консоль
                4. Перемножить матрицу, сохранить перемноженную матрицу в БД и вывести в консоль
                5. Показать меню
                6. Сохранить результаты из БД в Excel и вывести их в консоль
                7. Остановить программу.
                """;
        BaseTask.tableSchemas = tableSchemas;
    }

    public static void main(String[] args) throws SQLException {
        final Map<String, Map<String, String>> tableSchemas = Map.of(
                "matrices", Map.of(
                        "id", "SERIAL",
                        "first_matrix", "JSON",
                        "second_matrix", "JSON",
                        "result_multiply", "JSON"
                )
        );
        final String dbName = "task_6";
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
                case 3 -> main.inputMatrices();
                case 4 -> main.multiplyMatrices();
                case 5 -> main.showMenu(menuText);
                case 6 -> main.saveToExcel();
                case 7 -> System.out.println("Программа завершила работу");
                default -> System.out.println("Неверная опция");
            }
        }
        main.closeConnection();
    }

    private void inputMatrices() throws SQLException {
        if (!hasTables()) {
            System.out.println("Ошибка: нет созданных таблиц. Сначала создайте хотя бы одну (пункт 2).");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        TableAndColumns tableAndCols = promptTableAndColumns(scanner, List.of(
                "Укажите столбец для первой матрицы",
                "Укажите столбец для второй матрицы"
        ));

        matrix = new Matrix();
        matrix.fillMatricesFromKeyboard();
        isEnd = true;

        PGobject matrixAJson = new PGobject();
        PGobject matrixBJson = new PGobject();
        try {
            matrixAJson.setType("json");
            matrixBJson.setType("json");
            matrixAJson.setValue(objectMapper.writeValueAsString(matrix.getMatrixA()));
            matrixBJson.setValue(objectMapper.writeValueAsString(matrix.getMatrixB()));
        } catch (Exception e) {
            System.out.println("Ошибка при преобразовании в JSON: " + e.getMessage());
            return;
        }

        Map<String, Object> logicalMap = Map.of(
                "Укажите столбец для первой матрицы", matrixAJson,
                "Укажите столбец для второй матрицы", matrixBJson
        );

        Map<String, Object> dbMap = tableAndCols.createInsertMap(logicalMap);
        String table = tableAndCols.getTableName();

        insertRowIntoDB(table, dbMap);

        this.lastInsertedId = sqlTools.getLastInsertedId(table, sqlTools.findSerialColumn(table));

        System.out.println("Матрицы успешно сохранены в таблицу: " + tableAndCols.getTableName());
    }

    private void multiplyMatrices() throws SQLException {
        if (!isEnd) {
            System.out.println("Сначала введите матрицы (пункт 3).");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        TableAndColumns tableAndCols = promptTableAndColumns(
                scanner,
                List.of("Укажите столбец для результирующей матрицы")
        );

        matrix.multiplyMatrices();
        matrix.printResultMatrix();

        PGobject resultJson = new PGobject();
        try {
            resultJson.setType("json");
            resultJson.setValue(objectMapper.writeValueAsString(matrix.getResultMatrix()));
        } catch (Exception e) {
            System.out.println("Ошибка при преобразовании результата в JSON: " + e.getMessage());
            return;
        }

        Map<String, Object> logicalMap = Map.of(
                "Укажите столбец для результирующей матрицы", resultJson
        );
        Map<String, Object> dbMap = tableAndCols.createInsertMap(logicalMap);

        updateRowInDB(
                tableAndCols.getTableName(),
                dbMap,
                lastInsertedId
        );

        System.out.println("Результат умножения матриц сохранён в строку с ID = " + lastInsertedId);
        isEnd = false;
    }
}