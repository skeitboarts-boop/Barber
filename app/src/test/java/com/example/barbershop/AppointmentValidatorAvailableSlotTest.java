package com.example.barbershop;

import com.example.barbershop.utils.AppointmentValidator;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AppointmentValidatorAvailableSlotTest {

    @Test
    public void canCreateAppointment_shouldReturnTrue_whenAllFieldsAreFilledAndTimeSlotIsFree() {
        boolean result = AppointmentValidator.canCreateAppointment(
                "Мужская стрижка",
                "Barber House Центр",
                "Артём Волков",
                "2026-04-10",
                "14:00",
                0
        );

        assertTrue(result);
    }
}