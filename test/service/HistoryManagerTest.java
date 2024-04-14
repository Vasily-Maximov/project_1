package service;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HistoryManagerTest {

    protected HistoryManager taskManager;
    protected Task task1;
    protected Task task2;
    protected Epic epic1;
    protected SubTask subTask1_1;
    protected SubTask subTask1_2;
    protected Epic epic2;
    protected SubTask subTask2_1;

    @BeforeEach
    public void initialization() {
        taskManager = new InMemoryHistoryManager();
        task1 = new Task("Открыть смену на ККМ", "Перед началом работы необходимо открыть смену на ККМ");
        task2 = new Task("Закрыть смену на ККМ", "Перед завершением работы необходимо закрыть смену на ККМ");
        epic1 = new Epic("Провести инвентаризацию", "Проверка наличия имущества организации");
        subTask1_1 = new SubTask("Начать инвентаризацию", "Пересчет фактического наличия товара", epic1);
        subTask1_2 = new SubTask("Начать инвентаризацию", "Пересчет фактического наличия товара", epic1);
        epic2 = new Epic("Принять товар", "Фактическое получение товара от экспедитора");
        subTask2_1 = new SubTask("Проверить товар", "Сверить количество товара по накладной с фактическим",
                epic2);
        taskManager.add(task1);
        taskManager.add(task2);
        taskManager.add(epic1);
        taskManager.add(subTask1_1);
        taskManager.add(subTask1_2);
        taskManager.add(epic2);
        taskManager.add(subTask2_1);
    }

    @Test
    void addTest1() {

    }

    @Test
    void removeTest2() {

    }

    @Test
    void getHistoryTest3() {

    }
}