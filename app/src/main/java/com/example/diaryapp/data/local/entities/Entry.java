package com.example.diaryapp.data.local.entities;

import static androidx.room.ForeignKey.CASCADE;

import android.os.Build;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Entity(tableName = "entries",
        foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "user_id", onDelete = CASCADE))
public class Entry {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "user_id")
    public long userId;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "mood")
    public String mood;

    @ColumnInfo(name = "content")
    public String content;

    @ColumnInfo(name = "created_at")
    public long createdAt;


    public Entry(long userId, String title, String content, String mood, long createdAt) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.mood = mood;
        this.createdAt = createdAt;
    }


    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getMood() {
        return mood;
    }

    public void setId(long id) {
        this.id = id;
    }
}
