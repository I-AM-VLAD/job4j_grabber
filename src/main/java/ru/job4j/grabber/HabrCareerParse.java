package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";
    private final DateTimeParser dateTimeParser;
    private int id = 1;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list(String link) throws IOException {
       List<Post> rsl = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            String numberStr = Integer.toString(i);
            String page = link + numberStr;
            Connection connection = Jsoup.connect(page);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                Element dateElement = row.select(".vacancy-card__date").first().child(0);
                String dateTime = dateElement.attr("datetime");
                String vacancyName = titleElement.text();
                String newLink = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                String vacancyDescription = retrieveDescription(newLink);
                rsl.add(new Post(id++, vacancyName, newLink, vacancyDescription, dateTimeParser.parse(dateTime)));
            });
        }
        return  rsl;
    }

    private static String retrieveDescription(String link) {
        try {
            Connection connection = Jsoup.connect(link);
            Document document = connection.get();
            Elements row = document.select(".faded-content__body");
            Element description = row.select(".vacancy-description__text").first();
            return description.text();
        } catch (IOException e) {
            e.printStackTrace();
            return  null;
        }
    }
}


