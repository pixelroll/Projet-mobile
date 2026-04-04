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

import com.paullouis.travel.data.MockDataProvider;
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

        // Load current user data
        currentUser = MockDataProvider.getCurrentUser();
        populateFields();

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
                // In a real app we would upload this URI to Firebase Storage
            }
        }
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Le nom ne peut pas être vide", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update MockDataProvider
        currentUser.setName(name);
        currentUser.setBio(etBio.getText().toString().trim());
        currentUser.setEmail(etEmail.getText().toString().trim());
        // Location and website would be saved here too

        Toast.makeText(this, "Profil mis à jour", Toast.LENGTH_SHORT).show();
        finish();
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
