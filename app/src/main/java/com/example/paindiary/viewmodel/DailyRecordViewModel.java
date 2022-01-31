package com.example.paindiary.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.paindiary.entity.PainRecord;

public class DailyRecordViewModel extends ViewModel {
    private MutableLiveData<PainRecord> record;

    public DailyRecordViewModel() {
        record = new MutableLiveData<>();
    }

    public LiveData<PainRecord> getText() {
        return record;
    }

    public void setMessage(PainRecord painRecord) {
        record.setValue(painRecord);
    }
}
