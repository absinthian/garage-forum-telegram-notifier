package dev.alex_anghel;

import io.quarkus.scheduler.Scheduled;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

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

//    @Scheduled(every = "PT24H", delayed = "30s")
//    public void run24h() {
//        messagePosts();
//    }

    @Scheduled(every = "PT2H")
    public void runNas() {
        bot.sendMessage(getNas());
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
    @Path("nas")
    public String getNas() {
        Document doc;
        String product;
        try {
            // fetching the target website
            product = "https://www.emag.ro/network-attached-storage-synology-diskstation-cu-procesor-intel-celeron-j4125-2ghz-2-bay-2gb-ddr4-ds224/pd/D9ZY6YYBM/";
            doc = Jsoup.connect(product).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36").referrer("https://www.google.com").get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Elements price = doc.selectXpath("//*[@id=\"main-container\"]/section[3]/div/div[1]/div[2]/div/div[2]/div[2]/form/div/div[1]/div[1]/div/div/div[1]/p[2]");
        Elements priceResigilat = doc.getElementsByClass("product-new-price has-deal");
        if (priceResigilat.toString().isEmpty()) {
            return product + " pret intreg: " + price.text() + "\n nu are resigilate";
        }
        return product + " pret intreg: " + price.text() + "\n resigilat la \n" + String.join("\n", priceResigilat.text().split(" Lei"));
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
