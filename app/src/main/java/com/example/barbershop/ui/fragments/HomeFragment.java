package com.example.barbershop.ui.fragments;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.barbershop.R;
import com.example.barbershop.data.local.dao.AppointmentDao;
import com.example.barbershop.data.local.dao.BranchDao;
import com.example.barbershop.data.local.dao.CityDao;
import com.example.barbershop.data.local.dao.UserDao;
import com.example.barbershop.data.local.db.AppDatabase;
import com.example.barbershop.data.local.entity.BranchEntity;
import com.example.barbershop.data.local.entity.CityEntity;
import com.example.barbershop.data.local.entity.UserEntity;
import com.example.barbershop.data.local.model.AppointmentDetails;
import com.example.barbershop.session.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private TextView tvGreeting, tvSelectedCity, tvNearestRecord, tvBranchAddress;
    private MaterialAutoCompleteTextView actvBranch;
    private MaterialButton btnGoBooking;

    private SessionManager sessionManager;
    private UserDao userDao;
    private CityDao cityDao;
    private BranchDao branchDao;
    private AppointmentDao appointmentDao;
    private ExecutorService executorService;

    private final List<BranchEntity> branchList = new ArrayList<>();
    private AppointmentDetails nearestAppointment;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        AppDatabase db = AppDatabase.getInstance(requireContext());
        userDao = db.userDao();
        cityDao = db.cityDao();
        branchDao = db.branchDao();
        appointmentDao = db.appointmentDao();
        executorService = Executors.newSingleThreadExecutor();

        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvSelectedCity = view.findViewById(R.id.tvSelectedCity);
        tvNearestRecord = view.findViewById(R.id.tvNearestRecord);
        tvBranchAddress = view.findViewById(R.id.tvBranchAddress);
        actvBranch = view.findViewById(R.id.actvBranch);
        btnGoBooking = view.findViewById(R.id.btnGoBooking);

        loadData();

        btnGoBooking.setOnClickListener(v -> {
            BottomNavigationView bottomNavigationView =
                    requireActivity().findViewById(R.id.bottomNavigationView);

            if (bottomNavigationView == null) return;

            if (nearestAppointment != null) {
                bottomNavigationView.setSelectedItemId(R.id.appointmentsFragment);
            } else {
                bottomNavigationView.setSelectedItemId(R.id.bookingFragment);
            }
        });

        actvBranch.setOnClickListener(v -> actvBranch.showDropDown());
    }

    private void loadData() {
        executorService.execute(() -> {
            long userId = sessionManager.getUserId();
            UserEntity user = userDao.getUserById(userId);
            if (user == null) return;

            CityEntity city = cityDao.getCityById(user.getCityId());
            List<BranchEntity> branches = branchDao.getBranchesByCityId(user.getCityId());

            String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String nowTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

            nearestAppointment = appointmentDao.getNearestActiveAppointment(userId, todayDate, nowTime);

            requireActivity().runOnUiThread(() -> {
                String fullName = user.getFullName();
                String cityName = city != null ? city.getName() : "Не выбран";

                tvGreeting.setText("Привет, " + getFirstName(fullName) + " 👋");
                tvSelectedCity.setText(cityName);

                if (nearestAppointment == null) {
                    tvNearestRecord.setText("Пока нет активных записей");
                    btnGoBooking.setText("Записаться");
                } else {
                    tvNearestRecord.setText(
                            formatDate(nearestAppointment.appointmentDate) + " • " + nearestAppointment.appointmentTime + "\n" +
                                    nearestAppointment.serviceName + " • " + nearestAppointment.barberName
                    );
                    btnGoBooking.setText("Перейти к записи");
                }

                setupBranches(branches);
            });
        });
    }

    private void setupBranches(List<BranchEntity> branches) {
        branchList.clear();
        branchList.addAll(branches);

        ArrayAdapter<BranchEntity> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                branchList
        );

        actvBranch.setAdapter(adapter);
        actvBranch.setThreshold(0);

        if (!branchList.isEmpty()) {
            BranchEntity firstBranch = branchList.get(0);
            actvBranch.setText(firstBranch.toString(), false);
            tvBranchAddress.setText("Адрес: " + firstBranch.getAddress());
        } else {
            tvBranchAddress.setText("Адрес филиала не найден");
        }

        actvBranch.setOnItemClickListener((parent, view, position, id) -> {
            BranchEntity selectedBranch = branchList.get(position);
            tvBranchAddress.setText("Адрес: " + selectedBranch.getAddress());
        });
    }

    private String getFirstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "друг";
        String[] parts = fullName.trim().split("\\s+");
        return parts[0];
    }

    private String formatDate(String isoDate) {
        if (isoDate == null || isoDate.length() != 10) return isoDate;
        return isoDate.substring(8, 10) + "." + isoDate.substring(5, 7) + "." + isoDate.substring(0, 4);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}