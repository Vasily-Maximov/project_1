import server.HttpTaskServer;
import service.Manager;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Manager.getDefault(), 8080);
        httpTaskServer.start();
    }
}