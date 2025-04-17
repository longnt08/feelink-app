package com.example.diaryapp.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.diaryapp.data.local.entities.Entry;

import java.util.List;

@Dao
public interface EntryDao {
    @Insert
    long insertEntry(Entry entry);

    @Query("SELECT * FROM entries WHERE user_id  = :userId ORDER BY created_at DESC")
    List<Entry> getEntriesByUserId(int userId);

    @Query("SELECT * FROM entries WHERE id = :entryId")
    Entry getEntryById(long entryId);

    @Update
    void updateEntry(Entry newEntry);
}
