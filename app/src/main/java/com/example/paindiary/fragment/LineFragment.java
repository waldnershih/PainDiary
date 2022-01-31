package com.example.paindiary.fragment;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.paindiary.R;
import com.example.paindiary.databinding.LineFragmentBinding;
import com.example.paindiary.entity.PainRecord;
import com.example.paindiary.converter.DateStringConverter;
import com.example.paindiary.viewmodel.PainRecordViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LineFragment extends Fragment {
    private LineFragmentBinding lineBinding;
    private LineChart lineChart;
    private DatePickerDialog.OnDateSetListener sDateSetListener;
    private DatePickerDialog.OnDateSetListener eDateSetListener;
    private PainRecordViewModel painRecordViewModel;

    private final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private long[] dateListSelected = {-1l, -1l};
    private String[] datesSelected = {"", ""};
    private String[] variable = {""};    // variable selected
    private List<String> dates;
    private List<Float> painLevels;
    private List<Float> variableValues;

    public LineFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        lineBinding = LineFragmentBinding.inflate(inflater, container, false);
        View view = lineBinding.getRoot();
        getActivity().setTitle("Pain and Weather Line Chart");

        lineChart = (LineChart) lineBinding.idLineChart;

        painRecordViewModel = ViewModelProvider
                .AndroidViewModelFactory
                .getInstance(getActivity().getApplication())
                .create(PainRecordViewModel.class);

        dates = new ArrayList<>();
        painLevels = new ArrayList<>();
        variableValues = new ArrayList<>();
        List<String> weatherVariables = getWeatherVariable();

        final ArrayAdapter<String> painLevelSpinnerAdapter = new ArrayAdapter<String>(
                getActivity(), R.layout.my_spinner_2, weatherVariables);
        lineBinding.weatherVariableSpinner.setAdapter(painLevelSpinnerAdapter);

        lineBinding.weatherVariableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedVariable = parent.getItemAtPosition(position).toString();
                if (selectedVariable != null) {
                    variable[0] = selectedVariable;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        lineBinding.startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate("start");
            }
        });

        lineBinding.endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate("end");
            }
        });

        sDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Date startDate = getDate(dayOfMonth, month + 1, year);
                dateListSelected[0] = startDate.getTime();

                String dayStr = "";
                if (dayOfMonth < 10) {
                    dayStr += "0" + dayOfMonth;
                } else {
                    dayStr += dayOfMonth;
                }

                String date = "" + dayStr + " - " + months[month] + " - " + year;

                datesSelected[0] = date;
                lineBinding.startDate.setText(date);
            }
        };

        eDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Date endDate = getDate(dayOfMonth, month + 1, year);
                dateListSelected[1] = endDate.getTime();

                String dayStr = "";
                if (dayOfMonth < 10) {
                    dayStr += "0" + dayOfMonth;
                } else {
                    dayStr += dayOfMonth;
                }

                String date = "" + dayStr + " - " + months[month] + " - " + year;

                datesSelected[1] = date;
                lineBinding.endDate.setText(date);
            }
        };

        lineBinding.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dates.clear();
                painLevels.clear();
                variableValues.clear();

                setRecordBetweenDate();
            }
        });

        lineBinding.testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineBinding.correlationResult.setText(testCorrelation());
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        lineBinding = null;
    }

    private void drawLindChart() {
        Legend legend = lineChart.getLegend();
        legend.setWordWrapEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setTextSize(15);
        legend.setDrawInside(false);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setGranularity(10f);
        rightAxis.setAxisMinimum(0f);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        rightAxis.setGranularity(1f);
        leftAxis.setAxisMinimum(0f);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);

        int size = dates.size();
        if (size == 0) {
            Toast.makeText(getActivity(), "No pain records between these two days", Toast.LENGTH_SHORT).show();
        }

        String[] xText = new String[size];
        for (int i = 0; i < size; i++) {
            xText[i] = dates.get(i);
         }

        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

        lineDataSets.add(getPainLevelDataSet());
        lineDataSets.add(getWeatherVariableDataSet());
        lineChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xText));

        LineData data = new LineData(lineDataSets);
        lineChart.setData(data);
        lineChart.getDescription().setEnabled(false);
        lineChart.setVisibility(View.VISIBLE);
        lineChart.animateY(2000);

        lineChart.invalidate();
    }

    public Date getDate(int day, int month, int year) {
        String dayText = String.valueOf(day);
        String monthText = String.valueOf(month);
        String yearText = String.valueOf(year);

        if (day < 10) { dayText = "0" + dayText; }

        if (month < 10) { monthText = "0" + monthText; }

        for (int i = 4; i > yearText.length(); i--) {
            yearText = "0" + yearText;
        }

        String dateText = dayText + "-" + monthText + "-" + yearText;

        return DateStringConverter.parseStrToDate("dd-MM-yyyy", dateText);
    }

    private LineDataSet getPainLevelDataSet() {

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int i = 0; i < painLevels.size(); i++){
            float level = painLevels.get(i);
            entries.add(new Entry(i, level));
        }

        LineDataSet set = new LineDataSet(entries, "Pain Level");
        set.setColor(Color.rgb(255,0,0));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(255,0,0));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(255,0,0));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(255,0,0));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        return set;
    }

    private List<String> getWeatherVariable() {
        List<String> weatherVariables = new ArrayList<>();
        weatherVariables.add("Temperature");
        weatherVariables.add("Humidity");
        weatherVariables.add("Pressure");

        return weatherVariables;
    }

    private LineDataSet getWeatherVariableDataSet() {
        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int i = 0; i < variableValues.size(); i++){
            float num = variableValues.get(i);
            entries.add(new Entry(i, num));
        }

        LineDataSet set = new LineDataSet(entries, variable[0]);
        set.setColor(Color.rgb(0,0,255));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(0,0,255));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(0,0,255));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(0,0,255));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        return set;
    }

    private void selectDate(String type) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        if (type.equals("start")) {
            DatePickerDialog dialog = new DatePickerDialog(
                    getActivity(),
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    sDateSetListener,
                    year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        } else if (type.equals("end")) {
            DatePickerDialog dialog = new DatePickerDialog(
                    getActivity(),
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    eDateSetListener,
                    year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }

    }

    private void setRecordBetweenDate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            CompletableFuture<List<PainRecord>> painRecordCompletableFuture = painRecordViewModel
                    .findPainRecordBetweenDate(dateListSelected[0], dateListSelected[1]);
            painRecordCompletableFuture.thenApply(painRecords -> {
                if (painRecords.size() != 0) {
                    for (PainRecord painRecord: painRecords) {
                        dates.add(DateStringConverter.parseDateToStr("dd-MM-yyyy", painRecord.dateEntry));
                        painLevels.add((float) painRecord.painLevel);
                        if (variable[0].equals("Temperature")) {
                            variableValues.add((float) painRecord.temp);
                        } else if (variable[0].equals("Humidity")){
                            variableValues.add((float) painRecord.humidity);
                        } else{
                            variableValues.add((float) painRecord.pressure);
                        }
                    }
                }
                getActivity().runOnUiThread(() -> {
                    if (dateListSelected[0] > dateListSelected[1]) {
                        Toast.makeText(getActivity(), "End date have to be before Start date !", Toast.LENGTH_SHORT).show();
                    } else if (dateListSelected[0] > 0 && dateListSelected[1] > 0 && variable[0] != null) {
                        drawLindChart();
                    } else {
                        Toast.makeText(getActivity(),
                                "Start Date, End date and Weather Variable" + "\n" + "All cannot be empty !",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return painRecords;
            });
        }
    }

    public String testCorrelation(){
        if (painLevels.size() > 9) {
            int len = painLevels.size();
            int pNum = 0;
            int vNum = 0;

            double data[][] = new double[len][2];
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    if (pNum <= vNum) {
                        data[i][j] = painLevels.get(pNum).doubleValue();
                        pNum++;
                    }else {
                        data[i][j] = variableValues.get(vNum).doubleValue();
                        vNum++;
                    }
                }
            }

            RealMatrix m = MatrixUtils.createRealMatrix(data);

            for (int i = 0; i < m.getColumnDimension(); i++) {
                for (int j = 0; j < m.getColumnDimension(); j++) {
                    PearsonsCorrelation pc = new PearsonsCorrelation();
                    double cor = pc.correlation(m.getColumn(i), m.getColumn(j));
                    System.out.println(i + "," + j + "=[" + String.format(".%2f", cor) + "," + "]");
                }
            }

            PearsonsCorrelation pc = new PearsonsCorrelation(m);
            RealMatrix corM = pc.getCorrelationMatrix();
            RealMatrix pM = pc.getCorrelationPValues();
            return ("P value: " + pM.getEntry(0, 1)+ "\n" + "Correlation: " + corM.getEntry(0, 1));
        }
        return "Pain Records are lower than 10";
    }
}

/**
 * Reference:
 * https://www.youtube.com/watch?v=hwe1abDO2Ag&list=PLgCYzUzKIBE_e_pvdwnIikDrZVHM8CM-e&ab_channel=CodingWithMitch
 */