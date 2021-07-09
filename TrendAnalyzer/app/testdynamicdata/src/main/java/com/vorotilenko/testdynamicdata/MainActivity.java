package com.vorotilenko.testdynamicdata;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LineChart chart = findViewById(R.id.chart);
        int GRAPH_WIDTH = 10;
        LineData lineData = chart.getData();
        LineDataSet lineDataSet = (LineDataSet) lineData.getDataSetByIndex(0);
        int count = lineDataSet.getEntryCount();

        // Make rolling window
        if (lineData.getXValCount() <= count) {
            // Remove/Add XVal
            lineData.getXVals().add("" + count);
            lineData.getXVals().remove(0);

            // Move all entries 1 to the left..
            for (int i = 0; i < count; i++) {
                Entry e = lineDataSet.getEntryForXIndex(i);
                if (e == null) continue;

                e.setXIndex(e.getXIndex() - 1);
            }

            // Set correct index to add value
            count = GRAPH_WIDTH;
        }

        // Add new value
        lineData.addEntry(new Entry([random value],count),0);

        // Make sure to draw
        chart.notifyDataSetChanged();
        chart.invalidate();
    }
}