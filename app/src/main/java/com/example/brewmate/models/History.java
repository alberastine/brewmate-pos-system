package com.example.brewmate.models;

public class History {
    private String action;
    private String description;
    private long timestamp;

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
