package dev.alex_anghel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class UpdateDifferences {
    final Logger LOG = LogManager.getLogger(UpdateDifferences.class);
    Document doc;

    public String compareFirstPost() {
        String currentPost = getPost();
        String response = LocalDateTime.now().withNano(0) + " " + "no updates";

        if (!currentPost.equals(MessageController.initialPost)) {
            response = LocalDateTime.now().withNano(0) + " " + currentPost;
            LOG.info(response);
            MessageController.initialPost = currentPost;
        }
//        else {
//            LOG.info(response);
//        }
        return response;
    }

    public String comparePosts() {
        List<String> listCurrent = getPosts();

//        List<String> differences;
        String response;
//        response = LocalDateTime.now().withNano(0) + " no updates";

//        if (!listCurrent.equals(MessageCalculator.initialPosts)) {
//            differences = new ArrayList<>(listCurrent);
//            differences.removeAll(MessageCalculator.initialPosts);
            response = LocalDateTime.now().withNano(0) + " " + String.join(",\n\n", listCurrent.subList(4, listCurrent.size()));
            LOG.info(response);
            MessageController.initialPosts = listCurrent;
//        }
        return response;
    }

    public String getPost() {
        try {
            // fetching the target website
            doc = Jsoup
                    .connect("https://www.mygarage.ro/vanzari")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                    .referrer("https://www.google.com")
                    .get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return doc.selectXpath("/html/body/div[2]/div/div/div/form[1]/table[3]/tbody[2]/tr[6]/td[3]/div[1]/a[1]").attr("href");
    }

    public List<String> getPosts() {
        List<String> currentPosts = new ArrayList<>();

        try {
            // fetching the target website
            doc = Jsoup
                    .connect("https://www.mygarage.ro/vanzari")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                    .referrer("https://www.google.com")
                    .ignoreContentType(true)
                    .get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Elements nodes = doc.select("#threadbits_forum_35").get(0).selectXpath("//td[starts-with(@id,'td_threadtitle_')]");
        for (Element e : nodes) {
            currentPosts.add(e.getElementsByAttributeStarting("href").last().attribute("href").getValue());
        }
        return currentPosts;
    }
}
