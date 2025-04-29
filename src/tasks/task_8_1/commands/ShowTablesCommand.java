package tasks.task_8_1.commands;

import common.SQLTools;

import java.sql.SQLException;

public final class ShowTablesCommand extends MenuCommand {
    public ShowTablesCommand(SQLTools tools) throws SQLException {
        super(tools);
    }

    @Override
    public void execute() throws SQLException {
        showTables();
    }
}
