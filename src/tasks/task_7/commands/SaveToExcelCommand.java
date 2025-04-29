package tasks.task_7.commands;

import common.SQLTools;

import java.sql.SQLException;

public final class SaveToExcelCommand extends MenuCommand {
    public SaveToExcelCommand(SQLTools tools) throws SQLException {
        super(tools);
    }

    @Override
    public void execute() throws SQLException {
        saveToExcel();
    }
}
