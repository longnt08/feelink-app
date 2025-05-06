package com.example.diaryapp.data.local.entities;

public class MoodCount {
    private String mood;
    private int count;

    public MoodCount(String mood, int count) {
        this.mood = mood;
        this.count = count;
    }

    public String getMood() {
        return mood;
    }

    public int getCount() {
        return count;
    }
}
