package dev.alex_anghel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ListDiff {
    Logger LOG = LoggerFactory.getLogger(ListDiff.class);

    public String extract(List<String> listOld) {
        List<String> listCurrent = getPosts();
        List<String> differences;
        String response = LocalDateTime.now().withNano(0) + " no updates";

        if (listCurrent.equals(listOld)) {
            LOG.info(response);
        } else {
            differences = new ArrayList<>(listCurrent);
            differences.removeAll(listOld);
            response = LocalDateTime.now().withNano(0) + " " + differences;
            LOG.info(response);
//            listOld = listCurrent;
        }
        return response;
    }

    public List<String> getPosts() {
        List<String> listCurrent = new ArrayList<>();

        // initializing the HTML Document page variable
        Document doc;
        try {
            // fetching the target website
            doc = Jsoup
                    .connect("https://www.mygarage.ro/vanzari/")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                    .referrer("http://www.google.com")
                    .get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // retrieving the list of product HTML elements
        // in the target page
        Elements nodes = doc.select("#threadbits_forum_35").get(0).selectXpath("//td[starts-with(@id,'td_threadtitle_')]");
        for (Element e : nodes) {
            listCurrent.add(e.getElementsByAttributeStarting("href").last().attribute("href").getValue());
        }
        return listCurrent;
    }
}
