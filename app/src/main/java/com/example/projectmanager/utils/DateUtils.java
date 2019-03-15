package com.example.projectmanager.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static Date toDate(String dateString) throws Exception {
        return new SimpleDateFormat("yyyy/MM/dd").parse(dateString);
    }

    public static String toString(Date date) {
        return new SimpleDateFormat("yyyy/MM/dd").format(date);
    }

    public static void addPopUpCalendar(final TextView editText, final Context context) {
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                monthOfYear++;
                                String month = String.format("%02d", monthOfYear);
                                String day = String.format("%02d", dayOfMonth);
                                editText.setText(year + "/" + month + "/" + day);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }
}
