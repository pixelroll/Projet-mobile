package com.paullouis.travel;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import android.view.View;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private androidx.navigation.NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            NavigationUI.setupWithNavController(bottomNav, navController);
            
            // Handle display cutout / system bars safe area over the fragment content
            View fragmentView = findViewById(R.id.nav_host_fragment);
            ViewCompat.setOnApplyWindowInsetsListener(fragmentView, (v, windowInsets) -> {
                Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(0, insets.top, 0, 0); // Only push top down to dodge camera notch
                return windowInsets;
            });

            handleIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(android.content.Intent intent) {
        if (intent != null && intent.hasExtra("target_fragment_id")) {
            int targetId = intent.getIntExtra("target_fragment_id", -1);
            if (targetId != -1 && navController != null) {
                // Check current destination to avoid redundant navigations
                if (navController.getCurrentDestination() == null || navController.getCurrentDestination().getId() != targetId) {
                    navController.navigate(targetId);
                }
            }
        }
    }
}
