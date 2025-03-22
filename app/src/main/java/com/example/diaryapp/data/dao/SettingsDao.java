package com.example.diaryapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.diaryapp.data.entities.Settings;

@Dao
public interface SettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(Settings settings);

    @Query("SELECT * FROM settings WHERE userId = :userId")
    Settings getSettingsByUserId(int userId);

}
