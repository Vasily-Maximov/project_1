package service;

import java.io.File;

public class Manager {

    public static TaskManager getDefault() {
        return FileBackedTasksManager.loadFromFile(new File("resources/backup.csv"));
    }

    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}