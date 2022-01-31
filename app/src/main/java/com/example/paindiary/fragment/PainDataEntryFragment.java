package com.example.paindiary.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.paindiary.DefaultValue;
import com.example.paindiary.R;
import com.example.paindiary.alarm.AlarmReceiver;
import com.example.paindiary.databinding.PainDataEntryFragmentBinding;
import com.example.paindiary.entity.PainRecord;
import com.example.paindiary.model.WeatherResponse;
import com.example.paindiary.retrofit.WeatherRetrofit;
import com.example.paindiary.service.WeatherService;
import com.example.paindiary.converter.DateStringConverter;
import com.example.paindiary.viewmodel.PainRecordViewModel;
import com.example.paindiary.viewmodel.TimePickerViewModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PainDataEntryFragment extends Fragment{
    private PainDataEntryFragmentBinding painDataEntryBinding;
    private PainRecordViewModel painRecordViewModel;

    private static final String APP_ID = "898ef19b846722554449f6068e7c7253";
    private static final String CITY_NAME = "Melbourne";
    private static final String UNITS = "metric";
    private WeatherService weatherService;

    public PainDataEntryFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        painDataEntryBinding = PainDataEntryFragmentBinding.inflate(inflater, container, false);
        View view = painDataEntryBinding.getRoot();

        getActivity().setTitle("Pain Data Entry");

        TimePickerViewModel tpModel = new ViewModelProvider(requireActivity()).get(TimePickerViewModel.class);

        if (!getTimePicker().isEmpty()) {
            painDataEntryBinding.timePicker.setText(getTimePicker());
        }

        painDataEntryBinding.timePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });

        tpModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (!s.isEmpty()) {
                    String timeStr = "Remind at ";
                    Calendar calendar = getCalendar(s);
                    timeStr += DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
                    painDataEntryBinding.timePicker.setText(timeStr);
                    startAlarm(calendar, timeStr);
                }
            }
        });

        painDataEntryBinding.timeCancellerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
            }
        });

        painDataEntryBinding.arrow.setText(getArrowSymbol());

        painRecordViewModel = ViewModelProvider
                .AndroidViewModelFactory
                .getInstance(getActivity().getApplication())
                .create(PainRecordViewModel.class);

        List<String> painLevelList = getPainLevelList();
        List<String> painLocationList = getPainLocationList();
        List<String> moodLevelList = getMoodLevelList();

        final ArrayAdapter<String> painLevelSpinnerAdapter = new ArrayAdapter<String>(
                getActivity(), R.layout.my_spinner, painLevelList);
        painDataEntryBinding.painLevelSpinner.setAdapter(painLevelSpinnerAdapter);

        final ArrayAdapter<String> painLocationSpinnerAdapter = new ArrayAdapter<String>(
                getActivity(), R.layout.my_spinner, painLocationList);
        painDataEntryBinding.painLocationSpinner.setAdapter(painLocationSpinnerAdapter);

        final ArrayAdapter<String> moodLevelSpinnerAdapter = new ArrayAdapter<String>(
                getActivity(), R.layout.my_spinner, moodLevelList);
        painDataEntryBinding.moodLevelSpinner.setAdapter(moodLevelSpinnerAdapter);

        weatherService = WeatherRetrofit.getRetrofitService();
        List<String> weatherList = getWeather();
        final String[] painLevel = {""};
        final String[] painLocation = {""};
        final String[] moodLevel = {""};

        painDataEntryBinding.updateButton.setEnabled(false);
        setDefaultView();

        painDataEntryBinding.painLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLevel = parent.getItemAtPosition(position).toString();
                if(selectedLevel != null){
                    painLevel[0] = selectedLevel;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        painDataEntryBinding.painLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLocation = parent.getItemAtPosition(position).toString();
                if(selectedLocation != null){
                    painLocation[0] = selectedLocation;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        painDataEntryBinding.moodLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMoodLevel = parent.getItemAtPosition(position).toString();
                if(selectedMoodLevel != null){
                    moodLevel[0] = selectedMoodLevel;
                    changeIcon(selectedMoodLevel);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        painDataEntryBinding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewPainRecord(painLevel[0], painLocation[0], moodLevel[0], weatherList);
            }
        });

        painDataEntryBinding.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCurrentPainRecord(painLevel[0], painLocation[0], moodLevel[0], weatherList);
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        painDataEntryBinding = null;
    }

    private void addNewPainRecord(String pLevel, String location, String mLevel, List<String >weatherList) {
        String painLevel = pLevel;
        String painLocation = location;
        String moodLevel = mLevel;
        String currentSteps = getCurrentSteps();
        String goal = getGoal();
        String dateEntry = getDateEntry();
        String temp = weatherList.get(0);
        String humidity = weatherList.get(1);
        String pressure = weatherList.get(2);
        String email = getUserEmail();

        if ((!pLevel.isEmpty()) && (!painLevel.isEmpty()) &&
                (!mLevel.isEmpty()) && (!dateEntry.isEmpty()) &&
                (!temp.isEmpty()) && (!humidity.isEmpty()) &&
                (!pressure.isEmpty()) && (!email.isEmpty()) &&
                validatePainLevel(pLevel) && validateCurrentSteps(currentSteps) &&
                validateGoal(goal)) {
            int iPLevel = Integer.parseInt(pLevel);
            int iStep = Integer.parseInt(currentSteps);
            Date date = DateStringConverter.parseStrToDate("dd-MM-yyyy", dateEntry);
            double dTemp = Double.parseDouble(temp);
            double dHumidity = Double.parseDouble(humidity);
            double dPressure = Double.parseDouble(pressure);

            PainRecord painRecord = new PainRecord(iPLevel, painLocation, moodLevel, iStep, date, dTemp,dHumidity, dPressure, email);
            painRecordViewModel.insert(painRecord);
            recordCurrentDetails(painLevel, painLocation, moodLevel);
            painDataEntryBinding.saveButton.setEnabled(false);
            painDataEntryBinding.updateButton.setEnabled(true);

            Toast.makeText(getActivity(), "Save successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Save failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 1, intent, 0);
        alarmManager.cancel(pendingIntent);

        setTimePicker("");
        TimePickerViewModel model = new ViewModelProvider(getActivity()).get(TimePickerViewModel.class);
        model.setMessage("");
        painDataEntryBinding.timePicker.setText("Please select a time reminder first");
        Toast.makeText(getActivity(), "Alarm canceled successfully !", Toast.LENGTH_SHORT).show();
    }

    private void changeIcon(String moodLevel) {
        if (moodLevel.equals("Very Good")) {
            painDataEntryBinding.icon.setImageDrawable(getResources().getDrawable(R.drawable.very_good));
        } else if (moodLevel.equals("Good")) {
            painDataEntryBinding.icon.setImageDrawable(getResources().getDrawable(R.drawable.good));
        } else if (moodLevel.equals("Average")) {
            painDataEntryBinding.icon.setImageDrawable(getResources().getDrawable(R.drawable.average));
        } else if (moodLevel.equals("Low")) {
            painDataEntryBinding.icon.setImageDrawable(getResources().getDrawable(R.drawable.bad));
        } else if (moodLevel.equals("Very Low")) {
            painDataEntryBinding.icon.setImageDrawable(getResources().getDrawable(R.drawable.very_bad));
        }
    }

    private void editCurrentPainRecord(String pLevel, String location, String mLevel, List<String >weatherList) {
        String painLevel = pLevel;
        String painLocation = location;
        String moodLevel = mLevel;
        String currentSteps = getCurrentSteps();
        String goal = getGoal();
        String dateEntry = getDateEntry();
        String temp = weatherList.get(0);
        String humidity = weatherList.get(1);
        String pressure = weatherList.get(2);
        String email = getUserEmail();

        if ((!pLevel.isEmpty()) && (!painLevel.isEmpty()) &&
                (!mLevel.isEmpty()) && (!dateEntry.isEmpty()) &&
                (!temp.isEmpty()) && (!humidity.isEmpty()) &&
                (!pressure.isEmpty()) && (!email.isEmpty()) &&
                validatePainLevel(pLevel) && validateCurrentSteps(currentSteps) &&
                validateGoal(goal)) {
            int iPLevel = Integer.parseInt(pLevel);
            int iStep = Integer.parseInt(currentSteps);
            Date date = DateStringConverter.parseStrToDate("dd-MM-yyyy", dateEntry);
            double dTemp = Double.parseDouble(temp);
            double dHumidity = Double.parseDouble(humidity);
            double dPressure = Double.parseDouble(pressure);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                CompletableFuture<PainRecord> painRecordCompletableFuture = painRecordViewModel.findByDateFuture(date.getTime());
                painRecordCompletableFuture.thenApply(painRecord -> {
                    if (painRecord != null) {
                        painRecord.painLevel = iPLevel;
                        painRecord.painLocation = painLocation;
                        painRecord.moodLevel = moodLevel;
                        painRecord.step = iStep;
                        painRecord.dateEntry = date;
                        painRecord.temp = dTemp;
                        painRecord.humidity = dHumidity;
                        painRecord.pressure = dPressure;
                        painRecord.userEmail = email;

                        painRecordViewModel.update(painRecord);
                        recordCurrentDetails(painLevel, painLocation, moodLevel);

                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getActivity(), "Update successfully", Toast.LENGTH_SHORT).show();
                        });
                    }else {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getActivity(), "Data did not exist. ", Toast.LENGTH_SHORT).show();
                        });
                    }
                    return painRecord;
                });
            }
        }else {
            Toast.makeText(getActivity(), "Update failed", Toast.LENGTH_SHORT).show();
        }
    }

    public String getArrowSymbol() {
        String aSymbol = "";
        for (int i = 0; i < 15; i++) {
            aSymbol += "\u2191";
        }
        return aSymbol;
    }

    public Calendar getCalendar(String timeString) {
        String[] time = timeString.split(" ");
        String hourOfDayString = time[0];
        int hourOfDay = Integer.parseInt(hourOfDayString);
        String minuteString = time[2];
        int minute = Integer.parseInt(minuteString);

        if (time[3].equals("AM") && hourOfDay == 12) {
            hourOfDay = 0;
        } else if (time[3].equals("PM") && hourOfDay != 12) {
            hourOfDay += 12;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.MINUTE, -2);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        return calendar;
    }

    public String getCurrentSteps() {
        return painDataEntryBinding.editCurrentSteps.getText().toString();
    }

    public String getGoal() {
        return painDataEntryBinding.editGoal.getText().toString();
    }

    public String getDateEntry() {
        return new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    }

    public List<String> getMoodLevelList() {
        List<String> moodLevelList = new ArrayList<>(
                Arrays.asList(DefaultValue.getMoodLevel())
        );
        return moodLevelList;
    }

    public List<String> getPainLevelList(){
        List<String> painLevelList = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            painLevelList.add("" + i);
        }
        return painLevelList;
    }

    public List<String> getPainLocationList(){
        List<String> painLocationList = new ArrayList<>(
                Arrays.asList(DefaultValue.getPainLocation())
        );
        return painLocationList;
    }

    public String getTimePicker() {
        SharedPreferences sharedPref = requireActivity().getApplicationContext().getSharedPreferences("TimePicker", Context.MODE_PRIVATE);
        String timePicker = sharedPref.getString("TimePicker","");
        return timePicker;
    }

    public void setTimePicker(String timePicker) {
        SharedPreferences sharedPref = requireActivity().getApplicationContext().getSharedPreferences("TimePicker", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putString("TimePicker", timePicker);
        spEditor.apply();
    }

    public String getUserEmail() {
        SharedPreferences sharedPref = requireActivity().getApplicationContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String userEmail = sharedPref.getString("Login",null);
        return userEmail;
    }

    public List<String> getWeather() {
        List<String> weatherList = new ArrayList<>();

        Call<WeatherResponse> callAsync = weatherService.getCurrentWeatherData(CITY_NAME, APP_ID, UNITS);
        callAsync.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();

                    String temperature = "" + weatherResponse.getMain().getTemp();
                    String humidity = "" + weatherResponse.getMain().getHumidity();
                    String pressure = "" + weatherResponse.getMain().getPressure();

                    weatherList.add(temperature);
                    weatherList.add(humidity);
                    weatherList.add(pressure);

                } else {
                    for (int i = 0; i < 3; i++) {
                        weatherList.add("");
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return weatherList;
    }

    private void recordCurrentDetails(String pLevel, String pLocation, String mLevel) {
        String userEmail = getUserEmail();
        String currentDate = getDateEntry();
        String userPLevel = pLevel;
        String userPLocation = pLocation;
        String userMLevel = mLevel;
        String userGoal = painDataEntryBinding.editGoal.getText().toString();
        String userCurrentSteps = getCurrentSteps();

        String userPainData = currentDate + "," + userPLevel + "," + userPLocation + "," + userMLevel + "," + userGoal + "," + userCurrentSteps;

        SharedPreferences sharedPref = requireActivity().getApplicationContext().getSharedPreferences(userEmail, Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putString(userEmail, userPainData);
        spEditor.apply();
    }

    private void startAlarm(Calendar calendar, String timeText) {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 1, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(getActivity(), "Alarm set successfully !", Toast.LENGTH_SHORT).show();
            setTimePicker(timeText);
        }
    }

    private void setDefaultView() {
        String userEmail = getUserEmail();
        String currentDate = getDateEntry();
        String[] currentDateRecordArray = {"00-00-0000", "0", "back", "very good", "10000", null}; // this is the default value

        SharedPreferences sharedPref = requireActivity().getApplicationContext().getSharedPreferences(userEmail, Context.MODE_PRIVATE);
        String currentDataRecord = sharedPref.getString(userEmail,null);

        if (currentDataRecord != null) {
            currentDateRecordArray = currentDataRecord.split(",", -1);
        }

        String dateRecord = currentDateRecordArray[0];
        String painLevel = currentDateRecordArray[1];
        String painLocation = currentDateRecordArray[2];
        String moodLevel = currentDateRecordArray[3];
        String goal = currentDateRecordArray[4];
        String currentSteps = currentDateRecordArray[5];

        if (currentDate.equals(dateRecord)) {
            painDataEntryBinding.saveButton.setEnabled(false);
            painDataEntryBinding.updateButton.setEnabled(true);
            for(int i = 0; i < painDataEntryBinding.painLevelSpinner.getCount(); i++){
                if(painDataEntryBinding.painLevelSpinner.getItemAtPosition(i).toString().equals(painLevel)){
                    painDataEntryBinding.painLevelSpinner.setSelection(i);
                    break;
                }
            }

            for(int i = 0; i < painDataEntryBinding.painLocationSpinner.getCount(); i++){
                if(painDataEntryBinding.painLocationSpinner.getItemAtPosition(i).toString().equals(painLocation)){
                    painDataEntryBinding.painLocationSpinner.setSelection(i);
                    break;
                }
            }


            for(int i = 0; i < painDataEntryBinding.moodLevelSpinner.getCount(); i++){
                if(painDataEntryBinding.moodLevelSpinner.getItemAtPosition(i).toString().equals(moodLevel)){
                    painDataEntryBinding.moodLevelSpinner.setSelection(i);
                    break;
                }
            }

            painDataEntryBinding.editGoal.setText(goal);
            painDataEntryBinding.editCurrentSteps.setText(currentSteps);
        }
    }

    private boolean validateCurrentSteps(String s) {
        if (s.isEmpty()) {
            Toast.makeText(getActivity(), "Current steps cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))){
                Toast.makeText(getActivity(), "Current steps need to be a number and non-negative", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (Integer.parseInt(s) < 0) {
            Toast.makeText(getActivity(), "Current steps need to non-negative", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validateGoal(String s) {
        if (s.isEmpty()) {
            Toast.makeText(getActivity(), "Goal cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))){
                Toast.makeText(getActivity(), "Goal needs to be a number and non-negative", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (Integer.parseInt(s) < 5000) {
            Toast.makeText(getActivity(), "Goal needs to be at least 5000", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validatePainLevel(String s) {
        if (s.isEmpty()) {
            Toast.makeText(getActivity(), "Pain level cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}

/**
 * Reference
 * https://stackoverflow.com/questions/2974862/changing-imageview-source
 * https://stackoverflow.com/questions/10634180/how-to-set-spinner-default-by-its-value-instead-of-position
 * https://www.youtube.com/watch?v=yrpimdBRk5Q&list=PLt_kGICKCFVPJ_rBwCMVb-9KdigyXaecU&index=3&t=39s&ab_channel=CodinginFlow
 * https://www.youtube.com/watch?v=QquRXzJguQM&list=PLt_kGICKCFVPJ_rBwCMVb-9KdigyXaecU&index=9&t=312s&ab_channel=AndroidCoding
 */