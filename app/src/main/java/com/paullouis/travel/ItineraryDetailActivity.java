package com.paullouis.travel;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.paullouis.travel.LoginRequiredDialogFragment;
import com.paullouis.travel.adapter.ItineraryStepAdapter;
import com.paullouis.travel.data.DataCallback;
import com.paullouis.travel.data.FirebaseRepository;
import com.paullouis.travel.data.ItineraryCache;
import com.paullouis.travel.model.GeneratedItinerary;
import com.paullouis.travel.model.ItineraryStep;
import com.paullouis.travel.model.Photo;
import com.paullouis.travel.model.SavedItinerary;
import com.paullouis.travel.model.StepPhoto;
import com.paullouis.travel.model.TravelDestination;

import java.util.ArrayList;
import java.util.List;

public class ItineraryDetailActivity extends AppCompatActivity {

    public static final String EXTRA_SAVED_ITINERARY_ID = "SAVED_ITINERARY_ID";

    private String savedItineraryId;
    private SavedItinerary savedItinerary;
    private boolean editMode = false;
    private ItineraryStepAdapter stepAdapter;
    private List<ItineraryStep> currentSteps = new ArrayList<>();
    private String city;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), systemBars.bottom);
            return insets;
        });

        savedItineraryId = getIntent().getStringExtra(EXTRA_SAVED_ITINERARY_ID);

        if (savedItineraryId != null) {
            loadSavedItinerary(savedItineraryId);
        } else {
            loadFromCache();
        }
    }

    private void loadFromCache() {
        GeneratedItinerary itinerary = ItineraryCache.getSelected();
        if (itinerary == null) {
            finish();
            return;
        }

        city = getIntent().getStringExtra("LOCATION_NAME");
        date = getIntent().getStringExtra("LOCATION_DATE");
        if (city == null) city = "Paris, France";
        if (date == null) date = "17 Mars 2026";

        setupToolbar(itinerary.getTitle());
        bindHeader(itinerary.getTitle(), city, date);
        bindStats(itinerary.getBudget(), itinerary.getDuration(), String.valueOf(itinerary.getNumberOfSteps()));
        setupNavFab(itinerary);

        if (itinerary.getDestinations() != null && !itinerary.getDestinations().isEmpty()) {
            currentSteps = destinationsToSteps(itinerary.getDestinations());
        } else {
            currentSteps = new ArrayList<>();
        }
        setupStepList();

        // Generated mode: save and regenerate buttons
        findViewById(R.id.btnRegenerate).setVisibility(View.VISIBLE);
        findViewById(R.id.btnSave).setVisibility(View.VISIBLE);
        View footerLayout = findViewById(R.id.footerActionsLayout);
        footerLayout.setVisibility(View.VISIBLE);

        final GeneratedItinerary finalItinerary = itinerary;
        final String finalCity = city;
        final String finalDate = date;

        findViewById(R.id.btnRegenerate).setOnClickListener(v -> {
            setResult(RESULT_FIRST_USER);
            finish();
        });

        findViewById(R.id.btnSave).setOnClickListener(v -> saveItinerary(finalItinerary, finalCity, finalDate));

        // Edit FAB hidden in generated mode
        FloatingActionButton fabEdit = findViewById(R.id.fabEdit);
        if (fabEdit != null) fabEdit.setVisibility(View.GONE);
        View btnAddStep = findViewById(R.id.btnAddStep);
        if (btnAddStep != null) btnAddStep.setVisibility(View.GONE);

        setupBottomNavigation();
    }

    private void loadSavedItinerary(String id) {
        setupToolbar("Parcours");
        FirebaseRepository.getInstance().getItineraryById(id, new DataCallback<SavedItinerary>() {
            @Override
            public void onSuccess(SavedItinerary itinerary) {
                savedItinerary = itinerary;
                city = itinerary.getLocationName() != null ? itinerary.getLocationName() : "";
                date = itinerary.getDate() != null ? itinerary.getDate() : "";

                String shortTitle = itinerary.getTitle();
                // Update toolbar after data loads
                TextView tvTitle = findViewById(R.id.tvTitle);
                if (tvTitle != null) tvTitle.setText(shortTitle);
                TextView tvSubtitle = findViewById(R.id.tvSubtitle);
                if (tvSubtitle != null) tvSubtitle.setText(city + (date.isEmpty() ? "" : " • " + date));

                bindStats(itinerary.getBudgetFormatted(), itinerary.getDurationFormatted(),
                        String.valueOf(itinerary.getSteps() != null ? itinerary.getSteps().size() : 0));

                if (itinerary.getSteps() != null && !itinerary.getSteps().isEmpty()) {
                    currentSteps = destinationsToSteps(itinerary.getSteps());
                }
                setupStepList();
                loadStepPhotos();

                String currentUserId = FirebaseRepository.getInstance().getCurrentUserId();
                boolean isOwner = currentUserId != null && currentUserId.equals(itinerary.getUserId());

                View footerLayout = findViewById(R.id.footerActionsLayout);
                FloatingActionButton fabEdit = findViewById(R.id.fabEdit);

                if (isOwner) {
                    footerLayout.setVisibility(View.GONE);
                    if (fabEdit != null) {
                        fabEdit.setVisibility(View.VISIBLE);
                        fabEdit.setOnClickListener(v -> toggleEditMode());
                    }
                } else {
                    if (fabEdit != null) fabEdit.setVisibility(View.GONE);
                    findViewById(R.id.btnRegenerate).setVisibility(View.GONE);
                    View btnSave = findViewById(R.id.btnSave);
                    android.widget.LinearLayout.LayoutParams params =
                            new android.widget.LinearLayout.LayoutParams(
                                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                                    (int) (44 * getResources().getDisplayMetrics().density));
                    params.weight = 2;
                    btnSave.setLayoutParams(params);
                    ((android.widget.TextView) findViewById(R.id.tvBtnSaveLabel)).setText("Sauvegarder ce parcours");
                    footerLayout.setVisibility(View.VISIBLE);
                    final SavedItinerary finalItinerary = itinerary;
                    btnSave.setOnClickListener(v -> saveItineraryCopy(finalItinerary));
                }

                setupNavFabForSaved();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ItineraryDetailActivity.this, "Erreur de chargement du parcours", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        setupBottomNavigation();
    }

    private void setupToolbar(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView tvTitle = findViewById(R.id.tvTitle);
        if (tvTitle != null) tvTitle.setText("Parcours " + title);
    }

    private void bindHeader(String title, String city, String date) {
        TextView tvTitle = findViewById(R.id.tvTitle);
        if (tvTitle != null) tvTitle.setText("Parcours " + title);
        TextView tvSubtitle = findViewById(R.id.tvSubtitle);
        if (tvSubtitle != null) tvSubtitle.setText(city + " • " + date);
    }

    private void bindStats(String budget, String duration, String steps) {
        ((TextView) findViewById(R.id.tvStatBudget)).setText(budget);
        ((TextView) findViewById(R.id.tvStatDuration)).setText(duration);
        ((TextView) findViewById(R.id.tvStatSteps)).setText(steps);
    }

    private void setupNavFab(GeneratedItinerary itinerary) {
        final String finalTitle = itinerary.getTitle();
        final String finalCity = city;

        findViewById(R.id.mapCard).setOnClickListener(v -> {
            Intent i = new Intent(this, TripMapActivity.class);
            i.putExtra("TRIP_TITLE", finalTitle);
            i.putExtra("TRIP_DESTINATION", finalCity);
            startActivity(i);
        });
    }

    private void setupNavFabForSaved() {
        final String finalTitle = savedItinerary != null ? savedItinerary.getTitle() : "";
        final String finalCity = city;

        View mapCard = findViewById(R.id.mapCard);
        if (mapCard != null) mapCard.setOnClickListener(v -> {
            Intent i = new Intent(this, TripMapActivity.class);
            i.putExtra("TRIP_TITLE", finalTitle);
            i.putExtra("TRIP_DESTINATION", finalCity);
            startActivity(i);
        });
    }


    private void setupStepList() {
        RecyclerView rv = findViewById(R.id.rvStepsTimeline);
        rv.setLayoutManager(new LinearLayoutManager(this));
        stepAdapter = new ItineraryStepAdapter(currentSteps, this);
        rv.setAdapter(stepAdapter);

        View btnAddStep = findViewById(R.id.btnAddStep);
        if (btnAddStep != null) {
            btnAddStep.setVisibility(View.GONE);
            btnAddStep.setOnClickListener(v -> showAddStepDialog());
        }
    }

    private void loadStepPhotos() {
        if (savedItinerary == null || savedItinerary.getSteps() == null) return;
        List<TravelDestination> destinations = savedItinerary.getSteps();
        for (int i = 0; i < currentSteps.size() && i < destinations.size(); i++) {
            final int idx = i;
            final ItineraryStep uiStep = currentSteps.get(i);
            final int stepOrder = destinations.get(i).getOrder();
            FirebaseRepository.getInstance().getPhotosByItineraryStep(
                    savedItineraryId, stepOrder, new DataCallback<List<Photo>>() {
                        @Override
                        public void onSuccess(List<Photo> photos) {
                            if (photos.isEmpty()) return;
                            List<StepPhoto> stepPhotos = new ArrayList<>();
                            for (Photo p : photos) {
                                stepPhotos.add(new StepPhoto(p.getImageUrl(), p.getTitle()));
                            }
                            uiStep.setPhotos(stepPhotos);
                            if (stepAdapter != null) stepAdapter.notifyItemChanged(idx);
                        }

                        @Override
                        public void onError(Exception e) {}
                    });
        }
    }

    private void toggleEditMode() {
        editMode = !editMode;
        FloatingActionButton fabEdit = findViewById(R.id.fabEdit);
        if (fabEdit != null) {
            fabEdit.setImageResource(editMode ? R.drawable.ic_check : R.drawable.ic_edit);
        }

        View btnAddStep = findViewById(R.id.btnAddStep);
        if (btnAddStep != null) btnAddStep.setVisibility(editMode ? View.VISIBLE : View.GONE);

        if (!editMode && savedItinerary != null) {
            // Rebuild step list from currentSteps back to TravelDestinations
            savedItinerary.setSteps(stepsToDestinations(currentSteps));
            FirebaseRepository.getInstance().updateItinerary(savedItinerary, new DataCallback<Void>() {
                @Override
                public void onSuccess(Void r) {
                    Toast.makeText(ItineraryDetailActivity.this, "Parcours mis à jour", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(ItineraryDetailActivity.this, "Erreur lors de la sauvegarde", Toast.LENGTH_SHORT).show();
                }
            });
            stepAdapter.setEditMode(false, null);
        } else {
            stepAdapter.setEditMode(true, position -> {
                if (position >= 0 && position < currentSteps.size()) {
                    currentSteps.remove(position);
                    if (savedItinerary != null && savedItinerary.getSteps() != null
                            && position < savedItinerary.getSteps().size()) {
                        savedItinerary.getSteps().remove(position);
                    }
                    stepAdapter.notifyItemRemoved(position);
                    stepAdapter.notifyItemRangeChanged(position, currentSteps.size());
                    ((TextView) findViewById(R.id.tvStatSteps)).setText(String.valueOf(currentSteps.size()));
                }
            });
        }
    }

    private void showAddStepDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad / 2, pad, 0);

        EditText etName = new EditText(this);
        etName.setHint("Nom de l'étape");
        layout.addView(etName);

        EditText etDesc = new EditText(this);
        etDesc.setHint("Description (optionnel)");
        layout.addView(etDesc);

        new AlertDialog.Builder(this)
                .setTitle("Ajouter une étape")
                .setView(layout)
                .setPositiveButton("Ajouter", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    if (name.isEmpty()) return;
                    String desc = etDesc.getText().toString().trim();
                    int order = currentSteps.size() + 1;

                    ItineraryStep newStep = new ItineraryStep(
                            String.valueOf(order), name, desc, "?", "", "?", "?€", "Soir", "",
                            R.drawable.ic_map_pin, 0
                    );
                    currentSteps.add(newStep);
                    stepAdapter.notifyItemInserted(currentSteps.size() - 1);
                    ((TextView) findViewById(R.id.tvStatSteps)).setText(String.valueOf(currentSteps.size()));

                    if (savedItinerary != null) {
                        TravelDestination newDest = new TravelDestination(
                                order, name, desc, "", 0, 0, 0, 0, "Découverte"
                        );
                        if (savedItinerary.getSteps() == null) savedItinerary.setSteps(new ArrayList<>());
                        savedItinerary.getSteps().add(newDest);
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void saveItinerary(GeneratedItinerary itinerary, String city, String date) {
        EditText etName = new EditText(this);
        String suggested = itinerary.getTitle() + (city != null && !city.isEmpty() ? " - " + city : "");
        etName.setText(suggested);
        etName.selectAll();
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        etName.setPadding(pad, pad / 2, pad, pad / 2);

        new AlertDialog.Builder(this)
                .setTitle("Nommer ce parcours")
                .setView(etName)
                .setPositiveButton("Sauvegarder", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    if (name.isEmpty()) name = suggested;
                    doSaveItinerary(itinerary, name, city, date);
                })
                .setNegativeButton("Annuler", null)
                .show();

        etName.post(() -> {
            etName.requestFocus();
            android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) imm.showSoftInput(etName, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
        });
    }

    private void saveItineraryCopy(SavedItinerary original) {
        String currentUserId = FirebaseRepository.getInstance().getCurrentUserId();
        if (currentUserId == null) {
            LoginRequiredDialogFragment.newInstance().show(getSupportFragmentManager(), "login_required");
            return;
        }

        EditText etName = new EditText(this);
        etName.setText(original.getTitle());
        etName.selectAll();
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        etName.setPadding(pad, pad / 2, pad, pad / 2);

        final String fallbackName = original.getTitle();
        new AlertDialog.Builder(this)
                .setTitle("Nommer votre parcours")
                .setView(etName)
                .setPositiveButton("Sauvegarder", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    if (name.isEmpty()) name = fallbackName;
                    doSaveItineraryCopy(original, name, currentUserId);
                })
                .setNegativeButton("Annuler", null)
                .show();

        etName.post(() -> {
            etName.requestFocus();
            android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) imm.showSoftInput(etName, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
        });
    }

    private void doSaveItineraryCopy(SavedItinerary original, String name, String userId) {
        View btnSave = findViewById(R.id.btnSave);
        btnSave.setEnabled(false);

        SavedItinerary copy = new SavedItinerary();
        copy.setUserId(userId);
        copy.setTitle(name);
        copy.setLocationName(original.getLocationName());
        copy.setDate(original.getDate());
        copy.setType(original.getType());
        copy.setDescription(original.getDescription());
        copy.setTotalBudget(original.getTotalBudget());
        copy.setEstimatedDurationHours(original.getEstimatedDurationHours());
        copy.setEffort(original.getEffort());
        copy.setCreatedAt(System.currentTimeMillis());
        copy.setSteps(original.getSteps() != null ? new ArrayList<>(original.getSteps()) : new ArrayList<>());

        FirebaseRepository.getInstance().saveItinerary(copy, new DataCallback<String>() {
            @Override
            public void onSuccess(String id) {
                ImageView ivSaveIcon = findViewById(R.id.ivSaveIcon);
                if (ivSaveIcon != null) ivSaveIcon.setImageResource(R.drawable.ic_check);
                TextView tvLabel = findViewById(R.id.tvBtnSaveLabel);
                if (tvLabel != null) tvLabel.setText("Parcours sauvegardé");
                btnSave.setClickable(false);
            }

            @Override
            public void onError(Exception e) {
                btnSave.setEnabled(true);
                Toast.makeText(ItineraryDetailActivity.this, "Erreur lors de la sauvegarde", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doSaveItinerary(GeneratedItinerary itinerary, String name, String city, String date) {
        View btnSave = findViewById(R.id.btnSave);
        btnSave.setEnabled(false);

        SavedItinerary saved = SavedItinerary.from(itinerary,
                FirebaseRepository.getInstance().getCurrentUserId(), city, date);
        saved.setTitle(name);

        FirebaseRepository.getInstance().saveItinerary(saved, new DataCallback<String>() {
            @Override
            public void onSuccess(String id) {
                btnSave.setEnabled(true);
                ImageView ivSaveIcon = findViewById(R.id.ivSaveIcon);
                if (ivSaveIcon != null) ivSaveIcon.setImageResource(R.drawable.ic_heart_filled);
                Toast.makeText(ItineraryDetailActivity.this, "Parcours sauvegardé !", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                btnSave.setEnabled(true);
                Toast.makeText(ItineraryDetailActivity.this, "Erreur lors de la sauvegarde", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<ItineraryStep> destinationsToSteps(List<TravelDestination> destinations) {
        List<ItineraryStep> steps = new ArrayList<>();
        int total = destinations.size();
        for (int i = 0; i < total; i++) {
            TravelDestination dest = destinations.get(i);
            String period = computePeriod(i, total);
            String duration = dest.getEstimatedDurationMinutes() + " min";
            ItineraryStep step = new ItineraryStep(
                    String.valueOf(dest.getOrder() > 0 ? dest.getOrder() : i + 1),
                    dest.getName(),
                    dest.getDescription() + (dest.getReason() != null && !dest.getReason().isEmpty()
                            ? "\n" + dest.getReason() : ""),
                    duration, "", duration,
                    dest.getEstimatedPriceEuros() + "€",
                    period, "",
                    iconResForType(dest.getType()),
                    0,
                    dest.getLatitude(), dest.getLongitude()
            );
            if (i + 1 < total) {
                TravelDestination next = destinations.get(i + 1);
                step.setTravelDurationMinutes(next.getTravelDurationMinutes());
                step.setTransportationMode(next.getTransportationMode());
            }
            steps.add(step);
        }
        return steps;
    }

    private List<TravelDestination> stepsToDestinations(List<ItineraryStep> steps) {
        List<TravelDestination> destinations = new ArrayList<>();
        for (int i = 0; i < steps.size(); i++) {
            ItineraryStep step = steps.get(i);
            TravelDestination dest;
            // Try to recover from saved itinerary's original destinations
            if (savedItinerary != null && savedItinerary.getSteps() != null
                    && i < savedItinerary.getSteps().size()) {
                dest = savedItinerary.getSteps().get(i);
                dest.setOrder(i + 1);
                dest.setName(step.getTitle());
                dest.setDescription(step.getDescription());
            } else {
                dest = new TravelDestination(i + 1, step.getTitle(), step.getDescription(),
                        "", 0, 0, 0, 0, "Découverte");
            }
            destinations.add(dest);
        }
        return destinations;
    }

    private String computePeriod(int index, int total) {
        if (total == 0) return "Matin";
        int third = Math.max(1, total / 3);
        if (index < third) return "Matin";
        if (index < 2 * third) return "Après-midi";
        return "Soir";
    }

    private int iconResForType(String type) {
        if (type == null) return R.drawable.ic_map_pin;
        switch (type) {
            case "Restauration": return R.drawable.ic_utensils;
            case "Loisirs": return R.drawable.ic_gamepad;
            case "Découverte": return R.drawable.ic_compass;
            case "Culture": return R.drawable.ic_sparkles;
            case "Monument": return R.drawable.ic_building_2;
            default: return R.drawable.ic_map_pin;
        }
    }

    private void setupBottomNavigation() {
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.travelPathPreferencesFragment);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.travelPathPreferencesFragment) {
                finish();
                return true;
            }
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("target_fragment_id", itemId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            return true;
        });
    }

}
