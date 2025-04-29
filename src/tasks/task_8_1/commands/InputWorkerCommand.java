package tasks.task_8_1.commands;

import tasks.task_8_1.Worker;
import tasks.task_8_1.WorkerContext;
import common.SQLTools;

import java.sql.SQLException;
import java.util.*;

public final class InputWorkerCommand extends MenuCommand {
    private final WorkerContext ctx;

    public InputWorkerCommand(SQLTools tools, WorkerContext ctx) throws SQLException {
        super(tools);
        this.ctx = ctx;
    }

    @Override
    public void execute() throws SQLException {

        if (!hasTables()) {
            System.out.println("Ошибка: нет созданных таблиц (сначала пункт 2).");
            return;
        }

        Scanner sc = new Scanner(System.in);
        TableAndColumns tc = promptTableAndColumns(
                sc,
                List.of(
                        "Укажите столбец для имени (тип VARCHAR(255))",
                        "Укажите столбец для возраста (тип INT4)",
                        "Укажите столбец для зарплаты (тип FLOAT8)"
                )
        );

        Worker w = new Worker();
        String name;
        do {
            System.out.print("Имя: ");
            name = sc.nextLine().trim();
        } while (name.isEmpty() || name.length() > 255);
        w.setName(name);

        int age;
        while (true) {
            System.out.print("Возраст: ");
            try {
                age = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Введите целое число");
                continue;
            }
            if (age >= 0 && age <= 120) break;
            System.out.println("Возраст 0–120.");
        }
        w.setAge(age);

        double salary;
        while (true) {
            System.out.print("Зарплата: ");
            try {
                salary = Double.parseDouble(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Введите число");
                continue;
            }
            if (salary >= 0) break;
            System.out.println("Зарплата не может быть отрицательной.");
        }
        w.setSalary(salary);

        Map<String, Object> logical = Map.of(
                "Укажите столбец для имени (тип VARCHAR(255))", w.getName(),
                "Укажите столбец для возраста (тип INT4)", w.getAge(),
                "Укажите столбец для зарплаты (тип FLOAT8)", w.getSalary()
        );
        insertRowIntoDB(tc.getTableName(), tc.createInsertMap(logical));

        System.out.printf("Сотрудник %s (%d лет, %.2f ₽) сохранён%n",
                w.getName(), w.getAge(), w.getSalary());

        ctx.setWorker(w);
    }
}
