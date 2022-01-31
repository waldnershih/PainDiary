package com.example.paindiary.viewmodel;

import android.app.Application;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.paindiary.converter.DateStringConverter;
import com.example.paindiary.entity.PainRecord;
import com.example.paindiary.workmanager.UploadWorker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class WorkManagerViewModel extends AndroidViewModel {
    private WorkManager workManager;
    private PainRecordViewModel prModel;

    public WorkManagerViewModel(@NonNull Application application) {
        super(application);
        workManager = WorkManager.getInstance(application);

    }

    public void applyRequest() {

        prModel = ViewModelProvider
                .AndroidViewModelFactory
                .getInstance(getApplication())
                .create(PainRecordViewModel.class);


        String dateEntry = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());;
        Date date = DateStringConverter.parseStrToDate("dd-MM-yyyy", dateEntry);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            CompletableFuture<PainRecord> painRecordCompletableFuture = prModel.findByDateFuture(date.getTime());
            painRecordCompletableFuture.thenApply(painRecord -> {
                if (painRecord != null) {
                    if (checkCalendar()) {
                        PeriodicWorkRequest request = new PeriodicWorkRequest
                                .Builder(UploadWorker.class, 24, TimeUnit.HOURS)
                                .setInputData(getData(painRecord, dateEntry).build())
                                .build();

                        workManager.enqueue(request);
                        workManager.enqueueUniquePeriodicWork("jobTag", ExistingPeriodicWorkPolicy.KEEP, request);
                    } else {
                        Toast.makeText(getApplication(), "Initialise at 10 PM !", Toast.LENGTH_SHORT).show();
                    }

                }
                return painRecord;
            });
        } else {
            Log.d("workManagerViewModel", "version is incorrect");
        }
    }

    public Data.Builder getData(PainRecord painRecord, String dateEntry) {
        Data.Builder data = new Data.Builder();
        data.putInt("painLevel", painRecord.painLevel);
        data.putString("painLocation", painRecord.painLocation);
        data.putString("moodLevel", painRecord.moodLevel);
        data.putString("dateEntry", dateEntry);
        data.putInt("step", painRecord.step);
        data.putDouble("temp", painRecord.temp);
        data.putDouble("humidity", painRecord.humidity);
        data.putDouble("pressure", painRecord.pressure);
        data.putString("userEmail", painRecord.userEmail);
        return data;
    }

    private boolean checkCalendar() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, 22);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY, 22);
        calendar2.set(Calendar.MINUTE, 5);
        calendar2.set(Calendar.SECOND, 0);

        if (calendar1.before(Calendar.getInstance()) && calendar2.after(Calendar.getInstance())) {
            return true;
        }
        return false;
    }
}

/**
 * Reference
 * https://developer.android.com/topic/libraries/architecture/workmanager/advanced
 */