package com.example.barbershop;

import com.example.barbershop.utils.AppointmentValidator;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class AppointmentValidatorOccupiedSlotTest {

    @Test
    public void canCreateAppointment_shouldReturnFalse_whenTimeSlotIsOccupied() {
        boolean result = AppointmentValidator.canCreateAppointment(
                "Мужская стрижка",
                "Barber House Центр",
                "Артём Волков",
                "2026-04-10",
                "14:00",
                1
        );

        assertFalse(result);
    }
}