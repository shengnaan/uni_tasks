package tasks.task_6.commands;

import common.SQLTools;

import java.sql.SQLException;

public final class CreateTablesCommand extends MenuCommand {
    public CreateTablesCommand(SQLTools tools) throws SQLException {
        super(tools);
    }

    @Override
    public void execute() throws SQLException {
        createTables();
    }
}
