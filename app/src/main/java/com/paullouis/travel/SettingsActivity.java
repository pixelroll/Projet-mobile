package com.paullouis.travel;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.User;

public class SettingsActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail;
    private ImageView ivAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // SettingsActivity is standalone, we must handle its insets specifically
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        ivAvatar = findViewById(R.id.ivAvatar);

        User currentUser = MockDataProvider.getCurrentUser();
        tvUserName.setText(currentUser.getName());
        tvUserEmail.setText(currentUser.getEmail());
        // For avatar we rely on XML default profile_sophie for the mock

        setupItem(findViewById(R.id.btnEditProfile), R.drawable.ic_user, "Modifier le profil", null, v -> {
            startActivity(new Intent(this, EditProfileActivity.class));
        });

        setupItem(findViewById(R.id.btnNotifications), R.drawable.ic_bell, "Notifications", null, v -> {
            startActivity(new Intent(this, NotificationSettingsActivity.class));
        });

        findViewById(R.id.btnLogout).setOnClickListener(v -> showLogoutDialog());
    }

    private void setupItem(View itemView, int iconRes, String label, String value, View.OnClickListener listener) {
        ImageView ivIcon = itemView.findViewById(R.id.ivIcon);
        TextView tvLabel = itemView.findViewById(R.id.tvLabel);
        TextView tvCurrentValue = itemView.findViewById(R.id.tvCurrentValue);

        ivIcon.setImageResource(iconRes);
        tvLabel.setText(label);

        if (value != null) {
            tvCurrentValue.setVisibility(View.VISIBLE);
            tvCurrentValue.setText(value);
        } else {
            tvCurrentValue.setVisibility(View.GONE);
        }

        itemView.setOnClickListener(listener);
    }


    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Se déconnecter ?")
                .setMessage("Voulez-vous vraiment vous déconnecter de votre compte ?")
                .setPositiveButton("Confirmer", (dialog, which) -> {
                    // Simulation of logout
                    MockDataProvider.setUserLoggedIn(false);
                    Toast.makeText(this, "Déconnexion réussie", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    // Clear all activities and start fresh in anonymous mode
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finishAffinity();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }
}
