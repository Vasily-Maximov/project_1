package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends AbstractTask {

    private final List<SubTask> subTasksOfEpic = new ArrayList<>();

    public Epic(String name, String description) {
        super(TaskType.EPIC, name, description);
    }

    public List<SubTask> getSubTasksOfEpic() {
        return subTasksOfEpic;
    }

    private List<Integer> getIdOfSubtasks() {
        List<Integer> listId = new ArrayList<>();
        for (SubTask subTask : subTasksOfEpic) {
            listId.add(subTask.getId());
        }

        return listId;
    }

    private TaskStatus calculateStatus() {
        TaskStatus taskStatus;
        int amountNewTask = 0;
        int amountDoneTask = 0;
        for (SubTask task : subTasksOfEpic) {
            switch (task.getTaskStatus()) {
                case NEW:
                    amountNewTask++;
                    break;
                case DONE:
                    amountDoneTask++;
                    break;
                default:
            }
        }
        if (subTasksOfEpic.size() == amountNewTask) {
            taskStatus = TaskStatus.NEW;
        } else if (subTasksOfEpic.size() == amountDoneTask) {
            taskStatus = TaskStatus.DONE;
        } else {
            taskStatus = TaskStatus.IN_PROGRESS;
        }
        return taskStatus;
    }

    @Override
    public void setTaskStatus(TaskStatus taskStatus) {
        taskStatus = calculateStatus();
        super.setTaskStatus(taskStatus);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id='" + getId() + '\'' +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus=" + getTaskStatus() +
                ", subTasksOfEpic=" + getIdOfSubtasks() +
                '}';
    }
}