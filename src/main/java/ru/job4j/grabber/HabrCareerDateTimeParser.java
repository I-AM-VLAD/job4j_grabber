package ru.job4j.grabber;

import ru.job4j.grabber.DateTimeParser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HabrCareerDateTimeParser implements DateTimeParser {
    @Override
    public LocalDateTime parse(String parse) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-mm-ddТhh:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(parse, formatter);
        System.out.println(dateTime);
        return dateTime;
    }
}




