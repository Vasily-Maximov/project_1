package model;

public class Task extends AbstractTask {

    public Task(String name, String description) {
        super(TaskType.TASK, name, description);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + getId() + '\'' +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus=" + getTaskStatus() +
                '}';
    }
}