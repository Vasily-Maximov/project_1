import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskType;
import service.Manager;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Manager.getDefault();
        Task task1 = new Task("Открыть смену на ККМ", "Перед началом работы необходимо открыть смену на ККМ");
        Task task2 = new Task("Закрыть смену на ККМ", "Перед завершением работы необходимо закрыть смену на ККМ");
        Epic epic1 = new Epic("Провести инвентаризацию", "Проверка наличия имущества организации");
        SubTask subTask1_1 = new SubTask("Начать инвентаризацию", "Пересчет фактического наличия товара", epic1);
        SubTask subTask1_2 = new SubTask("Начать инвентаризацию", "Пересчет фактического наличия товара", epic1);
        Epic epic2 = new Epic("Принять товар", "Фактическое получение товара от экспедитора");
        SubTask subTask2_1 = new SubTask("Проверить товар", "Сверить количество товара по накладной с фактическим",
                epic2);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(epic1);
        taskManager.addTask(subTask1_1);
        taskManager.addTask(subTask1_2);
        taskManager.addTask(epic2);
        taskManager.addTask(subTask2_1);

        System.out.println("Список задач после добавления:\n");
        printTasks(taskManager);
        System.out.println();
        taskManager.getTaskById(1);
        taskManager.getTaskById(3);
        taskManager.getTaskById(7);
        taskManager.getTaskById(2);
        taskManager.getTaskById(4);
        taskManager.getTaskById(5);
        taskManager.getTaskById(6);
        System.out.println("История задач:\n");
        System.out.println(taskManager.getHistory());
        taskManager.clearTasks(TaskType.EPIC);
        System.out.println("История задач:\n");
        System.out.println(taskManager.getHistory());
    }

    public static void printTasks(TaskManager taskManager) {
        System.out.println(taskManager.getTasks(TaskType.TASK));
        System.out.println(taskManager.getTasks(TaskType.EPIC));
        System.out.println(taskManager.getTasks(TaskType.SUBTASK));
    }
}