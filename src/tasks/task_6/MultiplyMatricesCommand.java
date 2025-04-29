package tasks.task_6;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.postgresql.util.PGobject;
import common.SQLTools;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public final class MultiplyMatricesCommand extends MenuCommand {
    private final MatrixContext ctx;
    private final ObjectMapper mapper = new ObjectMapper();

    public MultiplyMatricesCommand(SQLTools tools, MatrixContext ctx) throws SQLException {
        super(tools);
        this.ctx = ctx;
    }

    @Override
    public void execute() throws SQLException {
        if (!ctx.hasMatrices()) {
            System.out.println("Сначала введите матрицы (пункт 3).");
            return;
        }

        Scanner sc = new Scanner(System.in);
        TableAndColumns tc = promptTableAndColumns(sc, List.of(
                "Укажите столбец для результирующей матрицы"
        ));

        ctx.getMatrix().multiplyMatrices();
        ctx.getMatrix().printResultMatrix();

        PGobject resJson = new PGobject();
        try {
            resJson.setType("json");
            resJson.setValue(mapper.writeValueAsString(ctx.getMatrix().getResultMatrix()));
        } catch (Exception e) {
            System.out.println("Ошибка JSON: " + e.getMessage());
            return;
        }

        Map<String, Object> logical = Map.of(
                "Укажите столбец для результирующей матрицы", resJson
        );
        updateRowInDB(tc.getTableName(), tc.createInsertMap(logical), ctx.getLastInsertedId());

        ctx.setMatrix(null);   // сбрасываем — цикл завершён
        System.out.println("Результат сохранён в строку ID = " + ctx.getLastInsertedId());
    }
}
