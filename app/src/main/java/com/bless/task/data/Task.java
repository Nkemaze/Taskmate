package com.bless.task.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Task Entity representing the tasks table in Room database.
 */
@Entity(tableName = "tasks")
public class Task {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "subject")
    private String subject;

    @ColumnInfo(name = "due_date")
    private String dueDate; // Formatted as "dd MMM yyyy"

    @ColumnInfo(name = "due_time")
    private String dueTime; // Formatted as "HH:mm"

    @ColumnInfo(name = "is_completed", defaultValue = "0")
    private int isCompleted; // 0 = pending, 1 = done

    @ColumnInfo(name = "reminder_enabled", defaultValue = "0")
    private int reminderEnabled; // 0 = disabled, 1 = enabled

    @ColumnInfo(name = "default_reminders")
    private String defaultReminders; // e.g., "1h,1d,2d"

    @ColumnInfo(name = "custom_reminders")
    private String customReminders; // Comma separated timestamps

    @ColumnInfo(name = "notes")
    private String notes;

    public Task(String title, String subject, String dueDate, String dueTime, int isCompleted, int reminderEnabled, String defaultReminders, String customReminders, String notes) {
        this.title = title;
        this.subject = subject;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.isCompleted = isCompleted;
        this.reminderEnabled = reminderEnabled;
        this.defaultReminders = defaultReminders;
        this.customReminders = customReminders;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public int getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(int isCompleted) {
        this.isCompleted = isCompleted;
    }

    public int getReminderEnabled() {
        return reminderEnabled;
    }

    public void setReminderEnabled(int reminderEnabled) {
        this.reminderEnabled = reminderEnabled;
    }

    public String getDefaultReminders() {
        return defaultReminders;
    }

    public void setDefaultReminders(String defaultReminders) {
        this.defaultReminders = defaultReminders;
    }

    public String getCustomReminders() {
        return customReminders;
    }

    public void setCustomReminders(String customReminders) {
        this.customReminders = customReminders;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
