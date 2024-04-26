import server.HttpTaskServer;
import server.KVServer;
import service.Manager;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        KVServer kvServer = Manager.getDefaultKVServer();
        kvServer.start();
        HttpTaskServer httpTaskServer = new HttpTaskServer(Manager.getDefault(), 8080);
        httpTaskServer.start();
    }
}