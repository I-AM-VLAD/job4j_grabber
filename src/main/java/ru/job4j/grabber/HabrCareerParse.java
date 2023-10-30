package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class HabrCareerParse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    public static void main(String[] args) throws IOException {

        for (int i = 1; i <= 5; i++) {
            String string = "https://career.habr.com/vacancies/java_developer?page=";
            String numberStr = Integer.toString(i);
            String page = string + numberStr;
            Connection connection = Jsoup.connect(page);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                Element dateElement = row.select(".vacancy-card__date").first().child(0);
                String dateTime = dateElement.attr("datetime");
                String vacancyName = titleElement.text();
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                String vacancyDescription = retrieveDescription(link);
                System.out.printf("%s %s %s %s%n", vacancyName, link, dateTime, vacancyDescription);
            });
        }
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


