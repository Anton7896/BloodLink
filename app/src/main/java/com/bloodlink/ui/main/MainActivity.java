package com.bloodlink.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.bloodlink.R;
import com.bloodlink.databinding.ActivityMainBinding;
import com.bloodlink.ui.auth.LoginActivity;
import com.bloodlink.utils.NotificationHelper;
import com.bloodlink.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!new SessionManager(this).isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish(); return;
        }
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHost = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHost != null;
        navController = navHost.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

        NotificationHelper.createChannel(this);
        requestPerms();
    }

    private void requestPerms() {
        String[] perms = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.POST_NOTIFICATIONS }
                : new String[]{ Manifest.permission.ACCESS_FINE_LOCATION };
        boolean needs = false;
        for (String p : perms)
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) { needs = true; break; }
        if (needs) ActivityCompat.requestPermissions(this, perms, 100);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
