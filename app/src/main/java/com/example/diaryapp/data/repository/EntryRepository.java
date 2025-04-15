package com.example.diaryapp.data.repository;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.diaryapp.data.DiaryDatabase;
import com.example.diaryapp.data.local.dao.EntryDao;
import com.example.diaryapp.data.local.entities.Entry;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EntryRepository {
    private final EntryDao entryDao;

    public EntryRepository(Context context) {
        DiaryDatabase diaryDatabase = DiaryDatabase.getInstance(context);
        entryDao = diaryDatabase.entryDao();
    }

    public void insertDiary(Entry entry) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Log.d("DEBUG", "Insert running in background");
                long res = entryDao.insertEntry(entry);
                Log.d("DEBUG", "Entry inserted with ID: " + res);
            } catch (Exception e) {
                Log.e("ERROR", "Insert failed", e);
            }
        });
    }

    // TODO: create update method
    public void updateDiary(Entry newEntry) {
        Executors.newSingleThreadExecutor().execute(() -> {
            entryDao.updateEntry(newEntry);
        });
    }
}
