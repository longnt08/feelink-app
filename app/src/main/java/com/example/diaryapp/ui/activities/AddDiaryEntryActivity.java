package com.example.diaryapp.ui.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.Objects;

public class AddDiaryEntryActivity extends AppCompatActivity {
    public static final String MODE_KEY = "mode";
    public static final String MODE_CREATE = "create";
    public static final String MODE_EDIT = "edit";
    public static final String MODE_VIEW = "view";
    public static final String ENTRY_ID_KEY = "entry_id";

    private TextView tvSelectedDate;
    private EditText textTitle, textContent;
    private Button saveButton;
    private ImageButton backButton, editButton;
    private TextView tvEmojiButton;
    private EntryRepository entryRepository;
    private long selectedTimestamp;
    private String selectedEmoji = "😊";
    private String selectedEmojiDescription = "happy";
    private final Map<String, String> emojiMap = new LinkedHashMap<>();
    private View bottomToolBar;
    private View rootLayout;
    private String currentMode;
    private long currentEntryId = -1;

    private static final int REQUEST_IMAGE_PICK = 1001;
    private static final int REQUEST_AUDIO_RECORD = 1002;
    private static final int REQUEST_BACKGROUND_PICK = 1003;
    private ImageButton btnAddImage, btnRecordAudio, btnChangeBackground;
    private long currentUserId = -1;
    private static final String PREF_NAME = "DiaryAppPrefs";
    private static final String KEY_USER_ID = "user_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        // Get user ID from intent first (if passed)
        currentUserId = getIntent().getLongExtra("user_id", -1);

        // If not in intent, try getting from SharedPreferences
        if (currentUserId == -1) {
            SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            currentUserId = sharedPreferences.getLong(KEY_USER_ID, -1);
        }
        
        // Still no user ID, redirect to login
        if (currentUserId == -1) {
            Toast.makeText(this, "Please login to add entries", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // khoi tao map anh xa
        initializeEmojiMap();
        // lien ket cac view
        findViews();

        // khoi tao Repository
        entryRepository = new EntryRepository(this);

        Intent intent = getIntent();
        String mode = intent.getStringExtra(MODE_KEY);
        long entryId = intent.getLongExtra(ENTRY_ID_KEY, -1);

        // kiem tra mode hop le
        if (currentMode == null) {
            currentMode = MODE_CREATE;
        }

        setUpInitialUIState(); // cai dat trang thai ban dau dua tren mode
        setupListeners(); // cai dat cac listners

        // tai du lieu neu la mode VIEW hoac EDIT
        if ((currentMode.equals(MODE_VIEW) || currentMode.equals(MODE_EDIT)) && entryId != -1) {
            loadEntryData(entryId);
        } else if (currentMode.equals(MODE_CREATE)) {
            // dat ngay mac dinh
            setDefaultDate();
            updateEmojiUI(selectedEmoji, emojiMap.get(selectedEmoji));
        } else {
            // truong hop ko hop le
            Toast.makeText(this, "Error: Can not find your diary.", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (MODE_CREATE.equals(mode)) {

        } else if (MODE_EDIT.equals(mode) || MODE_VIEW.equals(mode) && entryId != -1) {
            EntryDao dao = DiaryDatabase.getInstance(this).entryDao();
            new Thread(() -> {
                Entry entry = dao.getEntryById(entryId);
                runOnUiThread(() -> {
                    if (entry != null) {
                        populateEntryFields(entry);
                    }
                });
            });
        }
    }

    private void updateEmojiUI(String selectedEmoji, String description) {
        selectedEmoji = selectedEmoji;
        selectedEmojiDescription = description;
        tvEmojiButton.setText(selectedEmoji);
    }

    private String findEmojiDescription(String description) {
        for (Map.Entry<String, String> mapEntry : emojiMap.entrySet()) {
            if (Objects.equals(description, mapEntry.getValue())) {
                return mapEntry.getKey();
            }
        }
        return "😊";
    }

    private void setDefaultDate() {
        Calendar currentCal = Calendar.getInstance();
        // dat gio, phut, giay, mili giay ve 0 de lay timestamp dau ngay
        currentCal.set(Calendar.HOUR_OF_DAY, 0);
        currentCal.set(Calendar.MINUTE, 0);
        currentCal.set(Calendar.SECOND, 0);
        currentCal.set(Calendar.MILLISECOND, 0);
        selectedTimestamp = currentCal.getTimeInMillis();
        updateDateUI(selectedTimestamp);
    }

    private void updateDateUI(long timestamp) {
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvSelectedDate.setText(displayFormat.format(timestamp));
    }

    private void loadEntryData(long entryId) {
        EntryDao entryDao = DiaryDatabase.getInstance(this).entryDao();
        // TODO: dung Executor hoac Coroutines/LiveData
        new Thread(() -> {
            Entry entry = entryDao.getEntryById(entryId);
            runOnUiThread(() -> {
                if (entry != null) {
                    populateEntryFields(entry);
                } else {
                    Toast.makeText(this, "Error: Can not find your diary.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                configureUIForMode();
            });
        }).start();
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> finish());
        saveButton.setOnClickListener(v -> saveDiaryEntry());

        editButton.setOnClickListener(v -> {
            // chuyen tu VIEW sang EDIT
            if (currentMode.equals(MODE_VIEW)) {
                currentMode = MODE_EDIT;
                configureUIForMode();
            }
        });

        tvSelectedDate.setOnClickListener( v -> {
            // chi cho chon ngay khi o CREATE hoac EDIT
            if (currentMode.equals(MODE_CREATE) || currentMode.equals(MODE_EDIT)) {
                showDatePickerDialog();
            }
        });

        tvEmojiButton.setOnClickListener(v -> {
            // chi cho phep chon emoji khi o mode CREATE hoac EDIT
            showEmojiSelectorDialog();
        });

        btnChangeBackground.setOnClickListener(v -> chooseBackgroundImage());
        btnAddImage.setOnClickListener(v -> openGalleryForImage());
        btnRecordAudio.setOnClickListener(v -> openAudioRecorder());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        // Nếu đang sửa và đã có ngày, dùng ngày đó làm mặc định cho DatePicker
        if (selectedTimestamp > 0) {
            calendar.setTimeInMillis(selectedTimestamp);
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    Calendar selectedCal = Calendar.getInstance();
                    selectedCal.set(year1, month1, dayOfMonth, 0, 0, 0);
                    selectedCal.set(Calendar.MILLISECOND, 0);
                    selectedTimestamp = selectedCal.getTimeInMillis(); // Cập nhật timestamp
                    updateDateUI(selectedTimestamp); // Cập nhật TextView
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void setUpInitialUIState() {
        // an het cac nut dieu khien chinh truoc
//        saveButton.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);

        // an cac nut toolbar khi o che do view
        if (currentMode.equals(MODE_VIEW)) {
            bottomToolBar.setVisibility(View.GONE);
        } else {
            bottomToolBar.setVisibility(View.VISIBLE);
        }
    }

    // cau hinh UI cuoi cung sau khi da co mode va data
    private void configureUIForMode() {
        switch (currentMode) {
            case MODE_CREATE:
                setTitle("Create Diary");
                enableEditing(true);
                saveButton.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.GONE);
                bottomToolBar.setVisibility(View.VISIBLE);
                break;
            case MODE_EDIT:
                setTitle("Edit Diary");
                enableEditing(true);
                saveButton.setText("Update");
                saveButton.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.GONE);
                bottomToolBar.setVisibility(View.VISIBLE);
                break;
            case MODE_VIEW:
            default:
                setTitle("View diary");
                enableEditing(false);
                saveButton.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
                bottomToolBar.setVisibility(View.GONE);
                break;
        }
    }

    private void enableEditing(boolean b) {
        textTitle.setEnabled(b);
        textContent.setEnabled(b);
        tvSelectedDate.setEnabled(b);
        tvEmojiButton.setEnabled(b);
        saveButton.setEnabled(b);
        backButton.setEnabled(b);

        btnAddImage.setEnabled(b);
        btnRecordAudio.setEnabled(b);
        btnChangeBackground.setEnabled(b);
    }

    private void findViews() {
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
        editButton = findViewById(R.id.editButton);
    }

    private void populateEntryFields(Entry entry) {
        textTitle.setText(entry.getTitle());
        textContent.setText(entry.getContent());
        //TODO: xem lai cach chuyen tu long sang date
        tvSelectedDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(entry.getCreatedAt()));
        selectedTimestamp = entry.getCreatedAt();
        selectedEmoji = convertToEmoji(entry.getMood());
        tvEmojiButton.setText(selectedEmoji);
        selectedEmojiDescription = entry.getMood();
    }

    private String convertToEmoji(String mood) {
        switch (mood) {
            case "Happy": return "\uD83D\uDE0A";
            case "Sad": return "\uD83D\uDE14";
            case "Angry": return "\uD83D\uDE21";
            case "Funny": return "\uD83D\uDE02";
            case "Love": return "\uD83D\uDE0D";
            default: return "\uD83D\uDE0A";
        }
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

    private void saveDiaryEntry() {
//        Log.d("DEBUG", "saveDiaryEntry() called");

        String title = textTitle.getText().toString().trim();
        String content = textContent.getText().toString().trim();

//        Log.d("DEBUG", "Title " + title);
//        Log.d("DEBUG", "Content " + content);
//        Log.d("DEBUG", "Date " + selectedDateInMillis );

        if(title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please enter both title and content", Toast.LENGTH_SHORT).show();
            return;
        }

        Entry newEntry = new Entry(currentUserId, title, content, selectedEmojiDescription, selectedTimestamp);

        // TODO: dung Executor hoac Coroutines/LiveData
        new Thread(() -> {
            boolean success = false;
            String message = "";

            if (currentMode.equals(MODE_EDIT) && currentEntryId != -1) {
                // neu la EDIT mode, dat ID cho entry de Room biet update ban ghi nao
                newEntry.setId(currentEntryId);
                try {
                    entryRepository.updateDiary(newEntry);
                    success = true;
                    message = "Update success";
                } catch (Exception e) {
                    message = "Update failed";
                }
            } else if (currentMode.equals(MODE_CREATE)) {
                try {
                    entryRepository.insertDiary(newEntry);
                    success = true;
                    message = "Insert success";
                } catch (Exception e) {
                    message = "Insert failed";
                }
            }
            // hien thi Toast va dong Activity tren UI thread
            String finalMessage = message;
            boolean finalSuccess = success;
            runOnUiThread(() -> {
                Toast.makeText(this, finalMessage, Toast.LENGTH_SHORT).show();
                if (finalSuccess) {
                    finish();
                }
            });
        }).start();
    }

    private void disableEditing() {
        textTitle.setEnabled(false);
        textContent.setEnabled(false);
        tvSelectedDate.setEnabled(false);
        tvEmojiButton.setEnabled(false);
        findViewById(R.id.saveButton).setVisibility(View.GONE);
        btnAddImage.setVisibility(View.GONE);
        btnChangeBackground.setVisibility(View.GONE);
        btnRecordAudio.setVisibility(View.GONE);
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
