package com.example.astrojet;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class ChartsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);

        LineChart lineChart = (LineChart)findViewById(R.id.pressure_chart);
        List<Entry> entries = new ArrayList<Entry>();

        entries.add(new Entry(0, 20));
        entries.add(new Entry(10, 40));

        LineDataSet lineDataSet = new LineDataSet(entries, "Pressure");
        lineDataSet.setColor(Color.CYAN);
        lineDataSet.setValueTextColor(Color.WHITE);
        lineDataSet.setDrawHighlightIndicators(true);

        LineData lineData = new LineData(lineDataSet);

        lineChart.setData(lineData);
        lineChart.invalidate();

    }
}