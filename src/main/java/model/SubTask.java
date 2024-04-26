package model;

import java.time.LocalDateTime;

public class SubTask extends AbstractTask {

    private final int epicId;

    public SubTask(String name, String description, Epic epic) {
        super(TaskType.SUBTASK, name, description);
        this.epicId = epic.getId();
    }

    public SubTask(String name, String description, Epic epic, long duration, LocalDateTime startTime) {
        super(TaskType.SUBTASK, name, description, duration, startTime);
        this.epicId = epic.getId();
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public void setTaskStatus(TaskStatus taskStatus) {
        super.setTaskStatus(taskStatus);
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id='" + getId() + '\'' +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", taskStatus=" + getTaskStatus() +
                ", epicId=" + epicId +
                '}';
    }
}