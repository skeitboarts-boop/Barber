package com.example.barbershop.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barbershop.R;
import com.example.barbershop.data.local.model.AppointmentDetails;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.AppointmentViewHolder> {

    public interface OnAppointmentActionListener {
        void onCancelClick(AppointmentDetails appointment);
    }

    private final List<AppointmentDetails> items = new ArrayList<>();
    private final OnAppointmentActionListener listener;

    public AppointmentsAdapter(OnAppointmentActionListener listener) {
        this.listener = listener;
    }

    public void submitList(List<AppointmentDetails> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        AppointmentDetails item = items.get(position);

        holder.tvItemService.setText(item.serviceName);
        holder.tvItemDateTime.setText("Дата: " + formatDate(item.appointmentDate) + " • " + item.appointmentTime);
        holder.tvItemBarber.setText("Мастер: " + item.barberName + " (" + item.barberSpecialization + ")");
        holder.tvItemBranch.setText(item.branchTitle + "\n" + item.branchAddress);
        holder.tvItemStatus.setText(getStatusLabel(item.status));

        boolean canCancel = "ACTIVE".equals(item.status);
        holder.btnCancelAppointment.setVisibility(canCancel ? View.VISIBLE : View.GONE);
        holder.btnCancelAppointment.setOnClickListener(v -> listener.onCancelClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String formatDate(String isoDate) {
        if (isoDate == null || isoDate.length() != 10) return isoDate;
        return isoDate.substring(8, 10) + "." + isoDate.substring(5, 7) + "." + isoDate.substring(0, 4);
    }

    private String getStatusLabel(String status) {
        if ("CANCELLED".equals(status)) return "Отменена";
        if ("COMPLETED".equals(status)) return "Завершена";
        return "Активна";
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        android.widget.TextView tvItemService, tvItemDateTime, tvItemBarber, tvItemBranch, tvItemStatus;
        MaterialButton btnCancelAppointment;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemService = itemView.findViewById(R.id.tvItemService);
            tvItemDateTime = itemView.findViewById(R.id.tvItemDateTime);
            tvItemBarber = itemView.findViewById(R.id.tvItemBarber);
            tvItemBranch = itemView.findViewById(R.id.tvItemBranch);
            tvItemStatus = itemView.findViewById(R.id.tvItemStatus);
            btnCancelAppointment = itemView.findViewById(R.id.btnCancelAppointment);
        }
    }
}