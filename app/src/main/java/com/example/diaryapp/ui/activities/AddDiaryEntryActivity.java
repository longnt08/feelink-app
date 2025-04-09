package com.example.diaryapp.ui.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.diaryapp.R;
import com.example.diaryapp.data.DiaryDatabase;
import com.example.diaryapp.data.local.dao.EntryDao;
import com.example.diaryapp.data.local.entities.Entry;
import com.example.diaryapp.data.repository.EntryRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class AddDiaryEntryActivity extends AppCompatActivity {

    private TextView tvSelectedDate;
    private EditText textTitle, textContent;
    private Button saveButton;
    private ImageButton backButton;
    private TextView tvEmojiButton;
    private EntryRepository entryRepository;
    private final long[] selectedTimestamp = new long[1];
    private String selectedEmoji = "😊";
    private String selectedEmojiDescription = "happy";
    private final Map<String, String> emojiMap = new LinkedHashMap<>();
    private View bottomToolBar;
    private View rootLayout;
    private static final int REQUEST_IMAGE_PICK = 1001;
    private static final int REQUEST_AUDIO_RECORD = 1002;
    private static final int REQUEST_BACKGROUND_PICK = 1003;
    private ImageButton btnAddImage, btnRecordAudio, btnChangeBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        // khoi tao map anh xa
        initializeEmojiMap();

        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        textTitle = findViewById(R.id.textTitle);
        textContent = findViewById(R.id.textContent);
        backButton = findViewById(R.id.backButton);
        saveButton = findViewById(R.id.saveButton);
        tvEmojiButton = findViewById(R.id.tvEmojiButton);
        rootLayout = findViewById(R.id.rootLayout);
        bottomToolBar = findViewById(R.id.cardBottomToolbar);
        btnAddImage = findViewById(R.id.btnAddImage);
        btnChangeBackground = findViewById(R.id.btnChangeBackground);
        btnRecordAudio = findViewById(R.id.btnRecordAudio);

        entryRepository = new EntryRepository(this);

        // dat emoji mac dinh va mo ta mac dinh
        selectedEmoji = "😊";
        selectedEmojiDescription = emojiMap.get(selectedEmoji);
        tvEmojiButton.setText(selectedEmoji);

        // xu ly su kien an nut back
        backButton.setOnClickListener(v -> finish());

        // khi bam nut icon
        tvEmojiButton.setOnClickListener(v -> {
            showEmojiSelectorDialog();
        });

        // lay ra ngay hien tai
        Calendar currentCal = Calendar.getInstance();
        // dat gio, phut, giay, mili giay ve 0 de lay timestamp dau ngay
        currentCal.set(Calendar.HOUR_OF_DAY, 0);
        currentCal.set(Calendar.MINUTE, 0);
        currentCal.set(Calendar.SECOND, 0);
        currentCal.set(Calendar.MILLISECOND, 0);
        // luu timestamp cua ngay hien tai lam gia tri mac dinh
        selectedTimestamp[0] = currentCal.getTimeInMillis();
        // dinh dang ngay de hien thi
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDateStr = displayFormat.format(currentCal.getTime());
        //dat cho text view
        tvSelectedDate.setText(currentDateStr);

        // su kien chon ngay
        tvSelectedDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year1, month1, dayOfMonth) -> {
                        Calendar selectedCal = Calendar.getInstance();
                        selectedCal.set(year1, month1, dayOfMonth, 0, 0, 0);
                        selectedTimestamp[0] = selectedCal.getTimeInMillis();

                        String dateStr = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedCal.getTime());
                        tvSelectedDate.setText(dateStr);
                    },
            year, month, day);
            datePickerDialog.show();
        });

        // su ly su kien an nut save
        saveButton.setOnClickListener(v -> saveDiaryEntry(selectedTimestamp[0]));

        // su ly su kien an nut them anh
        btnAddImage.setOnClickListener(v -> openGalleryForImage());
        btnRecordAudio.setOnClickListener(v -> openAudioRecorder());
        btnChangeBackground.setOnClickListener(v -> chooseBackgroundImage());
    }

    private void initializeEmojiMap() {
        emojiMap.put("😊", "happy");
        emojiMap.put("😢", "sad");
        emojiMap.put("😠", "angry");
        emojiMap.put("😍", "love");
        emojiMap.put("🤔", "thinking");
        emojiMap.put("😐", "normal");
    }

    private void showEmojiSelectorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_emoji_selector, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        // tim cac TextView emoji trong dialog
        TextView emojiSimle = dialogView.findViewById(R.id.emoji_smile);
        TextView emojiSad = dialogView.findViewById(R.id.emoji_sad);
        TextView emojiAngry = dialogView.findViewById(R.id.emoji_angry);
        TextView emojiLove = dialogView.findViewById(R.id.emoji_love);
        TextView emojiThinking = dialogView.findViewById(R.id.emoji_thinking);
        TextView emojiNormal = dialogView.findViewById(R.id.emoji_normal);

        // tao mot OnClickListener chung
        View.OnClickListener emojiClickListener = view -> {
            TextView clickedEmojiView = (TextView) view;
            String clickedEmoji = clickedEmojiView.getText().toString();

            // lay mo ta tu map
            String description = emojiMap.get(clickedEmoji);

            if (description != null) { // Kiểm tra xem emoji có trong map không
                selectedEmoji = clickedEmoji;           // Cập nhật emoji cho UI
                selectedEmojiDescription = description; // Cập nhật MÔ TẢ để lưu DB

                tvEmojiButton.setText(selectedEmoji); // Cập nhật TextView chính (hiển thị emoji)
                Toast.makeText(this, "Đã chọn: " + description, Toast.LENGTH_SHORT).show(); // Hiển thị mô tả
            } else {
                // Xử lý trường hợp không tìm thấy mô tả (nếu có thể xảy ra)
                Toast.makeText(this, "Lỗi: Không tìm thấy mô tả cho " + clickedEmoji, Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss(); // Đóng dialog
        };
        // gan listener cho tung emoji
        emojiSimle.setOnClickListener(emojiClickListener);
        emojiSad.setOnClickListener(emojiClickListener);
        emojiAngry.setOnClickListener(emojiClickListener);
        emojiLove.setOnClickListener(emojiClickListener);
        emojiThinking.setOnClickListener(emojiClickListener);
        emojiNormal.setOnClickListener(emojiClickListener);

        dialog.show();
    }

    private void saveDiaryEntry(long date) {
//        Log.d("DEBUG", "saveDiaryEntry() called");

        String title = textTitle.getText().toString();
        String content = textContent.getText().toString();

//        Log.d("DEBUG", "Title " + title);
//        Log.d("DEBUG", "Content " + content);
//        Log.d("DEBUG", "Date " + selectedDateInMillis );

        if(title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please enter both title and content", Toast.LENGTH_SHORT).show();
            return;
        }

        Entry newEntry = new Entry(6, title, content, selectedEmojiDescription, date);
//        Log.d("DEBUG", "New entry created");
        entryRepository.insertDiary(newEntry);
//        Log.d("DEBUG", "Insert called");

        // thong bao luu thanh cong
        Toast.makeText(this, "Diary saved successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void openGalleryForImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void openAudioRecorder() {
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        startActivityForResult(intent, REQUEST_AUDIO_RECORD);
    }

    private void chooseBackgroundImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_BACKGROUND_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && data != null) {
            Uri selectedUri = data.getData();
        }

        switch (requestCode) {
            case REQUEST_IMAGE_PICK:
                // TODO: xu ly them anh
                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
                break;

            case REQUEST_AUDIO_RECORD:
                // TODO: xu ly ghi am
                Toast.makeText(this, "Audio recorded", Toast.LENGTH_SHORT).show();
                break;

            case REQUEST_BACKGROUND_PICK:
                // TODO: xu ly chon anh nen
                Toast.makeText(this, "Background image selected", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
