package com.example.paindiary.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.paindiary.viewmodel.TimePickerViewModel;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), TimePickerFragment.this, hour, minute, android.text.format.DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String selectedTime = getTimeText(hourOfDay, minute);
        TimePickerViewModel model = new ViewModelProvider(getActivity()).get(TimePickerViewModel.class);

        if (!selectedTime.isEmpty() ) {
            model.setMessage(selectedTime);
        }
    }

    private String getTimeText(int hourOfDay, int minute) {
        String selectedTime = "";
        String hourText = Integer.toString(hourOfDay);
        String minuteText = Integer.toString(minute);

        if (hourOfDay % 12 < 10) {
            hourText = "0" + hourOfDay % 12;
            if (hourText.equals("00")) {hourText = "12";}
        }

        if (minute < 10) {
            minuteText = "0" + minuteText;
        }

        if (hourOfDay / 12 > 0) {
            selectedTime = hourText + " : " + minuteText + " PM";
        }else {
            selectedTime = hourText + " : " + minuteText + " AM";
        }

        return selectedTime;
    }

}




