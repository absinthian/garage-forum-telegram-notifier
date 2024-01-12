package dev.alex_anghel;

import io.quarkus.scheduler.Scheduled;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class MessageCalculator {

    @Inject
    TelegramBot bot;

    @Inject
    ListDiff listDiff;

    @Scheduled(every = "60s")
    public void message() {
        List<String> initialPosts = listDiff.getPosts();
        String message = listDiff.extract(initialPosts);
        if (!message.contains("no updates")) {
            bot.sendMessage(message);
        }
    }

}
