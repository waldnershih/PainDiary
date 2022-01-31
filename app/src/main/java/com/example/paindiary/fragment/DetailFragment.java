package com.example.paindiary.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.paindiary.converter.DateStringConverter;
import com.example.paindiary.databinding.DetailFragmentBinding;
import com.example.paindiary.entity.PainRecord;
import com.example.paindiary.viewmodel.DailyRecordViewModel;

import java.util.Date;

public class DetailFragment extends Fragment {

    private DetailFragmentBinding dBinding;

    public DetailFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dBinding = DetailFragmentBinding.inflate(inflater, container, false);
        View view = dBinding.getRoot();

        DailyRecordViewModel drModel = new ViewModelProvider(requireActivity()).get(DailyRecordViewModel.class);
        drModel.getText().observe(getViewLifecycleOwner(), new Observer<PainRecord>() {
            @Override
            public void onChanged(PainRecord painRecord) {
                Log.e("SHOW_ME_PAIN", painRecord.toString());

                if (painRecord != null) {
                    String detail = getDetail(painRecord.dateEntry, painRecord.painLevel, painRecord.painLocation,
                            painRecord.moodLevel, painRecord.step, painRecord.temp,
                            painRecord.humidity, painRecord.pressure, painRecord.userEmail);
                    dBinding.detail.setText(detail);
                }

            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dBinding = null;
    }

    public String getDetail(Date date, int painLevel, String painLocation, String moodLevel,
                            int step, double temp, double humidity, double pressure, String userEmail) {
        String dateText = DateStringConverter.parseDateToStr("dd-MM-yyyy", date);
        String painLevelText = String.valueOf(painLevel);
        String stepText = String.valueOf(step);
        String tempText = String.valueOf((double) Math.round(temp * 10) / 10);
        String humidityText = String.valueOf((double) Math.round(humidity * 10) / 10);
        String pressureText = String.valueOf((double) Math.round(pressure * 10) / 10);
        return "Date: " + dateText + "\n" +
                "Email: " + userEmail + "\n" +
                "Pain Level: " +  painLevelText + "\n" +
                "Pain Location: " +  painLocation + "\n" +
                "Mood Level: " +  moodLevel + "\n" +
                "Steps: " +  stepText + "\n" +
                "Temperature: " +  tempText + "\n" +
                "Humidity: " +  humidityText + "\n" +
                "Pressure: " +  pressureText;

    }
}
