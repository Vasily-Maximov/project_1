package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.time.LocalDateTime;

public class Manager {

    public static TaskManager getDefault() {
        return FileBackedTasksManager.loadFromFile(new File("resources/backup.csv"));
    }

    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        return gsonBuilder.create();
    }
}