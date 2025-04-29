package tasks.task_7.commands;

import tasks.task_7.ArrayContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.postgresql.util.PGobject;
import common.SQLTools;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public final class SortArrayCommand extends MenuCommand {
    private final ArrayContext ctx;
    private final ObjectMapper mapper = new ObjectMapper();

    public SortArrayCommand(SQLTools tools, ArrayContext ctx) throws SQLException {
        super(tools);
        this.ctx = ctx;
    }

    @Override
    public void execute() throws SQLException {
        if (!ctx.hasArray()) {
            System.out.println("Сначала введите массив (пункт 3).");
            return;
        }

        Scanner sc = new Scanner(System.in);
        TableAndColumns tc = promptTableAndColumns(
                sc,
                List.of(
                        "Укажите столбец для массива, отсортированного по возрастанию",
                        "Укажите столбец для массива, отсортированного по убыванию"
                )
        );

        ctx.getSort().bubbleSortAscending();
        PGobject ascJson = makeJson(ctx.getSort().array);

        ctx.getSort().bubbleSortDescending();
        PGobject descJson = makeJson(ctx.getSort().array);

        updateRowInDB(
                tc.getTableName(),
                tc.createInsertMap(Map.of(
                        "Укажите столбец для массива, отсортированного по возрастанию", ascJson,
                        "Укажите столбец для массива, отсортированного по убыванию", descJson
                )),
                ctx.getLastInsertedId()
        );

        ctx.setSort(null);
        System.out.println("Отсортированные массивы сохранены в строку ID = " + ctx.getLastInsertedId());
    }

    private PGobject makeJson(double[] arr) {
        PGobject obj = new PGobject();
        try {
            obj.setType("json");
            obj.setValue(mapper.writeValueAsString(arr));
        } catch (Exception ignore) {
        }
        return obj;
    }
}
