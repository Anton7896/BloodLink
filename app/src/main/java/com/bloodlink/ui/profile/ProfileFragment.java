package com.bloodlink.ui.profile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.ArrayAdapter;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.snackbar.Snackbar;
import com.bloodlink.adapters.RatingAdapter;
import com.bloodlink.data.database.AppDatabase;
import com.bloodlink.databinding.FragmentProfileBinding;
import com.bloodlink.ui.auth.LoginActivity;
import com.bloodlink.utils.BulgarianData;
import com.bloodlink.utils.SessionManager;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private AppDatabase db;
    private SessionManager session;
    private boolean citySpinnerLoaded = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater i, ViewGroup c, Bundle s) {
        binding = FragmentProfileBinding.inflate(i, c, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        db        = AppDatabase.getInstance(requireContext());
        session   = new SessionManager(requireContext());

        // спинър за град
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, BulgarianData.CITIES);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCity.setAdapter(cityAdapter);


        RatingAdapter ratingAdapter = new RatingAdapter();
        binding.rvRatings.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRatings.setAdapter(ratingAdapter);

        // Профилни данни
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) return;

            binding.tvFullName.setText(user.getFullName());
            binding.tvBloodType.setText(user.bloodType);
            binding.tvCity.setText("📍 " + user.city);


            binding.tvDonations.setText(String.valueOf(user.donationCount));


            binding.tvRating.setText(user.getDisplayRating());


            binding.tvLevel.setText(user.getDonorLevelName());
            try {
                int level = user.getDonorLevel();

                if (level >= 10) {
                    binding.tvLevel.setTextColor(Color.parseColor("#4CAF50")); // зелено
                } else if (level >= 5) {
                    binding.tvLevel.setTextColor(Color.parseColor("#FF9800")); // оранжево
                } else {
                    binding.tvLevel.setTextColor(Color.parseColor("#F44336")); // червено
                }
            } catch (Exception ignored) {}
            binding.tvLevelProgress.setText(user.getProgressToNextLevel());

            // Progress bar
            int progress = 0;
            if (user.donationCount >= 10)     progress = 100;
            else if (user.donationCount >= 3) progress = 30 + (user.donationCount - 3) * 10;
            else                               progress = user.donationCount * 10;
            binding.progressLevel.setProgress(progress);

            // наличност
            binding.switchAvailable.setChecked(user.isAvailableToDonate);
            binding.tvAvailableStatus.setText(user.isAvailableToDonate
                    ? "✅ Готов за даряване" : "⛔ Не е наличен");


            binding.etFirstName.setText(user.firstName);
            binding.etLastName.setText(user.lastName);
            binding.etPhone.setText(user.phone);

            // град
            if (!citySpinnerLoaded && user.city != null) {
                for (int i = 0; i < BulgarianData.CITIES.length; i++) {
                    if (BulgarianData.CITIES[i].equalsIgnoreCase(user.city)) {
                        binding.spinnerCity.setSelection(i);
                        break;
                    }
                }
                citySpinnerLoaded = true;
            }
        });


        db.donorResponseDao().getResponseCountForUser(session.getUserId())
                .observe(getViewLifecycleOwner(), count ->
                    binding.tvResponseCount.setText(String.valueOf(count != null ? count : 0)));

        // наличност
        binding.switchAvailable.setOnCheckedChangeListener((btn, checked) ->
                viewModel.toggleAvailability(checked));

        // Запази профил
        binding.btnSave.setOnClickListener(v -> {
            String selectedCity = (String) binding.spinnerCity.getSelectedItem();
            viewModel.updateProfile(
                    binding.etFirstName.getText().toString(),
                    binding.etLastName.getText().toString(),
                    binding.etPhone.getText().toString(),
                    selectedCity
            );
        });

        // Изход
        binding.btnLogout.setOnClickListener(v -> {
            viewModel.logout();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Оценки
        viewModel.getMyRatings().observe(getViewLifecycleOwner(), ratings -> {
            ratingAdapter.submitList(ratings);
            binding.tvNoRatings.setVisibility(ratings.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.operationResult.observe(getViewLifecycleOwner(), msg ->
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroyView() { super.onDestroyView(); binding = null; }
}
