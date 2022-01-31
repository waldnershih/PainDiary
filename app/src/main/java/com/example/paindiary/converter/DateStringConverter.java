package com.example.paindiary.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateStringConverter {

    public static final String parseDateToStr(final String format, final Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    public static final Date parseStrToDate(final String format, final String date) {
        try {
            return new SimpleDateFormat(format).parse(date);
        } catch (ParseException pe) {
            throw new RuntimeException();
        }
    }
}
