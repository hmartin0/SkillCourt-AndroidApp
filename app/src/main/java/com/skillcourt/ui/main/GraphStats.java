package com.skillcourt.ui.main;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.skillcourt.R;
import com.skillcourt.structures.PlayerData;

import java.util.ArrayList;

public class GraphStats extends Fragment {

    private LineChart myLineChart;
    private ArrayList<String> xAxisdateList;
    private ArrayList<String> hitList;

    public GraphStats() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.graph_stats_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null)
        {
            myLineChart = getActivity().findViewById(R.id.lineChartGraph);
            ArrayList<Entry> yValues = new ArrayList<>();
            ArrayList<String> xAxisLabel = new ArrayList<>();

            xAxisdateList = getArguments().getStringArrayList("dateData");
            hitList = getArguments().getStringArrayList("hitData");
            int xValue = 0;

            for(int i = xAxisdateList.size() - 1; i >= 0;i--)
            {
                // get hit/miss percentage
                String score = hitList.get(i);
                String numerator =  score.substring(0,score.indexOf("/"));
                String denominator =  score.substring(score.indexOf("/") + 1);
                float yValue = (Float.parseFloat(numerator)/Float.parseFloat(denominator)) * 100;
                yValue = (float)(Math.round(yValue * 100.0) / 100.0);

                yValues.add(new Entry(xValue,yValue));
                xAxisLabel.add(xAxisdateList.get(i));
                xValue++;
                //System.out.println("GO IT " + xAxisdateList.get(i) + " : " + hitList.get(i));

            }

            LineDataSet mySet = new LineDataSet(yValues,"Hit/Miss Percent");
            mySet.setColor(Color.parseColor("#f20707"));
            mySet.setLineWidth(3f);
            mySet.setValueTextColor(Color.parseColor("#16191f"));
            mySet.setValueTextSize(12f);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(mySet);

            LineData theData = new LineData(dataSets);
            myLineChart.setData(theData);
            myLineChart.invalidate();

            myLineChart.setDragEnabled(true);
            //myLineChart.setScaleEnabled(false);
            myLineChart.getAxisRight().setEnabled(false);

            myLineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
            myLineChart.getXAxis().setLabelRotationAngle(-45);
            myLineChart.getXAxis().setCenterAxisLabels(false);
            myLineChart.getXAxis().setAxisMaximum(xValue);
            myLineChart.getXAxis().setLabelCount(xValue);
            myLineChart.getDescription().setText("Hit/Miss From HIT Game Type");
            myLineChart.getDescription().setTextSize(12f);
            myLineChart.getDescription().setTextColor(Color.parseColor("#16191f"));
        }
    }
}
