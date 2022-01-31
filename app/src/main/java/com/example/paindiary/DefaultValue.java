package com.example.paindiary;

import com.example.paindiary.entity.PainRecord;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DefaultValue {
    private List<PainRecord> painRecords;
    final static int[] PAIN_LEVEL = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    private final static String[] PAIN_LOCATION = {"Back", "Neck", "Head", "Knees", "Hips", "Abdomen", "Elbows", "Shoulders", "Shins", "Jaw", "Facial"};
    private final static String[] MOOD_LEVEL = {"Very Good", "Good", "Average", "Low", "Very Low"};
    private final static String EMAIL = "321@gmail.com";

    private final static double[] TEMP_RANGE = {19d,6d};
    private final static double[] HUMIDITY_RANGE = {70d, 25d};
    private final static double[] PRESSURE_RANGE = {20d,1007d};

    private Calendar calendar;

    public DefaultValue() {
        painRecords = new ArrayList<>();
        calendar = Calendar.getInstance();
        generateDefaultRecords(60);
    }

    public List<PainRecord> getPainRecords() {
        return painRecords;
    }

    public void setPainRecords(List<PainRecord> painRecords) {
        this.painRecords = painRecords;
    }

    public final static String[] getMoodLevel() {
        return MOOD_LEVEL;
    }

    public final static String[] getPainLocation() {
        return PAIN_LOCATION;
    }

    public final static int[] getPainLevel() {
        return PAIN_LEVEL;
    }

    public double getRandomDouble(double range, double startsFrom) {
        return (double) (Math.random() * range) + startsFrom;
    }

    public int getRandomInteger(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }

    public void generateDefaultRecords(int size) {
        int painLevel = 0;
        String painLocation = "";
        String moodLevel = "";
        int step = 0;
        double temp = 0d;
        double humidity = 0d;
        double pressure = 0d;

        for (int i = 0; i < size; i++) {
            painLevel = PAIN_LEVEL[getRandomInteger(0, 10)];
            painLocation = PAIN_LOCATION[getRandomInteger(0,10)];
            moodLevel = MOOD_LEVEL[getRandomInteger(0,4)];
            step = getRandomInteger(1000, 9000);
            temp = getRandomDouble(TEMP_RANGE[0], TEMP_RANGE[1]);
            humidity = getRandomDouble(HUMIDITY_RANGE[0], HUMIDITY_RANGE[1]);
            pressure = getRandomDouble(PRESSURE_RANGE[0], PRESSURE_RANGE[1]);

            calendar.add(Calendar.DATE, -1);
            Date date = calendar.getTime();

            PainRecord painRecord = new PainRecord(painLevel, painLocation, moodLevel, step,
                    date, temp, humidity, pressure, EMAIL);
            painRecords.add(painRecord);
        }
    }
}

