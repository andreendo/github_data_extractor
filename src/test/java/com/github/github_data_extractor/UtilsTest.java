package com.github.github_data_extractor;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void getSixMonthsAgoDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2025, Calendar.FEBRUARY, 7, 0, 0, 0); // Set to Feb 7, 2025, at 00:00:00
        calendar.set(Calendar.MILLISECOND, 0); // Ensure no milliseconds
        Date date = calendar.getTime();

        Calendar retCalendar = Calendar.getInstance();
        retCalendar.setTime(Utils.getSixMonthsAgoDate(date));

        assertEquals(7, retCalendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(8, retCalendar.get(Calendar.MONTH)+1);
        assertEquals(2024, retCalendar.get(Calendar.YEAR));
    }
}