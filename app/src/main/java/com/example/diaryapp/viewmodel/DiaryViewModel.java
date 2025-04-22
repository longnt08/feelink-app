package com.example.diaryapp.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.diaryapp.data.DiaryDatabase;
import com.example.diaryapp.data.local.entities.Entry;
import com.example.diaryapp.data.local.entities.MoodCount;

import java.util.List;
import java.util.concurrent.Executors;

public class DiaryViewModel extends ViewModel {
    private final MutableLiveData<List<Entry>> entriesLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<MoodCount>> moodStatisticsLiveData = new MutableLiveData<>();
    private DiaryDatabase diaryDatabase;
    private MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<List<MoodCount>> getMoodStatistics() {
        return moodStatisticsLiveData;
    }
    public void loadMoodStats(long userId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<MoodCount> result = diaryDatabase.entryDao().getMoodStatistics(userId);
            moodStatisticsLiveData.postValue(result);
            Log.d("userID", "userID: " + userId);
            Log.d("DiaryViewModel", "Loaded mood stats: " + result.size());
        });
    }
    public void init(Context context) {
        if (diaryDatabase == null) {
            diaryDatabase = DiaryDatabase.getInstance(context);
        }
    }

    public LiveData<List<Entry>> getEntries() {
        return entriesLiveData;
    }

    public void loadEntries(long userId) {
       loading.postValue(true);
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Entry> result = diaryDatabase.entryDao().getEntriesByUserId(userId);
            entriesLiveData.postValue(result);
            loading.postValue(false);
            Log.d("DiaryViewModel", "Loaded entries: " + result.size());
        });
    }
}
