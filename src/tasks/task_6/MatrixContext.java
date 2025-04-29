package tasks.task_6;

public class MatrixContext {
    private Matrix matrix;
    private long lastInsertedId;

    public boolean hasMatrices() {
        return matrix != null;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix m) {
        this.matrix = m;
    }

    public long getLastInsertedId() {
        return lastInsertedId;
    }

    public void setLastInsertedId(long id) {
        this.lastInsertedId = id;
    }
}


