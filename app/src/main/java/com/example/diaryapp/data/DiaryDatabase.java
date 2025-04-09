package com.example.diaryapp.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.diaryapp.data.local.dao.EntryDao;
import com.example.diaryapp.data.local.dao.SettingsDao;
import com.example.diaryapp.data.local.dao.UserDao;
import com.example.diaryapp.data.local.entities.Entry;
import com.example.diaryapp.data.local.entities.EntryMood;
import com.example.diaryapp.data.local.entities.Media;
import com.example.diaryapp.data.local.entities.Mood;
import com.example.diaryapp.data.local.entities.Settings;
import com.example.diaryapp.data.local.entities.User;
import com.example.diaryapp.data.repository.UserRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Database(entities = {User.class, Settings.class, Entry.class, Media.class, Mood.class, EntryMood.class}, version = 1)
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
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        UserRepository userRepository = new UserRepository(context);
                                        User user = new User("Goku", "goku@gmail.com", "12345678", System.currentTimeMillis());

                                        userRepository.insertNewUser(user);
                                    });
                                }
                            })
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
