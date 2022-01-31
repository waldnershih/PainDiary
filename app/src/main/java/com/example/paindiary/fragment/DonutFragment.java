package com.example.paindiary.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.paindiary.R;
import com.example.paindiary.databinding.DonutFragmentBinding;
import com.example.paindiary.databinding.PieFragmentBinding;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DonutFragment extends Fragment {
    private DonutFragmentBinding donutBinding;
    private static String TAG = "DonutFragment";
    PieChart pieChart;
    public DonutFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        donutBinding = DonutFragmentBinding.inflate(inflater, container, false);
        View view = donutBinding.getRoot();
        getActivity().setTitle("Donut Pie Chart");

        pieChart = (PieChart) donutBinding.idDonutChart;

        String[] xData = new String[]{"Current steps", "Rest steps"};
        float[] yData = getData();

        if (yData[0] >= 0 && yData[0] >= 0) {
            createDonutChart(xData, yData);
        }
//        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
//            @Override
//            public void onValueSelected(Entry e, Highlight h) {
//                Log.d(TAG, "OnValueSelected: value selected from the chart");
//                Log.d(TAG, "OnValueSelected:" + e.toString());
//                Log.d(TAG, "OnValueSelected:" + h.toString());
//
//            }
//
//            @Override
//            public void onNothingSelected() {
//                Log.d(TAG, "onNothingSelected: value selected from the chart");
//            }
//        });

        return view;
    }

    private void createDonutChart(String[] xData, float[] yData) {
        Description description = new Description();
        description.setText("Current steps out of aim");
        description.setTextSize(25);

        pieChart.setDescription(description);

        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(70f);

        if ((yData[0] + yData[1]) != 0) {
            float percentage = (float) Math.round(yData[0] / (yData[0] + yData[1]) * 10000) / 100;
            String percentageStr =  "" + percentage + " %";
            pieChart.setCenterText(percentageStr);
            pieChart.setCenterTextSize(20);
        }

        ArrayList<PieEntry> yEntries = new ArrayList<>();
        ArrayList<String> xEntries = new ArrayList<>();

        for(int i = 0; i < yData.length; i++) {
            yEntries.add(new PieEntry(yData[i], xData[i]));
            xEntries.add(xData[i]);
        }

        // create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntries, "");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        // add colors to data set
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(255, 204, 102));
        colors.add(Color.rgb(204, 153, 255));
        pieDataSet.setColors(colors);


        // add legends to the chart
        Legend legend = pieChart.getLegend();
//        legend.setEnabled(false);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setTextSize(15);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);

        // create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.setVisibility(View.VISIBLE);
        pieChart.animateY(2000);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setTouchEnabled(true);

        pieChart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        donutBinding = null;
    }

    public float[] getData() {
        String userEmail = getUserEmail();
        String currentDate = getDateEntry();

        SharedPreferences sharedPref = requireActivity().getApplicationContext().getSharedPreferences(userEmail, Context.MODE_PRIVATE);
        String currentPainDataRecord = sharedPref.getString(userEmail,null);

        String[] currentDateRecordArray = new String[6]; // this is the default value

        if (currentPainDataRecord != null) {
            currentDateRecordArray = currentPainDataRecord.split(",", -1);
        }

        float[] step = {-1, -1};

        if (currentDate.equals(currentDateRecordArray[0])) {
            float restSteps = Float.parseFloat(currentDateRecordArray[4]) - Float.parseFloat(currentDateRecordArray[5]);
            if (restSteps < 0) {
                restSteps = 0;
            }
            step[0] = Float.parseFloat(currentDateRecordArray[5]);
            step[1] = restSteps;
        }

        return step;
    }

    public String getDateEntry() {
        return new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    }

    public String getUserEmail() {
        SharedPreferences sharedPref = requireActivity().getApplicationContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String userEmail = sharedPref.getString("Login",null);
        return userEmail;
    }

}
