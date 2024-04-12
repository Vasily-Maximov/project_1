package service;

import org.junit.jupiter.api.BeforeEach;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends AbstractTaskManagerTest<FileBackedTasksManager> {

    public FileBackedTasksManagerTest() {
        super(new FileBackedTasksManager(new File("resources/test.csv")));
    }
}