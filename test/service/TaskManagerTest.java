package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskType;
import model.AbstractTask;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected Task task1;
    protected Task task2;
    protected Epic epic1;
    protected SubTask subTask1_1;
    protected SubTask subTask1_2;
    protected Epic epic2;
    protected SubTask subTask2_1;

    public TaskManagerTest() {
    }

    public TaskManagerTest(T taskManager) {
        this.taskManager = taskManager;
    }

    private static Comparator<AbstractTask> comparatorById() {
        return Comparator.comparingInt(AbstractTask::getId);
    }

    @BeforeEach
    public void initialization() {
        task1 = new Task("Открыть смену на ККМ", "Перед началом работы необходимо открыть смену на ККМ");
        taskManager.addTask(task1);
        task2 = new Task("Закрыть смену на ККМ", "Перед завершением работы необходимо закрыть смену на ККМ");
        taskManager.addTask(task2);
        epic1 = new Epic("Провести инвентаризацию", "Проверка наличия имущества организации");
        taskManager.addTask(epic1);
        subTask1_1 = new SubTask("Начать инвентаризацию", "Пересчет фактического наличия товара", epic1);
        taskManager.addTask(subTask1_1);
        subTask1_2 = new SubTask("Начать инвентаризацию", "Пересчет фактического наличия товара", epic1);
        taskManager.addTask(subTask1_2);
        epic2 = new Epic("Принять товар", "Фактическое получение товара от экспедитора");
        taskManager.addTask(epic2);
        subTask2_1 = new SubTask("Проверить товар", "Сверить количество товара по накладной с фактическим",
                epic2);
        taskManager.addTask(subTask2_1);
    }


    @Test
    void addTaskTest1() {
        Assertions.assertEquals(7, taskManager.getAllTasks().size(), String.format("Не все задачи созданы, ожидали %d," +
                " а получили %d", 7, taskManager.getAllTasks().size()));
        Assertions.assertEquals(task1, taskManager.getAllTasks().get(task1.getId()));
        Assertions.assertEquals(subTask2_1, taskManager.getAllTasks().get(subTask2_1.getId()));
    }

    @Test
    void getTasksTest2() {
        List<AbstractTask> expectedTasks = new ArrayList<>();
        expectedTasks.add(task2);
        expectedTasks.add(task1);
        List<AbstractTask> actualTasks = taskManager.getTasks(TaskType.TASK);
        Assertions.assertEquals(actualTasks.size(), actualTasks.size());
        Assertions.assertEquals(TaskType.TASK, actualTasks.get(0).getTaskType());
        Assertions.assertEquals(TaskType.TASK, actualTasks.get(1).getTaskType());
        actualTasks.sort(comparatorById());
        expectedTasks.sort(comparatorById());
        Assertions.assertEquals(expectedTasks, actualTasks);

        List<AbstractTask> expectedEpics = new ArrayList<>();
        expectedEpics.add(epic2);
        expectedEpics.add(epic1);
        List<AbstractTask> actualEpics = taskManager.getTasks(TaskType.EPIC);
        Assertions.assertEquals(expectedEpics.size(), actualEpics.size());
        Assertions.assertEquals(TaskType.EPIC, actualEpics.get(0).getTaskType());
        Assertions.assertEquals(TaskType.EPIC, actualEpics.get(1).getTaskType());
        expectedEpics.sort(comparatorById());
        actualEpics.sort(comparatorById());
        Assertions.assertEquals(expectedEpics, actualEpics);

        List<AbstractTask> expectedSubTasks = new ArrayList<>();
        expectedSubTasks.add(subTask2_1);
        expectedSubTasks.add(subTask1_2);
        expectedSubTasks.add(subTask1_1);
        List<AbstractTask> actualSubTasks = taskManager.getTasks(TaskType.SUBTASK);
        Assertions.assertEquals(expectedSubTasks.size(), actualSubTasks.size());
        Assertions.assertEquals(TaskType.SUBTASK, actualSubTasks.get(0).getTaskType());
        Assertions.assertEquals(TaskType.SUBTASK, actualSubTasks.get(1).getTaskType());
        Assertions.assertEquals(TaskType.SUBTASK, actualSubTasks.get(2).getTaskType());
        expectedSubTasks.sort(comparatorById());
        actualSubTasks.sort(comparatorById());
        Assertions.assertEquals(expectedSubTasks, actualSubTasks);
    }

    @Test
    void getAllTasksTest3() {
        Map<Integer, AbstractTask> actualTasks = taskManager.getAllTasks();
        Assertions.assertEquals(7, actualTasks.size());
        Assertions.assertEquals(task1, actualTasks.get(task1.getId()));
        Assertions.assertEquals(task2, actualTasks.get(task2.getId()));
        Assertions.assertEquals(epic1, actualTasks.get(epic1.getId()));
        Assertions.assertEquals(epic2, actualTasks.get(epic2.getId()));
        Assertions.assertEquals(subTask1_1, actualTasks.get(subTask1_1.getId()));
        Assertions.assertEquals(subTask1_2, actualTasks.get(subTask1_2.getId()));
        Assertions.assertEquals(subTask2_1, actualTasks.get(subTask2_1.getId()));
    }

    @Test
    void clearTasksTest4() {
        Map<Integer, AbstractTask> actualTasks = taskManager.getAllTasks();
        taskManager.clearTasks(TaskType.SUBTASK);
        Assertions.assertEquals(4, actualTasks.size());
        taskManager.clearTasks(TaskType.TASK);
        Assertions.assertEquals(2, actualTasks.size());
        taskManager.clearTasks(TaskType.EPIC);
        Assertions.assertEquals(0, actualTasks.size());
    }

    @Test
    void getTaskByIdTest5() {
        AbstractTask taskById = taskManager.getTaskById(0);
        Assertions.assertNull(taskById);
        taskById = taskManager.getTaskById(8);
        Assertions.assertNull(taskById);
        Assertions.assertEquals(epic1, taskManager.getTaskById(epic1.getId()));
    }

    @Test
    void delTaskByIdTest6() {
        taskManager.delTaskById(epic2.getId());
        Assertions.assertNull(taskManager.getTaskById(epic2.getId()));
        Assertions.assertNull(taskManager.getTaskById(epic2.getSubTasksOfEpic().get(0).getId()));
        taskManager.delTaskById(subTask1_2.getId());
        Assertions.assertEquals(1, epic1.getSubTasksOfEpic().size());
    }

    @Test
    void updateTaskTest7() {
        subTask2_1.setTaskStatus(TaskStatus.DONE);
        taskManager.updateTask(subTask2_1);
        subTask1_1.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(subTask1_1);
        Assertions.assertEquals(epic1, taskManager.getTaskById(epic1.getId()));
        task1.setTaskStatus(TaskStatus.DONE);
        taskManager.updateTask(task1);
        Assertions.assertEquals(task1, taskManager.getTaskById(task1.getId()));
    }

    @Test
    void getSubTasksOfEpicByIdTest8() {
        List<AbstractTask> subTasksOfEpicById = taskManager.getSubTasksOfEpicById(epic2.getId());
        Assertions.assertEquals(1, subTasksOfEpicById.size());
        Assertions.assertEquals(subTask2_1, subTasksOfEpicById.get(0));
        subTasksOfEpicById = taskManager.getSubTasksOfEpicById(epic1.getId());
        Assertions.assertEquals(2, subTasksOfEpicById.size());
    }

    @Test
    void getHistoryTest9() {
        List<AbstractTask> expectedHistory = new ArrayList<>();
        expectedHistory.add(subTask1_1);
        expectedHistory.add(epic2);
        expectedHistory.add(task1);
        taskManager.getTaskById(subTask1_1.getId());
        taskManager.getTaskById(epic2.getId());
        taskManager.getTaskById(task1.getId());
        List<AbstractTask> actualHistory = taskManager.getHistory();
        expectedHistory.sort(comparatorById());
        actualHistory.sort(comparatorById());
        Assertions.assertEquals(3, actualHistory.size());
        Assertions.assertEquals(expectedHistory, actualHistory);
    }
}