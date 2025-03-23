package com.example.diaryapp.data.repository;

import android.app.Application;

import com.example.diaryapp.data.DiaryDatabase;
import com.example.diaryapp.data.local.dao.EntryDao;
import com.example.diaryapp.data.local.entities.Entry;

public class EntryRepository {
    private EntryDao entryDao;

    public EntryRepository(Application application) {
        DiaryDatabase diaryDatabase = DiaryDatabase.getInstance(application);
        entryDao = diaryDatabase.entryDao();
    }

    public void insertDiary(Entry entry) {
        new Thread(() -> entryDao.insertNewEntry(entry)).start();
    }
}
