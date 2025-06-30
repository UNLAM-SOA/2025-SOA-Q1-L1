package com.example.pastilleroapp;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class VolumeActivity extends AppCompatActivity {
    private TextView tvVolume;

    private final VolumeBroadcastReceiver volumeBroadcastReceiver =  new VolumeBroadcastReceiver();
    public static final String VOLUME_SHARED = "volume_store";
    public static final String LAST_VOLUME_SAVED = "last_volume";
    public static final String EXTRA_VOLUME = "volume";
    public static final String ACTION_VOLUME_RECEIVED = "com.example.pastilleroapp.mqtt.MQTT_VOLUME_RECEIVED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_volume);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.volume), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvVolume = findViewById(R.id.txtVolume);

        SharedPreferences prefs = getSharedPreferences(VOLUME_SHARED, MODE_PRIVATE);
        String volume = prefs.getString(LAST_VOLUME_SAVED, "0");
        tvVolume.setText(volume);
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onStart(){
        super.onStart();
        IntentFilter filter = new IntentFilter(ACTION_VOLUME_RECEIVED);
        registerReceiver(volumeBroadcastReceiver, filter);
    }
    @Override
    protected void onStop(){
        unregisterReceiver(volumeBroadcastReceiver);
        super.onStop();
    }

    private class VolumeBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_VOLUME_RECEIVED.equals(intent.getAction())) {
                String value = intent.getStringExtra(EXTRA_VOLUME);

                Log.d("MQTT", "Mensaje MQTT: " + value);
                tvVolume.setText(String.valueOf(value));
            }
        }
    };
}