package com.example.pastilleroapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.time.ZoneId;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;

public class AddScheduleActivity extends AppCompatActivity {
    private static final  DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ZoneId zoneId = ZoneId.of("America/Argentina/Buenos_Aires");
    private static final Long TO_SECONDS = 1000L;
    private static final Long MINUTE = 60000L;

    TextView tvDateTime;
    Button btnPickDate, btnSave;
    String finalDateTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_schedule);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.schedule), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvDateTime = findViewById(R.id.tvDateTime);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnSave = findViewById(R.id.btnSave);

        this.setupListeners();
    }

    private void setupListeners() {
        btnPickDate.setOnClickListener(v -> pickDateTime());

        btnSave.setOnClickListener(v -> {
            if (finalDateTime.isEmpty()) {
                Toast.makeText(this, "Por favor, selecciona una fecha y horario.", Toast.LENGTH_SHORT).show();
            } else {
                LocalDateTime dateTime = LocalDateTime.parse(finalDateTime, FORMATTER);
                
                long nowMillis = LocalDateTime.now(zoneId).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                long dateTimeMillis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

                long delay = dateTimeMillis - nowMillis;
                if (delay > 0) {
                    Data inputData = new Data.Builder()
                        .putString("dateTime", String.valueOf(dateTimeMillis/TO_SECONDS))
                        .build();

                    OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(ScheduleWorker.class)
                        .setInitialDelay(delay - MINUTE, TimeUnit.MILLISECONDS)
                        .setInputData(inputData)
                        .build();
                    String workId = workRequest.getId().toString();
                    
                    ScheduledTime newTime = new ScheduledTime(finalDateTime, workId);
                    ScheduleStorage.add(this, newTime);
                    
                    WorkManager.getInstance(this).enqueue(workRequest);
                } else {
                    ScheduledTime newTime = new ScheduledTime(finalDateTime);
                    ScheduleStorage.add(this, newTime);
                }

                Toast.makeText(this, "Horario guardado correctamente.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    void pickDateTime() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, day) -> {
            TimePickerDialog timePicker = new TimePickerDialog(this, (tpView, hour, minute) -> {
                int second = 0;
                finalDateTime = String.format("%04d-%02d-%02d %02d:%02d:%02d",
                        year, month + 1, day, hour, minute, second);
                tvDateTime.setText(finalDateTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

            timePicker.show();

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis());
        datePicker.show();
    }
}