package com.example.pastilleroapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private ListView lvScheduleList;
    private TextView tvEmptyMessage;
    private Button btnAddSchedule;
    private Button btnViewHistory;

    private List<ScheduledTime> schedulesList;
    private List<String> stringList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvScheduleList = findViewById(R.id.lvScheduleList);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        btnAddSchedule = findViewById(R.id.fabAddSchedule);
        btnViewHistory = findViewById(R.id.btnViewHistory);

        schedulesList = ScheduleStorage.load(this);
        stringList = new ArrayList<>();

        for (ScheduledTime item : schedulesList) {
            stringList.add(item.getDateTime());
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringList);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        schedulesList = ScheduleStorage.load(this);
        stringList.clear();
        for (ScheduledTime item : schedulesList) {
            stringList.add(item.getDateTime());
        }
        adapter.notifyDataSetChanged();
        ifSchedulesListEmpty();
    }

    private void ifSchedulesListEmpty() {
        if (stringList.isEmpty()) {
            tvEmptyMessage.setVisibility(View.VISIBLE);
            lvScheduleList.setVisibility(View.GONE);
        } else {
            tvEmptyMessage.setVisibility(View.GONE);
            lvScheduleList.setVisibility(View.VISIBLE);
        }
    }
}



/*package com.example.pastilleroapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends Activity {

    private RecyclerView lvScheduleList;
    private TextView tvEmptyMessage;
    private Button btnAddSchedule;
    private Button btnViewHistory;

    private List<ScheduledTime> schedulesList;
    private ScheduleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enlazar elementos del layout
        recyclerScheduleList = findViewById(R.id.recyclerScheduleList);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        btnAddSchedule = findViewById(R.id.fabAddSchedule);
        btnViewHistory = findViewById(R.id.btnViewHistory);

        // Obtener lista de horarios desde almacenamiento
        schedulesList = ScheduleStorage.load(this);

        // Preparar RecyclerView con su adaptador
        adapter = new ScheduleAdapter(schedulesList);
        recyclerScheduleList.setLayoutManager(new LinearLayoutManager(this));
        recyclerScheduleList.setAdapter(adapter);

        // Mostrar u ocultar mensaje según si hay datos
        ifSchedulesListEmpty();

        // Configurar botón "+" para ir a agregar horario
        btnAddSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddScheduleActivity.class);
                startActivity(intent);
            }
        });

        // Configurar botón para ver historial
        btnViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar la lista al volver
        schedulesList.clear();
        schedulesList.addAll(ScheduleStorage.load(this));
        adapter.notifyDataSetChanged();
        ifSchedulesListEmpty();
    }

    // Mostrar mensaje si no hay horarios
    private void ifSchedulesListEmpty() {
        if (schedulesList.isEmpty()) {
            tvEmptyMessage.setVisibility(View.VISIBLE);
            recyclerScheduleList.setVisibility(View.GONE);
        } else {
            tvEmptyMessage.setVisibility(View.GONE);
            recyclerScheduleList.setVisibility(View.VISIBLE);
        }
    }
}
*/