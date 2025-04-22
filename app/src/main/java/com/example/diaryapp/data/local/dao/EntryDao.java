package com.example.diaryapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.diaryapp.data.local.entities.Entry;
import com.example.diaryapp.data.local.entities.MoodCount;

import java.util.List;

@Dao
public interface EntryDao {
    @Insert
    long insertEntry(Entry entry);

    @Query("SELECT * FROM entries WHERE user_id  = :userId ORDER BY created_at DESC")
    List<Entry> getEntriesByUserId(long userId);

    @Query("SELECT * FROM entries WHERE id = :entryId")
    Entry getEntryById(long entryId);

    @Update
    void updateEntry(Entry newEntry);

    @Query("SELECT mood, COUNT(*) as count FROM entries WHERE user_id = :userId GROUP BY mood")
    List<MoodCount> getMoodStatistics(long userId);
}
