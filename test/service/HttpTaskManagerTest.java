package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import server.KVServer;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private final KVServer kvServer;
    private final String API_TOKEN;
    private final Gson gson;

    public HttpTaskManagerTest() throws IOException, InterruptedException {
        gson = Manager.getGson();
        kvServer = Manager.getDefaultKVServer();
        kvServer.start();
        taskManager =  new HttpTaskManager(new URL("http://localhost:8078"));
        API_TOKEN = taskManager.getKvTaskClient().getAPI_TOKEN();
    }

    @AfterEach
    public void stopSerer() {
        kvServer.stop();
    }

    @Test
    public void saveTest() {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            URI uri = URI.create("http://localhost:8078/load/tasks?API_TOKEN=" + API_TOKEN);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            ArrayList<Task> listTask = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {}.getType());
            assertNotNull(listTask, "Задачи не возвратились");
            assertEquals(2, listTask.size(), "Не верное количество задач");
            assertEquals(taskManager.getTaskById(1), listTask.get(0), "Задачи не совпадают");

            uri = URI.create("http://localhost:8078/load/subtasks?API_TOKEN=" + API_TOKEN);
            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            ArrayList<SubTask> listSubtask = gson.fromJson(response.body(), new TypeToken<ArrayList<SubTask>>() {
            }.getType());
            assertNotNull(listSubtask, "Задачи не возвратились");
            assertEquals(3, listSubtask.size(), "Не верное количество задач");
            assertEquals(taskManager.getTaskById(4), listSubtask.get(0), "Задачи не совпадают");

            uri = URI.create("http://localhost:8078/load/epics?API_TOKEN=" + API_TOKEN);
            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            ArrayList<Epic> listEpic = gson.fromJson(response.body(), new TypeToken<ArrayList<Epic>>() {
            }.getType());
            assertNotNull(listEpic, "Задачи не возвратились");
            assertEquals(2, listEpic.size(), "Не верное количество задач");
            assertEquals(taskManager.getTaskById(3), listEpic.get(0), "Задачи не совпадают");

            taskManager.getTaskById(1);
            taskManager.getTaskById(2);
            taskManager.getTaskById(3);
            taskManager.getTaskById(1);
            uri = URI.create("http://localhost:8078/load/history?API_TOKEN=" + API_TOKEN);
            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            ArrayList<Task> listHistory = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
            }.getType());
            assertNotNull(listHistory, "Задачи не возвратились");
            assertEquals(4, listHistory.size(), "Не верное количество задач");
            assertEquals(taskManager.getHistory().get(0).getId(), listHistory.get(0).getId(), "Задачи не совпадают");
            assertEquals(taskManager.getHistory().get(1).getId(), listHistory.get(1).getId(), "Задачи не совпадают");
            assertEquals(taskManager.getHistory().get(2).getId(), listHistory.get(2).getId(), "Задачи не совпадают");
        } catch (IOException | InterruptedException e ) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadFromKvServer() throws IOException, InterruptedException {
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);
        taskManager.getTaskById(1);
        HttpTaskManager loadedTaskManager = new HttpTaskManager(new URL("http://localhost:8078"));
        assertEquals(taskManager.getTasks(TaskType.TASK).size(), loadedTaskManager.getTasks(TaskType.TASK).size(),
                "Неверное количество загруженных задач");
        assertEquals(taskManager.getTasks(TaskType.TASK).size(), loadedTaskManager.getTasks(TaskType.TASK).size(),
                "Неверное количество загруженных задач");
        assertEquals(taskManager.getTasks(TaskType.EPIC).size(), loadedTaskManager.getTasks(TaskType.EPIC).size(),
                "Неверное количество загруженных задач");
        assertEquals(taskManager.getTasks(TaskType.SUBTASK).size(), loadedTaskManager.getTasks(TaskType.SUBTASK).size(),
                "Неверное количество загруженных задач");
        assertEquals(taskManager.getHistory().size(), loadedTaskManager.getHistory().size(), "Неверный список истории");
        assertEquals(taskManager.getHistory().get(0), loadedTaskManager.getHistory().get(0), "Неверный список истории");
        assertEquals(taskManager.getHistory().get(1), loadedTaskManager.getHistory().get(1), "Неверный список истории");
        assertEquals(taskManager.getHistory().get(2), loadedTaskManager.getHistory().get(2), "Неверный список истории");
        assertEquals(taskManager.getPrioritizedTasks().size(), loadedTaskManager.getPrioritizedTasks().size(),
                "Неверное количество задач в списке");
    }
}