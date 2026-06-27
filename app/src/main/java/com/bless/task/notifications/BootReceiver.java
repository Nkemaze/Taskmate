package com.bless.task.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bless.task.data.Task;
import com.bless.task.data.TaskDatabase;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Reschedule all alerts from the database
            TaskDatabase.databaseWriteExecutor.execute(() -> {
                List<Task> tasks = TaskDatabase.getDatabase(context).taskDao().getAllTasksSync();
                for (Task task : tasks) {
                    if (task.getReminderEnabled() == 1) {
                        NotificationScheduler.scheduleReminders(context, task);
                    }
                }
            });
        }
    }
}
