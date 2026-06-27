package com.bless.task.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.bless.task.data.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NotificationScheduler {

    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
    public static final String ACTION_REMINDER = "com.bless.task.ACTION_REMINDER";

    public static void scheduleReminders(Context context, Task task) {
        // Step 1: Deep clean old alarms for this specific task
        cancelReminders(context, task);

        if (task.getReminderEnabled() == 0) return;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;
        
        scheduleDefaultReminders(context, alarmManager, task);
        scheduleCustomReminders(context, alarmManager, task);
    }

    private static void scheduleDefaultReminders(Context context, AlarmManager alarmManager, Task task) {
        String defaults = task.getDefaultReminders();
        if (defaults == null || defaults.isEmpty()) return;

        Calendar dueCalendar = getDueCalendar(task);
        if (dueCalendar == null) return;

        String[] parts = defaults.split(",");
        for (String part : parts) {
            Calendar reminderTime = (Calendar) dueCalendar.clone();
            int idOffset = -1;

            switch (part.trim()) {
                case "0m":  idOffset = 0; break; 
                case "30m": reminderTime.add(Calendar.MINUTE, -30); idOffset = 1; break;
                case "1h":  reminderTime.add(Calendar.HOUR, -1);    idOffset = 2; break;
                case "1d":  reminderTime.add(Calendar.DAY_OF_YEAR, -1); idOffset = 3; break;
                case "2d":  reminderTime.add(Calendar.DAY_OF_YEAR, -2); idOffset = 4; break;
            }

            if (idOffset != -1 && reminderTime.getTimeInMillis() > System.currentTimeMillis()) {
                scheduleAlarm(context, alarmManager, task, reminderTime.getTimeInMillis(), task.getId() * 100 + idOffset);
            }
        }
    }

    private static void scheduleCustomReminders(Context context, AlarmManager alarmManager, Task task) {
        String customs = task.getCustomReminders();
        if (customs == null || customs.isEmpty()) return;

        String[] parts = customs.split(",");
        for (int i = 0; i < parts.length; i++) {
            try {
                long timestamp = Long.parseLong(parts[i].trim());
                if (timestamp > System.currentTimeMillis()) {
                    scheduleAlarm(context, alarmManager, task, timestamp, task.getId() * 100 + 10 + i);
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    private static void scheduleAlarm(Context context, AlarmManager alarmManager, Task task, long time, int requestId) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.setAction(ACTION_REMINDER);
        intent.putExtra("TASK_TITLE", task.getTitle());
        intent.putExtra("TASK_ID", task.getId());
        
        // Ensure every single alert is unique
        intent.setData(Uri.parse("task_reminder://" + requestId));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
            }
        } catch (Exception e) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }
    }

    private static Calendar getDueCalendar(Task task) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateTimeFormat.parse(task.getDueDate().trim() + " " + task.getDueTime().trim()));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal;
        } catch (ParseException | NullPointerException e) {
            return null;
        }
    }

    public static void cancelReminders(Context context, Task task) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.setAction(ACTION_REMINDER); // Added action to match scheduleAlarm

        for (int i = 0; i < 100; i++) {
            int requestId = task.getId() * 100 + i;
            intent.setData(Uri.parse("task_reminder://" + requestId));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, requestId, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent);
                pendingIntent.cancel();
            }
        }
    }
}
