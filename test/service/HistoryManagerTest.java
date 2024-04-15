package service;

import exeption.ManagerSaveException;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HistoryManagerTest {

    protected HistoryManager historyManager;
    protected Task task1;
    protected Task task2;
    protected Epic epic1;
    protected SubTask subTask1_1;
    protected SubTask subTask1_2;
    protected Epic epic2;
    protected SubTask subTask2_1;

    @BeforeEach
    public void initialization() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("Открыть смену на ККМ", "Перед началом работы необходимо открыть смену на ККМ");
        task1.setId(1);
        task2 = new Task("Закрыть смену на ККМ", "Перед завершением работы необходимо закрыть смену на ККМ");
        task2.setId(2);
        epic1 = new Epic("Провести инвентаризацию", "Проверка наличия имущества организации");
        epic1.setId(3);
        subTask1_1 = new SubTask("Начать инвентаризацию", "Пересчет фактического наличия товара", epic1);
        subTask1_1.setId(4);
        subTask1_2 = new SubTask("Начать инвентаризацию", "Пересчет фактического наличия товара", epic1);
        subTask1_2.setId(5);
        epic2 = new Epic("Принять товар", "Фактическое получение товара от экспедитора");
        epic2.setId(6);
        subTask2_1 = new SubTask("Проверить товар", "Сверить количество товара по накладной с фактическим",
                epic2);
        subTask2_1.setId(7);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(epic1);
        historyManager.add(subTask1_1);
        historyManager.add(subTask1_2);
        historyManager.add(epic2);
        historyManager.add(subTask2_1);
    }

    @Test
    void addTest1() {
        Assertions.assertEquals(7, historyManager.getHistory().size());
        Assertions.assertEquals(task1, historyManager.getHistory().get(0));
        Assertions.assertEquals(subTask1_2, historyManager.getHistory().get(4));
        Assertions.assertEquals(subTask2_1, historyManager.getHistory().get(6));
        historyManager.add(task1);
        Assertions.assertEquals(task2, historyManager.getHistory().get(0));
        Assertions.assertEquals(task1, historyManager.getHistory().get(6));
    }

    @Test
    void removeTest2() {
        Assertions.assertEquals(7, historyManager.getHistory().size());
        historyManager.remove(1);
        Assertions.assertEquals(6, historyManager.getHistory().size());
        Assertions.assertEquals(task2, historyManager.getHistory().get(0));
        Assertions.assertEquals(subTask2_1, historyManager.getHistory().get(5));
        historyManager.remove(7);
        Assertions.assertEquals(5, historyManager.getHistory().size());
        Assertions.assertEquals(task2, historyManager.getHistory().get(0));
        Assertions.assertEquals(epic2, historyManager.getHistory().get(4));

    }

    @Test
    void getHistoryTest3() {
        Assertions.assertEquals(7, historyManager.getHistory().size());
        Assertions.assertEquals(task1, historyManager.getHistory().get(0));
        Assertions.assertEquals(subTask2_1, historyManager.getHistory().get(6));
    }

    @Test
    public void removeFromEmptyHistoryTest4() {
        int id = 999;
        ManagerSaveException exception = assertThrows(
                ManagerSaveException.class,
                () -> {
                    if (historyManager.getHistory().contains(id)) {
                        historyManager.remove(id);
                    } else {
                        throw new ManagerSaveException(String.format("История не содержит значение по id = %d", id));
                    }
                });
        assertEquals(String.format("История не содержит значение по id = %d", id), exception.getMessage());

    }
}