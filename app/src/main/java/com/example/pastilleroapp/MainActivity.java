package com.example.pastilleroapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.WorkManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener, ScheduleAdapter.OnDeleteListener {
    private static final String TAG = "MainActivity";
    private ListView lvScheduleList;
    private TextView tvEmptyMessage;
    private FloatingActionButton btnAddSchedule;
    private Button btnViewHistory;
    private List<ScheduledTime> schedulesList;
    private ScheduleAdapter adapter;
    private androidx.appcompat.widget.SwitchCompat tbSync;

    private static final String MAIN_SHARED = "main_store";
    private static final String SERVICE_STATE_SAVED = "service_state";

    private SensorManager sensorManager;
    private long lastShakeTime = 0;
    private static final int UMBRAL_SHAKE_THRESHOLD = 20;
    private static final int UMBRAL_SHAKE_TIMEOUT = 2000;

    private final BroadcastReceiver timeTickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                schedulesList = ScheduleStorage.load(MainActivity.this);
                adapter.clear();
                adapter.addAll(schedulesList);
                adapter.notifyDataSetChanged();
                ifSchedulesListEmpty();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        lvScheduleList = findViewById(R.id.lvScheduleList);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        btnAddSchedule = findViewById(R.id.fabAddSchedule);
        btnViewHistory = findViewById(R.id.btnViewHistory);
        tbSync = findViewById(R.id.switchService);

        SharedPreferences prefs = getSharedPreferences(MAIN_SHARED, MODE_PRIVATE);
        tbSync.setChecked(prefs.getBoolean(SERVICE_STATE_SAVED, false));

        schedulesList = ScheduleStorage.load(this);
        
        adapter = new ScheduleAdapter(this, schedulesList, this);
        lvScheduleList.setAdapter(adapter);

        ifSchedulesListEmpty();

        btnAddSchedule.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddScheduleActivity.class);
            startActivity(intent);
        });

        btnViewHistory.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        tbSync.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Intent intentService = new Intent(this, MQTTForegroundService.class);

            if (isChecked) {
                ContextCompat.startForegroundService(this, intentService);
            } else {
                stopService(intentService);
            }

            SharedPreferences pp = getSharedPreferences(MAIN_SHARED, MODE_PRIVATE);
            pp.edit()
                .putBoolean(SERVICE_STATE_SAVED, isChecked)
                .apply();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),   SensorManager.SENSOR_DELAY_NORMAL);

        schedulesList = ScheduleStorage.load(this);
        adapter.clear();
        adapter.addAll(schedulesList);
        adapter.notifyDataSetChanged();

        ifSchedulesListEmpty();

        registerReceiver(timeTickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    protected void onPause(){
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        unregisterReceiver(timeTickReceiver);
        super.onPause();
    }

    @Override
    protected void onRestart(){
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),   SensorManager.SENSOR_DELAY_NORMAL);
        super.onRestart();
    }

    private void ifSchedulesListEmpty() {
        if (schedulesList.isEmpty()) {
            tvEmptyMessage.setVisibility(View.VISIBLE);
            lvScheduleList.setVisibility(View.GONE);
        } else {
            tvEmptyMessage.setVisibility(View.GONE);
            lvScheduleList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDelete(ScheduledTime item) {
        Log.d(TAG, "Eliminando horario: " + item.getDateTime());
        
        // Cancel worker
        if (item.getWorkId() != null) {
            WorkManager.getInstance(this).cancelWorkById(java.util.UUID.fromString(item.getWorkId()));
            Log.d(TAG, "Worker cancelado: " + item.getWorkId());
        }
        
        schedulesList.remove(item);
        
        ScheduleStorage.save(this, schedulesList);
        
        // Update adapter
        adapter.clear();
        adapter.addAll(schedulesList);
        adapter.notifyDataSetChanged();
        
        ifSchedulesListEmpty();
        
        Log.d(TAG, "Horario eliminado exitosamente");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long ct = System.currentTimeMillis();

        if ((ct - lastShakeTime) > UMBRAL_SHAKE_TIMEOUT) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double acceleration = Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;

            if (acceleration > UMBRAL_SHAKE_THRESHOLD) {
                lastShakeTime = ct;

                Intent intent = new Intent(MainActivity.this, VolumeActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}

