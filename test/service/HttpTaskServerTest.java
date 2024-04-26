package service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import model.Task;
import model.Epic;
import model.SubTask;
import model.TaskType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    private TaskManager taskManager;
    private HttpTaskServer httpTaskServer;
    private Gson gson;

    @BeforeEach
    public void init() throws IOException {
        gson = Manager.getGson();
        taskManager = new InMemoryTaskManager();
        httpTaskServer = new HttpTaskServer(taskManager, 8080);
        httpTaskServer.start();
        Task task = new Task("Открыть смену на ККМ", "Перед началом работы необходимо открыть смену на ККМ");
        taskManager.addTask(task);
        Epic epic = new Epic("Провести инвентаризацию", "Проверка наличия имущества организации");
        taskManager.addTask(epic);
        SubTask subtask = new SubTask("Начать инвентаризацию", "Пересчет фактического наличия товара", epic);
        taskManager.addTask(subtask);
    }
    @AfterEach
    public void stopSerer() {
        httpTaskServer.stop();
    }

    @Test
    public void TaskHandlerTest() throws IOException, InterruptedException {
        String taskBody = gson.toJson(new Task("Закрыть смену на ККМ", "Перед завершением работы необходимо закрыть" +
                "смену на ККМ", 7, LocalDateTime.of(2022, 12, 30, 0, 30,
                2).plusDays(2)));

        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(taskManager.getTasks(TaskType.TASK).get(0))))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Задача не создалась");
        assertEquals(2, taskManager.getTasks(TaskType.TASK).size(), "Неверное количество задач");

        httpClient = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskBody))
                .build();

        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Задача не создалась");
        assertEquals(3, taskManager.getTasks(TaskType.TASK).size(), "Неверное количество задач");


        httpClient = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        response = httpClient.send(request, handler);
        assertEquals(200, response.statusCode(), "Список задач пуст");
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertNotNull(jsonElement, "Ответ от сервера не содержит данных");
        JsonArray asJsonArray = jsonElement.getAsJsonArray();
        Task loadTask = gson.fromJson(asJsonArray.get(1), Task.class);
        assertEquals(taskManager.getTasks(TaskType.TASK).get(1), loadTask, "Полученная задача не соответствует ожидаемой");
        assertEquals(taskManager.getTasks(TaskType.TASK).size(), asJsonArray.size(), "Неверное количество задач");



        uri = URI.create("http://localhost:8080/tasks/task/?id=1");
        httpClient = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .GET()
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Список задач пуст");
        jsonElement = JsonParser.parseString(response.body());
        assertNotNull(jsonElement, "Ответ от сервера не содержит данных");
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        loadTask = gson.fromJson(jsonObject, Task.class);
        assertEquals(taskManager.getTaskById(1), loadTask, "Полученная задача не соответствует созданной");

        httpClient = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Задача удалена");
        assertEquals(2, taskManager.getTasks(TaskType.TASK).size(), "Полученная задача не соответствует созданной");
        assertNull(taskManager.getTaskById(1), "Задача по id не удаленна");

        uri = URI.create("http://localhost:8080/tasks/task");
        httpClient = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Задачи удалены");
        assertEquals(0, taskManager.getTasks(TaskType.TASK).size(), "Задачи не очищены");
    }

    @Test
    public void EpicHandlerTest() throws IOException, InterruptedException {
        String epicBody = gson.toJson(new Epic("Принять товар", "Фактическое получение товара от экспедитора"));

        URI uri = URI.create("http://localhost:8080/tasks/epic");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(taskManager.getTaskById(2))))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Задача не создалась");
        assertEquals(2, taskManager.getTasks(TaskType.EPIC).size(), "Неверное количество задач");

        httpClient = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicBody))
                .build();

        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Задача не создалась");
        assertEquals(3, taskManager.getTasks(TaskType.EPIC).size(), "Неверное количество задач");


        httpClient = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .GET()
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Список задач пуст");
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertNotNull(jsonElement, "Ответ от сервера не содержит данных");
        JsonArray asJsonArray = jsonElement.getAsJsonArray();
        Epic loadEpic = gson.fromJson(asJsonArray.get(1), Epic.class);
        assertEquals(taskManager.getTasks(TaskType.EPIC).get(1), loadEpic, "Полученная задача не соответствует ожидаемой");
        assertEquals(taskManager.getTasks(TaskType.EPIC).size(), asJsonArray.size(), "Неверное количество задач");


        uri = URI.create("http://localhost:8080/tasks/epic/?id=2");
        httpClient = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .GET()
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Список задач пуст");
        jsonElement = JsonParser.parseString(response.body());
        assertNotNull(jsonElement, "Ответ от сервера не содержит данных");
        JsonObject asJsonObject = jsonElement.getAsJsonObject();
        loadEpic = gson.fromJson(asJsonObject, Epic.class);
        assertEquals(taskManager.getTaskById(2), loadEpic, "Полученная задача не соответствует созданной");

        httpClient = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Задача удалена");
        assertEquals(2, taskManager.getTasks(TaskType.EPIC).size(), "Полученная задача не соответствует созданной");
        assertNull(taskManager.getTaskById(2), "Задача по id не удаленна");

        uri = URI.create("http://localhost:8080/tasks/epic");
        httpClient = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Задачи удалены");
        assertEquals(0, taskManager.getTasks(TaskType.EPIC).size(), "Задачи не очищены");
    }

    @Test
    public void SubtaskHandlerTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Провести инвентаризацию", "Проверка наличия имущества организации");
        taskManager.addTask(epic);
        String subtaskBody = gson.toJson(new SubTask("Проверить товар", "Сверить количество товара по накладной с " +
                "фактическим количеством", epic, 1, LocalDateTime.now().plusMinutes(2)));

        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(taskManager.getTaskById(3))))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Задача не создалась");
        assertEquals(2, taskManager.getTasks(TaskType.SUBTASK).size(), "Неверное количество задач");

        httpClient = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskBody))
                .build();

        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Задача не создалась");
        assertEquals(3, taskManager.getTasks(TaskType.SUBTASK).size(), "Неверное количество задач");


        httpClient = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .GET()
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Список задач пуст");
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertNotNull(jsonElement, "Ответ от сервера не содержит данных");
        JsonArray asJsonArray = jsonElement.getAsJsonArray();
        SubTask loadSubtask = gson.fromJson(asJsonArray.get(1), SubTask.class);
        assertEquals(taskManager.getTasks(TaskType.SUBTASK).get(1), loadSubtask, "Полученная задача не соответствует " +
                "ожидаемой");
        assertEquals(taskManager.getTasks(TaskType.SUBTASK).size(), asJsonArray.size(), "Неверное количество задач");


        uri = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        httpClient = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .GET()
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Список задач пуст");
        jsonElement = JsonParser.parseString(response.body());
        assertNotNull(jsonElement, "Ответ от сервера не содержит данных");
        JsonObject asJsonObject = jsonElement.getAsJsonObject();
        loadSubtask = gson.fromJson(asJsonObject, SubTask.class);
        assertEquals(taskManager.getTaskById(3), loadSubtask, "Полученная задача не соответствует " +
                "созданной");

        uri = URI.create("http://localhost:8080/tasks/subtask/epic/?id=2");
        httpClient = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .GET()
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Список задач пуст");
        jsonElement = JsonParser.parseString(response.body());
        assertNotNull(jsonElement, "Ответ от сервера не содержит данных");
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        ArrayList<SubTask> listSubtask =  gson.fromJson(jsonArray, new TypeToken<ArrayList<SubTask>>(){}.getType());
        assertEquals(taskManager.getSubTasksOfEpicById(2).size(), listSubtask.size(), "Полученные" +
                "задачи не соответствуют Эпику");
        assertEquals(taskManager.getSubTasksOfEpicById(2), listSubtask, "Полученные задачи" +
                "не соответствуют Эпику");


        uri = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        httpClient = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Задача удалена");
        assertEquals(2, taskManager.getTasks(TaskType.SUBTASK).size(), "Полученная задача не соответствует созданной");
        assertNull(taskManager.getTaskById(3), "Задача по id не удаленна");

        uri = URI.create("http://localhost:8080/tasks/subtask");
        httpClient = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Задачи удалены");
        assertEquals(0, taskManager.getTasks(TaskType.SUBTASK).size(), "Задачи не очищены");
    }

    @Test
    public void historyHandlerTest() throws IOException, InterruptedException {
        taskManager.getTaskById(1);
        taskManager.getTaskById(3);
        taskManager.getTaskById(2);
        taskManager.getTaskById(1);

        URI uri = URI.create("http://localhost:8080/tasks/history");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "История пуста");
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertNotNull(jsonElement, "Ответ от сервера не содержит данных");
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        ArrayList<Task> listHistory = new ArrayList<>(gson.fromJson(jsonArray, new TypeToken<ArrayList<Task>>(){}.getType()));
        assertEquals(taskManager.getHistory().size(), listHistory.size(), "Неверное количество задач в истории");
        assertEquals(taskManager.getHistory().get(0).getId(), listHistory.get(0).getId(), "История задач не соответствует" +
                "ожидаемой");
        assertEquals(taskManager.getHistory().get(1).getId(), listHistory.get(1).getId(), "История задач не соответствует" +
                "ожидаемой");
        assertEquals(taskManager.getHistory().get(2).getId(), listHistory.get(2).getId(), "История задач не соответствует" +
                "ожидаемой");
    }

    @Test
    public void allTasksHandlerTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "История пуста");
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertNotNull(jsonElement, "Ответ от сервера не содержит данных");
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        ArrayList<Task> listTask = new ArrayList<>(gson.fromJson(jsonArray, new TypeToken<ArrayList<Task>>(){}.getType()));
        assertEquals(taskManager.getTasks(TaskType.TASK).size() + taskManager.getTasks(TaskType.EPIC).size() + taskManager
                        .getTasks(TaskType.TASK).size(), listTask.size(), "Неверное общее количество задач");
    }
}