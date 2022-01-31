package com.example.paindiary.converter;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateLongConverter {

    @TypeConverter
    public static Date fromTimeStamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}

/**
 * Reference:
 * https://developer.android.com/training/data-storage/room/referencing-data
 */
