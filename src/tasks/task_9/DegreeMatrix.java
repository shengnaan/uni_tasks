package tasks.task_9;

public final class DegreeMatrix extends ArrayPI{

    private int[][] resultMatrix;

    public DegreeMatrix() {
        super();
    }

    public void degreeMatrix(int degree) {
        resultMatrix = new int[7][7];

        // Инициализация resultMatrix как единичной матрицы 7x7
        for (int i = 0; i < 7; i++) {
            resultMatrix[i][i] = 1;
        }

        for (int d = 0; d < degree; d++) {
            MultiplyMatrix mult = new MultiplyMatrix();

            mult.setMatrixA(resultMatrix);
            mult.setMatrixB(this.matrixA);

            mult.multiplyMatrices();
            resultMatrix = mult.getResultMatrix();
        }
    }

    public void printResultMatrix() {
        System.out.println("Результирующая матрица (произведение A и B):");
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                System.out.printf(" %4d", resultMatrix[i][j]);
            }
            System.out.println();
        }
    }

    public void setMatrixA(int[][] matrixA) {
        this.matrixA = matrixA;
    }

    public int[][] getResultMatrix() {
        return resultMatrix;
    }
}
