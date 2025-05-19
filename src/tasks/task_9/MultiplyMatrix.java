package tasks.task_9;

public final class MultiplyMatrix extends ArrayPI {

    private int[][] resultMatrix;

    public MultiplyMatrix() {
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

    public void setMatrixA(int[][] matrixA) {
        this.matrixA = matrixA;
    }

    public void setMatrixB(int[][] matrixB) {
        this.matrixB = matrixB;
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
