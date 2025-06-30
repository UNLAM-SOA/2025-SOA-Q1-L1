package com.example.pastilleroapp;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ScheduleStorage {
    private static final String PREF_NAME = "SchedulesPref";
    private static final String KEY_LIST = "schedule_list";
    private static final String KEY_HISTORY = "history_list";
    private static final  DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void save(Context context, List<ScheduledTime> list) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        JSONArray array = new JSONArray();
        for (ScheduledTime st : list) {
            JSONArray itemArray = new JSONArray();
            itemArray.put(st.getDateTime());
            itemArray.put(st.getWorkId() != null ? st.getWorkId() : "");
            array.put(itemArray);
        }

        editor.putString(KEY_LIST, array.toString());
        editor.putString(KEY_HISTORY, array.toString());
        editor.apply();
    }

    public static List<ScheduledTime> load(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_LIST, null);
        LocalDateTime now = LocalDateTime.now();
        List<ScheduledTime> list = new ArrayList<>();

        if (json != null) {
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    JSONArray itemArray = array.getJSONArray(i);
                    String dateTime = itemArray.getString(0);
                    String workId = itemArray.length() > 1 ? itemArray.getString(1) : null;
                    
                    LocalDateTime date = LocalDateTime.parse(dateTime, FORMATTER);
                    if(!date.isBefore(now)) {
                        if (workId != null && !workId.isEmpty()) {
                            list.add(new ScheduledTime(dateTime, workId));
                        } else {
                            list.add(new ScheduledTime(dateTime));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    // Add a new schedule and save it
    public static void add(Context context, ScheduledTime st) {
        List<ScheduledTime> current = load(context);
        current.add(st);
        save(context, current);
    }

    public static void addToHistory(Context context, String dateTime) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_HISTORY, "[]");

        List<String> history = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                history.add(arr.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        history.add(dateTime);

        JSONArray newArray = new JSONArray();
        for (String s : history) {
            newArray.put(s);
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_HISTORY, newArray.toString());
        editor.apply();
    }

    public static List<String> loadHistory(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_HISTORY, "[]");

        List<String> result = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONArray scheduleTime = arr.getJSONArray(i);
                String datetime = scheduleTime.getString(0);
                result.add(datetime);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }
}

