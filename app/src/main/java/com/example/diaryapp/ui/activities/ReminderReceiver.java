package com.example.diaryapp.ui.activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.diaryapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Kiểm tra đã có nhật ký hôm nay chưa
        if (!hasWrittenToday(context)) {
            sendNotification(context);
        }
    }

    private boolean hasWrittenToday(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("DiaryPrefs", Context.MODE_PRIVATE);
        String lastWrittenDate = prefs.getString("lastDiaryDate", "");
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return today.equals(lastWrittenDate);
    }

    private void sendNotification(Context context) {
        // ⚠️ Chỉ kiểm tra từ Android 13 trở lên (API 33)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Không có quyền, không gửi thông báo (BroadcastReceiver không nên request permission)
                Log.w("ReminderReceiver", "Permission POST_NOTIFICATIONS not granted");
                return;
            }
        }

        Intent intent = new Intent(context, PostDetailActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "diaryReminder")
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("Nhật ký hôm nay")
                .setContentText("Bạn chưa viết nhật ký hôm nay. Hãy ghi lại vài dòng nhé!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(1001, builder.build());
    }
}