package service;

import exeption.ManagerSaveException;
import model.Epic;
import model.AbstractTask;
import model.SubTask;
import model.TaskStatus;
import model.TaskType;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected int generatorId;
    private final TaskStatus taskStatusNew = TaskStatus.NEW;
    private final Map<Integer, AbstractTask> tasks = new HashMap<>();
    protected final HistoryManager historyManager;
    private final Comparator<AbstractTask> taskComparator = Comparator
            .comparing(AbstractTask::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(AbstractTask::getId);
    private final TreeSet<AbstractTask> tasksTreeSet = new TreeSet<>(taskComparator);

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
            tasksTreeSet.remove(subTask);
            tasks.remove(id);
        }
    }

    private void updEpic(SubTask subTask) {
        Epic epic = (Epic)tasks.get(subTask.getEpicId());
        List<SubTask> subTasksOfEpic;
        if (epic != null) {
            subTasksOfEpic = epic.getSubTasksOfEpic();
            if (!subTasksOfEpic.contains(subTask)) {
                subTasksOfEpic.add(subTask);
            }
            epic.setTaskStatus(taskStatusNew);
            epic.setDuration(0);
            epic.setStartTime(LocalDateTime.MIN);
            epic.calculateEndTimeForEpic();
            //updateTask(epic);
        }
    }

    private void calculateEpicStatusAfterDelSubtask(SubTask subTask) {
        Epic epic = (Epic)tasks.get(subTask.getEpicId());
        if (epic != null) {
            epic.getSubTasksOfEpic().remove(subTask);
            epic.setTaskStatus(taskStatusNew);
            epic.setDuration(0);
            epic.setStartTime(LocalDateTime.MIN);
            epic.calculateEndTimeForEpic();
        }
    }

    public boolean isCheckIntersections(AbstractTask task) {
        boolean isIntersection = false;
        List<AbstractTask> sortedListTask = getPrioritizedTasks();
        LocalDateTime startTime = task.getStartTime();
        LocalDateTime endTime = task.getEndTime().orElse(null);
        if(startTime != null) {
            for (AbstractTask valueTask : sortedListTask) {
                if (valueTask.getStartTime() != null) {
                    if (!(startTime.isBefore(valueTask.getStartTime()) && endTime.isBefore(valueTask.getStartTime()))) {
                        if (!(startTime.isAfter(valueTask.getEndTime().orElse(null)) && endTime.isAfter(valueTask.getEndTime()
                                .orElse(null)))) {
                            isIntersection = true;
                        }
                    }
                }
            }
        }
        return isIntersection;
    }

    @Override
    public void addTask(AbstractTask task) {
        task.setId(++generatorId);
        tasks.put(task.getId(), task);
        switch (task.getTaskType()) {
            case TASK:
                if (isCheckIntersections(task)) {
                    throw new ManagerSaveException("Новый объект пересекается с другими объектами в списке");
                }
                tasksTreeSet.add(task);
                break;
            case SUBTASK:
                updateTask(task);
                if (isCheckIntersections(task)) {
                    throw new ManagerSaveException("Новый объект пересекается с другими объектами в списке");
                }
                tasksTreeSet.add(task);
                break;
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
                tasksTreeSet.remove(task);
                break;
            case TASK:
                tasksTreeSet.remove(task);
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

    @Override
    public List<AbstractTask> getPrioritizedTasks() {
        return new ArrayList<>(tasksTreeSet);
    }
}