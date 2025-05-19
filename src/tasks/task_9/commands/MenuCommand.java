package tasks.task_9.commands;

import common.BaseTask;
import common.SQLTools;

import java.sql.SQLException;

public abstract class MenuCommand extends BaseTask {
    protected MenuCommand(SQLTools tools) throws SQLException {
        super(tools);
    }

    public abstract void execute() throws SQLException;
}
