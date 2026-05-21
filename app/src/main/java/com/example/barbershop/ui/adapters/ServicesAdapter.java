package com.example.barbershop.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barbershop.R;
import com.example.barbershop.data.local.entity.ServiceEntity;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ServiceViewHolder> {

    public interface OnServiceClickListener {
        void onServiceClick(ServiceEntity service);
    }

    private final List<ServiceEntity> items = new ArrayList<>();
    private final OnServiceClickListener listener;
    private long selectedServiceId = -1L;

    public ServicesAdapter(OnServiceClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<ServiceEntity> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public void setSelectedServiceId(long selectedServiceId) {
        this.selectedServiceId = selectedServiceId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        ServiceEntity item = items.get(position);

        holder.tvServiceName.setText(item.getName());
        holder.tvServiceDescription.setText(item.getDescription());
        holder.tvServicePrice.setText(item.getPrice() + " ₽");
        holder.tvServiceDuration.setText(item.getDurationMinutes() + " мин");

        boolean isSelected = item.getId() == selectedServiceId;
        holder.tvSelectedServiceBadge.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        holder.cardService.setStrokeWidth(isSelected ? 3 : 1);
        holder.cardService.setStrokeColor(holder.itemView.getContext().getColor(
                isSelected ? R.color.accent_500 : R.color.stroke_light
        ));

        holder.itemView.setOnClickListener(v -> listener.onServiceClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardService;
        android.widget.TextView tvServiceName, tvServiceDescription, tvServicePrice, tvServiceDuration, tvSelectedServiceBadge;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            cardService = itemView.findViewById(R.id.cardService);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvServiceDescription = itemView.findViewById(R.id.tvServiceDescription);
            tvServicePrice = itemView.findViewById(R.id.tvServicePrice);
            tvServiceDuration = itemView.findViewById(R.id.tvServiceDuration);
            tvSelectedServiceBadge = itemView.findViewById(R.id.tvSelectedServiceBadge);
        }
    }
}