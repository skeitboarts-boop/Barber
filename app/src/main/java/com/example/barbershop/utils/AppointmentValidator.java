package com.example.barbershop.utils;

public class AppointmentValidator {

    private AppointmentValidator() {
    }

    public static boolean isTimeSlotAvailable(int activeAppointmentsCount) {
        return activeAppointmentsCount == 0;
    }

    public static boolean canCreateAppointment(
            String service,
            String branch,
            String barber,
            String date,
            String time,
            int activeAppointmentsCount
    ) {
        return isFilled(service)
                && isFilled(branch)
                && isFilled(barber)
                && isFilled(date)
                && isFilled(time)
                && isTimeSlotAvailable(activeAppointmentsCount);
    }

    private static boolean isFilled(String value) {
        return value != null && !value.trim().isEmpty();
    }
}