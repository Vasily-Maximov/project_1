package server;

public enum HttpUri {
    URI_ALL_TASKS("/tasks/"),
    URI_TASKS("/tasks/task"),
    URI_TASK_ID("/tasks/task/?"),
    URI_EPICS("/tasks/epic"),
    URI_EPIC_ID("/tasks/epic/?"),
    URI_SUBTASKS("/tasks/subtask"),
    URI_SUBTASK_ID("/tasks/subtask/?"),
    URI_SUBTASKS_EPIC_ID("/tasks/subtask/epic/?"),
    URI_HISTORY("/tasks/history");

    private final String endpointName;

    HttpUri(String endpointName) {
        this.endpointName = endpointName;
    }

    public String getEndpointName() {
        return endpointName;
    }
}