package dev.alex_anghel;

import io.quarkus.scheduler.Scheduled;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

@ApplicationScoped
@Path("/")
public class MessageCalculator {

    @Inject
    TelegramBot bot;

    @Inject
    ListDiff listDiff;
    String initialPost;
    List<String> initialPosts;

    @PostConstruct
    void getInitialPost() {
        initialPost = listDiff.getPost();
        initialPosts = listDiff.getPosts();
    }

    @Scheduled(every = "10s")
    public void run() {
//        message();
        messagePosts();
    }

//    public String message() {
//        String message = listDiff.compareFirstPost(initialPost);
//        if (!message.contains("no updates")) {
//            bot.sendMessage(message);
//        }
//        return message;
//    }

    public String messagePosts() {
        String message = listDiff.comparePosts(initialPosts);
        if (!message.contains("no updates")) {
            bot.sendMessage(message);
        }
        return message;
    }

//    @GET()
//    @Path("info")
//    public String getPost() {
//        return initialPost;
//    }

    @GET()
    @Path("all")
    public List<String> getAllPosts() {
        return listDiff.getPosts();
    }

}
