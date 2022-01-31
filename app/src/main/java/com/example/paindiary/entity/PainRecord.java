package com.example.paindiary.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.paindiary.converter.DateLongConverter;

import java.util.Date;

@Entity(tableName = "pain_record")
public class PainRecord {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "pain_level")
    @NonNull
    public int painLevel;

    @ColumnInfo(name = "pain_location")
    @NonNull
    public String painLocation;

    @ColumnInfo(name = "mood_level")
    @NonNull
    public String moodLevel;

    @NonNull
    public int step;

    @ColumnInfo(name = "date_entry")
    @NonNull
    @TypeConverters(DateLongConverter.class)
    public Date dateEntry;

    @NonNull
    public double temp;

    @NonNull
    public double humidity;

    @NonNull
    public double pressure;

    @NonNull
    public String userEmail;

    public PainRecord( @NonNull int painLevel, @NonNull String painLocation, @NonNull String moodLevel, @NonNull int step,
                       @NonNull Date dateEntry, @NonNull double temp, @NonNull double humidity, @NonNull double pressure,
                       @NonNull String userEmail) {
        this.painLevel = painLevel;
        this.painLocation = painLocation;
        this.moodLevel = moodLevel;
        this.step = step;
        this.dateEntry = dateEntry;
        this.temp = temp;
        this.humidity = humidity;
        this.pressure = pressure;
        this.userEmail = userEmail;
    }

    @Override
    public String toString() {
        return "PainRecord {" +
                "uid= " + uid +
                ", painLevel= " + painLevel +
                ", painLocation= " + painLocation +
                ", moodLevel= " + moodLevel +
                ", step= " + step +
                ", dateEntry= " + dateEntry +
                ", temp= " + temp +
                ", humidity= " + humidity +
                ", pressure= " + pressure +
                ", userEmail= " + userEmail +
                " }";
    }
}
