package com.example.barbershop.ui.fragments;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.barbershop.R;
import com.example.barbershop.data.local.dao.CityDao;
import com.example.barbershop.data.local.dao.UserDao;
import com.example.barbershop.data.local.db.AppDatabase;
import com.example.barbershop.data.local.entity.CityEntity;
import com.example.barbershop.data.local.entity.UserEntity;
import com.example.barbershop.session.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditProfileFragment extends Fragment {

    private TextInputLayout tilEditEmail, tilEditPhone, tilEditCity;
    private TextInputEditText etEditEmail, etEditPhone;
    private MaterialAutoCompleteTextView actvEditCity;
    private MaterialButton btnSaveProfile;
    private LinearProgressIndicator progressEditProfile;

    private SessionManager sessionManager;
    private UserDao userDao;
    private CityDao cityDao;
    private ExecutorService executorService;

    private final List<CityEntity> cityList = new ArrayList<>();
    private CityEntity selectedCity;
    private UserEntity currentUser;

    public EditProfileFragment() {
        super(R.layout.fragment_edit_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tilEditEmail = view.findViewById(R.id.tilEditEmail);
        tilEditPhone = view.findViewById(R.id.tilEditPhone);
        tilEditCity = view.findViewById(R.id.tilEditCity);
        etEditEmail = view.findViewById(R.id.etEditEmail);
        etEditPhone = view.findViewById(R.id.etEditPhone);
        actvEditCity = view.findViewById(R.id.actvEditCity);
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);
        progressEditProfile = view.findViewById(R.id.progressEditProfile);

        sessionManager = new SessionManager(requireContext());
        AppDatabase db = AppDatabase.getInstance(requireContext());
        userDao = db.userDao();
        cityDao = db.cityDao();
        executorService = Executors.newSingleThreadExecutor();

        actvEditCity.setOnClickListener(v -> actvEditCity.showDropDown());
        btnSaveProfile.setOnClickListener(v -> saveProfile());

        loadData();
    }

    private void loadData() {
        setLoading(true);
        executorService.execute(() -> {
            currentUser = userDao.getUserById(sessionManager.getUserId());
            List<CityEntity> cities = cityDao.getAllCities();

            requireActivity().runOnUiThread(() -> {
                cityList.clear();
                cityList.addAll(cities);

                ArrayAdapter<CityEntity> adapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        cityList
                );
                actvEditCity.setAdapter(adapter);
                actvEditCity.setThreshold(0);

                actvEditCity.setOnItemClickListener((parent, view, position, id) -> {
                    selectedCity = cityList.get(position);
                    tilEditCity.setError(null);
                });

                if (currentUser != null) {
                    etEditEmail.setText(currentUser.getEmail());
                    etEditPhone.setText(currentUser.getPhone());

                    for (CityEntity city : cityList) {
                        if (city.getId() == currentUser.getCityId()) {
                            selectedCity = city;
                            actvEditCity.setText(city.getName(), false);
                            break;
                        }
                    }
                }

                setLoading(false);
            });
        });
    }

    private void saveProfile() {
        if (currentUser == null) return;

        String email = getText(etEditEmail).toLowerCase(Locale.ROOT);
        String phone = getText(etEditPhone);

        tilEditEmail.setError(null);
        tilEditPhone.setError(null);
        tilEditCity.setError(null);

        boolean isValid = true;

        if (email.isEmpty()) {
            tilEditEmail.setError("Введите email");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEditEmail.setError("Некорректный email");
            isValid = false;
        }

        String digitsOnlyPhone = phone.replaceAll("[^0-9]", "");
        if (phone.isEmpty()) {
            tilEditPhone.setError("Введите телефон");
            isValid = false;
        } else if (digitsOnlyPhone.length() < 10) {
            tilEditPhone.setError("Некорректный телефон");
            isValid = false;
        }

        if (selectedCity == null) {
            tilEditCity.setError("Выберите город");
            isValid = false;
        }

        if (!isValid) return;

        setLoading(true);

        executorService.execute(() -> {
            UserEntity userWithEmail = userDao.getUserByEmail(email);
            if (userWithEmail != null && userWithEmail.getId() != currentUser.getId()) {
                requireActivity().runOnUiThread(() -> {
                    tilEditEmail.setError("Этот email уже используется");
                    setLoading(false);
                });
                return;
            }

            userDao.updateProfile(currentUser.getId(), email, phone, selectedCity.getId());

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Данные профиля обновлены", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(EditProfileFragment.this).navigateUp();
            });
        });
    }

    private void setLoading(boolean isLoading) {
        progressEditProfile.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnSaveProfile.setEnabled(!isLoading);
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}