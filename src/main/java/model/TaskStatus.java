package model;

public enum TaskStatus {
    NEW("Задача только создана, но к её выполнению ещё не приступили"),
    IN_PROGRESS("Над задачей ведётся работа"),
    DONE("Задача выполнена");

    private final String statusName;

    TaskStatus(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }
}