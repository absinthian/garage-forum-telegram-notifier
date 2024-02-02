package dev.alex_anghel;

import io.quarkus.scheduler.Scheduled;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
@Path("/")
public class MessageController {

    @Inject
    TelegramBot bot;

    @Inject
    UpdateDifferences updateDifferences;
    static String initialPost;
    static List<String> initialPosts;

    @PostConstruct
    void init() {
        initialPost = updateDifferences.getPost();
        initialPosts = updateDifferences.getPosts();
    }

    @Scheduled(every = "60s")
    public void run() {
        message();
    }

    @Scheduled(every = "PT24H", delayed = "30s")
    public void run24h() {
        messagePosts();
    }

    public void message() {
        String message = updateDifferences.compareFirstPost();
        if (!message.contains("no updates")) {
            bot.sendMessage(message);
        }
    }

    public void messagePosts() {
        String message = updateDifferences.comparePosts();
        if (!message.contains("no updates")) {
            bot.sendMessage(message);
        }
    }

    @GET
    @Path("tb")
    public void testBot() {
        bot.sendMessage("test message at " + LocalDateTime.now().withNano(0));
    }

    @GET()
    @Path("first")
    public String getPost() {
        return MessageController.initialPost;
    }

    @GET()
    @Path("all")
    public List<String> getAllPosts() {
        return updateDifferences.getPosts();
    }

    @GET
    @Path("/temp")
    public String getTemperature() {
        try {
            // Run vcgencmd command to get temperature
            String[] parts = getTemperatureFromPi();
            if (parts.length == 2) {
                String temperatureValue = parts[1].replace("'", "");
                return "{\"temperature\":\"" + temperatureValue + "\"}";
            } else {
                return "{\"error\":\"Invalid temperature response\"}";
            }
        } catch (Exception e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    private String[] getTemperatureFromPi() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("vcgencmd", "measure_temp");
        Process process = processBuilder.start();

        // Capture output
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String temperatureStr = reader.readLine();
        reader.close();

        // Extract temperature from the output
        return temperatureStr.split("=");
    }

}
