package com.example.pastilleroapp;

public class ScheduledTime {
    private String dateTime;
    private String workId;

    public ScheduledTime(String dateTime, String workId) {
        this.dateTime = dateTime;
        this.workId = workId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    @Override
    public String toString() {
        return dateTime;
    }

    public ScheduledTime(String dateTime) {
        this.dateTime = dateTime;
        this.workId = null;
    }
}
