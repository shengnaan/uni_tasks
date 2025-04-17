package common;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public abstract class BaseTask {
    protected SQLTools sqlTools;

    public BaseTask(SQLTools sqlTools) throws SQLException {
        this.sqlTools = sqlTools;
    }

    public void showTables() throws SQLException {
        this.sqlTools.showTables();
    }

    public void createTables(List<String> tableNames, List<Map<String, String>> columnsList) throws SQLException {
        this.sqlTools.createTables(tableNames, columnsList);
    }

    public void insertRowIntoDB(String tablename, Map<String, Object> data) throws SQLException {
        this.sqlTools.insertRowIntoDB(tablename, data);
    }

    public abstract void saveToExcel();

}
