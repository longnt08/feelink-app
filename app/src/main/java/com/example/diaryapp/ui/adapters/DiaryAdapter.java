package com.example.diaryapp.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diaryapp.R;
import com.example.diaryapp.data.local.entities.Entry;
import com.example.diaryapp.ui.activities.AddDiaryEntryActivity;
import com.example.diaryapp.ui.activities.AddDiaryEntryActivity;
import com.example.diaryapp.viewmodel.DiaryViewModel;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private Context context;
    private List<Entry> entries = new ArrayList<>();
    private DiaryViewModel viewModel;

    public DiaryAdapter(Context context) {
        this.context = context;
        this.entries = new ArrayList<>();
    }

    public void setViewModel(DiaryViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? TYPE_HEADER : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.recycle_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_row, parent, false);
            return new DiaryViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof DiaryViewHolder) {
            int actualPosition = position -1;
            ((DiaryViewHolder) holder).bind(entries.get(actualPosition));
        }
    }

    @Override
    public int getItemCount() {
        // Đảm bảo dùng this.entries là biến thành viên của Adapter
        int currentEntriesSize = (this.entries == null ? 0 : this.entries.size());
        int countToReturn = currentEntriesSize + 1; // +1 cho header
        Log.d("DiaryAdapter", "getItemCount() - Kích thước this.entries: " + currentEntriesSize + " => Trả về: " + countToReturn);
        return countToReturn;

    }

    public void setData(List<Entry> entries) {
        if (entries == null) {
            this.entries.clear();
        } else {
            this.entries.clear();
            this.entries.addAll(entries);
        }
        notifyDataSetChanged();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class DiaryViewHolder extends RecyclerView.ViewHolder {
        TextView diaryTitle, diaryContent, diaryDate, entryEmoji;
        public DiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                diaryDate = itemView.findViewById(R.id.diaryDate);
                diaryTitle = itemView.findViewById(R.id.diaryTitle);
                diaryContent = itemView.findViewById(R.id.diaryContent);
                entryEmoji = itemView.findViewById(R.id.entryEmoji);

            } catch (Exception e) {
                Log.e("DiaryViewHolder", "Constructor - LỖI trong khi findViewById!", e);
            }
        }

        void bind(Entry entry) {
            diaryDate.setText(convertLongToString(entry.getCreatedAt()));
            diaryTitle.setText(entry.getTitle());
            diaryContent.setText(entry.getContent());
            entryEmoji.setText(convertToEmoji(entry.getMood()));

            // them su kien click
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), AddDiaryEntryActivity.class);
                intent.putExtra("entry_id", entry.getId());
                intent.putExtra(AddDiaryEntryActivity.MODE_KEY, AddDiaryEntryActivity.MODE_VIEW);
                itemView.getContext().startActivity(intent);
            });
        }
    }


     private static String convertLongToString(long createdAt) {
        DateTimeFormatter formatter = null;
        String formattedDate = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy").withZone(ZoneId.systemDefault());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formattedDate = formatter.format(Instant.ofEpochMilli(createdAt));
        }

        return formattedDate;
    }
    private static String convertToEmoji(String mood) {
        switch (mood) {
            case "happy": return "\uD83D\uDE0A";
            case "sad": return "\uD83D\uDE14";
            case "angry": return "\uD83D\uDE21";
            case "funny": return "\uD83D\uDE02";
            case "love": return "\uD83D\uDE0D";
            default: return "\uD83D\uDE0A";
        }
    }

//    private void setupSwipeToDelete() {
//        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView,
//                                  @NonNull RecyclerView.ViewHolder viewHolder,
//                                  @NonNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                int position = viewHolder.getAdapterPosition();
//
//                if (position == 0) {
//                    adapter.notifyItemChanged(position); // Không xoá header
//                    return;
//                }
//
//                // Lấy vị trí thật trong list entries (vì entries không chứa header)
//                int realPosition = position - 1;
//
//                Entry entryToDelete = adapter.entries.get(realPosition);
//                viewModel.deleteEntry(entryToDelete); // Gửi lệnh xoá lên ViewModel
//                adapter.entries.remove(realPosition);
//                adapter.notifyItemRemoved(position);
//            }
//
//            @Override
//            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
//                                    @NonNull RecyclerView.ViewHolder viewHolder,
//                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
//
//                // Nếu ông muốn vẽ màu đỏ + icon xoá thì xử lý ở đây
//                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//            }
//        };
//
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
//
//        itemTouchHelper.attachToRecyclerView(recyclerView);
//    }
}
