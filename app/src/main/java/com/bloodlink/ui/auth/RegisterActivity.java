package com.bloodlink.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.snackbar.Snackbar;
import com.bloodlink.data.database.entities.User;
import com.bloodlink.databinding.ActivityRegisterBinding;
import com.bloodlink.ui.main.MainActivity;
import com.bloodlink.utils.BulgarianData;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding   = ActivityRegisterBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        setContentView(binding.getRoot());

        // спинър за кръвни групи
        ArrayAdapter<String> btAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, User.BLOOD_TYPES);
        btAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerBloodType.setAdapter(btAdapter);

        // спинър за градове
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, BulgarianData.CITIES);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCity.setAdapter(cityAdapter);

        viewModel.authState.observe(this, state -> {
            switch (state.type) {
                case LOADING: setLoading(true); break;
                case SUCCESS:
                    setLoading(false);
                    Intent i = new Intent(this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    break;
                case ERROR:
                    setLoading(false);
                    Snackbar.make(binding.getRoot(), state.message, Snackbar.LENGTH_LONG).show();
                    break;
            }
        });

        binding.btnRegister.setOnClickListener(v -> {
            String bloodType = (String) binding.spinnerBloodType.getSelectedItem();
            String city      = (String) binding.spinnerCity.getSelectedItem();
            viewModel.register(
                    binding.etFirstName.getText().toString().trim(),
                    binding.etLastName.getText().toString().trim(),
                    binding.etEmail.getText().toString().trim(),
                    binding.etPhone.getText().toString().trim(),
                    binding.etPassword.getText().toString(),
                    binding.etConfirmPassword.getText().toString(),
                    bloodType, city
            );
        });

        binding.tvLoginLink.setOnClickListener(v -> finish());
    }

    private void setLoading(boolean l) {
        binding.progressBar.setVisibility(l ? View.VISIBLE : View.GONE);
        binding.btnRegister.setEnabled(!l);
    }
}
