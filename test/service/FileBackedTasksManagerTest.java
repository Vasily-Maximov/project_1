package service;

import java.io.File;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    public FileBackedTasksManagerTest() {
        super(new FileBackedTasksManager(new File("resources/test.csv")));
    }
}