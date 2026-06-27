package com.bless.task.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.bless.task.MainActivity;
import com.bless.task.R;
import com.bless.task.data.LocalAlert;
import com.bless.task.data.TaskDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReminderReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "TASK_REMINDER_CHANNEL";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        
        // Use goAsync() to ensure the process stays alive during the database write
        final PendingResult pendingResult = goAsync();
        
        String title = intent.getStringExtra("TASK_TITLE");
        int taskId = intent.getIntExtra("TASK_ID", 0);

        TaskDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // 1. Save to local database for the "Notifications" section
                String timestamp = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.US).format(new Date());
                LocalAlert alert = new LocalAlert("Task Reminder", title != null ? title : "Task Due", timestamp, "Task");
                TaskDatabase.getDatabase(context).localAlertDao().insert(alert);

                // 2. Show the system notification
                showNotification(context, title, taskId);
            } finally {
                // Always call finish() to release the receiver
                pendingResult.finish();
            }
        });
    }

    private void showNotification(Context context, String title, int taskId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Task Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(channel);
        }

        // Open app when notification is clicked
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.putExtra("TASK_ID", taskId);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 
                (int) System.currentTimeMillis(), // Unique request code to allow multiple notifications
                mainIntent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_bell_alert)
                .setContentTitle("Task Reminder")
                .setContentText(title != null ? title : "One of your tasks is due now!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(pendingIntent, true) // High priority pop-up
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
