package com.paullouis.travel;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;

import com.paullouis.travel.data.DataCallback;
import com.paullouis.travel.data.EventBus;
import com.paullouis.travel.data.FirebaseRepository;
import com.paullouis.travel.model.User;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etName, etBio, etLocation, etEmail, etWebsite;
    private TextView tvBioCount, tvStatsPhotos;
    private ImageView ivAvatar;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Edge to Edge handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        etName = findViewById(R.id.etName);
        etBio = findViewById(R.id.etBio);
        etLocation = findViewById(R.id.etLocation);
        etEmail = findViewById(R.id.etEmail);
        etWebsite = findViewById(R.id.etWebsite);
        tvBioCount = findViewById(R.id.tvBioCount);
        tvStatsPhotos = findViewById(R.id.tvStatsPhotos);
        ivAvatar = findViewById(R.id.ivAvatar);

        // Load current user data asynchronously
        FirebaseRepository.getInstance().getCurrentUser(new DataCallback<User>() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                populateFields();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(EditProfileActivity.this, "Erreur de chargement du profil", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Bio character counter
        etBio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvBioCount.setText(s.length() + " / 150 caractères");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Save Button
        findViewById(R.id.btnSave).setOnClickListener(v -> saveProfile());

        // Change Avatar (Action Pick)
        findViewById(R.id.flAvatarContainer).setOnClickListener(v -> openGallery());

        // Danger Zone
        findViewById(R.id.btnDeactivate).setOnClickListener(v -> showDeactivateDialog());
        findViewById(R.id.btnDelete).setOnClickListener(v -> showDeleteDialog());
    }

    private void populateFields() {
        if (currentUser.getName() != null) etName.setText(currentUser.getName());
        if (currentUser.getBio() != null) {
            etBio.setText(currentUser.getBio());
            tvBioCount.setText(currentUser.getBio().length() + " / 150 caractères");
        }
        if (currentUser.getEmail() != null) etEmail.setText(currentUser.getEmail());
        
        // Mocking location and website since User model doesn't have them yet
        // In a real scenario, these would come from currentUser
        etLocation.setText("Paris, France");
        etWebsite.setText("");

        tvStatsPhotos.setText(String.valueOf(currentUser.getPostsCount()));

        if (currentUser.getAvatarUrl() != null && !currentUser.getAvatarUrl().isEmpty()) {
            com.bumptech.glide.Glide.with(this)
                    .load(currentUser.getAvatarUrl())
                    .circleCrop()
                    .into(ivAvatar);
        } else {
            String userName = currentUser.getName();
            String initial = userName != null && !userName.isEmpty() ? userName.substring(0, 1).toUpperCase() : "?";
            ivAvatar.setImageDrawable(createInitialDrawable(initial, userName));
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                ivAvatar.setImageURI(imageUri);
                if (currentUser != null) {
                    currentUser.setAvatarUrl(imageUri.toString());
                }
            }
        }
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Le nom ne peut pas être vide", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the current user model
        currentUser.setName(name);
        currentUser.setBio(etBio.getText().toString().trim());
        currentUser.setEmail(etEmail.getText().toString().trim());
        // Optimistic update
        EventBus.notifyUserUpdated(currentUser);
        Toast.makeText(this, "Profil mis à jour", Toast.LENGTH_SHORT).show();
        finish();

        // Save via FirebaseRepository in background
        FirebaseRepository.getInstance().updateUser(currentUser, new com.paullouis.travel.data.DataCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                // Background update succeeded
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getApplicationContext(), "Erreur de synchro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    private void showDeactivateDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Désactiver le compte ?")
                .setMessage("Votre compte sera temporairement désactivé. Vous pourrez le réactiver en vous reconnectant.")
                .setPositiveButton("Désactiver", (dialog, which) -> {
                    Toast.makeText(this, "Compte désactivé", Toast.LENGTH_SHORT).show();
                    finishAffinity();
                    startActivity(new Intent(this, MainActivity.class)); // Redirect simulate
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer le compte ?")
                .setMessage("Cette action est irréversible. Toutes vos données seront définitivement supprimées.")
                .setPositiveButton("Supprimer", (dialog, which) -> {
                    // Force the button to be destructive visually by changing dialog theme if needed
                    Toast.makeText(this, "Compte supprimé", Toast.LENGTH_SHORT).show();
                    finishAffinity();
                    startActivity(new Intent(this, MainActivity.class)); // Redirect simulate
                })
                .setNegativeButton("Annuler", null)
                .show();
    }
}
