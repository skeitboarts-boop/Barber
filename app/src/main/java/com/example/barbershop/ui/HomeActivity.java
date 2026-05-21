package com.example.barbershop.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.barbershop.R;
import com.example.barbershop.data.local.dao.BranchDao;
import com.example.barbershop.data.local.dao.CityDao;
import com.example.barbershop.data.local.dao.UserDao;
import com.example.barbershop.data.local.db.AppDatabase;
import com.example.barbershop.data.local.entity.BranchEntity;
import com.example.barbershop.data.local.entity.CityEntity;
import com.example.barbershop.data.local.entity.UserEntity;
import com.example.barbershop.session.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {

    private TextView tvGreeting, tvUserName, tvUserEmail, tvUserPhone, tvSelectedCity, tvBranchAddress;
    private AutoCompleteTextView actvBranch;
    private MaterialButton btnLogout;

    private SessionManager sessionManager;
    private UserDao userDao;
    private CityDao cityDao;
    private BranchDao branchDao;
    private ExecutorService executorService;

    private final List<BranchEntity> branchList = new ArrayList<>();
    private BranchEntity selectedBranch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            openLogin();
            return;
        }

        setContentView(R.layout.activity_home);

        AppDatabase db = AppDatabase.getInstance(this);
        userDao = db.userDao();
        cityDao = db.cityDao();
        branchDao = db.branchDao();
        executorService = Executors.newSingleThreadExecutor();

        initViews();
        loadUserData();

        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            openLogin();
        });

        actvBranch.setOnClickListener(v -> actvBranch.showDropDown());
    }

    private void initViews() {
        tvGreeting = findViewById(R.id.tvGreeting);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        tvSelectedCity = findViewById(R.id.tvSelectedCity);
        tvBranchAddress = findViewById(R.id.tvBranchAddress);
        actvBranch = findViewById(R.id.actvBranch);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void loadUserData() {
        executorService.execute(() -> {
            long userId = sessionManager.getUserId();
            UserEntity user = userDao.getUserById(userId);

            if (user == null) {
                runOnUiThread(() -> {
                    sessionManager.logout();
                    openLogin();
                });
                return;
            }

            CityEntity city = cityDao.getCityById(user.getCityId());
            List<BranchEntity> branches = branchDao.getBranchesByCityId(user.getCityId());

            runOnUiThread(() -> {
                String fullName = user.getFullName();
                String cityName = city != null ? city.getName() : "Не выбран";

                tvGreeting.setText("Привет, " + getFirstName(fullName) + " 👋");
                tvUserName.setText("Имя: " + fullName);
                tvUserEmail.setText("Email: " + user.getEmail());
                tvUserPhone.setText("Телефон: " + user.getPhone());
                tvSelectedCity.setText("Ваш город: " + cityName);

                setupBranches(branches);
            });
        });
    }

    private void setupBranches(List<BranchEntity> branches) {
        branchList.clear();
        branchList.addAll(branches);

        ArrayAdapter<BranchEntity> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                branchList
        );
        actvBranch.setAdapter(adapter);

        if (!branchList.isEmpty()) {
            selectedBranch = branchList.get(0);
            actvBranch.setText(selectedBranch.toString(), false);
            tvBranchAddress.setText("Адрес: " + selectedBranch.getAddress());
        }

        actvBranch.setOnItemClickListener((parent, view, position, id) -> {
            selectedBranch = branchList.get(position);
            tvBranchAddress.setText("Адрес: " + selectedBranch.getAddress());
        });
    }

    private String getFirstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "друг";
        }
        String[] parts = fullName.trim().split("\\s+");
        return parts[0];
    }

    private void openLogin() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}