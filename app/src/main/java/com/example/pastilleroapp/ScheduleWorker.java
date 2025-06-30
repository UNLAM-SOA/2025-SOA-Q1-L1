package com.example.pastilleroapp;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ScheduleWorker extends Worker {
    private static final String TAG = "ScheduleWorker";

    public ScheduleWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String dateTimeStr = getInputData().getString("dateTime");
        if (dateTimeStr == null) {
            Log.e(TAG, "dateTime es null");
            return Result.failure();
        }

        Log.d(TAG, "Worker ejecutándose para fecha: " + dateTimeStr);
        
        Long dateTime = Long.valueOf(dateTimeStr);
        Intent intent = new Intent(getApplicationContext(), MQTTForegroundService.class);
        intent.setAction("com.example.pastilleroapp.mqtt.ACTION_PUBLISH_MQTT");
        intent.putExtra("extra_topic", "/v1.6/devices/esp32/fecha");
        intent.putExtra("extra_message", dateTimeStr);

        Log.d(TAG, "Enviando intent al MQTTForegroundService");
        // getApplicationContext().startService(intent);
        ContextCompat.startForegroundService(getApplicationContext(), intent);
        
        Log.d(TAG, "Worker completado exitosamente");
        return Result.success();
    }
}