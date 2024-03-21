package service;

import model.AbstractTask;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedList<AbstractTask> taskHistory = new LinkedList<>();

    @Override
    public void add(AbstractTask task) {
        if (taskHistory.size() == 10) {
            taskHistory.removeFirst();
        }
        taskHistory.addLast(task);
    }

    @Override
    public List<AbstractTask> getHistory() {
        return taskHistory;
    }
}