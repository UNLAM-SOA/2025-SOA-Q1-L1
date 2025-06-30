package com.example.pastilleroapp;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ScheduleAdapter extends ArrayAdapter<ScheduledTime> {
    private final Context context;
    private final List<ScheduledTime> schedules;
    private final OnDeleteListener onDeleteListener;

    public interface OnDeleteListener {
        void onDelete(ScheduledTime item);
    }

    public ScheduleAdapter(@NonNull Context context, List<ScheduledTime> schedules, OnDeleteListener onDeleteListener) {
        super(context, 0, schedules);
        this.context = context;
        this.schedules = schedules;
        this.onDeleteListener = onDeleteListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_schedule, parent, false);
        }

        ScheduledTime item = schedules.get(position);
        TextView tvDateTime = convertView.findViewById(R.id.tvScheduleDateTime);
        ImageButton btnDelete = convertView.findViewById(R.id.btnDeleteSchedule);

        tvDateTime.setText(item.getDateTime());

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                .setTitle("Eliminar horario")
                .setMessage("¿Estás seguro de que deseas eliminar este horario?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    if (onDeleteListener != null) {
                        onDeleteListener.onDelete(item);
                    }
                })
                .setNegativeButton("No", null)
                .show();
        });

        return convertView;
    }
} 