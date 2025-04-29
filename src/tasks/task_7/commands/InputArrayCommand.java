package tasks.task_7.commands;

import tasks.task_7.ArrayContext;
import tasks.task_7.Sort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.postgresql.util.PGobject;
import common.SQLTools;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public final class InputArrayCommand extends MenuCommand {
    private final ArrayContext ctx;
    private final ObjectMapper mapper = new ObjectMapper();

    public InputArrayCommand(SQLTools tools, ArrayContext ctx) throws SQLException {
        super(tools);
        this.ctx = ctx;
    }

    @Override
    public void execute() throws SQLException {
        if (!hasTables()) {
            System.out.println("Ошибка: нет созданных таблиц (сначала пункт 2).");
            return;
        }

        Scanner sc = new Scanner(System.in);
        TableAndColumns tc = promptTableAndColumns(
                sc,
                List.of("Укажите столбец для исходного массива")
        );

        Sort sorter = new Sort();
        sorter.fillArrayFromKeyboard();
        ctx.setSort(sorter);

        PGobject initJson = new PGobject();
        try {
            initJson.setType("json");
            initJson.setValue(mapper.writeValueAsString(sorter.array));
        } catch (Exception e) {
            System.out.println("Ошибка JSON: " + e.getMessage());
            return;
        }

        insertRowIntoDB(
                tc.getTableName(),
                tc.createInsertMap(Map.of("Укажите столбец для исходного массива", initJson))
        );

        long id = sqlTools.getLastInsertedId(tc.getTableName(), getSerialColumn(tc.getTableName()));
        ctx.setLastInsertedId(id);

        System.out.println("Массив сохранён, ID = " + id);
    }
}
