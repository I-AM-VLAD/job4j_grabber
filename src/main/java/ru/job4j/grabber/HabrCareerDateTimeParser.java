package ru.job4j.grabber;

import ru.job4j.grabber.DateTimeParser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

public class HabrCareerDateTimeParser implements DateTimeParser {
    @Override
    public LocalDateTime parse(String parse) {
        LocalDateTime dateTime = LocalDateTime.parse(parse, ISO_OFFSET_DATE_TIME);
        return dateTime;
    }
}




