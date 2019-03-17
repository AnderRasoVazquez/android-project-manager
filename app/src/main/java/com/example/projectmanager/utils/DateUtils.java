package com.example.projectmanager.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Utilidades que tienen que ver con las fechas.
 */
public class DateUtils {

    /**
     * Convierte un String a un Date.
     * @param dateString
     * @return
     * @throws Exception
     */
    public static Date toDate(String dateString) throws Exception {
        return new SimpleDateFormat("yyyy/MM/dd").parse(dateString);
    }

    /**
     * Convierte un Date a un string
     *
     * Extraído de Stack Overflow
     * Pregunta: https://stackoverflow.com/questions/5683728/convert-java-util-date-to-string
     * Autor: https://stackoverflow.com/users/604156/ali-ben-messaoud
     * Modificado por Ander Raso Vazquez para adaptar el formato a la app.
     *
     * @param date
     * @return
     */
    public static String toString(Date date) {
        return new SimpleDateFormat("yyyy/MM/dd").format(date);
    }

    /**
     * Añade un listener a un textview que crea un dialogo de calendario al hacerle click.
     * @param editText
     * @param context
     */
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
