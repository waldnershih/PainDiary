package com.example.paindiary.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.WorkManager;

import com.example.paindiary.DefaultValue;
import com.example.paindiary.entity.PainRecord;
import com.example.paindiary.retrofit.WeatherRetrofit;
import com.example.paindiary.service.WeatherService;
import com.example.paindiary.databinding.HomeFragmentBinding;
import com.example.paindiary.model.WeatherResponse;
import com.example.paindiary.viewmodel.PainRecordViewModel;
import com.example.paindiary.viewmodel.WorkManagerViewModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private static final String APP_ID = "898ef19b846722554449f6068e7c7253";
    private static final String CITY_NAME = "Caulfield";
    private static final String UNITS = "metric";
    private WeatherService weatherService;

    private HomeFragmentBinding homeBinding;

    public HomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeBinding = HomeFragmentBinding.inflate(inflater, container, false);
        View view = homeBinding.getRoot();

        getActivity().setTitle("Home");

        homeBinding.homeLocation.setText("Location: " + CITY_NAME);


        weatherService = WeatherRetrofit.getRetrofitService();
        Call<WeatherResponse> callAsync = weatherService.getCurrentWeatherData(CITY_NAME, APP_ID,UNITS);

        callAsync.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();

                    String temperature = "Temperature: " + weatherResponse.getMain().getTemp() + " \u2103";
                    String humidity = "Humidity: " + weatherResponse.getMain().getHumidity() + " %";
                    String pressure = "Pressure: " + weatherResponse.getMain().getPressure();

                    homeBinding.temperature.setText(temperature);
                    homeBinding.humidity.setText(humidity);
                    homeBinding.pressure.setText(pressure);
                } else {
                    Log.i("Error ", "Response failed");
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
        public void onDestroyView() {
        super.onDestroyView();
        homeBinding = null;
    }
}

/**
 * Reference
 * https://stackoverflow.com/questions/3312001/degrees-symbol-as-in-degrees-celsius-fahrenheit-in-a-textview
 * http://clipart-library.com/clipart/welcome-home-clipart_4.htm
 */