package tasks.task_8_1;

import java.sql.SQLException;
import java.util.Map;

public class Worker extends Student {
    private double salary;


    public void setSalary(double salary) {
        this.salary = salary;
    }

    public double getSalary() {
        return this.salary;
    }
}

