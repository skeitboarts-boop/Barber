package com.example.barbershop.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.barbershop.R;
import com.example.barbershop.data.local.dao.UserDao;
import com.example.barbershop.data.local.db.AppDatabase;
import com.example.barbershop.data.local.entity.UserEntity;
import com.example.barbershop.session.SessionManager;
import com.example.barbershop.utils.PasswordUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvGoToRegister;
    private LinearProgressIndicator progressLogin;

    private UserDao userDao;
    private SessionManager sessionManager;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            openMain();
            return;
        }

        setContentView(R.layout.activity_login);

        userDao = AppDatabase.getInstance(this).userDao();
        executorService = Executors.newSingleThreadExecutor();

        initViews();
        setupListeners();
    }

    private void initViews() {
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        progressLogin = findViewById(R.id.progressLogin);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> loginUser());

        tvGoToRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );

        clearErrorOnTyping(etEmail, tilEmail);
        clearErrorOnTyping(etPassword, tilPassword);
    }

    private void loginUser() {
        String email = getTrimmedText(etEmail).toLowerCase(Locale.ROOT);
        String password = getRawText(etPassword);

        if (!validateInputs(email, password)) {
            return;
        }

        setLoading(true);

        executorService.execute(() -> {
            UserEntity user = userDao.getUserByEmail(email);

            if (user == null || !PasswordUtils.verifyPassword(password, user.getSalt(), user.getPasswordHash())) {
                runOnUiThread(() -> {
                    tilPassword.setError("Неверный email или пароль");
                    setLoading(false);
                });
                return;
            }

            sessionManager.createLoginSession(
                    user.getId(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getPhone()
            );

            runOnUiThread(this::openMain);
        });
    }

    private boolean validateInputs(String email, String password) {
        tilEmail.setError(null);
        tilPassword.setError(null);

        boolean isValid = true;

        if (email.isEmpty()) {
            tilEmail.setError("Введите email");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Некорректный email");
            isValid = false;
        }

        if (password.isEmpty()) {
            tilPassword.setError("Введите пароль");
            isValid = false;
        }

        return isValid;
    }

    private void setLoading(boolean isLoading) {
        btnLogin.setEnabled(!isLoading);
        etEmail.setEnabled(!isLoading);
        etPassword.setEnabled(!isLoading);
        progressLogin.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void openMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void clearErrorOnTyping(TextInputEditText editText, TextInputLayout inputLayout) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
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