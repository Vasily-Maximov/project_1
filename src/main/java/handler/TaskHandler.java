package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exeption.HttpException;
import exeption.ManagerSaveException;
import model.*;
import server.HttpMethod;
import server.HttpUri;
import service.Manager;
import service.TaskManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TaskHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;
    private int taskId;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = Manager.getGson();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.printf("Началась обработка %s запроса от клиента.%n", httpExchange.getRequestURI());
        try {
            HttpMethod httpMethod = getHttpMethod(httpExchange.getRequestMethod());
            HttpUri httpUri = getHttpUri(httpExchange.getRequestURI().toString());
            String lineResponse = "";
            switch (httpMethod) {
                case GET:
                    lineResponse = apiGet(httpUri);
                    break;
                case POST:
                    lineResponse = apiPost(httpExchange, httpUri);
                    break;
                case DELETE:
                    lineResponse = apiDelete(httpUri);
                    break;
            }
            writeResponse(httpExchange, lineResponse, 200);
            System.out.printf("Завершилась обработка %s запроса от клиента%n", httpExchange.getRequestURI());
        } catch (HttpException exception) {
            writeResponse(httpExchange, exception.getMessage(), 405);
        }
        httpExchange.close();
    }

    private String apiGet(HttpUri httpUri) {
        String response = "";
        switch (httpUri) {
            case URI_ALL_TASKS:
                response = responseFromApiGet(taskManager.getAllTasks().values(), "Список задач пуст");
                break;
            case URI_TASKS:
                response = responseFromApiGet(taskManager.getTasks(TaskType.TASK), "Список задач пуст");
                break;
            case URI_TASK_ID:
                response = responseFromApiGetByIdTask(taskManager.getTaskById(taskId), String.format("Не найдена задача по id = %d",
                        taskId));
                break;
            case URI_EPICS:
                response = responseFromApiGet(taskManager.getTasks(TaskType.EPIC), "Список епиков пуст");
                break;
            case URI_EPIC_ID:
                response = responseFromApiGetByIdTask(taskManager.getTaskById(taskId), String.format("Не найден епик по id = %d",
                        taskId));
                break;
            case URI_SUBTASKS:
                response = responseFromApiGet(taskManager.getTasks(TaskType.SUBTASK), "Список задач пуст");
                break;
            case URI_SUBTASK_ID:
                response = responseFromApiGetByIdTask(taskManager.getTaskById(taskId), String.format("Не найдена подзадача по id = %d",
                        taskId));
                break;
            case URI_SUBTASKS_EPIC_ID:
                response = responseFromApiGet(taskManager.getSubTasksOfEpicById(taskId), String.format("Не найдена подзадача по eпику " +
                        "c id = %d", taskId));
                break;
            case URI_HISTORY:
                response = responseFromApiGet(taskManager.getHistory(), "Список истории пуст");
                break;
        }
        return response;
    }

    private String apiPost(HttpExchange httpExchange, HttpUri httpUri) throws IOException{
        String response = "";
        switch (httpUri) {
            case URI_TASKS:
                response = responseFromApiPost(httpExchange, TaskType.TASK);
                break;
            case URI_EPICS:
                response = responseFromApiPost(httpExchange, TaskType.EPIC);
                break;
            case URI_SUBTASKS:
                response = responseFromApiPost(httpExchange, TaskType.SUBTASK);
                break;
        }
        return response;
    }

    private String apiDelete(HttpUri httpUri) {
        String response = "";
        switch (httpUri) {
            case URI_ALL_TASKS:
                taskManager.clearTasks(TaskType.TASK);
                taskManager.clearTasks(TaskType.EPIC);
                taskManager.clearTasks(TaskType.SUBTASK);
                response =  "Список всех задач очищен";
                break;
            case URI_TASKS:
                taskManager.clearTasks(TaskType.TASK);
                response = "Список задач очищен";
                break;
            case URI_EPICS:
                taskManager.clearTasks(TaskType.EPIC);
                response = "Список задач очищен";
                break;
            case URI_SUBTASKS:
                taskManager.clearTasks(TaskType.SUBTASK);
                response = "Список задач очищен";
                break;
            case URI_TASK_ID:
            case URI_EPIC_ID:
            case URI_SUBTASK_ID:
                response = responseFromDeleteByIdTask(taskManager.getTaskById(taskId));
                break;
        }
        return response;
    }

    private String responseFromApiGet(Collection<AbstractTask> tasks, String message) {
        String response;
        if (tasks.isEmpty()) {
            response = message;
        } else {
            response = gson.toJson(tasks);
        }
        System.out.println(response);
        return response;
    }

    private String responseFromApiGetByIdTask(AbstractTask task, String message) {
        String response;
        if (task == null) {
            response = message;
        } else {
            response = gson.toJson(task);
        }
        System.out.println(response);
        return response;
    }

    private String responseFromDeleteByIdTask(AbstractTask task) {
        String response;
        if (task == null) {
            response = String.format("Не найдена задача по id = %d", taskId);
        } else {
            taskManager.delTaskById(taskId);
            response = String.format("Задача удалена по id = %d", taskId);
        }
        return response;
    }

    private String responseFromApiPost(HttpExchange httpExchange, TaskType taskType) throws IOException {
        String response;
        AbstractTask task = null;
        Map<String, List<String>> requestHeaders = httpExchange.getRequestHeaders();
        List<String> listValue = requestHeaders.get("Content-type");
        if ((!listValue.isEmpty()) && (listValue.contains("application/json"))) {
            InputStream requestBody = httpExchange.getRequestBody();
            String stringRequestBody = new String(requestBody.readAllBytes(), DEFAULT_CHARSET);
            try {
                switch (taskType) {
                    case TASK:
                        task = gson.fromJson(stringRequestBody, Task.class);
                        break;
                    case EPIC:
                        task = gson.fromJson(stringRequestBody, Epic.class);
                        break;
                    case SUBTASK:
                        task = gson.fromJson(stringRequestBody, SubTask.class);
                        break;
                }
                try {
                    taskManager.addTask(task);
                    response = String.format("Задача успешно добавлена\n%s", gson.toJson(task));
                } catch (ManagerSaveException e) {
                    response = String.format("Задача успешно добавлена\n%s\n%s", gson.toJson(task), e.getMessage());
                }
            } catch (JsonSyntaxException exception) {
                response = "Получен не корректный формат обмена json";
            }
        } else {
            response = "Формат для обмена не указан или указан неверный, необходим формат json";
        }
        return response;
    }

    private HttpMethod getHttpMethod(String httpMethod) {
        try {
            switch (HttpMethod.valueOf(httpMethod)) {
                case GET:
                    return HttpMethod.GET;
                case POST:
                    return HttpMethod.POST;
                case DELETE:
                    return HttpMethod.DELETE;
                default:
                    throw new HttpException(String.format("Ожидали метод GET, POST, DELETE, а получили: %s", httpMethod));
            }
        } catch (IllegalArgumentException exception) {
            throw new HttpException(String.format("Ожидали метод GET, POST, DELETE, а получили: %s", httpMethod));
        }
    }

    private void setTaskIdFromRequest(String id) {
        try {
            taskId = Integer.parseInt(id);
        } catch (NumberFormatException exception) {
            throw new HttpException(String.format("Ошибка в id задачи, ожидали число, а получили: %s", id));
        }
    }

    private HttpUri getHttpUri(String httpUri) {
        final String uri;
        String[] pathParts = httpUri.split("'?id=");
        if (pathParts.length > 1) {
            uri = pathParts[0];
            setTaskIdFromRequest(pathParts[1]);
        } else {
            uri = httpUri;
        }
        return Arrays.stream(HttpUri.values()).filter(httpUri1 -> httpUri1.getEndpointName().equals(uri)).findFirst()
                .orElseThrow(() -> new HttpException(String.format("Переданный URI = %s, не соответствует ожидаемому", httpUri)));
    }

    private void writeResponse(HttpExchange exchange, String responseBody, int responseCode) throws IOException {
        exchange.sendResponseHeaders(responseCode, 0);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(responseBody.getBytes(DEFAULT_CHARSET));
        }
    }
}