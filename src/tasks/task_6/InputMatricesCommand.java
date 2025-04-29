package tasks.task_6;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.postgresql.util.PGobject;
import common.SQLTools;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public final class InputMatricesCommand extends MenuCommand {
    private final MatrixContext ctx;
    private final ObjectMapper mapper = new ObjectMapper();

    public InputMatricesCommand(SQLTools tools, MatrixContext ctx) throws SQLException {
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
        TableAndColumns tc = promptTableAndColumns(sc, List.of(
                "Укажите столбец для первой матрицы",
                "Укажите столбец для второй матрицы"
        ));

        Matrix m = new Matrix();
        m.fillMatricesFromKeyboard();
        ctx.setMatrix(m);                                     // сохраняем в контексте

        PGobject aJson = new PGobject();
        PGobject bJson = new PGobject();
        try {
            aJson.setType("json");
            bJson.setType("json");
            aJson.setValue(mapper.writeValueAsString(m.getMatrixA()));
            bJson.setValue(mapper.writeValueAsString(m.getMatrixB()));
        } catch (Exception e) {
            System.out.println("Ошибка JSON: " + e.getMessage());
            return;
        }

        Map<String, Object> logical = Map.of(
                "Укажите столбец для первой матрицы",  aJson,
                "Укажите столбец для второй матрицы", bJson
        );
        insertRowIntoDB(tc.getTableName(), tc.createInsertMap(logical));

        long id = sqlTools.getLastInsertedId(tc.getTableName(), getSerialColumn(tc.getTableName()));
        ctx.setLastInsertedId(id);

        System.out.println("Матрицы сохранены, ID = " + id);
    }
}
