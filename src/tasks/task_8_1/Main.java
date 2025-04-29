package tasks.task_8_1;

import tasks.task_8_1.commands.*;
import common.BaseTask;
import common.SQLTools;

import java.sql.SQLException;
import java.util.*;

public class Main {

    private static final String MENU_TEXT = """
            1. Вывести все таблицы из БД
            2. Создать таблицу(-ы)
            3. Ввести имя-возраст-зарплату и сохранить
            4. Сохранить данные в Excel
            5. Показать меню
            6. Выход
            """;

    private final Map<Integer, MenuCommand> commands = new HashMap<>();
    private final SQLTools sqlTools;

    public Main(String dbName, Map<String, Map<String, String>> schemas) throws SQLException {
        this.sqlTools = new SQLTools(dbName, schemas);
        BaseTask.tableSchemas = schemas;

        WorkerContext ctx = new WorkerContext();

        commands.put(1, new ShowTablesCommand(sqlTools));
        commands.put(2, new CreateTablesCommand(sqlTools));
        commands.put(3, new InputWorkerCommand(sqlTools, ctx));
        commands.put(4, new SaveToExcelCommand(sqlTools));
        commands.put(5, new ShowMenuCommand(sqlTools, MENU_TEXT));
    }

    public void run() throws SQLException {
        System.out.println("Практическая работа 8.1");
        System.out.println(MENU_TEXT);

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                String raw = sc.next();
                if ("6".equals(raw)) break;

                int key;
                try {
                    key = Integer.parseInt(raw);
                } catch (NumberFormatException e) {
                    System.out.println("Неверный ввод!");
                    continue;
                }

                MenuCommand cmd = commands.get(key);
                if (cmd == null) {
                    System.out.println("Нет такого пункта.");
                    continue;
                }
                cmd.execute();
            }
        }
        sqlTools.closeConnection();
        System.out.println("Программа завершила работу.");
    }

    public static void main(String[] args) throws SQLException {
        Map<String, Map<String, String>> schemas = Map.of(
                "students", Map.of(
                        "id", "SERIAL",
                        "name", "VARCHAR(255)",
                        "age", "INT4",
                        "salary", "FLOAT8"
                )
        );
        new Main("task_8_1", schemas).run();
    }
}
