package server;

import com.sun.net.httpserver.HttpServer;
import handler.TaskHandler;
import service.TaskManager;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private final int PORT;
    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager, int PORT) throws IOException {
        this.PORT = PORT;
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/", new TaskHandler(taskManager));
    }

    public void start() {
        httpServer.start();
        System.out.printf("HTTP-сервер запущен на %d порту!%n", PORT);
    }

    public void stop() {
        httpServer.stop(1);
        System.out.printf("HTTP-сервер остановлен на %d порту!%n", PORT);
    }
}