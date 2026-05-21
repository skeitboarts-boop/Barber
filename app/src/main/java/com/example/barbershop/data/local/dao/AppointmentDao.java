package com.example.barbershop.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.barbershop.data.local.entity.AppointmentEntity;
import com.example.barbershop.data.local.model.AppointmentDetails;

import java.util.List;

@Dao
public interface AppointmentDao {

    @Insert
    long insert(AppointmentEntity appointment);

    @Query("SELECT COUNT(*) FROM appointments " +
            "WHERE barberId = :barberId AND appointmentDate = :appointmentDate " +
            "AND appointmentTime = :appointmentTime AND status = 'ACTIVE'")
    int countActiveAppointmentsByBarberAndSlot(long barberId, String appointmentDate, String appointmentTime);

    @Query("UPDATE appointments SET status = :status WHERE id = :appointmentId")
    void updateStatus(long appointmentId, String status);

    @Query("SELECT " +
            "a.id AS appointmentId, " +
            "a.appointmentDate AS appointmentDate, " +
            "a.appointmentTime AS appointmentTime, " +
            "a.status AS status, " +
            "s.name AS serviceName, " +
            "s.durationMinutes AS durationMinutes, " +
            "b.name AS barberName, " +
            "b.specialization AS barberSpecialization, " +
            "br.title AS branchTitle, " +
            "br.address AS branchAddress " +
            "FROM appointments a " +
            "INNER JOIN services s ON s.id = a.serviceId " +
            "INNER JOIN barbers b ON b.id = a.barberId " +
            "INNER JOIN branches br ON br.id = a.branchId " +
            "WHERE a.userId = :userId " +
            "ORDER BY a.appointmentDate DESC, a.appointmentTime DESC")
    List<AppointmentDetails> getAppointmentsByUser(long userId);

    @Query("SELECT " +
            "a.id AS appointmentId, " +
            "a.appointmentDate AS appointmentDate, " +
            "a.appointmentTime AS appointmentTime, " +
            "a.status AS status, " +
            "s.name AS serviceName, " +
            "s.durationMinutes AS durationMinutes, " +
            "b.name AS barberName, " +
            "b.specialization AS barberSpecialization, " +
            "br.title AS branchTitle, " +
            "br.address AS branchAddress " +
            "FROM appointments a " +
            "INNER JOIN services s ON s.id = a.serviceId " +
            "INNER JOIN barbers b ON b.id = a.barberId " +
            "INNER JOIN branches br ON br.id = a.branchId " +
            "WHERE a.userId = :userId AND a.status = 'ACTIVE' " +
            "AND (a.appointmentDate > :todayDate OR (a.appointmentDate = :todayDate AND a.appointmentTime >= :nowTime)) " +
            "ORDER BY a.appointmentDate ASC, a.appointmentTime ASC " +
            "LIMIT 1")
    AppointmentDetails getNearestActiveAppointment(long userId, String todayDate, String nowTime);
}