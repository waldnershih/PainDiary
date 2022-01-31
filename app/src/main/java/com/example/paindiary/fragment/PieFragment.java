package com.example.paindiary.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.paindiary.dao.PainRecordDAO;
import com.example.paindiary.databinding.PieFragmentBinding;
import com.example.paindiary.viewmodel.PainRecordViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

public class PieFragment extends Fragment {
    private PainRecordViewModel painRecordViewModel;
    private PieFragmentBinding pieBinding;

    PieChart pieChart;

    public PieFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        pieBinding = PieFragmentBinding.inflate(inflater, container, false);
        View view = pieBinding.getRoot();
        getActivity().setTitle("Pie Chart");

        pieChart = pieBinding.idPieChart;
        List<Float> yData = new ArrayList<>();
        List<String> xData = new ArrayList<>();

        painRecordViewModel = ViewModelProvider
                .AndroidViewModelFactory
                .getInstance(getActivity().getApplication())
                .create(PainRecordViewModel.class);

        painRecordViewModel.getFrequencyGroupedByPainLocation().observe(getActivity(), new Observer<List<PainRecordDAO.PainLocationFrequency>>() {
            @Override
            public void onChanged(List<PainRecordDAO.PainLocationFrequency> painLocationFrequencies) {
                xData.clear();
                yData.clear();

                for (PainRecordDAO.PainLocationFrequency pLocationFreq : painLocationFrequencies) {
                    yData.add((float) pLocationFreq.count);
                    xData.add(pLocationFreq.pain_location);
                }

                createPieChart(yData, xData);

//                pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
//                    @Override
//                    public void onValueSelected(Entry e, Highlight h) {
//                    }
//
//                    @Override
//                    public void onNothingSelected() {
//                    }
//                });

            }
        });

        return view;
    }

    private void createPieChart(List<Float> yData, List<String> xData) {
        ArrayList<PieEntry> yEntries = new ArrayList<>();
        ArrayList<String> xEntries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        int[] colorList = getColorList();

        for(int i = 0; i < yData.size(); i++) {
            if (xData.get(i) == null) {
                xEntries.add("Default Value");
                yEntries.add(new PieEntry(yData.get(i), "Default Value"));
            } else {
                xEntries.add(xData.get(i));
                yEntries.add(new PieEntry(yData.get(i), xData.get(i)));
            }

            colors.add(Integer.valueOf(colorList[i]));
        }

        // create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntries, "Pain Location");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        // add colors to data set
        pieDataSet.setColors(colors);

        // add legends to the chart
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);

        // create pie data object
        PieData pieData = new PieData(pieDataSet);

        Description description = new Description();
        description.setText("Pain Location Distribution");
        description.setTextSize(25);

        pieChart.setDescription(description);

        pieChart.setRotationEnabled(true);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setCenterTextSize(10);

        pieChart.setEntryLabelColor(Color.BLACK);

        pieChart.setData(pieData);
        pieChart.setVisibility(View.VISIBLE);
        pieChart.animateY(2000);
        pieChart.setTouchEnabled(true);

        pieChart.invalidate();
    }

    private int[] getColorList() {
        int[] colors = {
                Color.rgb(255, 51, 153),
                Color.rgb(102, 153, 0),
                Color.rgb(0, 102, 255),
                Color.rgb(153, 102, 255),
                Color.rgb(255, 204, 102),
                Color.rgb(51, 204, 204),
                Color.rgb(204, 0, 0),
                Color.rgb(204, 153, 0),
                Color.rgb(0, 204, 0),
                Color.rgb(102, 102, 153),
                Color.rgb(204, 153, 255)
        };
        return colors;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        pieBinding = null;
    }
}
