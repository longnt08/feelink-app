package com.example.diaryapp.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.diaryapp.data.local.dao.EntryDao;
import com.example.diaryapp.data.local.dao.SettingsDao;
import com.example.diaryapp.data.local.dao.UserDao;
import com.example.diaryapp.data.local.entities.Entry;
import com.example.diaryapp.data.local.entities.Media;
import com.example.diaryapp.data.local.entities.Settings;
import com.example.diaryapp.data.local.entities.User;

@Database(entities = {User.class, Settings.class, Entry.class, Media.class}, version = 2)
public abstract class DiaryDatabase extends RoomDatabase {
    private static volatile DiaryDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract SettingsDao settingsDao();
    public abstract EntryDao entryDao();

    public static DiaryDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (DiaryDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    DiaryDatabase.class, "diary_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
