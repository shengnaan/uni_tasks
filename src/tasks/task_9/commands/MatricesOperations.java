package tasks.task_9.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.SQLTools;
import org.postgresql.util.PGobject;
import tasks.task_9.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public final class MatricesOperations extends MenuCommand {
    private final MatrixContext9 ctx;
    private final ObjectMapper mapper = new ObjectMapper();

    public MatricesOperations (SQLTools tools, MatrixContext9 ctx) throws SQLException {
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
                "Укажите столбец для результата умножения",
                "Укажите столбец для результата сложения",
                "Укажите столбец для результата вычитания",
                "Укажите столбец для результата возведения в степень"
        ));

        System.out.print("Введите степень, в которую нужно возвести первую матрицу: ");
        int degree;
        while(true) {
            try {
                degree = Integer.parseInt(sc.nextLine());
                if (degree < 1) throw new NumberFormatException();
                break;
            } catch (NumberFormatException e) {
                System.out.print("Неверный ввод. Введите положительное целое число.");
            }
        }

        MultiplyMatrix mult = ctx.getMatrix();
        mult.multiplyMatrices();

        SumMatrix sum = new SumMatrix();
        sum.setMatrixA(mult.getMatrixA());
        sum.setMatrixB(mult.getMatrixB());
        sum.addMatrices();

        SubtractMatrix sub = new SubtractMatrix();
        sub.setMatrixA(mult.getMatrixA());
        sub.setMatrixB(mult.getMatrixB());
        sub.subMatrices();

        DegreeMatrix deg = new DegreeMatrix();
        deg.setMatrixA(mult.getMatrixA());
        deg.degreeMatrix(degree);

        Map<String, Object> jsonResults = new HashMap<>();
        try {
            jsonResults.put("Укажите столбец для результата умножения", toJson(mult.getResultMatrix()));
            jsonResults.put("Укажите столбец для результата сложения", toJson(sum.getResultMatrix()));
            jsonResults.put("Укажите столбец для результата вычитания", toJson(sub.getResultMatrix()));
            jsonResults.put("Укажите столбец для результата возведения в степень", toJson(deg.getResultMatrix()));
        } catch (Exception e) {
            System.out.println("Ошибка сериализации JSON: " + e.getMessage());
            return;
        }

        updateRowInDB(tc.getTableName(), tc.createInsertMap(jsonResults), ctx.getLastInsertedId());

        ctx.setMatrix(null); // Сброс
        System.out.println("Результаты сохранены в строку ID = " + ctx.getLastInsertedId());
    }

    private PGobject toJson(int[][] matrix) throws Exception {
        PGobject json = new PGobject();
        json.setType("json");
        json.setValue(mapper.writeValueAsString(matrix));
        return json;
    }
}
