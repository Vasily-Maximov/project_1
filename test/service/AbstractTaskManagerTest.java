package service;

import org.junit.jupiter.api.Test;

abstract class AbstractTaskManagerTest<T extends TaskManager> {

    private T taskManager;

    public AbstractTaskManagerTest(T taskManager) {
        this.taskManager = taskManager;
    }

    @Test
    void addTask() {
    }

    @Test
    void getTasks() {
    }

    @Test
    void clearTasks() {
    }

    @Test
    void getTaskById() {
    }

    @Test
    void delTaskById() {
    }

    @Test
    void updateTask() {
    }

    @Test
    void getSubTasksOfEpicById() {
    }

    @Test
    void getHistory() {
    }
}