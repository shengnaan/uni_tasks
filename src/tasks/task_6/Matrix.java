package tasks.task_6;

public final class Matrix extends ArrayPI {

    private int[][] resultMatrix;

    public Matrix() {
        super();
    }

    void multiplyMatrices() {
        resultMatrix = new int[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int sum = 0;
                for (int k = 0; k < 3; k++) {
                    sum += matrixA[i][k] * matrixB[k][j];
                }
                resultMatrix[i][j] = sum;
            }
        }
    }

    public void printResultMatrix() {
        System.out.println("Результирующая матрица (произведение A и B):");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.printf(" %4d", resultMatrix[i][j]);
            }
            System.out.println();
        }
    }

    public int[][] getResultMatrix() {
        return resultMatrix;
    }

    public int[][] getMatrixA() {
        return matrixA;
    }

    public int[][] getMatrixB() {
        return matrixB;
    }
}