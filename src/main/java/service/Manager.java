package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.KVServer;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;

public class Manager {

    public static TaskManager getDefault() {
        try {
            return new HttpTaskManager(new URL("http://localhost:8078"));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static KVServer getDefaultKVServer()  {
        try {
            return new KVServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        //gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        return gsonBuilder.create();
    }
}