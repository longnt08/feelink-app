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

    // Thêm các trường mới hỗ trợ đồng bộ
    @ColumnInfo(name = "updated_at")
    public long updatedAt;

    @ColumnInfo(name = "last_synced_at")
    public long lastSyncedAt;

    @ColumnInfo(name = "firebase_id")
    public String firebaseId;

    @ColumnInfo(name = "is_synced")
    public boolean isSynced;

    @ColumnInfo(name = "is_deleted")
    public boolean isDeleted;


    public Entry(long userId, String title, String content, String mood, long createdAt) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.mood = mood;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
        this.lastSyncedAt = 0;
        this.isSynced = false;
        this.isDeleted = false;
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

    // Thêm getters và setters cho các trường mới
    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getLastSyncedAt() {
        return lastSyncedAt;
    }

    public void setLastSyncedAt(long lastSyncedAt) {
        this.lastSyncedAt = lastSyncedAt;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public long getId() {
        return id;
    }
}
