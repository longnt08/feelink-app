package com.example.diaryapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.diaryapp.data.local.entities.User;
import com.example.diaryapp.data.repository.UserRepository;

public class UserViewModel extends AndroidViewModel {
    private UserRepository repository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
    }

    public void insertNewUser(User user) {
        repository.insertNewUser(user);
    }
    
    // Add synchronous user insertion method
    public long insertUserSync(User user) {
        return repository.insertUserSync(user);
    }

    public User getUserByEmail(String email) {
        return repository.getUserByEmail(email);
    }
}
