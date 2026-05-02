package com.paullouis.travel;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import com.google.android.material.card.MaterialCardView;
import com.paullouis.travel.util.WindowInsetsHelper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ProgressBar;
import java.io.InputStream;
import org.json.JSONArray;
import org.json.JSONObject;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerativeBackend;

public class PublishPhotoActivity extends AppCompatActivity {

    public static final String EXTRA_GROUP_ID = "group_id";
    public static final String EXTRA_GROUP_NAME = "group_name";

    private ImageView ivSelectedPhoto;
    private View photoUploadZone, emptyPhotoContent;
    private EditText etTitle, etDescription, etTravelInfo, etNewTag, etLocation;
    private TextView tvDateDisplay, tvMomentDisplay;
    private ChipGroup chipGroupTags;
    private Uri selectedImageUri;

    // Place type selector
    private String selectedPlaceType = null;
    private MaterialCardView activePublishPlaceCard = null;

    // Destination selector (feed vs. group)
    private List<Group> availableGroups = new ArrayList<>();
    private Group selectedGroup = null;
    private boolean postToGroup = false;

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

    private ProgressBar pbAiAnalysis;
    private TextView tvAiAnalysisLabel;
    private GenerativeModelFutures aiModel;

    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivSelectedPhoto.setImageURI(uri);
                    ivSelectedPhoto.setVisibility(View.VISIBLE);
                    emptyPhotoContent.setVisibility(View.GONE);
                    analyzeImageWithGemini(uri);
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

        setupDestinationSelector();
        setupPlaceTypeCards();

        pbAiAnalysis = findViewById(R.id.pbAiAnalysis);
        tvAiAnalysisLabel = findViewById(R.id.tvAiAnalysisLabel);

        aiModel = GenerativeModelFutures.from(
            FirebaseAI.getInstance(GenerativeBackend.googleAI())
                .generativeModel("gemini-3-flash-preview")
        );
    }

    private void setupPlaceTypeCards() {
        int[][] cardAndTypes = {
            {R.id.cardPublishNature, 0},
            {R.id.cardPublishMusee, 1},
            {R.id.cardPublishRue, 2},
            {R.id.cardPublishMagasin, 3},
            {R.id.cardPublishRestaurant, 4},
            {R.id.cardPublishMonument, 5}
        };
        String[] types = {"Nature", "Musée", "Rue", "Magasin", "Restaurant", "Monument"};

        for (int[] pair : cardAndTypes) {
            MaterialCardView card = findViewById(pair[0]);
            if (card == null) continue;
            String type = types[pair[1]];
            card.setOnClickListener(v -> {
                if (type.equals(selectedPlaceType)) {
                    selectedPlaceType = null;
                    card.setStrokeColor(ContextCompat.getColor(this, R.color.muted_foreground));
                    card.setStrokeWidth(1);
                    activePublishPlaceCard = null;
                } else {
                    if (activePublishPlaceCard != null) {
                        activePublishPlaceCard.setStrokeColor(ContextCompat.getColor(this, R.color.muted_foreground));
                        activePublishPlaceCard.setStrokeWidth(1);
                    }
                    selectedPlaceType = type;
                    card.setStrokeColor(ContextCompat.getColor(this, R.color.primary));
                    card.setStrokeWidth(2);
                    activePublishPlaceCard = card;
                }
            });
        }
    }

    private void setupDestinationSelector() {
        View sectionDestination = findViewById(R.id.sectionDestination);
        if (sectionDestination == null) return;
        sectionDestination.setVisibility(View.VISIBLE);

        Spinner spDestination = findViewById(R.id.spDestination);
        String intentGroupId = getIntent().getStringExtra(EXTRA_GROUP_ID);

        List<String> items = new ArrayList<>();
        items.add("Fil public (général)");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDestination.setAdapter(adapter);

        FirebaseRepository.getInstance().getMyGroups(new DataCallback<List<Group>>() {
            @Override
            public void onSuccess(List<Group> groups) {
                availableGroups = groups;
                int preSelectedIndex = 0;
                for (int i = 0; i < groups.size(); i++) {
                    items.add("Groupe : " + groups.get(i).getName());
                    if (intentGroupId != null && groups.get(i).getId().equals(intentGroupId)) {
                        preSelectedIndex = i + 1;
                        selectedGroup = groups.get(i);
                        postToGroup = true;
                    }
                }
                adapter.notifyDataSetChanged();
                if (preSelectedIndex > 0) {
                    spDestination.setSelection(preSelectedIndex);
                }
            }

            @Override
            public void onError(Exception e) { /* keep Fil public as only option */ }
        });

        spDestination.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    postToGroup = false;
                    selectedGroup = null;
                } else {
                    int groupIndex = position - 1;
                    if (groupIndex < availableGroups.size()) {
                        postToGroup = true;
                        selectedGroup = availableGroups.get(groupIndex);
                    }
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                postToGroup = false;
                selectedGroup = null;
            }
        });
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

    private void analyzeImageWithGemini(Uri imageUri) {
        if (pbAiAnalysis != null) pbAiAnalysis.setVisibility(View.VISIBLE);
        if (tvAiAnalysisLabel != null) tvAiAnalysisLabel.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {
                Bitmap bitmap = loadBitmapFromUri(imageUri);
                Bitmap scaled = scaleBitmap(bitmap, 768);

                String prompt = "Analyse cette photo de voyage. Réponds UNIQUEMENT avec un objet JSON valide (sans markdown, sans blocs de code) :\n" +
                    "{\"type\": \"<un parmi: Nature, Musée, Rue, Magasin, Restaurant, Monument>\", " +
                    "\"tags\": [\"<tag1>\", \"<tag2>\", \"<tag3>\", \"<tag4>\"]}\n" +
                    "Règles :\n" +
                    "- type doit être exactement l'une des six valeurs listées\n" +
                    "- fournis exactement 4 tags de voyage pertinents en français\n" +
                    "- les tags décrivent ce que tu vois (ambiance, éléments notables, style du lieu)\n" +
                    "- réponds avec le JSON uniquement, rien d'autre";

                Content content = new Content.Builder()
                    .addImage(scaled)
                    .addText(prompt)
                    .build();

                ListenableFuture<GenerateContentResponse> future = aiModel.generateContent(content);
                GenerateContentResponse response = future.get();

                String text = response.getText();
                if (text == null) throw new Exception("Empty response");
                text = text.trim().replaceAll("(?s)```[a-z]*\\n?", "").replace("```", "").trim();

                JSONObject json = new JSONObject(text);
                String type = json.optString("type", null);
                JSONArray tagsArray = json.optJSONArray("tags");

                List<String> tags = new ArrayList<>();
                if (tagsArray != null) {
                    for (int i = 0; i < tagsArray.length(); i++) {
                        tags.add(tagsArray.getString(i));
                    }
                }

                final String finalType = type;
                final List<String> finalTags = tags;

                runOnUiThread(() -> {
                    hideAiLoadingIndicator();
                    if (finalType != null && !finalType.isEmpty()) {
                        autoSelectPlaceType(finalType);
                    }
                    for (String tag : finalTags) {
                        addTag(tag);
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    hideAiLoadingIndicator();
                    Toast.makeText(getApplicationContext(),
                        "Analyse IA indisponible — ajoutez vos tags manuellement",
                        Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void hideAiLoadingIndicator() {
        if (pbAiAnalysis != null) pbAiAnalysis.setVisibility(View.GONE);
        if (tvAiAnalysisLabel != null) tvAiAnalysisLabel.setVisibility(View.GONE);
    }

    private void autoSelectPlaceType(String type) {
        int[] cardIds = {
            R.id.cardPublishNature, R.id.cardPublishMusee, R.id.cardPublishRue,
            R.id.cardPublishMagasin, R.id.cardPublishRestaurant, R.id.cardPublishMonument
        };
        String[] types = {"Nature", "Musée", "Rue", "Magasin", "Restaurant", "Monument"};

        for (int i = 0; i < types.length; i++) {
            if (types[i].equalsIgnoreCase(type)) {
                MaterialCardView card = findViewById(cardIds[i]);
                if (card == null) break;
                if (activePublishPlaceCard != null) {
                    activePublishPlaceCard.setStrokeColor(ContextCompat.getColor(this, R.color.muted_foreground));
                    activePublishPlaceCard.setStrokeWidth(1);
                }
                selectedPlaceType = types[i];
                card.setStrokeColor(ContextCompat.getColor(this, R.color.primary));
                card.setStrokeWidth(2);
                activePublishPlaceCard = card;
                break;
            }
        }
    }

    private Bitmap loadBitmapFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        if (inputStream == null) throw new IOException("Cannot open URI");
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        inputStream.close();
        return bitmap;
    }

    private Bitmap scaleBitmap(Bitmap original, int maxDimension) {
        int w = original.getWidth();
        int h = original.getHeight();
        if (w <= maxDimension && h <= maxDimension) return original;
        float scale = Math.min((float) maxDimension / w, (float) maxDimension / h);
        return Bitmap.createScaledBitmap(original, (int) (w * scale), (int) (h * scale), true);
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

        if (postToGroup && selectedGroup == null && getIntent().getStringExtra(EXTRA_GROUP_ID) == null) {
            Toast.makeText(this, "Veuillez sélectionner un groupe", Toast.LENGTH_SHORT).show();
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

                // Place type
                if (selectedPlaceType != null) {
                    photo.setPlaceType(selectedPlaceType);
                }

                // Tags
                List<String> tags = new ArrayList<>();
                for (int i = 0; i < chipGroupTags.getChildCount(); i++) {
                    tags.add(((Chip) chipGroupTags.getChildAt(i)).getText().toString());
                }
                photo.setTags(tags);

                // Group
                if (postToGroup) {
                    if (selectedGroup != null) {
                        photo.setGroupId(selectedGroup.getId());
                        photo.setGroupName(selectedGroup.getName());
                    } else {
                        // Fallback: groups hadn't finished loading, use intent extras
                        String fallbackId = getIntent().getStringExtra(EXTRA_GROUP_ID);
                        if (fallbackId != null) {
                            photo.setGroupId(fallbackId);
                            photo.setGroupName(getIntent().getStringExtra(EXTRA_GROUP_NAME));
                        }
                    }
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
