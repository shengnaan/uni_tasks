package tasks.task_7;

public class ArrayContext {
    private Sort sort;
    private long lastInsertedId;

    public boolean hasArray() {
        return sort != null;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort s) {
        this.sort = s;
    }

    public long getLastInsertedId() {
        return lastInsertedId;
    }

    public void setLastInsertedId(long id) {
        this.lastInsertedId = id;
    }
}
