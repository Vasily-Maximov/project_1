package model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractTask {

    private int id;
    private final TaskType taskType;
    private String name;
    private String description;
    private TaskStatus taskStatus = TaskStatus.NEW;
    private long duration;
    private LocalDateTime startTime;

    public AbstractTask(TaskType taskType, String name, String description) {
        this.name = name;
        this.description = description;
        this.taskType = taskType;
    }

    public AbstractTask(TaskType taskType, String name, String description, long duration, LocalDateTime startTime) {
        this.taskType = taskType;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Optional<LocalDateTime> getEndTime() {
        if (startTime != null) {
            return Optional.of(startTime.plusMinutes(duration));
        } else {
            return Optional.empty();
        }
    }

    public long getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "AbstractTask{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus=" + taskStatus +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTask task = (AbstractTask) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}