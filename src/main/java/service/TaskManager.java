package service;

import model.AbstractTask;
import model.TaskType;
import java.util.List;

public interface TaskManager {

    void addTask(AbstractTask task);

    List<AbstractTask> getTasks(TaskType taskType);

    void clearTasks(TaskType taskType);

    AbstractTask getTaskById(Integer idTask);

    void delTaskById(Integer idTask);

    void updateTask(AbstractTask task);

    List<AbstractTask> getSubTasksOfEpicById(Integer taskId);

    List<AbstractTask> getHistory();
}