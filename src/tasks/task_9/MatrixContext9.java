package tasks.task_9;

public class MatrixContext9 {
    private MultiplyMatrix matrix;
    private long lastInsertedId;

    public boolean hasMatrices() {
        return matrix != null;
    }

    public MultiplyMatrix getMatrix() {
        return matrix;
    }

    public void setMatrix(MultiplyMatrix m) {
        this.matrix = m;
    }

    public long getLastInsertedId() {
        return lastInsertedId;
    }

    public void setLastInsertedId(long id) {
        this.lastInsertedId = id;
    }
}