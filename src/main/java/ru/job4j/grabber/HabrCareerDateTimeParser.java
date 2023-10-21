/*
package ru.job4j.grabber;

import ru.job4j.grabber.DateTimeParser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HabrCareerDateTimeParser implements DateTimeParser {
    public static String splt(String in) {
        if (in.contains("+")) {
            String[] words = in.split("\\+");
            return words[0];
        } else {
            return null;
        }
    }

    @Override
    public static LocalDateTime parse(String parse) {
        LocalDateTime dateTime = LocalDateTime.parse(splt(parse));
        System.out.println(dateTime);
        return dateTime;
    }

    public static void main(String[] args) {
        System.out.println(splt("2018-05-05T11:50:55+03:00"));
    }
}

 */


