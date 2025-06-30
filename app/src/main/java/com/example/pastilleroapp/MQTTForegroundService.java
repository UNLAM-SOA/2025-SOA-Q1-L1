package com.example.pastilleroapp;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

public class MQTTForegroundService extends Service implements MqttCallback {
    private static final String CHANNEL_ID = "MQTTServiceChannel";
    private MqttClient mqttClient;

    private static final String BROKER = "tcp://3.19.87.203:1883";
    private static final String CLIENT_ID = "android_client" + System.currentTimeMillis();
    public static final String USER = "BBUS-ESPZX2ACUxkzX1imwO6uDf35YUa66Y";
    public static final String PASS = "BBUS-ESPZX2ACUxkzX1imwO6uDf35YUa66Y";

    protected static final String TOPIC_DATE = "/v1.6/devices/esp32/fecha";
    private static final String TOPIC_VOLUME = "/v1.6/devices/esp32/volume";

    public static final String VOLUME_SHARED = "volume_store";
    public static final String LAST_VOLUME_SAVED = "last_volume";
    public static final String ACTION_VOLUME_RECEIVED = "com.example.pastilleroapp.mqtt.MQTT_VOLUME_RECEIVED";

    public static final String ACTION_PUBLISH_MQTT = "com.example.pastilleroapp.mqtt.ACTION_PUBLISH_MQTT";
    public static final String EXTRA_TOPIC = "extra_topic";
    public static final String EXTRA_MESSAGE = "extra_message";

    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void startMQTT() {
        new Thread(() -> {
            try {
                Log.d("MQTT", "Iniciando conexión MQTT en hilo en segundo plano");
                MemoryPersistence persistence = new MemoryPersistence();
                mqttClient = new MqttClient(BROKER, CLIENT_ID, persistence);
                MqttConnectOptions options = new MqttConnectOptions();
                options.setCleanSession(true);
                options.setUserName(USER);
                options.setPassword(PASS.toCharArray());
                options.setConnectionTimeout(15);
                mqttClient.setCallback(this);
                Log.d("MQTT", "Conectando a Ubidots...");
                mqttClient.connect(options);
                mqttClient.subscribe(TOPIC_VOLUME);
                Log.i("MQTT", "OK: Conexión establecida correctamente");
            } catch (MqttException e) {
                Log.e("MQTT", "ERROR: Error al conectar: " + e.getMessage() + " Código: " + e.getReasonCode());
            }
        }).start();
    }

    private void createNotificationChannel() {
        NotificationChannel canal = new NotificationChannel(
            CHANNEL_ID,
        "Canal de Servicio MQTT",
            NotificationManager.IMPORTANCE_LOW
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(canal);
        }
    }


    private Notification createNotification(String msg) {
    return new NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("MQTT")
        .setContentText(msg)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .build();
}

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MQTT", "onStartCommand llamado con intent: " + (intent != null ? intent.getAction() : "null"));
        
        createNotificationChannel();
        Log.d("MQTT", "Antes de crear notificación");
        Notification notification = createNotification("Servicio MQTT activo.");
        Log.d("MQTT", "Notificación creada");
        startForeground(1, notification);
        Log.d("MQTT", "startForeground ejecutado");
        startMQTT();

        if (intent != null && ACTION_PUBLISH_MQTT.equals(intent.getAction())) {
            Log.d("MQTT", "Recibido intent de publicación desde worker");
            String topic = intent.getStringExtra(EXTRA_TOPIC);
            String message = intent.getStringExtra(EXTRA_MESSAGE);
            Log.d("MQTT", "Topic: " + topic + ", Message: " + message);
            publish(topic, message);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.e("MQTT", "Servicio MQTT detenido.");
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
            }
        } catch ( MqttException e) {
            Log.e("MQTT", "Error al desconectar: " + e.getMessage());
        }
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.e("MQTT", "Conexión MQTT perdida.");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.d("MQTT", "Mensaje MQTT: " + message.toString());

        JSONObject payload = new JSONObject(new String(message.getPayload()));
        String volume = String.valueOf(payload.getInt("value"));

        SharedPreferences prefs = getSharedPreferences(VOLUME_SHARED, MODE_PRIVATE);
        prefs.edit()
                .putString(LAST_VOLUME_SAVED, volume)
                .apply();

        Intent volumeIntent = new Intent(ACTION_VOLUME_RECEIVED);
        volumeIntent.putExtra("volume", volume);
        sendBroadcast(volumeIntent);
    }

    private void publish(String topic, String message) {
        Log.d("MQTT", "Iniciando publicación: " + message + " en " + topic);
        new Thread(() -> {
            try {
                int attempts = 0;
                while ((mqttClient == null || !mqttClient.isConnected()) && attempts < 15) {
                    Log.d("MQTT", "Esperando conexión MQTT... intento " + (attempts + 1));
                    Thread.sleep(1000);
                    attempts++;
                }
                if (mqttClient != null && mqttClient.isConnected()) {
                    Log.d("MQTT", "Conexión lista, publicando mensaje...");
                    MqttMessage mqttMessage = new MqttMessage(message.getBytes());
                    mqttMessage.setQos(1);
                    mqttClient.publish(topic, mqttMessage);
                    Log.d("MQTT", "OK: Publicado exitosamente: " + message + " en " + topic);
                } else {
                    Log.e("MQTT", "Error: No se pudo conectar después de 15 intentos");
                    Log.e("MQTT", "Error: Mensaje perdido: " + message + " en " + topic);
                }
            } catch (MqttException e) {
                Log.e("MQTT", "Error: Error al publicar: " + e.getMessage() + " Código: " + e.getReasonCode());
            } catch (InterruptedException e) {
                Log.e("MQTT", "Error en espera: " + e.getMessage());
            }
        }).start();
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
