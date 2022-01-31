package com.example.paindiary.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TimePickerViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public TimePickerViewModel(){
        mText = new MutableLiveData<String>();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setMessage(String message) {
        mText.setValue(message);
    }
}
