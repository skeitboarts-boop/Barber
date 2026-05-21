package com.example.barbershop.ui.fragments;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChangePasswordFragment extends Fragment {

    private TextInputLayout tilOldPassword, tilNewPassword, tilConfirmNewPassword;
    private TextInputEditText etOldPassword, etNewPassword, etConfirmNewPassword;
    private MaterialButton btnSavePassword;
    private LinearProgressIndicator progressChangePassword;

    private SessionManager sessionManager;
    private UserDao userDao;
    private ExecutorService executorService;

    public ChangePasswordFragment() {
        super(R.layout.fragment_change_password);
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tilOldPassword = view.findViewById(R.id.tilOldPassword);
        tilNewPassword = view.findViewById(R.id.tilNewPassword);
        tilConfirmNewPassword = view.findViewById(R.id.tilConfirmNewPassword);

        etOldPassword = view.findViewById(R.id.etOldPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etConfirmNewPassword = view.findViewById(R.id.etConfirmNewPassword);

        btnSavePassword = view.findViewById(R.id.btnSavePassword);
        progressChangePassword = view.findViewById(R.id.progressChangePassword);

        sessionManager = new SessionManager(requireContext());
        userDao = AppDatabase.getInstance(requireContext()).userDao();
        executorService = Executors.newSingleThreadExecutor();

        btnSavePassword.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String oldPassword = getText(etOldPassword);
        String newPassword = getText(etNewPassword);
        String confirmPassword = getText(etConfirmNewPassword);

        tilOldPassword.setError(null);
        tilNewPassword.setError(null);
        tilConfirmNewPassword.setError(null);

        boolean isValid = true;

        if (oldPassword.isEmpty()) {
            tilOldPassword.setError("Введите старый пароль");
            isValid = false;
        }

        if (newPassword.isEmpty()) {
            tilNewPassword.setError("Введите новый пароль");
            isValid = false;
        } else if (newPassword.length() < 6) {
            tilNewPassword.setError("Минимум 6 символов");
            isValid = false;
        }

        if (confirmPassword.isEmpty()) {
            tilConfirmNewPassword.setError("Повторите новый пароль");
            isValid = false;
        } else if (!confirmPassword.equals(newPassword)) {
            tilConfirmNewPassword.setError("Пароли не совпадают");
            isValid = false;
        }

        if (!isValid) return;

        setLoading(true);

        executorService.execute(() -> {
            UserEntity user = userDao.getUserById(sessionManager.getUserId());
            if (user == null) {
                requireActivity().runOnUiThread(() -> setLoading(false));
                return;
            }

            boolean oldPasswordCorrect = PasswordUtils.verifyPassword(
                    oldPassword,
                    user.getSalt(),
                    user.getPasswordHash()
            );

            if (!oldPasswordCorrect) {
                requireActivity().runOnUiThread(() -> {
                    tilOldPassword.setError("Старый пароль неверный");
                    setLoading(false);
                });
                return;
            }

            String newSalt = PasswordUtils.generateSalt();
            String newHash = PasswordUtils.hashPassword(newPassword, newSalt);

            userDao.updatePassword(user.getId(), newHash, newSalt);

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Пароль успешно изменён", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(ChangePasswordFragment.this).navigateUp();
            });
        });
    }

    private void setLoading(boolean isLoading) {
        progressChangePassword.setVisibility(isLoading ? android.view.View.VISIBLE : android.view.View.GONE);
        btnSavePassword.setEnabled(!isLoading);
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