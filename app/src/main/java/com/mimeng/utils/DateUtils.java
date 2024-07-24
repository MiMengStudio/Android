package com.mimeng.utils;

import java.util.Calendar;

public final class DateUtils {
    public static boolean isSameDay(long unixTime1, long unixTime2) {
         Calendar a = Calendar.getInstance();
         a.setTimeInMillis(unixTime1);
         Calendar b = Calendar.getInstance();
         b.setTimeInMillis(unixTime2);
         return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
                 a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    }
}
