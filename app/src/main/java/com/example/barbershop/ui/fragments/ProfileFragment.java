package com.example.barbershop.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.barbershop.R;
import com.example.barbershop.data.local.dao.CityDao;
import com.example.barbershop.data.local.dao.UserDao;
import com.example.barbershop.data.local.db.AppDatabase;
import com.example.barbershop.data.local.entity.CityEntity;
import com.example.barbershop.data.local.entity.UserEntity;
import com.example.barbershop.session.SessionManager;
import com.example.barbershop.ui.LoginActivity;
import com.google.android.material.button.MaterialButton;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName, tvProfileEmail, tvProfilePhone, tvProfileCity, tvSessionStatus;
    private MaterialButton btnEditProfile, btnChangePassword, btnLogout;

    private SessionManager sessionManager;
    private UserDao userDao;
    private CityDao cityDao;
    private ExecutorService executorService;

    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        tvProfilePhone = view.findViewById(R.id.tvProfilePhone);
        tvProfileCity = view.findViewById(R.id.tvProfileCity);
        tvSessionStatus = view.findViewById(R.id.tvSessionStatus);

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogout = view.findViewById(R.id.btnLogout);

        sessionManager = new SessionManager(requireContext());
        AppDatabase db = AppDatabase.getInstance(requireContext());
        userDao = db.userDao();
        cityDao = db.cityDao();
        executorService = Executors.newSingleThreadExecutor();

        loadProfile();

        btnEditProfile.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(ProfileFragment.this);
            navController.navigate(R.id.action_profileFragment_to_editProfileFragment);
        });

        btnChangePassword.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(ProfileFragment.this);
            navController.navigate(R.id.action_profileFragment_to_changePasswordFragment);
        });

        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void loadProfile() {
        executorService.execute(() -> {
            UserEntity user = userDao.getUserById(sessionManager.getUserId());
            if (user == null) return;

            CityEntity city = cityDao.getCityById(user.getCityId());

            requireActivity().runOnUiThread(() -> {
                tvProfileName.setText("Имя: " + user.getFullName());
                tvProfileEmail.setText("Email: " + user.getEmail());
                tvProfilePhone.setText("Телефон: " + user.getPhone());
                tvProfileCity.setText("Город: " + (city != null ? city.getName() : "Не выбран"));
                tvSessionStatus.setText("Сессия активна");
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfile();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}