package service;

import exeption.ManagerSaveException;
import model.TaskType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.io.File;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    public FileBackedTasksManagerTest() {
        super(new FileBackedTasksManager(new File("resources/test.csv")));
    }

    private static void managerSaveException() {
        FileBackedTasksManager.loadFromFile(new File("resources/test1.csv"));
    }

    @Test
    public void loadFromFileTest1() {
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(new File("resources/test.csv"));
        Assertions.assertEquals(taskManager.getAllTasks().size(), fileBackedTasksManager.getAllTasks().size());
        Assertions.assertEquals(taskManager.getHistory().size(), fileBackedTasksManager.getHistory().size());
    }

    @Test
    public void addTaskToHistoryTest2() {
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(epic1.getId());
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(new File("resources/test.csv"));
        Assertions.assertEquals(taskManager.getHistory().size(), fileBackedTasksManager.getHistory().size());
        Assertions.assertEquals(task1, fileBackedTasksManager.getHistory().get(0));
        Assertions.assertEquals(epic1, fileBackedTasksManager.getHistory().get(1));
    }

    @Test
    public void loadFromFileNotFoundTest3() {
        Assertions.assertThrows(ManagerSaveException.class, FileBackedTasksManagerTest::managerSaveException);
        ManagerSaveException exception = Assertions.assertThrows(ManagerSaveException.class,
                FileBackedTasksManagerTest::managerSaveException);
        Assertions.assertEquals("Ошибка при открытии файла: test1.csv", exception.getMessage());
    }

    @Test
    public void saveFromFileNotFoundTest4() {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(new File("resources1/test1.csv"));
        ManagerSaveException exception = Assertions.assertThrows(ManagerSaveException.class,
                () -> fileBackedTasksManager.clearTasks(TaskType.TASK));
        Assertions.assertEquals("Ошибка при сохранении сущностей и истории в файл: test1.csv", exception.getMessage());
    }

    @Test
    public void loadFromFileEmptyListTest5() {
        taskManager.clearTasks(TaskType.TASK);
        taskManager.clearTasks(TaskType.EPIC);
        taskManager.clearTasks(TaskType.SUBTASK);
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(new File("resources/test.csv"));
        Assertions.assertEquals(taskManager.getAllTasks().size(), fileBackedTasksManager.getAllTasks().size());
    }
}