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
import com.example.diaryapp.data.local.entities.FrequencyCount;
import com.example.diaryapp.data.local.entities.MoodCount;
import com.example.diaryapp.viewmodel.DiaryViewModel;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private long currentUserId = -1;
    private PieChart pieChart;
    private LineChart lineChart;
    private EntryDao entryDao;
    private String currentUserName;
    private TextView usernameTextView;
    DiaryViewModel diaryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);

        // setup layout
        setupLayout();

        // tao bieu do tron
        diaryViewModel.loadMoodStats(currentUserId);

        diaryViewModel.getMoodStatistics().observe(this, moodCounts -> {
            if(moodCounts != null && !moodCounts.isEmpty()) {
                setupEmotionPieChart(pieChart, moodCounts);
            }
        });

        // tao bieu do duong
        diaryViewModel.loadFrequencyStats(currentUserId);
        diaryViewModel.getFrequencyStats().observe(this, frequencyCounts -> {
            if(frequencyCounts != null && !frequencyCounts.isEmpty()) {
                setupFrequencyLineChart(lineChart, frequencyCounts);
            }
        });
    }

    private void setupLayout() {
        //lay username & id
        currentUserName = getIntent().getStringExtra("username");
        currentUserId = getIntent().getLongExtra("user_id", -1);

        // khoi tao diaryViewModel
        diaryViewModel = new DiaryViewModel();
        diaryViewModel.init(this);

        // anh xa view
        pieChart = findViewById(R.id.emotionPieChart);
        lineChart = findViewById(R.id.frequencyLineChart);
        usernameTextView = findViewById(R.id.usernameText);

        // gan gia tri
        usernameTextView.setText(currentUserName);
    }

    private void setupEmotionPieChart(PieChart pieChart, List<MoodCount> moodCounts) {
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for (MoodCount item : moodCounts) {
            entries.add(new PieEntry(item.getCount(), item.getMood()));
            colors.add(getColorForEmotion(item.getMood()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Emotion");
        dataSet.setColors(colors);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.setDescription(null);
        pieChart.setCenterText("@strings/emotion");
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.animateY(1000, Easing.EaseInOutQuad);
        pieChart.invalidate();
    }

    private int getColorForEmotion(String mood) {
        switch (mood.toLowerCase()) {
            case "happy":
                return Color.parseColor("#FFD700"); // Vàng tươi
            case "sad":
                return Color.parseColor("#2F4F4F"); // Xanh lam đậm hoặc xám
            case "angry":
                return Color.parseColor("#FF4500"); // Đỏ cam
            case "love":
                return Color.parseColor("#FF69B4"); // Hồng
            case "thinking":
                return Color.parseColor("#9370DB"); // Tím nhạt
            case "normal":
                return Color.parseColor("#90EE90"); // Xanh lá nhạt
            default:
                return Color.GRAY;
        }
    }


    private void setupFrequencyLineChart(LineChart lineChart, List<FrequencyCount> frequencyCounts) {
        List<com.github.mikephil.charting.data.Entry> chartEntries = new ArrayList<>();
        List<String> weekLabels = new ArrayList<>();
        
        for(int i = 0; i < frequencyCounts.size(); i++) {
            chartEntries.add(new Entry((float)i, (float) frequencyCounts.get(i).count));
            weekLabels.add(frequencyCounts.get(i).getDate());
        }

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(weekLabels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        LineDataSet dataSet = new LineDataSet(chartEntries, "Number of diaries");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.CYAN);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.setDescription(null);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.animateX(1000);
        lineChart.invalidate();
    }
}
