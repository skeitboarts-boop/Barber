package com.example.barbershop.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "appointments")
public class AppointmentEntity {

    public static final String STATUS_ACTIVE    = "ACTIVE";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_COMPLETED = "COMPLETED";

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long userId;
    private long serviceId;
    private long barberId;
    private long branchId;

    @NonNull
    private String appointmentDate = "";

    @NonNull
    private String appointmentTime = "";

    @NonNull
    private String status = STATUS_ACTIVE;

    private long createdAt;

    public AppointmentEntity() {
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public long getServiceId() { return serviceId; }
    public void setServiceId(long serviceId) { this.serviceId = serviceId; }

    public long getBarberId() { return barberId; }
    public void setBarberId(long barberId) { this.barberId = barberId; }

    public long getBranchId() { return branchId; }
    public void setBranchId(long branchId) { this.branchId = branchId; }

    @NonNull
    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(@NonNull String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    @NonNull
    public String getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(@NonNull String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    @NonNull
    public String getStatus() { return status; }
    public void setStatus(@NonNull String status) { this.status = status; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}