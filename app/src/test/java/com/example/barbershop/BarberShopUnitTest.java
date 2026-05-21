package com.example.barbershop;

import com.example.barbershop.utils.AppointmentValidator;

import org.junit.Test;

import static org.junit.Assert.*;

public class BarberShopUnitTest {

    @Test
    public void canCreateAppointment_allFieldsFilled_slotFree_returnsTrue() {
        boolean result = AppointmentValidator.canCreateAppointment(
                "Мужская стрижка",
                "Barber House Центр",
                "Артём Волков",
                "2026-05-20",
                "14:00",
                0
        );
        assertTrue(result);
    }
    @Test
    public void canCreateAppointment_slotOccupied_returnsFalse() {
        boolean result = AppointmentValidator.canCreateAppointment(
                "Мужская стрижка",
                "Barber House Центр",
                "Артём Волков",
                "2026-05-20",
                "14:00",
                1
        );
        assertFalse(result);
    }
    @Test
    public void canCreateAppointment_nullService_returnsFalse() {
        boolean result = AppointmentValidator.canCreateAppointment(
                null,
                "Barber House Центр",
                "Артём Волков",
                "2026-05-20",
                "14:00",
                0
        );
        assertFalse(result);
    }
    @Test
    public void canCreateAppointment_emptyBranch_returnsFalse() {
        boolean result = AppointmentValidator.canCreateAppointment(
                "Мужская стрижка",
                "",
                "Артём Волков",
                "2026-05-20",
                "14:00",
                0
        );
        assertFalse(result);
    }
    @Test
    public void canCreateAppointment_nullBarber_returnsFalse() {
        boolean result = AppointmentValidator.canCreateAppointment(
                "Мужская стрижка",
                "Barber House Центр",
                null,
                "2026-05-20",
                "14:00",
                0
        );
        assertFalse(result);
    }
    @Test
    public void canCreateAppointment_blankDate_returnsFalse() {
        boolean result = AppointmentValidator.canCreateAppointment(
                "Мужская стрижка",
                "Barber House Центр",
                "Артём Волков",
                " ",
                "14:00",
                0
        );
        assertFalse(result);
    }
    @Test
    public void canCreateAppointment_nullTime_returnsFalse() {
        boolean result = AppointmentValidator.canCreateAppointment(
                "Мужская стрижка",
                "Barber House Центр",
                "Артём Волков",
                "2026-05-20",
                null,
                0
        );
        assertFalse(result);
    }
    @Test
    public void isTimeSlotAvailable_countZero_returnsTrue() {
        assertTrue(AppointmentValidator.isTimeSlotAvailable(0));
    }
    @Test
    public void isTimeSlotAvailable_countOne_returnsFalse() {
        assertFalse(AppointmentValidator.isTimeSlotAvailable(1));
    }
    @Test
    public void isTimeSlotAvailable_countFive_returnsFalse() {
        assertFalse(AppointmentValidator.isTimeSlotAvailable(5));
    }
    @Test
    public void canCreateAppointment_realData_slotFree_returnsTrue() {
        boolean result = AppointmentValidator.canCreateAppointment(
                "Стрижка + борода",
                "Barber House Энгельс",
                "Дмитрий Козлов",
                "2026-07-15",
                "11:00",
                0
        );
        assertTrue(result);
    }
    @Test
    public void canCreateAppointment_nullServiceAndBarber_returnsFalse() {
        boolean result = AppointmentValidator.canCreateAppointment(
                null,
                "Barber House Центр",
                null,
                "2026-05-20",
                "14:00",
                0
        );
        assertFalse(result);
    }
}