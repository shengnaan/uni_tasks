// tasks/task_7/Main.java
package tasks.task_7;

import tasks.task_7.commands.*;
import common.BaseTask;
import common.SQLTools;

import java.sql.SQLException;
import java.util.*;

public class Main {

    private static final String MENU_TEXT = """
            1. Вывести все таблицы из БД
            2. Создать таблицу в БД
            3. Ввести массив и сохранить его в БД
            4. Отсортировать массив (ASC & DESC) и сохранить
            5. Сохранить результаты в Excel
            6. Показать меню
            7. Выход
            """;

    private final Map<Integer, MenuCommand> commands = new HashMap<>();
    private final SQLTools sqlTools;

    public Main(String dbName, Map<String, Map<String, String>> schemas) throws SQLException {
        this.sqlTools = new SQLTools(dbName, schemas);

        BaseTask.tableSchemas = schemas;
        BaseTask.menuText = MENU_TEXT;

        ArrayContext ctx = new ArrayContext();

        commands.put(1, new ShowTablesCommand(sqlTools));
        commands.put(2, new CreateTablesCommand(sqlTools));
        commands.put(3, new InputArrayCommand(sqlTools, ctx));
        commands.put(4, new SortArrayCommand(sqlTools, ctx));
        commands.put(5, new SaveToExcelCommand(sqlTools));
        commands.put(6, new ShowMenuCommand(sqlTools, MENU_TEXT));
    }

    public void run() throws SQLException {
        System.out.println("Практическая работа 7");
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
                    System.out.println("Пункта меню не существует.");
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
                "arrays", Map.of(
                        "id", "SERIAL",
                        "initial_array", "JSON",
                        "asc_sorted_array", "JSON",
                        "desc_sorted_array", "JSON"
                )
        );
        new Main("task_7", schemas).run();
    }
}
