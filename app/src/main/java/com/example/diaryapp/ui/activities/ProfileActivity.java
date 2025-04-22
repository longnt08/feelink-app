package com.example.diaryapp.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.diaryapp.R;
import com.example.diaryapp.data.DiaryDatabase;
import com.example.diaryapp.data.local.dao.EntryDao;
import com.example.diaryapp.data.local.entities.Entry;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private long currentUserId = -1;
    private PieChart pieChart;
    private LineChart lineChart;
    private EntryDao entryDao;
    private String currentUserName;
    private TextView usernameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);

        // setup layout
        setupLayout();

        // tao bieu do
        setupEmotionPieChart(pieChart);
    }

    private void setupLayout() {
        //lay username
        currentUserName = getIntent().getStringExtra("username");

        // anh xa view
        pieChart = findViewById(R.id.emotionPieChart);
        lineChart = findViewById(R.id.frequencyLineChart);
        usernameTextView = findViewById(R.id.usernameText);

        // gan gia tri
        usernameTextView.setText(currentUserName);
    }

    private void setupEmotionPieChart(PieChart pieChart) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(40f, "Vui"));
        entries.add(new PieEntry(25f, "Buồn"));
        entries.add(new PieEntry(15f, "Tức giận"));
        entries.add(new PieEntry(20f, "Chill"));

        PieDataSet dataSet = new PieDataSet(entries, "Cảm xúc");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.setDescription(null);
        pieChart.setCenterText("Cảm Xúc");
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.animateY(1000, Easing.EaseInOutQuad);
        pieChart.invalidate();
    }


//    private void setupFrequencyLineChart(LineChart lineChart) {
//        List<Entry> entries = new ArrayList<>();
//        entryDao = DiaryDatabase.getInstance(this).entryDao();
//
//        entries = entryDao.getEntriesByUserId(currentUserId);
//        LineDataSet dataSet = new LineDataSet(entries, "Số bài viết");
//        dataSet.setColor(Color.BLUE);
//        dataSet.setValueTextColor(Color.BLACK);
//        dataSet.setCircleRadius(5f);
//        dataSet.setDrawFilled(true);
//        dataSet.setFillColor(Color.CYAN);
//
//        LineData lineData = new LineData(dataSet);
//        lineChart.setData(lineData);
//        lineChart.setDescription(null);
//        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
//        lineChart.getAxisRight().setEnabled(false);
//        lineChart.animateX(1000);
//        lineChart.invalidate();
//    }
}
