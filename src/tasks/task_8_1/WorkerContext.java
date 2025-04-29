package tasks.task_8_1;

public class WorkerContext {
    private Worker worker;

    public boolean hasWorker() {
        return worker != null;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker w) {
        this.worker = w;
    }
}
