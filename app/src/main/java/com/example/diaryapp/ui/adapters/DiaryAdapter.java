package com.example.diaryapp.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diaryapp.R;
import com.example.diaryapp.data.local.entities.Entry;

import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {

    private List<Entry> entries;
    private Context context;

    public DiaryAdapter(Context context, List<Entry> entries) {
        this.context = context;
        this.entries = entries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.diary_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryAdapter.ViewHolder holder, int position) {
        Entry entry = entries.get(position);
        holder.diaryTitle.setText(entry.getTitle());
        holder.diaryContent.setText(entry.getContent());
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView diaryTitle, diaryContent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            diaryTitle = itemView.findViewById(R.id.diaryTitle);
            diaryContent = itemView.findViewById(R.id.diaryContent);
        }
    }
}
