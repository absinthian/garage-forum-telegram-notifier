package dev.alex_anghel;

import io.quarkus.scheduler.Scheduled;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
@Path("/")
public class MessageCalculator {

    @Inject
    TelegramBot bot;

    @Inject
    ListDiff listDiff;
    static String initialPost;
    static List<String> initialPosts;

    @PostConstruct
    void init() {
        initialPost = listDiff.getPost();
        initialPosts = listDiff.getPosts();
    }

    @Scheduled(every = "60s")
    public void run() {
        message();
//        messagePosts();
    }

    public String message() {
        String message = listDiff.compareFirstPost();
        if (!message.contains("no updates")) {
            bot.sendMessage(message);
        }
        return message;
    }

    public String messagePosts() {
        String message = listDiff.comparePosts();
        if (!message.contains("no updates")) {
            bot.sendMessage(message);
        }
        return message;
    }

    @GET
    @Path("tb")
    public void testBot() {
        bot.sendMessage("test message at " + LocalDateTime.now().withNano(0));
    }

    @GET()
    @Path("first")
    public String getPost() {
        return MessageCalculator.initialPost;
    }

    @GET()
    @Path("all")
    public List<String> getAllPosts() {
        return listDiff.getPosts();
    }

}
