package model;

import java.time.LocalDateTime;

public class Task extends AbstractTask {

    public Task(String name, String description) {
        super(TaskType.TASK, name, description);
    }

    public Task(String name, String description, long duration, LocalDateTime startTime) {
        super(TaskType.TASK, name, description, duration, startTime);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + getId() + '\'' +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", taskStatus=" + getTaskStatus() +
                '}';
    }
}