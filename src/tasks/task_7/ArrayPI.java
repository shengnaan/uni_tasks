package tasks.task_7;

import java.sql.SQLException;
import java.util.Scanner;

public class ArrayPI {
    protected int[] array;

    public ArrayPI() {
        this.array = new int[35];
    }

    public void fillArrayFromKeyboard() {
        Scanner sc = new Scanner(System.in);

        System.out.println("Введите элементы массива:");
        for (int i = 0; i < 35; i++) {
            array[i] = readInt(sc);
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