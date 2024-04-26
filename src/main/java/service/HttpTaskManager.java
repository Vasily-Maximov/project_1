package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskType;
import server.KVTaskClient;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient;
    private final Gson gson;
    private final URL url;

    public HttpTaskManager(URL urlKvServer) throws IOException, InterruptedException {
        this.url = urlKvServer;
        kvTaskClient = new KVTaskClient(url);
        gson = Manager.getGson();
        loadFromKvServer();
    }

    public KVTaskClient getKvTaskClient() {
        return kvTaskClient;
    }

    @Override
    public void save() {
        String tasksBody = gson.toJson(getTasks(TaskType.TASK));
        kvTaskClient.put("tasks", tasksBody);
        String epicsBody = gson.toJson(getTasks(TaskType.EPIC));
        kvTaskClient.put("epics", epicsBody);
        String subtaskBody = gson.toJson(getTasks(TaskType.SUBTASK));
        kvTaskClient.put("subtasks", subtaskBody);
        String historyBody = gson.toJson(getHistory());
        kvTaskClient.put("history", historyBody);
    }

    public void loadFromKvServer() {
        String responseBodyTasks = kvTaskClient.load("tasks");
        String responseBodyEpics = kvTaskClient.load("epics");
        String responseBodySubtasks = kvTaskClient.load("subtasks");
        String responseBodyHistory = kvTaskClient.load("history");
        if (!responseBodyTasks.isBlank()) {
            ArrayList<Task> listTask =  gson.fromJson(responseBodyTasks, new TypeToken<ArrayList<Task>>(){}.getType());
            for (Task value : listTask) {
                getAllTasks().put(value.getId(), value);
                getTasksTreeSet().add(value);
            }
        }
        if (!responseBodyTasks.isBlank()) {
            ArrayList<Epic> listEpic =  gson.fromJson(responseBodyEpics, new TypeToken<ArrayList<Epic>>(){}.getType());
            for (Epic value : listEpic) {
                getAllTasks().put(value.getId(), value);
            }
        }
        if (!responseBodyTasks.isBlank()) {
            ArrayList<SubTask> listSubtask =  gson.fromJson(responseBodySubtasks, new TypeToken<ArrayList<SubTask>>(){}.getType());
            for (SubTask value : listSubtask) {
                getAllTasks().put(value.getId(), value);
                getTasksTreeSet().add(value);
            }
        }
        if (!responseBodyTasks.isBlank()) {
            ArrayList<Task> listHistory = new ArrayList<>(gson.fromJson(responseBodyHistory, new TypeToken<ArrayList<Task>>(){}
                    .getType()));
            for (Task element: listHistory) {
                historyManager.add(getAllTasks().get(element.getId()));
                historyManager.add(getAllTasks().get(element.getId()));
                historyManager.add(getAllTasks().get(element.getId()));
            }
        }
    }
}