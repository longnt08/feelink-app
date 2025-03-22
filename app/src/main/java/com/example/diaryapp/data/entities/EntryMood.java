package com.example.diaryapp.data.entities;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
        tableName = "entry_moods",
        primaryKeys = {"entryId", "moodId"},
        foreignKeys = {
                @ForeignKey(entity = Entry.class, parentColumns = "id", childColumns = "entryId", onDelete = CASCADE),
                @ForeignKey(entity = Mood.class, parentColumns = "id", childColumns = "moodId", onDelete = CASCADE)
        }
)
public class EntryMood {
    public int entryId;
    public int moodId;
}
