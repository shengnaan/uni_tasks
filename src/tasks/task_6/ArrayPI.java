package tasks.task_6;

import java.util.Scanner;

public class ArrayPI {
    protected int[][] matrixA;
    protected int[][] matrixB;

    public ArrayPI() {
        this.matrixA = new int[7][7];
        this.matrixB = new int[7][7];
    }

    public void fillMatricesFromKeyboard() {
        Scanner sc = new Scanner(System.in);

        System.out.println("Введите элементы первой матрицы (7x7):");
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                matrixA[i][j] = readInt(sc);
            }
        }

        System.out.println("Введите элементы второй матрицы (7x7):");
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                matrixB[i][j] = readInt(sc);
            }
        }
    }

    private int readInt(Scanner sc) {
        while (!sc.hasNextInt()) {
            System.out.println("Ошибка: введите целое число!");
            sc.next();
        }
        return sc.nextInt();
    }
}