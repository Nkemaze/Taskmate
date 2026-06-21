package com.bless.task.worker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.bless.task.R;

/**
 * Worker class to handle task notifications.
 */
public class NotificationWorker extends Worker {

    public static final String CHANNEL_ID = "taskmate_reminders";
    public static final String TASK_TITLE = "task_title";
    public static final String TASK_SUBJECT = "task_subject";

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String title = getInputData().getString(TASK_TITLE);
        String subject = getInputData().getString(TASK_SUBJECT);

        sendNotification(title, subject);

        return Result.success();
    }

    private void sendNotification(String title, String subject) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "TaskMate Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_task_notification) // Ensure this exists or use a default
                .setContentTitle("Task Due Tomorrow!")
                .setContentText(title + " — " + subject + " is due tomorrow")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
