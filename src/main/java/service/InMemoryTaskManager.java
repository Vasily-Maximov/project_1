package service;

import model.Epic;
import model.AbstractTask;
import model.SubTask;
import model.TaskStatus;
import model.TaskType;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;


public class InMemoryTaskManager implements TaskManager {

    protected int generatorId;

    private final TaskStatus taskStatusNew = TaskStatus.NEW;

    private final Map<Integer, AbstractTask> tasks = new HashMap<>();

    protected final HistoryManager historyManager;

    public InMemoryTaskManager() {
        historyManager = Manager.getDefaultHistory();
    }

    private List<AbstractTask> getTasksByType(TaskType taskType) {
        List<AbstractTask> tasksList = new ArrayList<>();
        for(AbstractTask task : tasks.values()) {
            if (task.getTaskType() == taskType) {
                tasksList.add(task);
            }
        }
        return tasksList;
    }

    private void clearByTypeTasks(TaskType taskType) {
        List<AbstractTask> tasksList = getTasksByType(taskType);
        for (AbstractTask task : tasksList) {
            switch (taskType) {
                case EPIC:
                    clearSubtasksOfEpic((Epic) task);
                    break;
                case SUBTASK:
                    calculateEpicStatusAfterDelSubtask((SubTask) task);
                    break;
                default:
            }
            int id = task.getId();
            historyManager.remove(id);
            tasks.remove(id);
        }
    }

    private void clearSubtasksOfEpic(Epic epic) {
        List<SubTask> subTaskList = epic.getSubTasksOfEpic();
        int id;
        for(SubTask subTask : subTaskList) {
            id = subTask.getId();
            historyManager.remove(id);
            tasks.remove(id);
        }
    }

    private void updEpic(SubTask subTask) {
        Epic epic = subTask.getEpic();
        if (epic != null) {
            epic.getSubTasksOfEpic().add(subTask);
            epic.setTaskStatus(taskStatusNew);
            epic.setDuration(0);
            epic.setStartTime(LocalDateTime.MIN);
            epic.calculateEndTimeForEpic();
        }
    }

    private void calculateEpicStatusAfterDelSubtask(SubTask subTask) {
        Epic epic = subTask.getEpic();
        if (epic != null) {
            epic.getSubTasksOfEpic().remove(subTask);
            epic.setTaskStatus(taskStatusNew);
        }
    }

    @Override
    public void addTask(AbstractTask task) {
        task.setId(++generatorId);
        tasks.put(task.getId(), task);
        if (task.getTaskType() == TaskType.SUBTASK) {
            updEpic((SubTask) task);
        }
    }

    @Override
    public List<AbstractTask> getTasks(TaskType taskType) {
        List<AbstractTask> tasksList = new ArrayList<>();
        switch (taskType) {
            case TASK:
                tasksList = getTasksByType(TaskType.TASK);
                break;
            case EPIC:
                tasksList = getTasksByType(TaskType.EPIC);
                break;
            case SUBTASK:
                tasksList = getTasksByType(TaskType.SUBTASK);
                break;
            default:
        }
        return tasksList;
    }

    @Override
    public Map<Integer, AbstractTask> getAllTasks() {
        return tasks;
    }

    @Override
    public void clearTasks(TaskType taskType) {
        switch (taskType) {
            case TASK:
                clearByTypeTasks(TaskType.TASK);
                break;
            case EPIC:
                clearByTypeTasks(TaskType.EPIC);
                break;
            case SUBTASK:
                clearByTypeTasks(TaskType.SUBTASK);
                break;
            default:
        }
    }

    @Override
    public AbstractTask getTaskById(Integer idTask) {
        AbstractTask task = tasks.get(idTask);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public void delTaskById(Integer idTask) {
        AbstractTask task = tasks.get(idTask);
        switch (task.getTaskType()) {
            case EPIC:
                clearSubtasksOfEpic((Epic) task);
                break;
            case SUBTASK:
                calculateEpicStatusAfterDelSubtask((SubTask) task);
                break;
            default:
        }
        historyManager.remove(idTask);
        tasks.remove(idTask);
    }

    @Override
    public void updateTask(AbstractTask task) {
        TaskType taskType = task.getTaskType();
        tasks.put(task.getId(), task);
        if (taskType == TaskType.SUBTASK) {
            updEpic((SubTask) task);
        }
    }

    @Override
    public List<AbstractTask> getSubTasksOfEpicById(Integer taskId) {
        List<AbstractTask> subTaskList = new ArrayList<>();
        Epic epic = (Epic) tasks.get(taskId);
        if (epic != null) {
            subTaskList.addAll(epic.getSubTasksOfEpic());
        }
        return subTaskList;
    }

    @Override
    public List<AbstractTask> getHistory() {
        return historyManager.getHistory();
    }
}