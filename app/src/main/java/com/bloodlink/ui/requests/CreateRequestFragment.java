package com.bloodlink.ui.requests;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.*;
import android.widget.ArrayAdapter;
import androidx.annotation.*;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.location.*;
import com.google.android.material.snackbar.Snackbar;
import com.bloodlink.data.database.entities.*;
import com.bloodlink.databinding.FragmentCreateRequestBinding;
import com.bloodlink.utils.BulgarianData;

import java.util.*;

public class CreateRequestFragment extends Fragment {

    private FragmentCreateRequestBinding binding;
    private RequestViewModel viewModel;
    private FusedLocationProviderClient fusedClient;
    private double reqLat = 0, reqLng = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater i, ViewGroup c, Bundle s) {
        binding = FragmentCreateRequestBinding.inflate(i, c, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel   = new ViewModelProvider(requireActivity()).get(RequestViewModel.class);
        fusedClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Spinner — кръвни групи
        ArrayAdapter<String> btAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, User.BLOOD_TYPES);
        btAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerBloodType.setAdapter(btAdapter);

        // спинер за спешност
        String[] urgencyLabels = {
            "🔴 CRITICAL — Животозастрашаващо (сега!)",
            "🟠 HIGH — Спешно (до 24 часа)",
            "🟡 NORMAL — Планирано"
        };
        ArrayAdapter<String> urgAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, urgencyLabels);
        urgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerUrgency.setAdapter(urgAdapter);

        // спинер за градове
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, BulgarianData.CITIES);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCity.setAdapter(cityAdapter);

        // При смяна на град обновяваме болниците
        binding.spinnerCity.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View v, int pos, long id) {
                String selectedCity = BulgarianData.CITIES[pos];
                updateHospitals(selectedCity);
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });


        updateHospitals(BulgarianData.CITIES[0]);


        binding.btnGetLocation.setOnClickListener(v -> getLocation());


        binding.btnPublish.setOnClickListener(v -> publishRequest());

        viewModel.operationResult.observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });
        viewModel.operationSuccess.observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                viewModel.clearOperationState();
                Navigation.findNavController(view).navigateUp();
            }
        });
    }

    private void updateHospitals(String city) {
        String[] hospitals = BulgarianData.getHospitalsForCity(city);
        ArrayAdapter<String> hospAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, hospitals);
        hospAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerHospital.setAdapter(hospAdapter);
    }

    private void publishRequest() {
        String[] urgencyMap = {
            BloodRequest.URGENCY_CRITICAL,
            BloodRequest.URGENCY_HIGH,
            BloodRequest.URGENCY_NORMAL
        };
        String urgency   = urgencyMap[binding.spinnerUrgency.getSelectedItemPosition()];
        String bloodType = (String) binding.spinnerBloodType.getSelectedItem();
        String city      = (String) binding.spinnerCity.getSelectedItem();
        String hospital  = (String) binding.spinnerHospital.getSelectedItem();

        int units = 1;
        try { units = Integer.parseInt(binding.etUnits.getText().toString()); }
        catch (Exception ignored) {}

        viewModel.createRequest(
                bloodType, hospital, city,
                binding.etDescription.getText().toString().trim(),
                urgency, units,
                binding.etPatientName.getText().toString().trim(),
                binding.etContactPhone.getText().toString().trim(),
                reqLat, reqLng
        );
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(binding.getRoot(), "Разрешете GPS достъп", Snackbar.LENGTH_SHORT).show();
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }

        binding.tvLocationStatus.setText("⏳ Определям местоположение...");
        binding.btnGetLocation.setEnabled(false);

        fusedClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
        ).addOnSuccessListener(loc -> {
            if (loc != null) {
                reqLat = loc.getLatitude();
                reqLng = loc.getLongitude();

                binding.tvLocationStatus.setText(String.format(Locale.getDefault(),
                        "📍 Намерено: %.4f, %.4f", reqLat, reqLng));

                viewModel.updateUserLocation(loc);
            } else {
                binding.tvLocationStatus.setText("❌ Не може да се определи локацията");
            }

            binding.btnGetLocation.setEnabled(true);
        }).addOnFailureListener(e -> {
            binding.tvLocationStatus.setText("❌ GPS грешка: " + e.getMessage());
            binding.btnGetLocation.setEnabled(true);
        });
    }

    @Override
    public void onDestroyView() { super.onDestroyView(); binding = null; }
}
