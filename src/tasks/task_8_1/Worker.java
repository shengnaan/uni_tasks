package tasks.task_8_1;

import java.sql.SQLException;
import java.util.Map;

public class Worker extends Student {
    private double salary;

    public Worker(String dbName, Map<String, Map<String, String>> tableSchemas) throws SQLException {
        super(dbName, tableSchemas);
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public double getSalary() {
        return this.salary;
    }
}

