package com.paullouis.travel;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.card.MaterialCardView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerativeBackend;
import com.paullouis.travel.adapter.PhotoAdapter;
import com.paullouis.travel.data.DataCallback;
import com.paullouis.travel.data.FirebaseRepository;
import com.paullouis.travel.model.Photo;
import com.paullouis.travel.model.SearchFilters;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvancedSearchFragment extends Fragment {

    private View containerFiltres;
    private RecyclerView rvSearchResults;
    private EditText etSearchQuery, etAuthorFilter, etTagsFilter;
    private PhotoAdapter searchResultsAdapter;
    private View containerSimilarLoading;
    private TextView tvSimilarUploadLabel;
    private GenerativeModelFutures aiModel;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private FusedLocationProviderClient fusedLocationClient;

    // Place type filter
    private String selectedPlaceType = null;
    private MaterialCardView cardNature, cardMusee, cardRue, cardMagasin, cardRestaurant, cardMonument;

    // Moment of day filter
    private String selectedMomentOfDay = null;
    private TextView btnMatin, btnMidi, btnApresmidi, btnSoir, btnNuit;

    // Period filter
    private String selectedPeriod = null;

    // Location filter
    private Double selectedLocationLat = null;
    private Double selectedLocationLng = null;
    private String selectedLocationName = null;
    private Integer selectedRadius = null;
    private TextView tvLocationValue, tvCustomRadius;
    private TextView btnRadius1km, btnRadius3km, btnRadius5km, btnRadius10km;
    private LinearLayout btnRadiusCustom, btnAroundMe;

    // Group filter
    private String selectedGroupId = null;
    private final Map<String, String> groupNameToId = new HashMap<>();

    private static final String COLOR_SELECTED_STROKE = "#0891b2";
    private static final String COLOR_UNSELECTED_STROKE = "#E2E8F0";
    private static final String COLOR_SELECTED_TEXT = "#0891b2";
    private static final String COLOR_UNSELECTED_TEXT = "#0F172A";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) analyzeAndSearchSimilar(uri);
            }
        );

        aiModel = GenerativeModelFutures.from(
            FirebaseAI.getInstance(GenerativeBackend.googleAI())
                .generativeModel("gemini-3-flash-preview")
        );

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_advanced_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupSearchResults();
        setupPlaceTypeCards();
        setupMomentButtons();
        setupDropdowns(view);
        setupLocationPicker(view);
        setupRadiusSelector();
        setupAroundMeButton();

        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });

        view.findViewById(R.id.btnSubmit).setOnClickListener(v -> handleFilterSearch());

        view.findViewById(R.id.btnDice).setOnClickListener(v -> handleRandomSearch());

        view.findViewById(R.id.llSimilarUpload).setOnClickListener(v ->
            imagePickerLauncher.launch("image/*")
        );
    }

    private void setupDropdowns(View view) {
        TextView tvPeriodValue = view.findViewById(R.id.tvPeriodValue);
        view.findViewById(R.id.btnPeriod).setOnClickListener(v -> {
            android.widget.PopupMenu popup = new android.widget.PopupMenu(getContext(), v);
            popup.getMenu().add("Aujourd'hui");
            popup.getMenu().add("Cette semaine");
            popup.getMenu().add("Ce mois");
            popup.getMenu().add("Cette année");
            popup.setOnMenuItemClickListener(item -> {
                selectedPeriod = item.getTitle().toString();
                tvPeriodValue.setText(selectedPeriod);
                tvPeriodValue.setTextColor(Color.parseColor("#0F172A"));
                return true;
            });
            popup.show();
        });

        TextView tvGroupFilter = view.findViewById(R.id.tvGroupFilter);
        view.findViewById(R.id.btnGroupFilter).setOnClickListener(v -> {
            android.widget.PopupMenu popup = new android.widget.PopupMenu(getContext(), v);
            popup.getMenu().add("Tous mes groupes");
            FirebaseRepository.getInstance().getMyGroups(new DataCallback<List<com.paullouis.travel.model.Group>>() {
                @Override
                public void onSuccess(List<com.paullouis.travel.model.Group> groups) {
                    groupNameToId.clear();
                    for (com.paullouis.travel.model.Group g : groups) {
                        popup.getMenu().add(g.getName());
                        groupNameToId.put(g.getName(), g.getId());
                    }
                }
                @Override
                public void onError(Exception e) {}
            });
            popup.setOnMenuItemClickListener(item -> {
                String name = item.getTitle().toString();
                tvGroupFilter.setText(name);
                tvGroupFilter.setTextColor(Color.parseColor("#0F172A"));
                if ("Tous mes groupes".equals(name)) {
                    selectedGroupId = null;
                } else {
                    selectedGroupId = groupNameToId.get(name);
                }
                return true;
            });
            popup.show();
        });
    }

    private void setupLocationPicker(View view) {
        view.findViewById(R.id.btnLocationPicker).setOnClickListener(v -> {
            LocationPickerDialogFragment picker = LocationPickerDialogFragment.newInstance((name, lat, lng) -> {
                selectedLocationName = name;
                selectedLocationLat = lat;
                selectedLocationLng = lng;
                tvLocationValue.setText(name);
                tvLocationValue.setTextColor(Color.parseColor("#0F172A"));
            });
            picker.show(getChildFragmentManager(), "location_picker");
        });
    }

    private void setupRadiusSelector() {
        List<TextView> radiusButtons = Arrays.asList(btnRadius1km, btnRadius3km, btnRadius5km, btnRadius10km);
        int[] radiusValues = {1, 3, 5, 10};

        for (int i = 0; i < radiusButtons.size(); i++) {
            TextView btn = radiusButtons.get(i);
            int radius = radiusValues[i];
            if (btn == null) continue;
            btn.setOnClickListener(v -> {
                if (selectedRadius != null && selectedRadius == radius) {
                    selectedRadius = null;
                    setRadiusButtonSelected(btn, false);
                    tvCustomRadius.setText("Personnalisé");
                } else {
                    if (selectedRadius != null) {
                        TextView prev = getRadiusButton(selectedRadius, radiusButtons, radiusValues);
                        if (prev != null) setRadiusButtonSelected(prev, false);
                    }
                    selectedRadius = radius;
                    setRadiusButtonSelected(btn, true);
                    tvCustomRadius.setText("Personnalisé");
                }
            });
        }

        btnRadiusCustom.setOnClickListener(v -> showCustomRadiusDialog());
    }

    private TextView getRadiusButton(int radius, List<TextView> buttons, int[] values) {
        for (int i = 0; i < values.length; i++) {
            if (values[i] == radius) return buttons.get(i);
        }
        return null;
    }

    private void setRadiusButtonSelected(TextView btn, boolean selected) {
        btn.setTextColor(Color.parseColor(selected ? COLOR_SELECTED_TEXT : COLOR_UNSELECTED_TEXT));
    }

    private void showCustomRadiusDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Rayon personnalisé");

        EditText input = new EditText(getContext());
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        if (selectedRadius != null && !Arrays.asList(1, 3, 5, 10).contains(selectedRadius)) {
            input.setText(String.valueOf(selectedRadius));
        }
        input.setHint("Entrez le rayon en km");
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String value = input.getText().toString().trim();
            if (!value.isEmpty()) {
                try {
                    int radius = Integer.parseInt(value);
                    if (radius > 0) {
                        selectedRadius = radius;
                        tvCustomRadius.setText(radius + " km");
                    } else {
                        Toast.makeText(getContext(), "Veuillez entrer un nombre positif", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Valeur invalide", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    private void setupAroundMeButton() {
        btnAroundMe.setOnClickListener(v -> getCurrentLocation());
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    selectedLocationLat = location.getLatitude();
                    selectedLocationLng = location.getLongitude();
                    selectedLocationName = "Ma position actuelle";
                    tvLocationValue.setText(selectedLocationName);
                    tvLocationValue.setTextColor(Color.parseColor("#0F172A"));
                    if (selectedRadius == null) {
                        selectedRadius = 5;
                        setRadiusButtonSelected(btnRadius5km, true);
                    }
                    Toast.makeText(getContext(), "Position détectée", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Localisation non disponible", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Permission de localisation requise", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews(View view) {
        containerFiltres = view.findViewById(R.id.containerFiltres);
        rvSearchResults = view.findViewById(R.id.rvSearchResults);
        containerSimilarLoading = view.findViewById(R.id.containerSimilarLoading);
        tvSimilarUploadLabel = view.findViewById(R.id.tvSimilarUploadLabel);
        etSearchQuery = view.findViewById(R.id.etSearchQuery);
        etAuthorFilter = view.findViewById(R.id.etAuthorFilter);
        etTagsFilter = view.findViewById(R.id.etTagsFilter);

        cardNature = view.findViewById(R.id.cardNature);
        cardMusee = view.findViewById(R.id.cardMusee);
        cardRue = view.findViewById(R.id.cardRue);
        cardMagasin = view.findViewById(R.id.cardMagasin);
        cardRestaurant = view.findViewById(R.id.cardRestaurant);
        cardMonument = view.findViewById(R.id.cardMonument);

        btnMatin = view.findViewById(R.id.btnMatin);
        btnMidi = view.findViewById(R.id.btnMidi);
        btnApresmidi = view.findViewById(R.id.btnApresmidi);
        btnSoir = view.findViewById(R.id.btnSoir);
        btnNuit = view.findViewById(R.id.btnNuit);

        tvLocationValue = view.findViewById(R.id.tvLocationValue);
        tvCustomRadius = view.findViewById(R.id.tvCustomRadius);
        btnRadius1km = view.findViewById(R.id.btnRadius1km);
        btnRadius3km = view.findViewById(R.id.btnRadius3km);
        btnRadius5km = view.findViewById(R.id.btnRadius5km);
        btnRadius10km = view.findViewById(R.id.btnRadius10km);
        btnRadiusCustom = view.findViewById(R.id.btnRadiusCustom);
        btnAroundMe = view.findViewById(R.id.btnAroundMe);
    }

    private void setupPlaceTypeCards() {
        Map<MaterialCardView, String> cardToType = new HashMap<>();
        cardToType.put(cardNature, "Nature");
        cardToType.put(cardMusee, "Musée");
        cardToType.put(cardRue, "Rue");
        cardToType.put(cardMagasin, "Magasin");
        cardToType.put(cardRestaurant, "Restaurant");
        cardToType.put(cardMonument, "Monument");

        for (Map.Entry<MaterialCardView, String> entry : cardToType.entrySet()) {
            MaterialCardView card = entry.getKey();
            String type = entry.getValue();
            if (card == null) continue;
            card.setOnClickListener(v -> {
                if (type.equals(selectedPlaceType)) {
                    selectedPlaceType = null;
                    setCardSelected(card, false);
                } else {
                    if (selectedPlaceType != null) {
                        MaterialCardView prev = getCardForType(selectedPlaceType, cardToType);
                        if (prev != null) setCardSelected(prev, false);
                    }
                    selectedPlaceType = type;
                    setCardSelected(card, true);
                }
            });
        }
    }

    private MaterialCardView getCardForType(String type, Map<MaterialCardView, String> map) {
        for (Map.Entry<MaterialCardView, String> e : map.entrySet()) {
            if (type.equals(e.getValue())) return e.getKey();
        }
        return null;
    }

    private void setCardSelected(MaterialCardView card, boolean selected) {
        card.setStrokeColor(Color.parseColor(selected ? COLOR_SELECTED_STROKE : COLOR_UNSELECTED_STROKE));
        card.setStrokeWidth(selected ? 2 : 1);
    }

    private void setupMomentButtons() {
        List<TextView> momentBtns = Arrays.asList(btnMatin, btnMidi, btnApresmidi, btnSoir, btnNuit);
        String[] moments = {"Matin", "Midi", "Après-midi", "Soir", "Nuit"};

        for (int i = 0; i < momentBtns.size(); i++) {
            TextView btn = momentBtns.get(i);
            String moment = moments[i];
            if (btn == null) continue;
            btn.setOnClickListener(v -> {
                if (moment.equals(selectedMomentOfDay)) {
                    selectedMomentOfDay = null;
                    setMomentSelected(btn, false);
                } else {
                    if (selectedMomentOfDay != null) {
                        TextView prev = getMomentButton(selectedMomentOfDay, momentBtns, moments);
                        if (prev != null) setMomentSelected(prev, false);
                    }
                    selectedMomentOfDay = moment;
                    setMomentSelected(btn, true);
                }
            });
        }
    }

    private TextView getMomentButton(String moment, List<TextView> btns, String[] moments) {
        for (int i = 0; i < moments.length; i++) {
            if (moment.equals(moments[i])) return btns.get(i);
        }
        return null;
    }

    private void setMomentSelected(TextView btn, boolean selected) {
        btn.setTextColor(Color.parseColor(selected ? COLOR_SELECTED_TEXT : COLOR_UNSELECTED_TEXT));
        btn.setTypeface(null, android.graphics.Typeface.BOLD);
    }

    private void setupSearchResults() {
        searchResultsAdapter = new PhotoAdapter(new ArrayList<>());
        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSearchResults.setAdapter(searchResultsAdapter);
    }

    private void showResults(List<Photo> photos) {
        containerFiltres.setVisibility(View.GONE);
        rvSearchResults.setVisibility(View.VISIBLE);
        searchResultsAdapter = new PhotoAdapter(new ArrayList<>(photos));
        rvSearchResults.setAdapter(searchResultsAdapter);
        if (photos.isEmpty()) {
            Toast.makeText(getContext(), "Aucune photo trouvée", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleFilterSearch() {
        SearchFilters filters = new SearchFilters();

        String query = etSearchQuery != null ? etSearchQuery.getText().toString().trim() : "";
        if (!query.isEmpty()) filters.setQuery(query);

        if (selectedPlaceType != null) filters.setPlaceType(selectedPlaceType);
        if (selectedMomentOfDay != null) filters.setMomentOfDay(selectedMomentOfDay);
        if (selectedPeriod != null) filters.setPeriod(selectedPeriod);

        if (selectedLocationLat != null && selectedLocationLng != null) {
            filters.setLatitude(selectedLocationLat);
            filters.setLongitude(selectedLocationLng);
            if (selectedLocationName != null) filters.setLocation(selectedLocationName);
            if (selectedRadius != null) filters.setRadiusKm(selectedRadius);
        }

        String author = etAuthorFilter != null ? etAuthorFilter.getText().toString().trim() : "";
        if (!author.isEmpty()) filters.setAuthor(author);

        if (selectedGroupId != null) filters.setGroupId(selectedGroupId);

        String tagsText = etTagsFilter != null ? etTagsFilter.getText().toString().trim() : "";
        if (!tagsText.isEmpty()) {
            List<String> tags = new ArrayList<>();
            for (String t : tagsText.split(",")) {
                String trimmed = t.trim();
                if (!trimmed.isEmpty()) tags.add(trimmed);
            }
            if (!tags.isEmpty()) filters.setTags(tags);
        }

        FirebaseRepository.getInstance().searchPhotosWithFilters(filters, new DataCallback<List<Photo>>() {
            @Override
            public void onSuccess(List<Photo> photos) {
                showResults(photos);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Erreur de recherche", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleRandomSearch() {
        FirebaseRepository.getInstance().getUserPhotos(new DataCallback<List<Photo>>() {
            @Override
            public void onSuccess(List<Photo> photos) {
                Collections.shuffle(photos);
                showResults(photos);
            }
            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Erreur de chargement", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void analyzeAndSearchSimilar(Uri imageUri) {
        if (tvSimilarUploadLabel != null) tvSimilarUploadLabel.setText("Photo sélectionnée");
        if (containerSimilarLoading != null) containerSimilarLoading.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {
                Bitmap bitmap = loadBitmapFromUri(imageUri);
                Bitmap scaled = scaleBitmap(bitmap, 768);

                String prompt = "Analyse cette photo de voyage. Réponds UNIQUEMENT avec un objet JSON valide (sans markdown, sans blocs de code) :\n" +
                    "{\"tags\": [\"<tag1>\", \"<tag2>\", ..., \"<tag10>\"]}\n" +
                    "Règles :\n" +
                    "- fournis exactement 10 tags de voyage pertinents en français\n" +
                    "- les tags décrivent ce que tu vois (ambiance, éléments notables, style du lieu, couleurs, saison, activité)\n" +
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
                JSONArray tagsArray = json.optJSONArray("tags");

                List<String> tags = new ArrayList<>();
                if (tagsArray != null) {
                    for (int i = 0; i < tagsArray.length(); i++) {
                        tags.add(tagsArray.getString(i).toLowerCase().trim());
                    }
                }

                final List<String> finalTags = tags;
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> searchBySimilarTags(finalTags));
                }

            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (containerSimilarLoading != null) containerSimilarLoading.setVisibility(View.GONE);
                        if (tvSimilarUploadLabel != null) tvSimilarUploadLabel.setText("Choisir une photo...");
                        Toast.makeText(getContext(), "Analyse IA indisponible", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }

    private void searchBySimilarTags(List<String> tags) {
        if (tags.isEmpty()) {
            if (containerSimilarLoading != null) containerSimilarLoading.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Aucun tag généré", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseRepository.getInstance().getUserPhotos(new DataCallback<List<Photo>>() {
            @Override
            public void onSuccess(List<Photo> photos) {
                Map<String, Integer> scoreMap = new HashMap<>();
                List<Photo> result = new ArrayList<>();

                for (Photo p : photos) {
                    int score = 0;
                    if (p.getTags() != null) {
                        for (String tag : tags) {
                            for (String photoTag : p.getTags()) {
                                if (photoTag.toLowerCase().trim().equals(tag)) {
                                    score++;
                                    break;
                                }
                            }
                        }
                    }
                    if (score > 0) {
                        scoreMap.put(p.getId(), score);
                        result.add(p);
                    }
                }

                result.sort((a, b) -> Integer.compare(
                    scoreMap.getOrDefault(b.getId(), 0),
                    scoreMap.getOrDefault(a.getId(), 0)
                ));

                if (containerSimilarLoading != null) containerSimilarLoading.setVisibility(View.GONE);
                showResults(result);
            }

            @Override
            public void onError(Exception e) {
                if (containerSimilarLoading != null) containerSimilarLoading.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Erreur de chargement", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Bitmap loadBitmapFromUri(Uri uri) throws IOException {
        InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
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
}
