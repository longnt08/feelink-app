package com.example.diaryapp.data;

import android.content.Context;
import android.nfc.Tag;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.diaryapp.data.dao.EntryDao;
import com.example.diaryapp.data.dao.SettingsDao;
import com.example.diaryapp.data.dao.UserDao;
import com.example.diaryapp.data.entities.Entry;
import com.example.diaryapp.data.entities.EntryMood;
import com.example.diaryapp.data.entities.Media;
import com.example.diaryapp.data.entities.Mood;
import com.example.diaryapp.data.entities.Settings;
import com.example.diaryapp.data.entities.User;

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
