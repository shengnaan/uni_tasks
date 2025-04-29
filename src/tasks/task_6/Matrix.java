package tasks.task_6;

public final class Matrix extends ArrayPI {

    private int[][] resultMatrix;

    public Matrix() {
        super();
    }

    public void multiplyMatrices() {
        resultMatrix = new int[7][7];

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                int sum = 0;
                for (int k = 0; k < 7; k++) {
                    sum += matrixA[i][k] * matrixB[k][j];
                }
                resultMatrix[i][j] = sum;
            }
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