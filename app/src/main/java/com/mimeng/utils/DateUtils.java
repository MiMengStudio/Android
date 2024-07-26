package com.mimeng.utils;

import java.util.Calendar;

public final class DateUtils {
    public static boolean isSameDay(Long unixTime1, Long unixTime2) {
        if (unixTime1 == null || unixTime2 == null) {
            return false;
        }
        Calendar a = Calendar.getInstance();
        a.setTimeInMillis(unixTime1);
        Calendar b = Calendar.getInstance();
        b.setTimeInMillis(unixTime2);
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
                a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    }
}
