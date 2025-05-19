package tasks.task_9;

import common.BaseTask;
import common.SQLTools;
import tasks.task_9.commands.*;

import java.sql.SQLException;
import java.util.*;

public class Main {

    private static final String MENU_TEXT = """
            1. Вывести все таблицы из БД
            2. Создать таблицу в БД
            3. Ввести две матрицы и сохранить их в БД
            4. Перемножить, сложить, вычесть,возвести в степень матрицы и сохранить результат
            5. Показать меню
            6. Сохранить результаты в Excel
            7. Выход
            """;

    private final Map<Integer, MenuCommand> commands = new HashMap<>();
    private final SQLTools sqlTools;

    public Main(String dbName, Map<String, Map<String, String>> schemas) throws SQLException {
        this.sqlTools = new SQLTools(dbName, schemas);
        BaseTask.tableSchemas = schemas;
        BaseTask.menuText = MENU_TEXT;
        MatrixContext9 ctx = new MatrixContext9();

        commands.put(1, new ShowTablesCommand(sqlTools));
        commands.put(2, new CreateTablesCommand(sqlTools));
        commands.put(3, new InputMatricesCommand(sqlTools, ctx));
        commands.put(4, new MatricesOperations(sqlTools, ctx));
        commands.put(5, new ShowMenuCommand(sqlTools, MENU_TEXT));
        commands.put(6, new SaveToExcelCommand(sqlTools));
    }

    public void run() throws SQLException {
        System.out.println("Практическая работа 9");
        System.out.println(MENU_TEXT);

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                String raw = sc.next();
                if ("7".equals(raw)) break;

                int key;
                try {
                    key = Integer.parseInt(raw);
                } catch (NumberFormatException e) {
                    System.out.println("Неверный ввод!");
                    continue;
                }

                MenuCommand cmd = commands.get(key);
                if (cmd == null) {
                    System.out.println("Неправильный пункт меню.");
                    continue;
                }
                cmd.execute();
            }
        }
        sqlTools.closeConnection();
        System.out.println("Программа завершила работу.");
    }

    public static void main(String[] args) throws SQLException{
        Map<String, Map<String, String>> schemas = Map.of(
                "matrices", Map.of(
                        "id", "SERIAL",
                        "first_matrix", "JSON",
                        "second_matrix", "JSON",
                        "result_multiply", "JSON",
                        "result_sum", "JSON",
                        "result_subtract", "JSON",
                        "result_degree", "JSON"
                )
        );
        new tasks.task_9.Main("task_9", schemas).run();
    }
}
