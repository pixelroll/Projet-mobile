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

import com.paullouis.travel.data.DataCallback;
import com.paullouis.travel.data.FirebaseRepository;
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

        FirebaseRepository.getInstance().getCurrentUser(new DataCallback<User>() {
            @Override
            public void onSuccess(User currentUser) {
                tvUserName.setText(currentUser.getName());
                tvUserEmail.setText(currentUser.getEmail());
                String avatarUrl = currentUser.getAvatarUrl();
                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    com.bumptech.glide.Glide.with(SettingsActivity.this)
                            .load(avatarUrl)
                            .circleCrop()
                            .into(ivAvatar);
                } else {
                    String userName = currentUser.getName();
                    String initial = userName != null && !userName.isEmpty() ? userName.substring(0, 1).toUpperCase() : "?";
                    ivAvatar.setImageDrawable(createInitialDrawable(initial, userName));
                }
            }

            @Override
            public void onError(Exception e) {
                tvUserName.setText("Utilisateur");
                tvUserEmail.setText("");
            }
        });

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

    private android.graphics.drawable.Drawable createInitialDrawable(String initial, String userName) {
        android.graphics.drawable.ShapeDrawable drawable = new android.graphics.drawable.ShapeDrawable(new android.graphics.drawable.shapes.OvalShape());
        int[] colors = {0xFF6366F1, 0xFF8B5CF6, 0xFFEC4899, 0xFFF59E0B, 0xFF10B981, 0xFF3B82F6};
        int colorIndex = (userName != null ? userName.hashCode() : 0) % colors.length;
        if (colorIndex < 0) colorIndex = -colorIndex;
        drawable.getPaint().setColor(colors[colorIndex]);

        android.graphics.drawable.LayerDrawable layerDrawable = new android.graphics.drawable.LayerDrawable(new android.graphics.drawable.Drawable[]{drawable});

        android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(200, 200, android.graphics.Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
        layerDrawable.setBounds(0, 0, 200, 200);
        layerDrawable.draw(canvas);

        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setColor(android.graphics.Color.WHITE);
        paint.setTextSize(80);
        paint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD));
        paint.setTextAlign(android.graphics.Paint.Align.CENTER);
        canvas.drawText(initial, 100, 130, paint);

        return new android.graphics.drawable.BitmapDrawable(getResources(), bitmap);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Se déconnecter ?")
                .setMessage("Voulez-vous vraiment vous déconnecter de votre compte ?")
                .setPositiveButton("Confirmer", (dialog, which) -> {
                    FirebaseRepository.getInstance().logout();
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
