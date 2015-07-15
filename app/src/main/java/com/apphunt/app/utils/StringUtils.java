package com.apphunt.app.utils;

import android.text.Html;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by nmp on 15-5-28.
 */
public class StringUtils {
    public static String decodeString(String str) {
        try {
            return URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String htmlDecodeString(String str) {
        return Html.fromHtml(str).toString();
    }

    public static String getDateStringFromCalendar(int minusDays) {
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -minusDays);

        return dayFormat.format(calendar.getTime());
    }

    public static String getMonthStringFromCalendar(int minusMonths) {
        SimpleDateFormat dayFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -minusMonths);

        return dayFormat.format(calendar.getTime());
    }

    public static String getYearStringFromCalendar(int minusYears) {
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -minusYears);

        return dayFormat.format(calendar.getTime());
    }
}
