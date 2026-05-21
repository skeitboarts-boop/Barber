package com.example.barbershop.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barbershop.R;
import com.example.barbershop.data.local.entity.BarberEntity;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BarbersAdapter extends RecyclerView.Adapter<BarbersAdapter.BarberViewHolder> {

    public interface OnBarberClickListener {
        void onBarberClick(BarberEntity barber);
    }

    private final List<BarberEntity> items = new ArrayList<>();
    private final OnBarberClickListener listener;
    private long selectedBarberId = -1L;

    public BarbersAdapter(OnBarberClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<BarberEntity> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public void setSelectedBarberId(long selectedBarberId) {
        this.selectedBarberId = selectedBarberId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BarberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_barber, parent, false);
        return new BarberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BarberViewHolder holder, int position) {
        BarberEntity item = items.get(position);

        holder.tvBarberName.setText(item.getName());
        holder.tvBarberSpec.setText(item.getSpecialization());
        holder.tvBarberMeta.setText(
                "Опыт: " + item.getExperienceYears() + " " + getYearsWord(item.getExperienceYears()) +
                        " • ★ " + String.format(Locale.getDefault(), "%.1f", item.getRating())
        );
        holder.tvBarberPhone.setText(item.getPhone());
        holder.tvBarberInitials.setText(getInitials(item.getName()));

        boolean isSelected = item.getId() == selectedBarberId;
        holder.tvSelectedBadge.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        holder.cardBarber.setStrokeWidth(isSelected ? 3 : 1);
        holder.cardBarber.setStrokeColor(holder.itemView.getContext().getColor(
                isSelected ? R.color.accent_500 : R.color.stroke_light
        ));

        holder.itemView.setOnClickListener(v -> listener.onBarberClick(item));
    }

    private String getYearsWord(int years) {
        int lastTwo = years % 100;
        int lastOne = years % 10;

        if (lastTwo >= 11 && lastTwo <= 14) {
            return "лет";
        }
        if (lastOne == 1) {
            return "год";
        }
        if (lastOne >= 2 && lastOne <= 4) {
            return "года";
        }
        return "лет";
    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    private String getInitials(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "B";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase(Locale.getDefault());
        return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase(Locale.getDefault());
    }

    static class BarberViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardBarber;
        android.widget.TextView tvBarberInitials, tvBarberName, tvBarberSpec, tvBarberMeta, tvBarberPhone, tvSelectedBadge;

        public BarberViewHolder(@NonNull View itemView) {
            super(itemView);
            cardBarber = itemView.findViewById(R.id.cardBarber);
            tvBarberInitials = itemView.findViewById(R.id.tvBarberInitials);
            tvBarberName = itemView.findViewById(R.id.tvBarberName);
            tvBarberSpec = itemView.findViewById(R.id.tvBarberSpec);
            tvBarberMeta = itemView.findViewById(R.id.tvBarberMeta);
            tvBarberPhone = itemView.findViewById(R.id.tvBarberPhone);
            tvSelectedBadge = itemView.findViewById(R.id.tvSelectedBadge);
        }
    }
}