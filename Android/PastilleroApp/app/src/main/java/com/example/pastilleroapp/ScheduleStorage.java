package com.example.pastilleroapp;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ScheduleStorage {

    private static final String PREF_NAME = "SchedulesPref";
    private static final String KEY_LIST = "schedule_list";
    private static final String KEY_HISTORY = "history_list";

    // Save schedule list
    public static void save(Context context, List<ScheduledTime> list) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        JSONArray array = new JSONArray();
        for (ScheduledTime st : list) {
            array.put(st.getDateTime());
        }

        editor.putString(KEY_LIST, array.toString());
        editor.apply();
    }

    // Load schedule list
    public static List<ScheduledTime> load(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_LIST, null);

        List<ScheduledTime> list = new ArrayList<>();

        if (json != null) {
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    String dateTime = array.getString(i);
                    list.add(new ScheduledTime(dateTime));
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

    // Add to history
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

    // Load history
    public static List<String> loadHistory(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_HISTORY, "[]");

        List<String> result = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                result.add(arr.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }
}
