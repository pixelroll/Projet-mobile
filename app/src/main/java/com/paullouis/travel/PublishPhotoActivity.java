package com.paullouis.travel;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.Photo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.paullouis.travel.util.WindowInsetsHelper;

public class PublishPhotoActivity extends AppCompatActivity {

    public static final String EXTRA_GROUP_ID = "group_id";
    public static final String EXTRA_GROUP_NAME = "group_name";

    private ImageView ivSelectedPhoto;
    private View photoUploadZone, emptyPhotoContent;
    private EditText etTitle, etDescription, etTravelInfo, etNewTag;
    private TextView tvDateDisplay, tvMomentDisplay;
    private ChipGroup chipGroupTags;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivSelectedPhoto.setImageURI(uri);
                    ivSelectedPhoto.setVisibility(View.VISIBLE);
                    emptyPhotoContent.setVisibility(View.GONE);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_photo);

        initViews();
        setupToolbar();
        handleWindowInsets();
        setupListeners();
        setupDefaultTags();
    }

    private void handleWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.publishRootLayout), (v, insets) -> {
            Insets statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(0, statusBars.top, 0, 0);
            return insets;
        });
    }

    private void initViews() {
        photoUploadZone = findViewById(R.id.photoUploadZone);
        emptyPhotoContent = findViewById(R.id.emptyPhotoContent);
        ivSelectedPhoto = findViewById(R.id.ivSelectedPhoto);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etTravelInfo = findViewById(R.id.etTravelInfo);
        etNewTag = findViewById(R.id.etNewTag);
        tvDateDisplay = findViewById(R.id.tvDateDisplay);
        tvMomentDisplay = findViewById(R.id.tvMomentDisplay);
        chipGroupTags = findViewById(R.id.chipGroupTags);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupListeners() {
        photoUploadZone.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        findViewById(R.id.btnAudioNote).setOnClickListener(v -> 
            Toast.makeText(this, "Fonctionnalité audio à venir", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnDatePicker).setOnClickListener(v -> showDatePicker());
        findViewById(R.id.btnMomentPicker).setOnClickListener(v -> showMomentPicker());

        findViewById(R.id.btnAiTags).setOnClickListener(v -> generateAiTags());
        findViewById(R.id.btnAddTag).setOnClickListener(v -> addManualTag());

        findViewById(R.id.btnPublish).setOnClickListener(v -> validateAndPublish());
    }

    private void setupDefaultTags() {
        addTag("Monument");
        addTag("Architecture");
        addTag("Coucher de soleil");
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, R.style.DatePickerTheme, (view, year, month, dayOfMonth) -> {
            String date = String.format(Locale.getDefault(), "%02d/%02d/%d", month + 1, dayOfMonth, year);
            tvDateDisplay.setText(date);
            tvDateDisplay.setTextColor(getResources().getColor(R.color.on_background));
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showMomentPicker() {
        PopupMenu popup = new PopupMenu(this, findViewById(R.id.btnMomentPicker));
        popup.getMenu().add("Matin");
        popup.getMenu().add("Midi");
        popup.getMenu().add("Après-midi");
        popup.getMenu().add("Soir");
        popup.getMenu().add("Nuit");
        popup.setOnMenuItemClickListener(item -> {
            tvMomentDisplay.setText(item.getTitle());
            tvMomentDisplay.setTextColor(getResources().getColor(R.color.on_background));
            return true;
        });
        popup.show();
    }

    private void addTag(String tagText) {
        if (tagText.isEmpty()) return;
        
        Chip chip = new Chip(this);
        chip.setText(tagText);
        chip.setCloseIconVisible(true);
        chip.setChipBackgroundColorResource(R.color.primary); // Cyan/Indigo
        chip.setTextColor(getResources().getColor(R.color.white));
        chip.setCloseIconTintResource(R.color.white);
        chip.setCheckable(false);
        chip.setClickable(false);
        
        chip.setOnCloseIconClickListener(v -> chipGroupTags.removeView(chip));
        chipGroupTags.addView(chip);
    }

    private void addManualTag() {
        String text = etNewTag.getText().toString().trim();
        if (!text.isEmpty()) {
            addTag(text);
            etNewTag.setText("");
        }
    }

    private void generateAiTags() {
        String title = etTitle.getText().toString().toLowerCase();
        if (title.isEmpty()) {
            Toast.makeText(this, "Saisissez un titre pour générer des tags", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.contains("paris") || title.contains("france")) {
            addTag("Paris");
            addTag("France");
            addTag("Voyage");
            Toast.makeText(this, "Tags IA générés !", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Aucun tag IA trouvé pour ce titre", Toast.LENGTH_SHORT).show();
        }
    }

    private void validateAndPublish() {
        String title = etTitle.getText().toString().trim();
        
        if (selectedImageUri == null) {
            Toast.makeText(this, "Veuillez sélectionner une photo", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.isEmpty()) {
            etTitle.setError("Titre obligatoire");
            return;
        }

        // Create Photo Object
        Photo photo = new Photo();
        photo.setId("p_new_" + System.currentTimeMillis());
        photo.setUserId("u1"); // Simulated current user
        photo.setTitle(title);
        photo.setDescription(etDescription.getText().toString());
        photo.setImageUrl(selectedImageUri.toString());
        photo.setAuthorName("Sophie Martin");
        photo.setAuthorInitial("S");
        photo.setDate(tvDateDisplay.getText().toString());
        photo.setLikes(0);
        photo.setComments(0);
        photo.setTimestamp(System.currentTimeMillis());

        List<String> tags = new ArrayList<>();
        for (int i = 0; i < chipGroupTags.getChildCount(); i++) {
            tags.add(((Chip) chipGroupTags.getChildAt(i)).getText().toString());
        }
        photo.setTags(tags);

        // Add to Mock Data
        MockDataProvider.addPhoto(photo);

        Toast.makeText(this, "Photo publiée avec succès !", Toast.LENGTH_LONG).show();
        finish();
    }
}
