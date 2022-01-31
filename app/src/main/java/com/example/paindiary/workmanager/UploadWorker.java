package com.example.paindiary.workmanager;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.paindiary.DefaultValue;
import com.example.paindiary.converter.DateStringConverter;
import com.example.paindiary.entity.PainRecord;
import com.example.paindiary.viewmodel.PainRecordViewModel;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class UploadWorker extends Worker {
    private static final String INSTANCE = "https://assignment-final-40de6-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private static final String TAG = "UnloadWorker";

    public UploadWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }


    @Override
    public Result doWork() {

        rootNode = FirebaseDatabase.getInstance(INSTANCE);
        reference = rootNode.getReference("user");

        try {
            uploadDailyRecord();
            Log.d("UploadWorker", getPainRecord().toString());
            return Result.success();
        } catch (Exception e) {
            return Result.failure();
        }
    }

    private void uploadDailyRecord() {
        reference.child("uid")
                .child(DateStringConverter
                        .parseDateToStr("dd-MM-yyyy", getPainRecord().dateEntry))
                .setValue(getPainRecord());

    }

    private PainRecord getPainRecord() {
        if (getInputData().getString("dateEntry") != null) {
            Date date = DateStringConverter.parseStrToDate("dd-MM-yyyy", getInputData().getString("dateEntry"));
            PainRecord painRecord = new PainRecord(getInputData().getInt("painLevel", 0),
                    getInputData().getString("painLocation"),
                    getInputData().getString("moodLevel"),
                    getInputData().getInt("step", 0),
                    date,
                    getInputData().getDouble("temp", 0d),
                    getInputData().getDouble("humidity", 0d),
                    getInputData().getDouble("pressure", 0d),
                    getInputData().getString("userEmail"));
            return painRecord;
        }
        return null;
    }
}

/**
 * Reference
 * https://developer.android.com/topic/libraries/architecture/workmanager/basics
 */
