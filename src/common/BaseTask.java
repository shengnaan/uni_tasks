package common;

import java.sql.SQLException;
import java.util.Map;

public abstract class BaseTask {
    protected SQLTools sqlTools;
    protected static String menuText = "";

    public BaseTask(SQLTools sqlTools) throws SQLException {
        this.sqlTools = sqlTools;
    }

    public void showMenu(String menuText) {
        System.out.println("📋 Меню:");
        System.out.println(menuText);
    }

    public void showTables() throws SQLException {
        this.sqlTools.showTables();
    }

    public void createTables() throws SQLException {
        this.sqlTools.createTables();
    }

    public void insertRowIntoDB(String tablename, Map<String, Object> data) throws SQLException {
        this.sqlTools.insertRowIntoDB(tablename, data);
    }

    public void saveToExcel() {
        this.sqlTools.saveToExcel();
    }


}
