package com.mimeng.utils;

import java.util.Calendar;
import java.util.Date;

public final class DateUtils {
    public static boolean isSameDay(long unixTime1, long unixTime2) {
        Calendar a = Calendar.getInstance();
        a.setTimeInMillis(unixTime1);
        Calendar b = Calendar.getInstance();
        b.setTimeInMillis(unixTime2);
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
                a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    }

    public static String getTimeAgo(long unixTime) {
        long now = System.currentTimeMillis();
        long diff = now - unixTime;

        if (diff < 60000) {
            return "刚刚";
        } else if (diff < 3600000) {
            return diff / 60000 + "分钟前";
        } else if (diff < 86400000) {
            return diff / 3600000 + "小时前";
        } else if (isSameDay(unixTime, now)) {
            return "今天";
        } else if (isSameDay(unixTime, now - 86400000)) {
            return "昨天";
        } else if (isSameDay(unixTime, now - 2 * 86400000)) {
            return "前天";
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(unixTime);
            return new StringBuilder()
                    .append(calendar.get(Calendar.YEAR)).append("-")
                    .append(calendar.get(Calendar.MONTH) + 1).append("-")
                    .append(calendar.get(Calendar.DAY_OF_MONTH)).toString();
        }
    }
}