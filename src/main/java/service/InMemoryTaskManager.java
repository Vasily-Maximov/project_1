package service;

import model.Epic;
import model.AbstractTask;
import model.SubTask;
import model.TaskStatus;
import model.TaskType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    private int generatorId;

    private final TaskStatus taskStatusNew = TaskStatus.NEW;

    private final Map<Integer, AbstractTask> tasks = new HashMap<>();

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
            tasks.remove(task.getId());
        }
    }

    private void clearSubtasksOfEpic(Epic epic) {
        List<SubTask> subTaskList = epic.getSubTasksOfEpic();
        for(SubTask subTask : subTaskList) {
            tasks.remove(subTask.getId());
        }
    }

    private void updEpicStatus(SubTask subTask) {
        Epic epic = subTask.getEpic();
        if (epic != null) {
            epic.setTaskStatus(taskStatusNew);
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
        if (task instanceof SubTask) {
            SubTask subTask = (SubTask) task;
            Epic epic = (Epic) tasks.get(subTask.getEpic().getId());
            epic.getSubTasksOfEpic().add(subTask);
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
        return tasks.get(idTask);
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
        tasks.remove(idTask);
    }

    @Override
    public void updateTask(AbstractTask task) {
        TaskType taskType = task.getTaskType();
        tasks.put(task.getId(), task);
        if (taskType == TaskType.SUBTASK) {
            updEpicStatus((SubTask) task);
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
}