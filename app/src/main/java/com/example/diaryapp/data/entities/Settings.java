package com.example.diaryapp.data.entities;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "settings",
        foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId", onDelete = CASCADE))
public class Settings {
    @PrimaryKey
    public int userId;

    @ColumnInfo(name = "dark_mode")
    public boolean darkMode;

    @ColumnInfo(name = "language")
    public String language;

}
