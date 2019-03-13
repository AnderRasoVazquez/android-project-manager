package com.example.projectmanager.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static Date toDate(String dateString) throws Exception {
        return new SimpleDateFormat("yyyy/MM/dd").parse(dateString);
    }

    public static String toString(Date date) {
        return new SimpleDateFormat("yyyy/MM/dd").format(date);
    }
}
