package com.example.diaryapp.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.diaryapp.R;
import com.example.diaryapp.data.DiaryDatabase;
import com.example.diaryapp.data.local.dao.EntryDao;
import com.example.diaryapp.data.local.entities.Entry;
import com.example.diaryapp.data.local.entities.User;
import com.example.diaryapp.data.repository.EntryRepository;

public class AddDiaryEntryActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private EditText textTitle, textContent;
    private Button backButton, saveButton;
    private ImageButton emojiButton;
    private EntryRepository entryRepository;
    private EntryDao entryDao;
    private DiaryDatabase diaryDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        calendarView = findViewById(R.id.calendarView);
        textTitle = findViewById(R.id.textTitle);
        textContent = findViewById(R.id.textContent);
        backButton = findViewById(R.id.backButton);
        saveButton = findViewById(R.id.saveButton);
        emojiButton = findViewById(R.id.imageButton);

        entryRepository = new EntryRepository(this);

        // xu ly su kien an nut back
        backButton.setOnClickListener(v -> finish());

        // su ly su kien an nut save
        saveButton.setOnClickListener(v -> saveDiaryEntry());
    }

    private void saveDiaryEntry() {
        Log.d("DEBUG", "saveDiaryEntry() called");

        long selectedDateInMillis = calendarView.getDate();
        String title = textTitle.getText().toString();
        String content = textContent.getText().toString();

        Log.d("DEBUG", "Title " + title);
        Log.d("DEBUG", "Content " + content);
        Log.d("DEBUG", "Date " + selectedDateInMillis );

        if(title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please enter both title and content", Toast.LENGTH_SHORT).show();
            return;
        }

        Entry newEntry = new Entry(1, title, content, selectedDateInMillis);
        Log.d("DEBUG", "New entry created");
        entryRepository.insertDiary(newEntry);
        Log.d("DEBUG", "Insert called");

        // thong bao luu thanh cong
        Toast.makeText(this, "Diary saved successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
