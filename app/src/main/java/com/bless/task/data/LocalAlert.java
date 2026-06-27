package com.bless.task.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "local_alerts")
public class LocalAlert {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String message;
    private String timestamp;
    private String type; // "Task" or "Campus"

    public LocalAlert(String title, String message, String timestamp, String type) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }
    public String getType() { return type; }
}
