package com.example.diaryapp.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.diaryapp.data.local.dao.EntryDao;
import com.example.diaryapp.data.local.dao.SettingsDao;
import com.example.diaryapp.data.local.dao.UserDao;
import com.example.diaryapp.data.local.entities.Entry;
import com.example.diaryapp.data.local.entities.EntryMood;
import com.example.diaryapp.data.local.entities.Media;
import com.example.diaryapp.data.local.entities.Mood;
import com.example.diaryapp.data.local.entities.Settings;
import com.example.diaryapp.data.local.entities.User;

@Database(entities = {User.class, Settings.class, Entry.class, Media.class, Mood.class, EntryMood.class}, version = 2)
public abstract class JournalDatabase extends RoomDatabase {
    private static volatile JournalDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract SettingsDao settingsDao();
    public abstract EntryDao entryDao();

    public static JournalDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (JournalDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    JournalDatabase.class, "journal_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
