package com.example.diaryapp.ui.adapters;

import android.content.Context;
import android.os.Build;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private Context context;
    private List<Entry> entries = new ArrayList<>();

    public DiaryAdapter(Context context) {
        this.context = context;
        this.entries = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? TYPE_HEADER : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("DiaryAdapter", "onCreateViewHolder");
        RecyclerView.ViewHolder viewHolder = null;

        if (viewType == TYPE_HEADER) {
            try {
                Log.d("DiaryAdapter", "onCreateViewHolder - Inflating header...");
                View view = LayoutInflater.from(context).inflate(R.layout.recycle_header, parent, false);
                Log.d("DiaryAdapter", "onCreateViewHolder - Inflated header view OK.");
                viewHolder = new HeaderViewHolder(view); // Gán vào biến tạm
                Log.d("DiaryAdapter", "onCreateViewHolder - Created HeaderViewHolder OK.");
            } catch (Exception e) {
                // Log lỗi chi tiết nếu inflate hoặc tạo ViewHolder thất bại
                Log.e("DiaryAdapter", "onCreateViewHolder - LỖI khi inflate/tạo Header view!", e);
                // Bạn có thể throw lại lỗi hoặc trả về một ViewHolder trống để tránh crash hoàn toàn
                // return new EmptyViewHolder(new FrameLayout(parent.getContext())); // Ví dụ trả về ViewHolder trống
            }
        } else { // TYPE_ITEM
            try {
                Log.d("DiaryAdapter", "onCreateViewHolder - Inflating item (diary_row)...");
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_row, parent, false);
                Log.d("DiaryAdapter", "onCreateViewHolder - Inflated item view OK.");
                viewHolder = new DiaryViewHolder(view); // Gán vào biến tạm
                Log.d("DiaryAdapter", "onCreateViewHolder - Created DiaryViewHolder OK.");
            } catch (Exception e) {
                // Log lỗi chi tiết
                Log.e("DiaryAdapter", "onCreateViewHolder - LỖI khi inflate/tạo Item view (diary_row.xml)!", e);
                // return new EmptyViewHolder(new FrameLayout(parent.getContext())); // Ví dụ
            }
        }

        if (viewHolder == null) {
            // Trường hợp rất xấu: không thể tạo bất kỳ ViewHolder nào
            Log.e("DiaryAdapter", "onCreateViewHolder - KHÔNG THỂ TẠO ViewHolder cho viewType: " + viewType);
            // Phải trả về một cái gì đó, ví dụ một ViewHolder trống đơn giản
            // Tạo một FrameLayout đơn giản làm view tạm
            throw new IllegalStateException("Không thể tạo ViewHolder cho viewType: " + viewType);
            //android.widget.FrameLayout dummyView = new android.widget.FrameLayout(parent.getContext());
            //return new RecyclerView.ViewHolder(dummyView) {}; // Trả về ViewHolder cơ bản
        }

        Log.d("DiaryAdapter", "onCreateViewHolder - KẾT THÚC - viewType: " + viewType);
        return viewHolder; // Trả về viewHolder đã tạo

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof DiaryViewHolder) {
            Log.d("DiaryAdapter", "Binding position: " + position);
            int actualPosition = position -1;
            if (actualPosition >= 0 && actualPosition < entries.size()) {
                Entry currentEntry = entries.get(actualPosition);
                Log.d("DiaryAdapter", "Current entry: " + currentEntry.getTitle());
                ((DiaryViewHolder) holder).bind(currentEntry);
            } else {
                Log.w("DiaryAdapter", "Invalid actualPosition: " + actualPosition);
            }
//            if (actualPosition >= 0 && actualPosition < entries.size()) {
//                ((DiaryViewHolder) holder).bind(entries.get(actualPosition));
//            }
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
        Log.d("DiaryAdapter", "setData - BẮT ĐẦU - newEntries size: " + (entries == null ? "null" : entries.size()));
        Log.d("DiaryAdapter", "setData - Kích thước entries HIỆN TẠI (trước clear): " + this.entries.size());

        if (entries == null) {
            Log.w("DiaryAdapter", "setData - newEntries là null, đang clear entries.");
            this.entries.clear();
        } else {
            this.entries.clear();
            Log.d("DiaryAdapter", "setData - Đã clear entries, chuẩn bị addAll.");
            this.entries.addAll(entries);
            // Log kích thước ngay sau khi cập nhật
            Log.d("DiaryAdapter", "setData - Kích thước entries SAU KHI addAll: " + this.entries.size());
        }

        Log.d("DiaryAdapter", "setData - Chuẩn bị gọi notifyDataSetChanged().");
        notifyDataSetChanged();
        Log.d("DiaryAdapter", "setData - ĐÃ GỌI notifyDataSetChanged().");

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
            Log.d("DiaryViewHolder", "Constructor - BẮT ĐẦU");
            try {
                diaryDate = itemView.findViewById(R.id.diaryDate);
                diaryTitle = itemView.findViewById(R.id.diaryTitle);
                diaryContent = itemView.findViewById(R.id.diaryContent);
                entryEmoji = itemView.findViewById(R.id.entryEmoji);
                Log.d("DiaryViewHolder", "Constructor - findViewById xong.");


                // Kiểm tra null ngay lập tức
                if (diaryDate == null) Log.e("DiaryViewHolder", "diaryDate is NULL!");
                if (diaryTitle == null) Log.e("DiaryViewHolder", "diaryTitle is NULL!");
                if (diaryContent == null) Log.e("DiaryViewHolder", "diaryContent is NULL!");
                if (entryEmoji == null) Log.e("DiaryViewHolder", "entryEmoji is NULL!");

            } catch (Exception e) {
                Log.e("DiaryViewHolder", "Constructor - LỖI trong khi findViewById!", e);
            }
            Log.d("DiaryViewHolder", "Constructor - KẾT THÚC");
        }

        void bind(Entry entry) {
            if (diaryTitle == null || diaryContent == null || diaryDate == null || entryEmoji == null) {
                Log.e("DiaryViewHolder", "One or more TextViews are NULL!");
                return; // Thoát nếu view bị null
            }
            Log.d("DiaryViewHolder", "Binding static text for title: " + entry.getTitle());

            // TODO: bo comment khi fix xong
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
