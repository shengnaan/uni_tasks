package tasks.task_9;

public final class SubtractMatrix extends ArrayPI {

    private int [][] resultMatrix;

    public SubtractMatrix() {
        super();
    }

    public void subMatrices() {
        resultMatrix = new int[7][7];

        for (int i = 0; i < 7; i++){
            for (int j = 0; j < 7; j++) {
                int res = 0;
                res += matrixA[i][j] - matrixB[i][j];
                resultMatrix[i][j] = res;
            }
        }
    }

    public void printResultMatrix() {
        System.out.println("Результирующая матрица (Вычитание А и В):");
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

    public int [][] getResultMatrix() {return resultMatrix;}

    public int [][] getMatrixA() {return matrixA;}

    public int [][] getMatrixB() {return matrixB;}
}
