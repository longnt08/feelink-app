package com.example.diaryapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.diaryapp.data.local.entities.Entry;
import com.example.diaryapp.data.local.entities.FrequencyCount;
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

    @Query("SELECT strftime('%Y-W%W', created_at / 1000, 'unixepoch') as date, COUNT(*) as count " +
            "FROM entries WHERE user_id = :userId GROUP BY strftime('%Y-W%W', created_at / 1000, 'unixepoch') ORDER BY date")
    List<FrequencyCount> getEntryFrequencyByWeek(long userId);
    
    // Thêm các phương thức hỗ trợ đồng bộ
    @Query("SELECT * FROM entries WHERE is_synced = 0 AND user_id = :userId AND is_deleted = 0")
    List<Entry> getUnsyncedEntries(long userId);
    
    @Query("UPDATE entries SET is_synced = 1, last_synced_at = :timestamp, firebase_id = :firebaseId WHERE id = :id")
    void markAsSynced(long id, String firebaseId, long timestamp);
    
    @Query("SELECT * FROM entries WHERE updated_at > last_synced_at AND user_id = :userId")
    List<Entry> getModifiedSinceLastSync(long userId);
    
    @Query("UPDATE entries SET is_deleted = 1, updated_at = :timestamp WHERE id = :id")
    void markAsDeleted(long id, long timestamp);
    
    @Query("SELECT * FROM entries WHERE is_deleted = 1 AND is_synced = 0 AND user_id = :userId")
    List<Entry> getDeletedUnsyncedEntries(long userId);
    
    @Query("DELETE FROM entries WHERE id = :id")
    void deleteEntry(long id);
}
