package com.example.diaryapp.data.local.entities;

public class FrequencyCount {
    public String date;
    public int count;

    public FrequencyCount(String date, int count) {
        this.date = date;
        this.count = count;
    }


    public String getDate() {
        return date;
    }

    public int getCount() {
        return count;
    }
}
