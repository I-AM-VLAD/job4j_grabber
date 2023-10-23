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
    private static ArrayList<String> pageLinks = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        pageLinks.add("https://career.habr.com/vacancies/java_developer?page=1");
        pageLinks.add("https://career.habr.com/vacancies/java_developer?page=2");
        pageLinks.add("https://career.habr.com/vacancies/java_developer?page=3");
        pageLinks.add("https://career.habr.com/vacancies/java_developer?page=4");
        pageLinks.add("https://career.habr.com/vacancies/java_developer?page=5");
        for (String page : pageLinks) {
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
                System.out.printf("%s %s %s%n", vacancyName, link, dateTime);
            });
        }
    }
}
