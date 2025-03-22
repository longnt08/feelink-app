package com.example.diaryapp.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "media")
public class Media {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "entry_id")
    public int entryId;

    @ColumnInfo(name = "file_path")
    public String filePath;

    @ColumnInfo(name = "type")
    public String type;

    @ColumnInfo(name = "created_at")
    public long createdAt;
}
