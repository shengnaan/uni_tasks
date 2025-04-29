package tasks.task_8_1.commands;

import common.SQLTools;

import java.sql.SQLException;

public final class ShowMenuCommand extends MenuCommand {
    private final String menu;

    public ShowMenuCommand(SQLTools tools, String menu) throws SQLException {
        super(tools);
        this.menu = menu;
    }

    @Override
    public void execute() {
        showMenu(menu);
    }
}
