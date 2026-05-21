package com.example.barbershop.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.barbershop.R;
import com.example.barbershop.data.local.dao.CityDao;
import com.example.barbershop.data.local.dao.UserDao;
import com.example.barbershop.data.local.db.AppDatabase;
import com.example.barbershop.data.local.entity.CityEntity;
import com.example.barbershop.data.local.entity.UserEntity;
import com.example.barbershop.utils.PasswordUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilFullName, tilRegEmail, tilPhone, tilCity, tilRegPassword, tilConfirmPassword;
    private TextInputEditText etFullName, etRegEmail, etPhone, etRegPassword, etConfirmPassword;
    private MaterialAutoCompleteTextView actvCity;
    private MaterialButton btnRegister;
    private TextView tvGoToLogin;
    private LinearProgressIndicator progressRegister;

    private UserDao userDao;
    private CityDao cityDao;
    private ExecutorService executorService;

    private final List<CityEntity> cityList = new ArrayList<>();
    private CityEntity selectedCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        AppDatabase db = AppDatabase.getInstance(this);
        userDao = db.userDao();
        cityDao = db.cityDao();
        executorService = Executors.newSingleThreadExecutor();

        initViews();
        setupListeners();
        loadCities();
    }

    private void initViews() {
        tilFullName = findViewById(R.id.tilFullName);
        tilRegEmail = findViewById(R.id.tilRegEmail);
        tilPhone = findViewById(R.id.tilPhone);
        tilCity = findViewById(R.id.tilCity);
        tilRegPassword = findViewById(R.id.tilRegPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        etFullName = findViewById(R.id.etFullName);
        etRegEmail = findViewById(R.id.etRegEmail);
        etPhone = findViewById(R.id.etPhone);
        etRegPassword = findViewById(R.id.etRegPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        actvCity = findViewById(R.id.actvCity);

        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);
        progressRegister = findViewById(R.id.progressRegister);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> registerUser());
        tvGoToLogin.setOnClickListener(v -> finish());

        clearErrorOnTyping(etFullName, tilFullName);
        clearErrorOnTyping(etRegEmail, tilRegEmail);
        clearErrorOnTyping(etPhone, tilPhone);
        clearErrorOnTyping(etRegPassword, tilRegPassword);
        clearErrorOnTyping(etConfirmPassword, tilConfirmPassword);

        actvCity.setOnClickListener(v -> {
            actvCity.requestFocus();
            actvCity.showDropDown();
        });

        actvCity.setOnTouchListener((v, event) -> {
            actvCity.requestFocus();
            actvCity.showDropDown();
            return false;
        });
    }

    private void loadCities() {
        executorService.execute(() -> {
            List<CityEntity> cities = cityDao.getAllCities();

            runOnUiThread(() -> {
                cityList.clear();
                cityList.addAll(cities);

                ArrayAdapter<CityEntity> adapter = new ArrayAdapter<>(
                        RegisterActivity.this,
                        android.R.layout.simple_list_item_1,
                        cityList
                );
                actvCity.setAdapter(adapter);

                actvCity.setOnItemClickListener((parent, view, position, id) -> {
                    selectedCity = cityList.get(position);
                    tilCity.setError(null);
                });
            });
        });
    }

    private void registerUser() {
        String fullName = getTrimmedText(etFullName);
        String email = getTrimmedText(etRegEmail).toLowerCase(Locale.ROOT);
        String phone = getTrimmedText(etPhone);
        String password = getRawText(etRegPassword);
        String confirmPassword = getRawText(etConfirmPassword);

        if (!validateInputs(fullName, email, phone, password, confirmPassword)) {
            return;
        }

        setLoading(true);

        executorService.execute(() -> {
            try {
                UserEntity existingUser = userDao.getUserByEmail(email);
                if (existingUser != null) {
                    runOnUiThread(() -> {
                        tilRegEmail.setError("Пользователь с таким email уже существует");
                        setLoading(false);
                    });
                    return;
                }

                String salt = PasswordUtils.generateSalt();
                String passwordHash = PasswordUtils.hashPassword(password, salt);

                UserEntity user = new UserEntity();
                user.setFullName(fullName);
                user.setEmail(email);
                user.setPhone(phone);
                user.setCityId(selectedCity.getId());
                user.setSalt(salt);
                user.setPasswordHash(passwordHash);
                user.setCreatedAt(System.currentTimeMillis());

                userDao.insert(user);

                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "Аккаунт создан. Теперь войдите в систему.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                    setLoading(false);
                });
            }
        });
    }

    private boolean validateInputs(String fullName, String email, String phone, String password, String confirmPassword) {
        tilFullName.setError(null);
        tilRegEmail.setError(null);
        tilPhone.setError(null);
        tilCity.setError(null);
        tilRegPassword.setError(null);
        tilConfirmPassword.setError(null);

        boolean isValid = true;

        if (fullName.isEmpty()) {
            tilFullName.setError("Введите имя");
            isValid = false;
        } else if (fullName.length() < 2) {
            tilFullName.setError("Минимум 2 символа");
            isValid = false;
        }

        if (email.isEmpty()) {
            tilRegEmail.setError("Введите email");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilRegEmail.setError("Некорректный email");
            isValid = false;
        }

        String digitsOnlyPhone = phone.replaceAll("[^0-9]", "");
        if (phone.isEmpty()) {
            tilPhone.setError("Введите телефон");
            isValid = false;
        } else if (digitsOnlyPhone.length() < 10) {
            tilPhone.setError("Некорректный телефон");
            isValid = false;
        }

        if (selectedCity == null) {
            tilCity.setError("Выберите город");
            isValid = false;
        }

        if (password.isEmpty()) {
            tilRegPassword.setError("Введите пароль");
            isValid = false;
        } else if (password.length() < 6) {
            tilRegPassword.setError("Минимум 6 символов");
            isValid = false;
        }

        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.setError("Повторите пароль");
            isValid = false;
        } else if (!confirmPassword.equals(password)) {
            tilConfirmPassword.setError("Пароли не совпадают");
            isValid = false;
        }

        return isValid;
    }

    private void setLoading(boolean isLoading) {
        btnRegister.setEnabled(!isLoading);
        etFullName.setEnabled(!isLoading);
        etRegEmail.setEnabled(!isLoading);
        etPhone.setEnabled(!isLoading);
        actvCity.setEnabled(!isLoading);
        etRegPassword.setEnabled(!isLoading);
        etConfirmPassword.setEnabled(!isLoading);
        progressRegister.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void clearErrorOnTyping(TextInputEditText editText, TextInputLayout inputLayout) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputLayout.setError(null);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private String getTrimmedText(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    private String getRawText(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}