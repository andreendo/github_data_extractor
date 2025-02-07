package com.github.github_data_extractor;

import java.util.Calendar;
import java.util.Date;

public class Utils {
    public static Date getSixMonthsAgoDate(Date currentDate) {
        var calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.MONTH, -6);
        return calendar.getTime();
    }
}
