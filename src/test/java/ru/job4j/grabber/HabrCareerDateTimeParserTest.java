package ru.job4j.grabber;

import org.junit.Test;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class HabrCareerDateTimeParserTest {
    @Test
    public void testParse() {
        String in = "2018-05-05T11:50:55+03:00";
        String expected = "2018-05-05T11:50:55";
        HabrCareerDateTimeParser dateTimeParser = new HabrCareerDateTimeParser();
        assertThat(dateTimeParser.parse(in).toString(), is(expected));
    }
}


