package service;

import exeption.ManagerSaveException;
import model.AbstractTask;
import model.TaskType;
import model.Task;
import model.Epic;
import model.SubTask;
import model.TaskStatus;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private File file;

    private static final String HEADLINE = "id,type,name,status,description,epic,duration,startTime";

    private static final String DELIMITER = ",";

    public FileBackedTasksManager() {
    }

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            String line;
            boolean isRestoreHistory = false;
            while (reader.ready()) {
                line = reader.readLine();
                if (!line.isBlank() && !isRestoreHistory) {
                    AbstractTask task = fileBackedTasksManager.fromString(line);
                    if (task != null) {
                        fileBackedTasksManager.addTaskToManager(task);
                    }
                } else if (!line.isBlank() && isRestoreHistory) {
                    List<Integer> idTasks = historyFromString(line);
                    if (!idTasks.isEmpty()) {
                        fileBackedTasksManager.addTaskToHistory(idTasks);
                    }
                } else {
                    isRestoreHistory = true;
                }
            }
            return fileBackedTasksManager;
        } catch (IOException exception) {
            throw new ManagerSaveException(String.format("Ошибка при открытии файла: %s", file.getName()));
        }
    }

    public void save() {
        try (FileWriter fileWriter = new FileWriter(file)) {
            StringBuilder stringBuilder = new StringBuilder(HEADLINE + System.lineSeparator());
            for (Map.Entry<Integer, AbstractTask> entry: getAllTasks().entrySet()) {
                stringBuilder.append(toString(entry.getValue()));
            }
            stringBuilder.append(System.lineSeparator());
            stringBuilder.append(historyToString(historyManager));
            fileWriter.write(stringBuilder.toString());
        } catch (IOException e) {
            throw new ManagerSaveException(String.format("Ошибка при сохранении сущностей и истории в файл: %s", file.getName()));
        }
    }

    private String toString(AbstractTask task) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(task.getId()).append(DELIMITER);
        stringBuilder.append(task.getTaskType()).append(DELIMITER);
        stringBuilder.append(task.getName()).append(DELIMITER);
        stringBuilder.append(task.getTaskStatus()).append(DELIMITER);
        stringBuilder.append(task.getDescription()).append(DELIMITER);
        if (task.getTaskType() == TaskType.SUBTASK) {
            stringBuilder.append(((SubTask)task).getEpicId()).append(DELIMITER);
        } else {
            stringBuilder.append("null").append(DELIMITER);
        }
        stringBuilder.append(task.getDuration()).append(DELIMITER);
        stringBuilder.append(task.getStartTime());
        return stringBuilder.append(System.lineSeparator()).toString();
    }

    private AbstractTask fromString(String value) {
        String[] fields = value.split(",");
        TaskType taskType = TaskType.valueOf(fields[1]);
        String name = fields[2];
        String description = fields[4];
        long duration = Long.parseLong(fields[6]);
        LocalDateTime startTime;
        try {
            startTime = LocalDateTime.parse(fields[7]);
        } catch (DateTimeException exception) {
            startTime = null;
        }
        AbstractTask task;
        switch (taskType) {
            case TASK:
                task = new Task(name, description, duration, startTime);
                break;
            case SUBTASK:
                task = new SubTask(name, description, (Epic) getAllTasks().get(Integer.parseInt(fields[5])), duration, startTime);
                break;
            case EPIC:
                task = new Epic(name, description);
                break;
            default:
                return null;
        }
        task.setTaskStatus(TaskStatus.valueOf(fields[3]));
        task.setId(Integer.parseInt(fields[0]));
        return task;
    }

    private static String historyToString(HistoryManager manager) {
        StringBuilder stringBuilder = new StringBuilder();
        List<AbstractTask> history = manager.getHistory();
        if (!history.isEmpty()) {
            stringBuilder.append(history.get(0).getId());
            for (int i = 1; i < history.size(); i++) {
                stringBuilder.append(",");
                stringBuilder.append(history.get(i).getId());
            }
        }
        return stringBuilder.toString();
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> idTasks = new ArrayList<>();
        for (String id : value.split(",")) {
            idTasks.add(Integer.parseInt(id));
        }
        return idTasks;
    }

    private void addTaskToHistory(List<Integer> idTasks) {
        for (Integer id : idTasks) {
            historyManager.add(getTaskById(id));
        }
    }

    private void addTaskToManager(AbstractTask task) {
        int idTask = task.getId();
        if (task.getTaskType() == TaskType.SUBTASK) {
            updateTask(task);
        }
        getAllTasks().put(idTask, task);
        generatorId = Math.max(generatorId, idTask);
    }

    @Override
    public void addTask(AbstractTask task) {
        super.addTask(task);
        save();
    }

    @Override
    public List<AbstractTask> getTasks(TaskType taskType) {
        return super.getTasks(taskType);
    }

    @Override
    public Map<Integer, AbstractTask> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public void clearTasks(TaskType taskType) {
        super.clearTasks(taskType);
        save();
    }

    @Override
    public AbstractTask getTaskById(Integer idTask) {
        AbstractTask abstractTask = super.getTaskById(idTask);
        save();
        return abstractTask;
    }

    @Override
    public void delTaskById(Integer idTask) {
        super.delTaskById(idTask);
        save();
    }

    @Override
    public void updateTask(AbstractTask task) {
        super.updateTask(task);
        save();
    }

    @Override
    public List<AbstractTask> getSubTasksOfEpicById(Integer taskId) {
        return super.getSubTasksOfEpicById(taskId);
    }

    @Override
    public List<AbstractTask> getHistory() {
        return super.getHistory();
    }
}