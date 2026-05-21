package com.example.barbershop.ui.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barbershop.R;
import com.example.barbershop.data.local.dao.AppointmentDao;
import com.example.barbershop.data.local.db.AppDatabase;
import com.example.barbershop.data.local.model.AppointmentDetails;
import com.example.barbershop.session.SessionManager;
import com.example.barbershop.ui.adapters.AppointmentsAdapter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppointmentsFragment extends Fragment {

    private RecyclerView recyclerAppointments;
    private TextView tvEmptyAppointments;

    private SessionManager sessionManager;
    private AppointmentDao appointmentDao;
    private ExecutorService executorService;
    private AppointmentsAdapter adapter;

    public AppointmentsFragment() {
        super(R.layout.fragment_appointments);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerAppointments = view.findViewById(R.id.recyclerAppointments);
        tvEmptyAppointments = view.findViewById(R.id.tvEmptyAppointments);

        sessionManager = new SessionManager(requireContext());
        appointmentDao = AppDatabase.getInstance(requireContext()).appointmentDao();
        executorService = Executors.newSingleThreadExecutor();

        adapter = new AppointmentsAdapter(this::cancelAppointment);
        recyclerAppointments.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerAppointments.setAdapter(adapter);

        loadAppointments();
    }

    private void loadAppointments() {
        executorService.execute(() -> {
            List<AppointmentDetails> appointments =
                    appointmentDao.getAppointmentsByUser(sessionManager.getUserId());

            requireActivity().runOnUiThread(() -> {
                adapter.submitList(appointments);
                tvEmptyAppointments.setVisibility(
                        appointments.isEmpty() ? View.VISIBLE : View.GONE);
                recyclerAppointments.setVisibility(
                        appointments.isEmpty() ? View.GONE : View.VISIBLE);
            });
        });
    }

    private void cancelAppointment(AppointmentDetails appointment) {
        executorService.execute(() -> {
            appointmentDao.updateStatus(appointment.appointmentId, "CANCELLED");
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Запись отменена", Toast.LENGTH_SHORT).show();
                loadAppointments();
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAppointments();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}