package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;
import java.util.List;
import java.util.Set;

class EpicTest {

    private Epic epic1;
    private SubTask subTask1_1;
    private SubTask subTask1_2;
    private Epic epic2;
    private SubTask subTask2_1;
    private TaskManager taskManager;

    @BeforeEach
    public void initialization() {
        taskManager = new InMemoryTaskManager();
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
    }

    @Test
    public void getSubTasksOfEpicTest1() {
        List<SubTask> subTasks = epic1.getSubTasksOfEpic();
        Assertions.assertEquals(2, subTasks.size(), String.format("Ожидали количество задач в списке %d, а получили %d",
                2, subTasks.size()));

        subTasks = epic2.getSubTasksOfEpic();
        Assertions.assertEquals(0, subTasks.size(), String.format("Ожидали количество задач в списке %d, а получили %d",
                0, subTasks.size()));
    }

    @Test
    public void setTaskStatusTest2() {
        Assertions.assertEquals(TaskStatus.NEW, epic1.getTaskStatus());
        epic1.setTaskStatus(TaskStatus.DONE);
        Assertions.assertEquals(TaskStatus.NEW, epic1.getTaskStatus());
        subTask1_1.setTaskStatus(TaskStatus.DONE);
        taskManager.updateTask(subTask1_1);
        subTask1_2.setTaskStatus(TaskStatus.DONE);
        taskManager.updateTask(subTask1_2);
        Assertions.assertEquals(TaskStatus.DONE, epic1.getTaskStatus());
        subTask1_1.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(subTask1_1);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic1.getTaskStatus());
        subTask1_2.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(subTask1_2);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic1.getTaskStatus());

        Assertions.assertEquals(TaskStatus.NEW, epic2.getTaskStatus());
        epic2.setTaskStatus(TaskStatus.DONE);
        Assertions.assertEquals(TaskStatus.NEW, epic2.getTaskStatus(), String.format("Ожидали получить статус задачи %s, " +
                "а получили %s",TaskStatus.NEW, epic2.getTaskStatus()));
        epic2.setTaskStatus(TaskStatus.IN_PROGRESS);
        Assertions.assertEquals(TaskStatus.NEW, epic2.getTaskStatus(), String.format("Ожидали получить статус задачи %s, " +
                "а получили %s",TaskStatus.NEW, epic2.getTaskStatus()));
    }

    @Test
    public void toStringTest3() {
        String epic1ToString = "Epic{id='1'name='Провести инвентаризацию', description='Проверка наличия имущества организации'," +
                " taskStatus=NEW, subTasksOfEpic=[2, 3]}";
        Assertions.assertEquals(epic1ToString, epic1.toString());
        subTask1_2.setTaskStatus(TaskStatus.DONE);
        taskManager.updateTask(subTask1_2);
        epic1ToString = "Epic{id='1'name='Провести инвентаризацию', description='Проверка наличия имущества организации'," +
                " taskStatus=IN_PROGRESS, subTasksOfEpic=[2, 3]}";
        Assertions.assertEquals(epic1ToString, epic1.toString());
        String epic2ToString = "Epic{id='4'name='Принять товар', description='Фактическое получение товара от экспедитора'," +
                " taskStatus=NEW, subTasksOfEpic=[]}";
        Assertions.assertEquals(epic2ToString, epic2.toString());
    }
}