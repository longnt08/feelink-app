package com.example.diaryapp.ui.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diaryapp.R;
import com.example.diaryapp.data.local.entities.Entry;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<Entry> entries;
    private Context context;

    public DiaryAdapter(Context context, List<Entry> entries) {
        this.context = context;
        this.entries = entries;
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
        return entries.size() + 1;
    }

    public void setData(List<Entry> entries) {
        this.entries.clear();
        this.entries.addAll(entries);
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
            diaryDate = itemView.findViewById(R.id.diaryDate);
            diaryTitle = itemView.findViewById(R.id.diaryTitle);
            diaryContent = itemView.findViewById(R.id.diaryContent);
            entryEmoji = itemView.findViewById(R.id.entryEmoji);
        }

        void bind(Entry entry) {
            diaryDate.setText(convertLongToString(entry.getCreatedAt()));
            diaryTitle.setText(entry.getTitle());
            diaryContent.setText(entry.getContent());
            entryEmoji.setText(convertToEmoji(entry.getMood()));
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
            case "Happy": return "\uD83D\uDE0A";
            case "Sad": return "\uD83D\uDE14";
            case "Angry": return "\uD83D\uDE21";
            case "Funny": return "\uD83D\uDE02";
            case "Love": return "\uD83D\uDE0D";
            default: return "\uD83D\uDE0A";
        }
    }
}
