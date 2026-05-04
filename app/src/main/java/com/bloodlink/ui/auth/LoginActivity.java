package com.bloodlink.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.snackbar.Snackbar;
import com.bloodlink.databinding.ActivityLoginBinding;
import com.bloodlink.ui.main.MainActivity;
import com.bloodlink.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (new SessionManager(this).isLoggedIn()) { goToMain(); return; }

        binding   = ActivityLoginBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        setContentView(binding.getRoot());

        viewModel.authState.observe(this, state -> {
            switch (state.type) {
                case LOADING: setLoading(true); break;
                case SUCCESS: setLoading(false); goToMain(); break;
                case ERROR:
                    setLoading(false);
                    Snackbar.make(binding.getRoot(), state.message, Snackbar.LENGTH_LONG).show();
                    break;
            }
        });

        binding.btnLogin.setOnClickListener(v ->
                viewModel.login(binding.etEmail.getText().toString().trim(),
                                binding.etPassword.getText().toString()));

        binding.tvRegisterLink.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void setLoading(boolean l) {
        binding.progressBar.setVisibility(l ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!l);
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
