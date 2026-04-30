package com.paullouis.travel;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.paullouis.travel.data.DataCallback;
import com.paullouis.travel.data.FirebaseRepository;
import com.paullouis.travel.data.EventBus;
import com.paullouis.travel.model.Group;
import com.paullouis.travel.model.Notification;
import com.paullouis.travel.model.Photo;

import java.io.File;
import java.io.IOException;
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
    private EditText etTitle, etDescription, etTravelInfo, etNewTag, etLocation;
    private TextView tvDateDisplay, tvMomentDisplay;
    private ChipGroup chipGroupTags;
    private Uri selectedImageUri;

    // Audio recording
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private String audioFilePath;
    private Uri audioUri;
    private Handler recordingHandler = new Handler(Looper.getMainLooper());
    private long recordingStartTime;
    private TextView tvAudioLabel, tvRecordingDuration;
    private ImageView ivAudioIcon;
    private LinearLayout audioRecordingIndicator;

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

    private final ActivityResultLauncher<String> audioPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            granted -> {
                if (granted) {
                    toggleAudioRecording();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission audio requise pour enregistrer", Toast.LENGTH_SHORT).show();
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
        etLocation = findViewById(R.id.etLocation);
        tvDateDisplay = findViewById(R.id.tvDateDisplay);
        tvMomentDisplay = findViewById(R.id.tvMomentDisplay);
        chipGroupTags = findViewById(R.id.chipGroupTags);

        // Audio views
        tvAudioLabel = findViewById(R.id.tvAudioLabel);
        ivAudioIcon = findViewById(R.id.ivAudioIcon);
        tvRecordingDuration = findViewById(R.id.tvRecordingDuration);
        audioRecordingIndicator = findViewById(R.id.audioRecordingIndicator);
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

        findViewById(R.id.btnAudioNote).setOnClickListener(v -> handleAudioClick());

        findViewById(R.id.btnDatePicker).setOnClickListener(v -> showDatePicker());
        findViewById(R.id.btnMomentPicker).setOnClickListener(v -> showMomentPicker());

        findViewById(R.id.btnAiTags).setOnClickListener(v -> generateAiTags());
        findViewById(R.id.btnAddTag).setOnClickListener(v -> addManualTag());

        findViewById(R.id.btnPublish).setOnClickListener(v -> validateAndPublish());
    }

    private void sendGroupNotifications(Photo photo) {
        FirebaseRepository.getInstance().getGroupById(photo.getGroupId(), new DataCallback<Group>() {
            @Override
            public void onSuccess(Group group) {
                if (group.getMemberIds() == null) return;
                String publisherId = FirebaseRepository.getInstance().getCurrentUserId();
                for (String memberId : group.getMemberIds()) {
                    if (memberId.equals(publisherId)) continue;
                    Notification notif = new Notification();
                    notif.setUserId(memberId);
                    notif.setType(Notification.Type.PHOTO_PUBLISHED);
                    notif.setUserName(photo.getAuthorName());
                    notif.setContent("a publié une photo dans " + group.getName());
                    notif.setPhotoUrl(photo.getImageUrl());
                    notif.setTimestamp(System.currentTimeMillis());
                    notif.setRead(false);
                    FirebaseRepository.getInstance().createNotification(notif, new DataCallback<Void>() {
                        @Override public void onSuccess(Void r) {}
                        @Override public void onError(Exception e) {}
                    });
                }
            }
            @Override
            public void onError(Exception e) {}
        });
    }

    // =========================================================================
    // Audio Recording
    // =========================================================================

    private void handleAudioClick() {
        if (isRecording) {
            stopAudioRecording();
        } else {
            // Check permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                toggleAudioRecording();
            } else {
                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
            }
        }
    }

    private void toggleAudioRecording() {
        if (isRecording) {
            stopAudioRecording();
        } else {
            startAudioRecording();
        }
    }

    private void startAudioRecording() {
        try {
            File audioFile = new File(getCacheDir(), "audio_note_" + System.currentTimeMillis() + ".3gp");
            audioFilePath = audioFile.getAbsolutePath();

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.prepare();
            mediaRecorder.start();

            isRecording = true;
            recordingStartTime = System.currentTimeMillis();

            // Update UI
            tvAudioLabel.setText("Appuyez pour arrêter");
            tvAudioLabel.setTextColor(ContextCompat.getColor(this, R.color.error));
            ivAudioIcon.setColorFilter(ContextCompat.getColor(this, R.color.error));
            audioRecordingIndicator.setVisibility(View.VISIBLE);

            // Start duration timer
            updateRecordingDuration();

        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Erreur de démarrage de l'enregistrement", Toast.LENGTH_SHORT).show();
            cleanupRecorder();
        }
    }

    private void stopAudioRecording() {
        try {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
            }

            isRecording = false;
            recordingHandler.removeCallbacksAndMessages(null);

            audioUri = Uri.fromFile(new File(audioFilePath));

            // Update UI to show recorded state
            tvAudioLabel.setText("Note audio enregistrée ✓");
            tvAudioLabel.setTextColor(ContextCompat.getColor(this, R.color.primary));
            ivAudioIcon.setColorFilter(ContextCompat.getColor(this, R.color.primary));
            audioRecordingIndicator.setVisibility(View.GONE);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Erreur d'arrêt de l'enregistrement", Toast.LENGTH_SHORT).show();
            cleanupRecorder();
        }
    }

    private void cleanupRecorder() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.release();
            } catch (Exception ignored) {}
            mediaRecorder = null;
        }
        isRecording = false;
        recordingHandler.removeCallbacksAndMessages(null);
        audioRecordingIndicator.setVisibility(View.GONE);
        tvAudioLabel.setText("Ajouter une note audio");
        tvAudioLabel.setTextColor(ContextCompat.getColor(this, R.color.muted_foreground));
        ivAudioIcon.setColorFilter(ContextCompat.getColor(this, R.color.muted_foreground));
    }

    private void updateRecordingDuration() {
        recordingHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isRecording) {
                    long elapsed = System.currentTimeMillis() - recordingStartTime;
                    int seconds = (int) (elapsed / 1000) % 60;
                    int minutes = (int) (elapsed / 1000) / 60;
                    tvRecordingDuration.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
                    recordingHandler.postDelayed(this, 500);
                }
            }
        }, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanupRecorder();
    }

    // =========================================================================
    // Date & Moment Pickers
    // =========================================================================

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Sélectionnez la date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        
        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);
            String date = String.format(Locale.getDefault(), "%02d/%02d/%d", 
                    calendar.get(Calendar.DAY_OF_MONTH), 
                    calendar.get(Calendar.MONTH) + 1, 
                    calendar.get(Calendar.YEAR));
            tvDateDisplay.setText(date);
            tvDateDisplay.setTextColor(getResources().getColor(R.color.on_background));
        });
        
        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
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

    // =========================================================================
    // Tags
    // =========================================================================

    private void addTag(String tagText) {
        if (tagText.isEmpty()) return;
        
        Chip chip = new Chip(this);
        chip.setText(tagText);
        chip.setCloseIconVisible(true);
        chip.setChipBackgroundColorResource(R.color.primary);
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

    // =========================================================================
    // Publish
    // =========================================================================

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

        // Stop recording if still active
        if (isRecording) {
            stopAudioRecording();
        }

        // Disable button while processing
        findViewById(R.id.btnPublish).setEnabled(false);

        FirebaseRepository.getInstance().getCurrentUser(new DataCallback<com.paullouis.travel.model.User>() {
            @Override
            public void onSuccess(com.paullouis.travel.model.User user) {
                // Create Photo Object
                Photo photo = new Photo();
                photo.setId("p_new_" + System.currentTimeMillis());
                photo.setUserId(user.getId());
                photo.setTitle(title);
                photo.setDescription(etDescription.getText().toString().trim());
                photo.setImageUrl(selectedImageUri.toString());
                
                String authorName = (user.getName() != null && !user.getName().isEmpty()) ? user.getName() : "Utilisateur";
                photo.setAuthorName(authorName);
                photo.setAuthorInitial(authorName.substring(0, 1).toUpperCase());
                photo.setAuthorAvatarUrl(user.getAvatarUrl());
                
                photo.setDate(tvDateDisplay.getText().toString());
                photo.setLikes(0);
                photo.setComments(0);
                photo.setTimestamp(System.currentTimeMillis());

                // Location
                String location = etLocation.getText().toString().trim();
                if (!location.isEmpty()) {
                    photo.setLocationName(location);
                }

                // Travel info
                String travelInfo = etTravelInfo.getText().toString().trim();
                if (!travelInfo.isEmpty()) {
                    photo.setTravelInfo(travelInfo);
                }

                // Moment of day
                String moment = tvMomentDisplay.getText().toString();
                if (!moment.equals("Moment...")) {
                    photo.setMomentOfDay(moment);
                }

                // Tags
                List<String> tags = new ArrayList<>();
                for (int i = 0; i < chipGroupTags.getChildCount(); i++) {
                    tags.add(((Chip) chipGroupTags.getChildAt(i)).getText().toString());
                }
                photo.setTags(tags);

                // Group
                String groupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
                String groupName = getIntent().getStringExtra(EXTRA_GROUP_NAME);
                if (groupId != null) {
                    photo.setGroupId(groupId);
                    photo.setGroupName(groupName);
                }

                photo.setLoading(true);

                // Publish optimistic event and close activity
                EventBus.notifyPhotoAdded(photo);
                finish();

                FirebaseRepository.getInstance().addPhoto(photo, new DataCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        if (audioUri != null) {
                            FirebaseRepository.getInstance().uploadAudio(photo.getId(), audioUri, new DataCallback<String>() {
                                @Override
                                public void onSuccess(String audioUrl) {
                                    photo.setAudioUrl(audioUrl);
                                    EventBus.notifyPhotoUpdated(photo);
                                    Toast.makeText(getApplicationContext(), "Photo publiée avec succès !", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onError(Exception e) {
                                    Toast.makeText(getApplicationContext(), "Photo publiée, mais l'audio n'a pas pu être envoyé", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Photo publiée avec succès !", Toast.LENGTH_LONG).show();
                        }
                        if (photo.getGroupId() != null) {
                            sendGroupNotifications(photo);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getApplicationContext(), "Erreur de publication: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                findViewById(R.id.btnPublish).setEnabled(true);
                Toast.makeText(getApplicationContext(), "Erreur utilisateur: impossible de publier", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
