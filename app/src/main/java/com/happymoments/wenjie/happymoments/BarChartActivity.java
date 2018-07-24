package com.happymoments.wenjie.happymoments;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BarChartActivity extends AppCompatActivity {
    // barchart
    BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);

        // barChart set
        barChart = (BarChart) findViewById(R.id.bar_chart);

        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);

        // get the hashmap passed from profile activity
        HashMap<String, Integer> map = (HashMap) getIntent().getSerializableExtra("hashmap");
//        String[] tags = new String[]{"Food", "Home", "Books", "Seattle", "Love", "Summer"};

        ArrayList<String> rawTags = new ArrayList<>();
        rawTags.add("Test");
        ArrayList<Integer> rawPoints = new ArrayList<>();
        map.forEach((k, v) -> {
            rawTags.add(k);
            rawPoints.add(v);
        });

        String[] tags = new String[6];
        tags = rawTags.toArray(tags);

        ArrayList<BarEntry> barEntries = new ArrayList<>();

        barEntries.add(new BarEntry(1, rawPoints.get(0)));
        barEntries.add(new BarEntry(2, rawPoints.get(1)));
        barEntries.add(new BarEntry(3, rawPoints.get(2)));
        barEntries.add(new BarEntry(4, rawPoints.get(3)));
        barEntries.add(new BarEntry(5, rawPoints.get(4)));


        BarDataSet barDataSet = new BarDataSet(barEntries, "Happy tags");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData data = new BarData(barDataSet);
        data.setBarWidth(0.8f);
        barChart.setData(data);


        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(tags));
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setGranularity(1);
//        xAxis.setCenterAxisLabels(true);
//        xAxis.setAxisMinimum(1);

    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter{
        private String[] mValues;
        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int)value];
        }
    }
}
