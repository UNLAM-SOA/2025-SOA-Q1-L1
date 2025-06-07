package com.example.pastilleroapp;

public class ScheduledTime {
    private String dateTime;

    public ScheduledTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return dateTime;
    }
}
