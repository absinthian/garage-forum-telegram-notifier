package dev.alex_anghel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ListDiff {
    final Logger LOG = LoggerFactory.getLogger(ListDiff.class);
    Document doc;

    @PostConstruct
    void getPage() {
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
    }

    public String compareFirstPost(String initialPost) {
        String currentPost = getPost();
        String response = addTimeToResponseMessage("no updates");

        if (currentPost.equals(initialPost)) {
            LOG.info(response);
        } else {
            response = addTimeToResponseMessage(currentPost);
            LOG.info(response);
        }
        return response;
    }

    private static String addTimeToResponseMessage(String message) {
        return LocalDateTime.now().withNano(0) + " " + message;
    }

    public String comparePosts(List<String> listOld) {
        List<String> listCurrent = getPosts();

        List<String> differences;
        String response = LocalDateTime.now().withNano(0) + " no updates";

        if (!listCurrent.equals(listOld)) {
            differences = new ArrayList<>(listCurrent);
            differences.removeAll(listOld);
            response = LocalDateTime.now().withNano(0) + " " + differences;
            LOG.info(response);
        }
        return response;
    }

    public String getPost() {
        return doc.selectXpath("/html/body/div[2]/div/div/div/form[1]/table[3]/tbody[2]/tr[6]/td[3]/div[1]/a[1]").attr("href");
    }

    public List<String> getPosts() {
        List<String> currentPosts = new ArrayList<>();

        Elements nodes = doc.select("#threadbits_forum_35").get(0).selectXpath("//td[starts-with(@id,'td_threadtitle_')]");
        for (Element e : nodes) {
            currentPosts.add(e.getElementsByAttributeStarting("href").last().attribute("href").getValue());
        }
        return currentPosts;
    }
}
