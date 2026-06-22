package com.bless.task.data;

public class CampusAlert {
    private String id;
    private String title;
    private String message;
    private String timestamp;
    private String type; // e.g., "Safety", "Classroom", "Urgent"

    public CampusAlert() {} // Required for Firestore

    public CampusAlert(String title, String message, String timestamp, String type) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
