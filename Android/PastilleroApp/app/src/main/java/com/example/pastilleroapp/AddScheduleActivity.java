/*package com.example.pastilleroapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddScheduleActivity extends AppCompatActivity {

    TextView tvDateTime;
    Button btnPickDate, btnSave;
    String finalDateTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule); // Using layout made with visual editor

        // Link components by their IDs
        tvDateTime = findViewById(R.id.tvDateTime);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnSave = findViewById(R.id.btnSave);

        // When user clicks "Pick Date", open date and time pickers
        btnPickDate.setOnClickListener(v -> pickDateTime());

        // When user clicks "Save", store the schedule
        btnSave.setOnClickListener(v -> {
            if (finalDateTime.isEmpty()) {
                Toast.makeText(this, "Please select a date and time", Toast.LENGTH_SHORT).show();
            } else {
                ScheduledTime newTime = new ScheduledTime(finalDateTime);
                ScheduleStorage.add(this, newTime);
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                finish(); // Go back to MainActivity
            }
        });
    }

    // Open DatePicker then TimePicker
    void pickDateTime() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, day) -> {
            TimePickerDialog timePicker = new TimePickerDialog(this, (tpView, hour, minute) -> {
                int second = 0;
                finalDateTime = String.format("%04d-%02d-%02d %02d-%02d-%02d", year, month + 1, day, hour, minute, second);
                tvDateTime.setText(finalDateTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

            timePicker.show();

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePicker.show();
    }
}
*/

package com.example.pastilleroapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddScheduleActivity extends AppCompatActivity {

    TextView tvDateTime;
    Button btnPickDate, btnSave;
    String finalDateTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule); // Using layout made with visual editor

        // Link components by their IDs
        tvDateTime = findViewById(R.id.tvDateTime);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnSave = findViewById(R.id.btnSave);

        setupListeners();
    }

    private void setupListeners() {
        // When user clicks "Pick Date", open date and time pickers
        btnPickDate.setOnClickListener(v -> pickDateTime());

        // When user clicks "Save", store the schedule
        btnSave.setOnClickListener(v -> {
            if (finalDateTime.isEmpty()) {
                Toast.makeText(this, "Please select a date and time", Toast.LENGTH_SHORT).show();
            } else {
                ScheduledTime newTime = new ScheduledTime(finalDateTime);
                ScheduleStorage.add(this, newTime);
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                finish(); // Go back to MainActivity
            }
        });
    }

    // Open DatePicker then TimePicker
    void pickDateTime() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, day) -> {
            TimePickerDialog timePicker = new TimePickerDialog(this, (tpView, hour, minute) -> {
                int second = 0;
                finalDateTime = String.format("%04d-%02d-%02d %02d-%02d-%02d",
                        year, month + 1, day, hour, minute, second);
                tvDateTime.setText(finalDateTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

            timePicker.show();

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePicker.show();
    }
}
