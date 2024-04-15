import model.Task;
import model.Epic;
import model.SubTask;
import model.TaskStatus;
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

        System.out.println("Список задач после загрузки:\n");
        printTasks(taskManager);
        System.out.println();

        subTask2_1.setTaskStatus(TaskStatus.DONE);
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
        taskManager.getTaskById(1);
        System.out.println("История задач:\n");
        System.out.println(taskManager.getHistory());
        taskManager.delTaskById(5);
        taskManager.delTaskById(6);
        System.out.println("История задач:\n");
        System.out.println(taskManager.getHistory());


        ///////////////////////////////////////////////////////////////////////////////////////////////////

        /*InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("новый эпик 1", "описание эпика 1");
        manager.addTask(epic);
        //Данная задача должна быть 4 по счёту
        SubTask subtask1 = new SubTask("новая подзадача 1", "описание подзадачи 1", epic,
                15,  LocalDateTime.of(2022, 12, 30, 0, 30).plusDays(2));
        manager.addTask(subtask1);
        //Данная задача должна быть 1 по счёту
        SubTask subtask2 = new SubTask("новая подзадача 2", "описание подзадачи 2", epic,
                30, LocalDateTime.of(2022, 12, 30, 0, 30) );
        manager.addTask(subtask2);
        //Данная задача должна быть 3 по счёту
        SubTask subtask3 = new SubTask("новая подзадача 3", "описание подзадачи 3", epic,
                45, LocalDateTime.of(2022, 12, 30, 0, 30).plusDays(1));
        manager.addTask(subtask3);
        //Данная задача должна быть 2 по счёту
        SubTask subtask4 = new SubTask("новая подзадача 4", "описание подзадачи 4", epic,
                60, LocalDateTime.of(2022, 12, 30, 0, 30).plusHours(12));
        manager.addTask(subtask4);
        //Порядок добавления следующий subtask1 -> subtask2 -> subtask3 -> subtask4
        //Порядок следующий subtask2 -> subtask4 -> subtask3 -> subtask1
        System.out.println(epic.getDuration() + " == " + (subtask1.getDuration() + subtask2.getDuration() + subtask3.getDuration()
                + subtask4.getDuration()) + " " + (epic.getDuration() == (subtask1.getDuration() + (subtask2.getDuration()
                + (subtask3.getDuration()) + subtask4.getDuration()))));
        System.out.println(epic.getStartTime() + " == " + subtask2.getStartTime() + " " + epic.getStartTime().equals(subtask2
                .getStartTime()));
        System.out.println(epic.getEndTime() + " == " + subtask1.getEndTime() + " " + epic.getEndTime().equals(subtask1.getEndTime()));
        System.out.println(manager.getPrioritizedTasks().stream().map(AbstractTask::getName).collect(Collectors.toList()));
        //Удаление подзадачи subtask3
        manager.delTaskById(subtask3.getId());
        //Новый порядок subtask2 -> subtask4 -> subtask1
        System.out.println(epic.getDuration() + " == " + (subtask1.getDuration() + subtask2.getDuration() + subtask4.getDuration())
                + " " + (epic.getDuration() == (subtask1.getDuration() + (subtask2.getDuration()) + (subtask4.getDuration()))));
        System.out.println(epic.getStartTime() + " == " + subtask2.getStartTime() + " " + epic.getStartTime().equals(subtask2
                .getStartTime()));
        System.out.println(epic.getEndTime() + " == " + subtask1.getEndTime() + " " + epic.getEndTime().equals(subtask1.getEndTime()));
        System.out.println(manager.getPrioritizedTasks().stream().map(AbstractTask::getName).collect(Collectors.toList()));
        //Удаление подзадачи subtask2
        manager.delTaskById(subtask2.getId());
        //Новый порядок subtask4 -> subtask1
        System.out.println(epic.getDuration() + " == " + (subtask1.getDuration() + subtask4.getDuration())  + " " + (epic
                .getDuration() == (subtask1.getDuration() + subtask4.getDuration())));
        System.out.println(epic.getStartTime() + " == " + subtask4.getStartTime() + " " + epic.getStartTime().equals(subtask4
                .getStartTime()));
        System.out.println(epic.getEndTime() + " == " + subtask1.getEndTime() + " " + epic.getEndTime().equals(subtask1.getEndTime()));
        System.out.println(manager.getPrioritizedTasks().stream().map(AbstractTask::getName).collect(Collectors.toList()));
        //Удаление подзадачи subtask1
        manager.delTaskById(subtask1.getId());
        //Новый порядок subtask4
        System.out.println(epic.getDuration() + " == " + subtask4.getDuration()  + " " + (epic.getDuration() == (subtask4
                .getDuration())));
        System.out.println(epic.getStartTime() + " == " + subtask4.getStartTime() + " " + epic.getStartTime().equals(subtask4
                .getStartTime()));
        System.out.println(epic.getEndTime() + " == " + subtask4.getEndTime() + " " + epic.getEndTime().equals(subtask4.getEndTime()));
        System.out.println(manager.getPrioritizedTasks().stream().map(AbstractTask::getName).collect(Collectors.toList()));*/
    }

    public static void printTasks(TaskManager taskManager) {
        System.out.println(taskManager.getTasks(TaskType.TASK));
        System.out.println(taskManager.getTasks(TaskType.EPIC));
        System.out.println(taskManager.getTasks(TaskType.SUBTASK));
    }
}