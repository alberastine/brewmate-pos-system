package com.example.brewmate.models;

public class History {
    private String action;      // e.g. "New cashier added"
    private String description; // e.g. "Emma Wilson joined the team"
    private long timestamp;     // e.g. System.currentTimeMillis()

    public History(String action, String description, long timestamp) {
        this.action = action;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getAction() {
        return action;
    }

    public String getDescription() {
        return description;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
