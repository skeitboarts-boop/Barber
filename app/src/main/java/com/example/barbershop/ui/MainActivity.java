package com.example.barbershop.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.barbershop.R;
import com.example.barbershop.session.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            openLogin();
            return;
        }

        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        setSupportActionBar(toolbar);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHostFragment);

        if (navHostFragment == null) return;

        navController = navHostFragment.getNavController();

        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.homeFragment);
        topLevelDestinations.add(R.id.bookingFragment);
        topLevelDestinations.add(R.id.appointmentsFragment);
        topLevelDestinations.add(R.id.profileFragment);

        appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations).build();

        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        toolbar.setNavigationOnClickListener(v -> navController.navigateUp());

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int id = destination.getId();

            boolean isTopLevel =
                    id == R.id.homeFragment ||
                            id == R.id.bookingFragment ||
                            id == R.id.appointmentsFragment ||
                            id == R.id.profileFragment;

            bottomNavigationView.setVisibility(isTopLevel ? android.view.View.VISIBLE : android.view.View.GONE);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(!isTopLevel);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (navController != null) {
            return navController.navigateUp() || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (navController != null) {
            int id = navController.getCurrentDestination() != null
                    ? navController.getCurrentDestination().getId()
                    : -1;

            boolean isTopLevel =
                    id == R.id.homeFragment ||
                            id == R.id.bookingFragment ||
                            id == R.id.appointmentsFragment ||
                            id == R.id.profileFragment;

            if (!isTopLevel) {
                navController.navigateUp();
                return;
            }
        }
        super.onBackPressed();
    }

    private void openLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}