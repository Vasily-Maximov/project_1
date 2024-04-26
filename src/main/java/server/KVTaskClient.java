package server;

import exeption.ManagerSaveException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    protected final HttpClient httpClient;
    private final String API_TOKEN;
    private final URL url;

    public KVTaskClient(URL url) throws IOException, InterruptedException {
        this.url = url;
        httpClient = HttpClient.newHttpClient();
        URI uri = URI.create(url.toString() + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Content-Type", "application/json")
                .uri(uri)
                .build();
        HttpResponse<String> responseApiToken = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (responseApiToken.statusCode() == 200) {
            API_TOKEN = responseApiToken.body();
        } else {
            System.out.println("API_TOKEN не получен. Сервер вернул код состояния: " + responseApiToken.statusCode());
            throw new ManagerSaveException("API_TOKEN не получен");
        }
    }

    public String getAPI_TOKEN() {
        return API_TOKEN;
    }

    public void put(String key, String json) {
        URI taskUri = URI.create(url.toString() + "/save/" + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(taskUri)
                .build();
        try {
            HttpResponse<String> responseApiToken = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (responseApiToken.statusCode() != 200) {
                System.out.println("Данные не сохранены. Сервер вернул код состояния: " + responseApiToken.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String load(String key) {
        URI taskUri = URI.create(url.toString() + "/load/" + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(taskUri)
                .header("Accept", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Данные не загружены. Сервер вернул код состояния: " + response.statusCode());
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException();
        }
    }
}