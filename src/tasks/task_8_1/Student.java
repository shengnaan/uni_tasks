package tasks.task_8_1;

import common.BaseTask;
import common.SQLTools;

import java.sql.SQLException;
import java.util.Map;

public abstract class Student {
    private String name;
    private int age;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setAge(int age) {
        if (!String.valueOf(age).matches("\\d+")) {
            System.out.println("Возраст должен быть целым числом.");
            return;
        }
        if (age < 0 || age > 120) {
            System.out.println("Возраст должен быть в пределах от 0 до 120 лет.");
            return;
        }
        this.age = age;
    }

    public int getAge() {
        return this.age;
    }

}
