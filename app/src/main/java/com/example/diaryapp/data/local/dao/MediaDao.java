package com.example.diaryapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.diaryapp.data.entities.Media;

import java.util.List;

@Dao
public interface MediaDao {
    @Insert
    void insertMedia(Media media);

    @Query("SELECT * FROM media WHERE entry_id = :entryId")
    List<Media> getMediaByEntryId(int entryId);
}
