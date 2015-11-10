package com.apphunt.app.utils;

import android.text.Html;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nmp on 15-5-28.
 */
public class StringUtils {
    private static final String TAG = StringUtils.class.getSimpleName();

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

    public static String getMonthStringFromCalendar(int month, boolean withShortName) {
        SimpleDateFormat dayFormat;
        if (withShortName) {
            dayFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);
        } else {
            dayFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);

        return dayFormat.format(calendar.getTime());
    }

    public static String getYearStringFromCalendar(int year) {
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);

        return dayFormat.format(calendar.getTime());
    }

    public static boolean isEmailValid(String email) {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        return matcher.matches();
    }

    public static String getDateAsTitleString(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        if(formatter.format(today).equals(dateStr)) {
            return "Today";
        }

        calendar.add(Calendar.DATE, -1);
        today = calendar.getTime();
        if(formatter.format(today).equals(dateStr)) {
            return "Yesterday";
        }

        return dateStr;
    }

    public static String getTimeDifferenceString(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        SimpleDateFormat returnDate = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar eventDate = Calendar.getInstance();
        long diff = 0;

        try {
            eventDate.setTime(formatter.parse(dateStr));
            diff = Calendar.getInstance().getTimeInMillis() - eventDate.getTimeInMillis();
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }

        int years = Math.abs(eventDate.get(Calendar.YEAR) - Calendar.getInstance().get(Calendar.YEAR));
        int months = Math.abs(years * 12 + eventDate.get(Calendar.MONTH) - Calendar.getInstance().get(Calendar.MONTH));
        int days = (int) diff / (24 * 60 * 60 * 1000);
        int hours = (int) diff / (60 * 60 * 1000) % 24;
        int min = (int) diff / (60 * 1000) % 60;
        int sec = (int) diff / 1000 % 60;

        if (months > 0) {
            return returnDate.format(eventDate.getTime());
        } else if (days > 0) {
            return days + "d";
        } else if (hours > 0) {
            return hours + "h";
        } else if (min > 0) {
            return min + "m";
        } else if (sec > 0) {
            return sec + "s";
        } else {
            return "0s";
        }
    }
}
