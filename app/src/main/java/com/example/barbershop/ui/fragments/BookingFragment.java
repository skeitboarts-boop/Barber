package com.example.barbershop.ui.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barbershop.R;
import com.example.barbershop.data.local.dao.AppointmentDao;
import com.example.barbershop.data.local.dao.BarberDao;
import com.example.barbershop.data.local.dao.BranchDao;
import com.example.barbershop.data.local.dao.ServiceDao;
import com.example.barbershop.data.local.dao.UserDao;
import com.example.barbershop.data.local.db.AppDatabase;
import com.example.barbershop.data.local.entity.AppointmentEntity;
import com.example.barbershop.data.local.entity.BarberEntity;
import com.example.barbershop.data.local.entity.BranchEntity;
import com.example.barbershop.data.local.entity.ServiceEntity;
import com.example.barbershop.data.local.entity.UserEntity;
import com.example.barbershop.session.SessionManager;
import com.example.barbershop.ui.adapters.BarbersAdapter;
import com.example.barbershop.ui.adapters.ServicesAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookingFragment extends Fragment {

    private MaterialAutoCompleteTextView actvBookingBranch, actvTime;
    private TextInputEditText etDate;
    private MaterialButton btnCreateAppointment;
    private LinearProgressIndicator progressBooking;
    private TextView tvBookingSummary, tvBarbersHint, tvSelectedServiceCompact;
    private RecyclerView recyclerBarbers, recyclerServices;
    private MaterialCardView cardServiceSelector;
    private LinearLayout layoutServicesContainer;
    private ImageView ivServiceExpand;

    private SessionManager sessionManager;
    private UserDao userDao;
    private ServiceDao serviceDao;
    private BranchDao branchDao;
    private BarberDao barberDao;
    private AppointmentDao appointmentDao;
    private ExecutorService executorService;

    private final List<ServiceEntity> serviceList = new ArrayList<>();
    private final List<BranchEntity> branchList = new ArrayList<>();

    private ServiceEntity selectedService;
    private BranchEntity selectedBranch;
    private BarberEntity selectedBarber;
    private String selectedDateIso = "";
    private String selectedDateRu = "";
    private String selectedTime = "";

    private BarbersAdapter barbersAdapter;
    private ServicesAdapter servicesAdapter;
    private boolean isServicesExpanded = false;

    private final String[] timeSlots = {
            "10:00", "11:00", "12:00", "13:00", "14:00",
            "15:00", "16:00", "17:00", "18:00", "19:00"
    };

    public BookingFragment() {
        super(R.layout.fragment_booking);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        actvBookingBranch = view.findViewById(R.id.actvBookingBranch);
        actvTime = view.findViewById(R.id.actvTime);
        etDate = view.findViewById(R.id.etDate);
        btnCreateAppointment = view.findViewById(R.id.btnCreateAppointment);
        progressBooking = view.findViewById(R.id.progressBooking);
        tvBookingSummary = view.findViewById(R.id.tvBookingSummary);
        tvBarbersHint = view.findViewById(R.id.tvBarbersHint);
        tvSelectedServiceCompact = view.findViewById(R.id.tvSelectedServiceCompact);
        recyclerBarbers = view.findViewById(R.id.recyclerBarbers);
        recyclerServices = view.findViewById(R.id.recyclerServices);
        cardServiceSelector = view.findViewById(R.id.cardServiceSelector);
        layoutServicesContainer = view.findViewById(R.id.layoutServicesContainer);
        ivServiceExpand = view.findViewById(R.id.ivServiceExpand);

        sessionManager = new SessionManager(requireContext());
        AppDatabase db = AppDatabase.getInstance(requireContext());
        userDao = db.userDao();
        serviceDao = db.serviceDao();
        branchDao = db.branchDao();
        barberDao = db.barberDao();
        appointmentDao = db.appointmentDao();
        executorService = Executors.newSingleThreadExecutor();

        setupRecycler();
        setupTimeDropdown();
        setupClicks();
        loadInitialData();
    }

    private void setupRecycler() {
        servicesAdapter = new ServicesAdapter(service -> {
            selectedService = service;
            servicesAdapter.setSelectedServiceId(service.getId());
            tvSelectedServiceCompact.setText(
                    service.getName() + " • " + service.getPrice() + " ₽ • " + service.getDurationMinutes() + " мин"
            );
            setServicesExpanded(false);
            updateSummary();
        });

        recyclerServices.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerServices.setAdapter(servicesAdapter);
        recyclerServices.setNestedScrollingEnabled(false);

        barbersAdapter = new BarbersAdapter(barber -> {
            selectedBarber = barber;
            barbersAdapter.setSelectedBarberId(barber.getId());
            updateSummary();
        });

        recyclerBarbers.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerBarbers.setAdapter(barbersAdapter);
        recyclerBarbers.setNestedScrollingEnabled(false);
    }

    private void setupTimeDropdown() {
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                timeSlots
        );
        actvTime.setAdapter(timeAdapter);
        actvTime.setThreshold(0);

        actvTime.setOnClickListener(v -> actvTime.showDropDown());
        actvTime.setOnItemClickListener((parent, view, position, id) -> {
            selectedTime = timeSlots[position];
            updateSummary();
        });
    }

    private void setupClicks() {
        cardServiceSelector.setOnClickListener(v -> setServicesExpanded(!isServicesExpanded));
        actvBookingBranch.setOnClickListener(v -> actvBookingBranch.showDropDown());
        etDate.setOnClickListener(v -> openDatePicker());
        btnCreateAppointment.setOnClickListener(v -> createAppointment());
    }

    private void setServicesExpanded(boolean expanded) {
        isServicesExpanded = expanded;
        layoutServicesContainer.setVisibility(expanded ? View.VISIBLE : View.GONE);
        ivServiceExpand.setImageResource(expanded ? R.drawable.ic_expand_less_24 : R.drawable.ic_expand_more_24);
    }

    private void loadInitialData() {
        setLoading(true);

        executorService.execute(() -> {
            UserEntity user = userDao.getUserById(sessionManager.getUserId());
            if (user == null) {
                requireActivity().runOnUiThread(() -> setLoading(false));
                return;
            }

            List<ServiceEntity> services = serviceDao.getAllServices();
            List<BranchEntity> branches = branchDao.getBranchesByCityId(user.getCityId());

            requireActivity().runOnUiThread(() -> {
                setupServices(services);
                setupBranches(branches);
                setLoading(false);
            });
        });
    }

    private void setupServices(List<ServiceEntity> services) {
        serviceList.clear();
        serviceList.addAll(services);
        servicesAdapter.submitList(serviceList);
    }

    private void setupBranches(List<BranchEntity> branches) {
        branchList.clear();
        branchList.addAll(branches);

        ArrayAdapter<BranchEntity> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                branchList
        );
        actvBookingBranch.setAdapter(adapter);
        actvBookingBranch.setThreshold(0);

        actvBookingBranch.setOnItemClickListener((parent, view, position, id) -> {
            selectedBranch = branchList.get(position);
            selectedBarber = null;
            barbersAdapter.setSelectedBarberId(-1);
            loadBarbersForBranch(selectedBranch.getId());
            updateSummary();
        });

        if (!branchList.isEmpty()) {
            selectedBranch = branchList.get(0);
            actvBookingBranch.setText(selectedBranch.toString(), false);
            loadBarbersForBranch(selectedBranch.getId());
        }
    }

    private void loadBarbersForBranch(long branchId) {
        tvBarbersHint.setText("Загружаем мастеров...");
        executorService.execute(() -> {
            List<BarberEntity> barbers = barberDao.getBarbersByBranchId(branchId);
            requireActivity().runOnUiThread(() -> {
                barbersAdapter.submitList(barbers);
                if (barbers.isEmpty()) {
                    tvBarbersHint.setText("В этом филиале пока нет доступных мастеров.");
                } else {
                    tvBarbersHint.setText("Нажмите на карточку мастера, чтобы выбрать его.");
                }
            });
        });
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);

                    SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    SimpleDateFormat ruFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

                    selectedDateIso = isoFormat.format(selected.getTime());
                    selectedDateRu = ruFormat.format(selected.getTime());
                    etDate.setText(selectedDateRu);
                    updateSummary();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.show();
    }

    private void createAppointment() {
        if (selectedService == null) {
            showToast("Выберите услугу");
            setServicesExpanded(true);
            return;
        }
        if (selectedBranch == null) {
            showToast("Выберите филиал");
            return;
        }
        if (selectedBarber == null) {
            showToast("Выберите мастера");
            return;
        }
        if (TextUtils.isEmpty(selectedDateIso)) {
            showToast("Выберите дату");
            return;
        }
        if (TextUtils.isEmpty(selectedTime)) {
            showToast("Выберите время");
            return;
        }

        setLoading(true);

        executorService.execute(() -> {
            int conflicts = appointmentDao.countActiveAppointmentsByBarberAndSlot(
                    selectedBarber.getId(),
                    selectedDateIso,
                    selectedTime
            );

            if (conflicts > 0) {
                requireActivity().runOnUiThread(() -> {
                    showToast("Это время уже занято у выбранного мастера");
                    setLoading(false);
                });
                return;
            }

            AppointmentEntity appointment = new AppointmentEntity();
            appointment.setUserId(sessionManager.getUserId());
            appointment.setServiceId(selectedService.getId());
            appointment.setBarberId(selectedBarber.getId());
            appointment.setBranchId(selectedBranch.getId());
            appointment.setAppointmentDate(selectedDateIso);
            appointment.setAppointmentTime(selectedTime);
            appointment.setStatus(AppointmentEntity.STATUS_ACTIVE);
            appointment.setCreatedAt(System.currentTimeMillis());

            appointmentDao.insert(appointment);

            requireActivity().runOnUiThread(() -> {
                showToast("Запись успешно создана");
                resetForm();
                setLoading(false);
            });
        });
    }

    private void resetForm() {
        selectedService = null;
        selectedBarber = null;
        selectedDateIso = "";
        selectedDateRu = "";
        selectedTime = "";

        servicesAdapter.setSelectedServiceId(-1);
        tvSelectedServiceCompact.setText("Нажмите, чтобы выбрать услугу");
        etDate.setText("");
        actvTime.setText("", false);
        barbersAdapter.setSelectedBarberId(-1);

        updateSummary();
    }

    private void updateSummary() {
        String service = selectedService != null ? selectedService.getName() : "—";
        String branch = selectedBranch != null ? selectedBranch.getTitle() : "—";
        String barber = selectedBarber != null ? selectedBarber.getName() : "—";
        String date = !TextUtils.isEmpty(selectedDateRu) ? selectedDateRu : "—";
        String time = !TextUtils.isEmpty(selectedTime) ? selectedTime : "—";

        tvBookingSummary.setText(
                "Услуга: " + service + "\n" +
                        "Филиал: " + branch + "\n" +
                        "Мастер: " + barber + "\n" +
                        "Дата: " + date + "\n" +
                        "Время: " + time
        );
    }

    private void setLoading(boolean isLoading) {
        progressBooking.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnCreateAppointment.setEnabled(!isLoading);
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}