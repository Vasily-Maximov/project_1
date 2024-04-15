package model;

import java.time.LocalDateTime;

public class SubTask extends AbstractTask {

    private final Epic epic;

    public SubTask(String name, String description, Epic epic) {
        super(TaskType.SUBTASK, name, description);
        this.epic = epic;
    }

    public SubTask(String name, String description, Epic epic, long duration, LocalDateTime startTime) {
        super(TaskType.SUBTASK, name, description, duration, startTime);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public void setTaskStatus(TaskStatus taskStatus) {
        super.setTaskStatus(taskStatus);
        if (epic != null) {
            epic.setTaskStatus(TaskStatus.NEW);
        }
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id='" + getId() + '\'' +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", taskStatus=" + getTaskStatus() +
                ", epicId=" + epic.getId() +
                '}';
    }
}