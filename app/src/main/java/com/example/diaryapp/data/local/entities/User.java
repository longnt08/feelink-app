package com.example.diaryapp.data.local.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "users",
        indices = {@Index(value = "email", unique = true)})
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "username")
    public String username;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "password_hash")
    public String passwordHash;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    public User(String username, String email, String passwordHash, long createdAt) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }
}
