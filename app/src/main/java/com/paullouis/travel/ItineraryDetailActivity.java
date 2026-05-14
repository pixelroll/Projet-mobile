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
        if (city == null || city.trim().isEmpty()) {
            city = (itinerary.getDestinationCity() != null && !itinerary.getDestinationCity().trim().isEmpty()) 
                ? itinerary.getDestinationCity().trim() 
                : "";
        }
        if (date == null || date.trim().isEmpty()) {
            date = new java.text.SimpleDateFormat("d MMMM yyyy", new java.util.Locale("fr", "FR")).format(new java.util.Date());
        }

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

        ImageView ivExportPdf = findViewById(R.id.ivExportPdf);
        if (ivExportPdf != null) {
            ivExportPdf.setOnClickListener(v -> exportCompiledPdf());
        }
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
            List<TravelDestination> dests = savedItinerary != null && savedItinerary.getSteps() != null ?
                    savedItinerary.getSteps() : stepsToDestinations(currentSteps);

            String budgetStr = ((TextView) findViewById(R.id.tvStatBudget)).getText().toString();
            int budgetVal = 0;
            try {
                String cleanB = budgetStr.replace("€", "").trim();
                budgetVal = Integer.parseInt(cleanB);
            } catch (Exception e) {}

            GeneratedItinerary fallback = new GeneratedItinerary(
                    "BALANCED", savedItinerary != null ? savedItinerary.getDescription() : "",
                    budgetVal, 4.0f, "MEDIUM", dests.size(), dests, true
            );
            fallback.setDestinationCity(finalCity);
            ItineraryCache.setSelectedFallback(fallback);

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

    private static class DeleteAction {
        int originalPosition;
        ItineraryStep uiStep;
        TravelDestination destination;

        DeleteAction(int pos, ItineraryStep uiStep, TravelDestination destination) {
            this.originalPosition = pos;
            this.uiStep = uiStep;
            this.destination = destination;
        }
    }
    private final java.util.Stack<DeleteAction> undoStack = new java.util.Stack<>();

    private void toggleEditMode() {
        editMode = !editMode;
        FloatingActionButton fabEdit = findViewById(R.id.fabEdit);
        if (fabEdit != null) {
            fabEdit.setImageResource(editMode ? R.drawable.ic_check : R.drawable.ic_edit);
        }

        View llEditModeActions = findViewById(R.id.llEditModeActions);
        if (llEditModeActions != null) llEditModeActions.setVisibility(editMode ? View.VISIBLE : View.GONE);

        if (!editMode) {
            undoStack.clear();
        }

        View btnUndoDelete = findViewById(R.id.btnUndoDelete);
        if (btnUndoDelete != null) {
            btnUndoDelete.setOnClickListener(v -> {
                if (!undoStack.isEmpty()) {
                    DeleteAction action = undoStack.pop();
                    if (action.destination != null) action.destination.setEnabled(true);

                    int insertPos = Math.min(action.originalPosition, currentSteps.size());
                    insertPos = Math.max(0, insertPos);

                    currentSteps.add(insertPos, action.uiStep);
                    if (savedItinerary != null && savedItinerary.getSteps() != null && action.destination != null) {
                        int destInsertPos = Math.min(action.originalPosition, savedItinerary.getSteps().size());
                        destInsertPos = Math.max(0, destInsertPos);
                        savedItinerary.getSteps().add(destInsertPos, action.destination);
                    }

                    for (int i = insertPos; i < currentSteps.size(); i++) {
                        currentSteps.get(i).setTime(String.valueOf(i + 1));
                        if (savedItinerary != null && savedItinerary.getSteps() != null
                                && i < savedItinerary.getSteps().size()) {
                            savedItinerary.getSteps().get(i).setOrder(i + 1);
                        }
                    }
                    recomputeTravelConnectors();
                    stepAdapter.notifyDataSetChanged();
                    ((TextView) findViewById(R.id.tvStatSteps)).setText(String.valueOf(currentSteps.size()));
                } else {
                    Toast.makeText(ItineraryDetailActivity.this, "Aucune suppression à annuler", Toast.LENGTH_SHORT).show();
                }
            });
        }

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
                    final ItineraryStep removedUiStep = currentSteps.get(position);
                    final TravelDestination removedDest = (savedItinerary != null && savedItinerary.getSteps() != null
                            && position < savedItinerary.getSteps().size()) ? savedItinerary.getSteps().get(position) : null;

                    if (removedDest != null) {
                        removedDest.setEnabled(false);
                    }

                    undoStack.push(new DeleteAction(position, removedUiStep, removedDest));

                    currentSteps.remove(position);
                    if (savedItinerary != null && savedItinerary.getSteps() != null && removedDest != null) {
                        savedItinerary.getSteps().remove(position);
                    }
                    // Re-index remaining steps
                    for (int i = position; i < currentSteps.size(); i++) {
                        currentSteps.get(i).setTime(String.valueOf(i + 1));
                        if (savedItinerary != null && savedItinerary.getSteps() != null
                                && i < savedItinerary.getSteps().size()) {
                            savedItinerary.getSteps().get(i).setOrder(i + 1);
                        }
                    }
                    recomputeTravelConnectors();
                    stepAdapter.notifyDataSetChanged();
                    ((TextView) findViewById(R.id.tvStatSteps)).setText(String.valueOf(currentSteps.size()));
                }
            });
        }
    }

    private int calculateWalkingTimeMinutes(double lat1, double lon1, double lat2, double lon2) {
        if (lat1 == 0 && lon1 == 0 && lat2 == 0 && lon2 == 0) {
            return 15;
        }
        double r = 6371.0; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distKm = r * c;

        // Walking speed ~ 5 km/h -> 12 minutes per km
        int minutes = (int) Math.round(distKm * 12.0);
        return Math.max(1, minutes);
    }

    private void recomputeTravelConnectors() {
        if (currentSteps == null) return;
        int total = currentSteps.size();
        for (int i = 0; i < total; i++) {
            ItineraryStep step = currentSteps.get(i);
            if (i + 1 < total) {
                ItineraryStep nextStep = currentSteps.get(i + 1);
                int travelMin = calculateWalkingTimeMinutes(
                        step.getLatitude(), step.getLongitude(),
                        nextStep.getLatitude(), nextStep.getLongitude()
                );
                String mode = "marche";

                if (savedItinerary != null && savedItinerary.getSteps() != null && i + 1 < savedItinerary.getSteps().size()) {
                    TravelDestination nextDest = savedItinerary.getSteps().get(i + 1);
                    if (nextDest.getTransportationMode() != null && !nextDest.getTransportationMode().isEmpty()) {
                        mode = nextDest.getTransportationMode();
                        if (!"marche".equalsIgnoreCase(mode) && nextDest.getTravelDurationMinutes() > 0) {
                            travelMin = nextDest.getTravelDurationMinutes();
                        }
                    }
                }
                step.setTravelDurationMinutes(travelMin);
                step.setTransportationMode(mode);
            } else {
                step.setTravelDurationMinutes(0);
                step.setTransportationMode("");
            }
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

        EditText etPrice = new EditText(this);
        etPrice.setHint("Prix estimé (€)");
        etPrice.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(etPrice);

        EditText etDuration = new EditText(this);
        etDuration.setHint("Durée estimée (minutes)");
        etDuration.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(etDuration);

        double defaultLat = 48.8566;
        double defaultLon = 2.3522;
        if (!currentSteps.isEmpty()) {
            defaultLat = currentSteps.get(0).getLatitude();
            defaultLon = currentSteps.get(0).getLongitude();
            if (defaultLat == 0 && defaultLon == 0 && savedItinerary != null && savedItinerary.getSteps() != null && !savedItinerary.getSteps().isEmpty()) {
                defaultLat = savedItinerary.getSteps().get(0).getLatitude();
                defaultLon = savedItinerary.getSteps().get(0).getLongitude();
            }
        }

        EditText etLat = new EditText(this);
        etLat.setHint("Latitude");
        etLat.setText(String.valueOf(defaultLat));
        etLat.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        layout.addView(etLat);

        EditText etLon = new EditText(this);
        etLon.setHint("Longitude");
        etLon.setText(String.valueOf(defaultLon));
        etLon.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        layout.addView(etLon);

        final double finalDefaultLat = defaultLat;
        final double finalDefaultLon = defaultLon;

        new AlertDialog.Builder(this)
                .setTitle("Ajouter une étape")
                .setView(layout)
                .setPositiveButton("Ajouter", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    if (name.isEmpty()) return;
                    String desc = etDesc.getText().toString().trim();

                    int priceVal = 0;
                    try { priceVal = Integer.parseInt(etPrice.getText().toString().trim()); } catch(Exception e){}

                    int durationVal = 60;
                    try { durationVal = Integer.parseInt(etDuration.getText().toString().trim()); } catch(Exception e){}

                    double latVal = finalDefaultLat;
                    try { latVal = Double.parseDouble(etLat.getText().toString().trim()); } catch(Exception e){}

                    double lonVal = finalDefaultLon;
                    try { lonVal = Double.parseDouble(etLon.getText().toString().trim()); } catch(Exception e){}

                    int order = currentSteps.size() + 1;

                    ItineraryStep newStep = new ItineraryStep(
                            String.valueOf(order), name, desc, "?", "", durationVal + " min", priceVal + "€", "Journée", "",
                            R.drawable.ic_map_pin, 0, latVal, lonVal
                    );
                    currentSteps.add(newStep);
                    if (savedItinerary != null) {
                        TravelDestination newDest = new TravelDestination(
                                order, name, desc, "", latVal, lonVal, priceVal, durationVal, "Découverte"
                        );
                        if (savedItinerary.getSteps() == null) savedItinerary.setSteps(new ArrayList<>());
                        savedItinerary.getSteps().add(newDest);
                    }
                    recomputeTravelConnectors();
                    stepAdapter.notifyDataSetChanged();
                    ((TextView) findViewById(R.id.tvStatSteps)).setText(String.valueOf(currentSteps.size()));
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
                int tMin = next.getTravelDurationMinutes();
                String mode = next.getTransportationMode();
                if (mode == null || mode.isEmpty()) mode = "marche";
                if ("marche".equalsIgnoreCase(mode) || tMin <= 0) {
                    tMin = calculateWalkingTimeMinutes(dest.getLatitude(), dest.getLongitude(), next.getLatitude(), next.getLongitude());
                    mode = "marche";
                }
                step.setTravelDurationMinutes(tMin);
                step.setTransportationMode(mode);
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

    private void exportCompiledPdf() {
        String title = savedItinerary != null ? savedItinerary.getTitle() : (ItineraryCache.getSelected() != null ? ItineraryCache.getSelected().getTitle() : "Parcours");
        String budget = savedItinerary != null ? savedItinerary.getBudgetFormatted() : (ItineraryCache.getSelected() != null ? ItineraryCache.getSelected().getBudget() : "");
        String duration = savedItinerary != null ? savedItinerary.getDurationFormatted() : (ItineraryCache.getSelected() != null ? ItineraryCache.getSelected().getDuration() : "");

        android.graphics.pdf.PdfDocument pdfDocument = new android.graphics.pdf.PdfDocument();
        int pageWidth = 595; // A4 width in points
        int pageHeight = 842; // A4 height in points
        int margin = 54; // 0.75 inch margin
        int contentWidth = pageWidth - 2 * margin;

        android.graphics.pdf.PdfDocument.PageInfo pageInfo = new android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        android.graphics.pdf.PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        android.graphics.Canvas canvas = page.getCanvas();

        android.text.TextPaint titlePaint = new android.text.TextPaint();
        titlePaint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.SERIF, android.graphics.Typeface.BOLD));
        titlePaint.setTextSize(24f);
        titlePaint.setColor(android.graphics.Color.parseColor("#0891B2"));
        titlePaint.setAntiAlias(true);

        android.text.TextPaint subtitlePaint = new android.text.TextPaint();
        subtitlePaint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.SERIF, android.graphics.Typeface.NORMAL));
        subtitlePaint.setTextSize(14f);
        subtitlePaint.setColor(android.graphics.Color.parseColor("#616161"));
        subtitlePaint.setAntiAlias(true);

        android.text.TextPaint sectionPaint = new android.text.TextPaint();
        sectionPaint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.SERIF, android.graphics.Typeface.BOLD));
        sectionPaint.setTextSize(16f);
        sectionPaint.setColor(android.graphics.Color.parseColor("#212121"));
        sectionPaint.setAntiAlias(true);

        android.text.TextPaint boldPaint = new android.text.TextPaint();
        boldPaint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.SERIF, android.graphics.Typeface.BOLD));
        boldPaint.setTextSize(13f);
        boldPaint.setColor(android.graphics.Color.parseColor("#212121"));
        boldPaint.setAntiAlias(true);

        android.text.TextPaint metaPaint = new android.text.TextPaint();
        metaPaint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.SERIF, android.graphics.Typeface.ITALIC));
        metaPaint.setTextSize(11f);
        metaPaint.setColor(android.graphics.Color.parseColor("#0891B2"));
        metaPaint.setAntiAlias(true);

        android.text.TextPaint normalPaint = new android.text.TextPaint();
        normalPaint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.SERIF, android.graphics.Typeface.NORMAL));
        normalPaint.setTextSize(12f);
        normalPaint.setColor(android.graphics.Color.parseColor("#424242"));
        normalPaint.setAntiAlias(true);

        android.text.TextPaint connectorPaint = new android.text.TextPaint();
        connectorPaint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.SERIF, android.graphics.Typeface.ITALIC));
        connectorPaint.setTextSize(11f);
        connectorPaint.setColor(android.graphics.Color.parseColor("#9E9E9E"));
        connectorPaint.setAntiAlias(true);

        android.graphics.Paint linePaint = new android.graphics.Paint();
        linePaint.setColor(android.graphics.Color.parseColor("#E2E8F0"));
        linePaint.setStrokeWidth(1f);

        int currentY = margin;

        // Title Block
        String docHeader = "CARNET DE VOYAGE";
        canvas.drawText(docHeader, margin, currentY + 20, titlePaint);
        currentY += 32;

        canvas.drawText(title != null ? title : "", margin, currentY + 16, sectionPaint);
        currentY += 24;

        String displayCity = (city != null && !city.trim().isEmpty()) ? city.trim() : "Choisie par l'IA (Itinéraire surprise)";
        String displayDate = (date != null && !date.trim().isEmpty()) ? date.trim() : "Date flexible";

        String subtitle = displayCity + " • " + displayDate;
        canvas.drawText(subtitle, margin, currentY + 14, subtitlePaint);
        currentY += 24;

        canvas.drawLine(margin, currentY, pageWidth - margin, currentY, linePaint);
        currentY += 20;

        // Informations Générales
        canvas.drawText("Informations Générales", margin, currentY + 16, sectionPaint);
        currentY += 28;

        String destStr = "• Destination : " + displayCity;
        canvas.drawText(destStr, margin + 10, currentY + 12, normalPaint);
        currentY += 20;

        String durStr = "• Durée totale estimée : " + (duration != null ? duration : "");
        canvas.drawText(durStr, margin + 10, currentY + 12, normalPaint);
        currentY += 20;

        String budStr = "• Budget estimé : " + (budget != null ? budget : "");
        canvas.drawText(budStr, margin + 10, currentY + 12, normalPaint);
        currentY += 20;

        String stepsStr = "• Nombre d'étapes : " + (currentSteps != null ? currentSteps.size() : 0);
        canvas.drawText(stepsStr, margin + 10, currentY + 12, normalPaint);
        currentY += 28;

        canvas.drawLine(margin, currentY, pageWidth - margin, currentY, linePaint);
        currentY += 24;

        // Itinéraire Détaillé
        canvas.drawText("Itinéraire Détaillé", margin, currentY + 16, sectionPaint);
        currentY += 28;

        int pageCount = 1;
        if (currentSteps != null) {
            for (int i = 0; i < currentSteps.size(); i++) {
                com.paullouis.travel.model.ItineraryStep step = currentSteps.get(i);
                
                String stepTime = step.getTime() != null ? step.getTime() : "";
                String stepTitleStr = (i + 1) + ". " + (step.getTitle() != null ? step.getTitle() : "");
                if (!stepTime.isEmpty()) {
                    stepTitleStr += " (" + stepTime + ")";
                }

                // Compile step metadata
                StringBuilder metaBuilder = new StringBuilder();
                if (step.getDuration() != null && !step.getDuration().isEmpty()) {
                    metaBuilder.append("Durée : ").append(step.getDuration());
                }
                if (step.getPrice() != null && !step.getPrice().isEmpty()) {
                    if (metaBuilder.length() > 0) metaBuilder.append("  •  ");
                    metaBuilder.append("Tarif : ").append(step.getPrice());
                }
                if (step.getPeriod() != null && !step.getPeriod().isEmpty()) {
                    if (metaBuilder.length() > 0) metaBuilder.append("  •  ");
                    metaBuilder.append("Période : ").append(step.getPeriod());
                }
                String stepMeta = metaBuilder.toString();

                String stepDesc = step.getDescription() != null ? step.getDescription() : "";

                // Measure description layout
                android.text.StaticLayout descLayout = null;
                int descHeight = 0;
                if (!stepDesc.isEmpty()) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        descLayout = android.text.StaticLayout.Builder.obtain(stepDesc, 0, stepDesc.length(), normalPaint, contentWidth - 16)
                                .setAlignment(android.text.Layout.Alignment.ALIGN_NORMAL)
                                .setLineSpacing(0f, 1.2f)
                                .setIncludePad(false).build();
                    } else {
                        descLayout = new android.text.StaticLayout(stepDesc, normalPaint, contentWidth - 16, android.text.Layout.Alignment.ALIGN_NORMAL, 1.2f, 0f, false);
                    }
                    descHeight = descLayout.getHeight();
                }

                // Check travel connector
                boolean hasConnector = i < currentSteps.size() - 1 && 
                    (step.getTravelDurationMinutes() > 0 || (step.getTransportationMode() != null && !step.getTransportationMode().isEmpty()));
                String travelText = "";
                if (hasConnector) {
                    travelText = "➔ Trajet vers l'étape suivante : " + step.getTravelDurationMinutes() + " min";
                    if (step.getTransportationMode() != null && !step.getTransportationMode().isEmpty()) {
                        travelText += " en " + step.getTransportationMode();
                    }
                }

                int neededHeight = 24 + (!stepMeta.isEmpty() ? 18 : 0) + (descHeight > 0 ? descHeight + 8 : 0) + (hasConnector ? 20 : 0) + 16;
                if (currentY + neededHeight > pageHeight - margin) {
                    normalPaint.setTextAlign(android.graphics.Paint.Align.CENTER);
                    canvas.drawText("- " + pageCount + " -", pageWidth / 2f, pageHeight - 30, normalPaint);
                    normalPaint.setTextAlign(android.graphics.Paint.Align.LEFT);

                    pdfDocument.finishPage(page);
                    pageCount++;
                    pageInfo = new android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageCount).create();
                    page = pdfDocument.startPage(pageInfo);
                    canvas = page.getCanvas();
                    currentY = margin;
                }

                // Draw Step Title
                canvas.drawText(stepTitleStr, margin, currentY + 14, boldPaint);
                currentY += 20;

                // Draw Step Metadata
                if (!stepMeta.isEmpty()) {
                    canvas.drawText(stepMeta, margin + 16, currentY + 12, metaPaint);
                    currentY += 18;
                }

                // Draw Step Description
                if (descLayout != null) {
                    canvas.save();
                    canvas.translate(margin + 16, currentY);
                    descLayout.draw(canvas);
                    canvas.restore();
                    currentY += descHeight + 8;
                }

                // Draw Travel Connector
                if (hasConnector) {
                    canvas.drawText(travelText, margin + 16, currentY + 12, connectorPaint);
                    currentY += 18;
                }

                currentY += 12; // extra space between steps
            }
        }

        normalPaint.setTextAlign(android.graphics.Paint.Align.CENTER);
        canvas.drawText("- " + pageCount + " -", pageWidth / 2f, pageHeight - 30, normalPaint);
        normalPaint.setTextAlign(android.graphics.Paint.Align.LEFT);

        pdfDocument.finishPage(page);

        try {
            java.io.File cachePath = new java.io.File(getCacheDir(), "itineraries");
            if (!cachePath.exists()) cachePath.mkdirs();
            java.io.File pdfFile = new java.io.File(cachePath, "Carnet_de_Voyage.pdf");
            java.io.FileOutputStream fos = new java.io.FileOutputStream(pdfFile);
            pdfDocument.writeTo(fos);
            pdfDocument.close();
            fos.close();

            android.net.Uri pdfUri = androidx.core.content.FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", pdfFile);

            android.content.Intent viewIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
            viewIntent.setDataAndType(pdfUri, "application/pdf");
            viewIntent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);

            android.content.Intent chooserIntent = android.content.Intent.createChooser(viewIntent, "Ouvrir le PDF compilé");
            startActivity(chooserIntent);

        } catch (Exception e) {
            android.widget.Toast.makeText(this, "Erreur de génération du PDF", android.widget.Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}
