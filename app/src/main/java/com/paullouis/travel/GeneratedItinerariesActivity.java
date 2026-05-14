package com.paullouis.travel;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.type.GenerativeBackend;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.paullouis.travel.adapter.GeneratedItineraryAdapter;
import com.paullouis.travel.data.ItineraryCache;
import com.paullouis.travel.model.GeneratedItinerary;
import com.paullouis.travel.model.TravelDestination;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GeneratedItinerariesActivity extends AppCompatActivity {

    private GenerativeModelFutures aiModel;
    private View loadingContainer;
    private NestedScrollView scrollContent;
    private MaterialButton btnRegenerate;
    private MaterialButton btnEditPreferences;
    private TextView tvBannerText;
    private RecyclerView rvItineraries;

    private static final int REQUEST_REGEN = 1;

    private String prefLocation;
    private String prefDate;
    private String prefActivities;
    private String prefDuration;
    private String prefProfile;
    private String prefRequiredPlaces;
    private String prefPhotoContext;
    private int prefBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generated_itineraries);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), systemBars.bottom);
            return insets;
        });

        // Extract intent extras
        prefLocation = getIntent().getStringExtra("LOCATION_NAME");
        prefDate = getIntent().getStringExtra("LOCATION_DATE");
        prefActivities = getIntent().getStringExtra("PREF_ACTIVITIES");
        prefDuration = getIntent().getStringExtra("PREF_DURATION");
        prefProfile = getIntent().getStringExtra("PREF_PROFILE");

        try {
            prefBudget = Integer.parseInt(getIntent().getStringExtra("PREF_BUDGET"));
        } catch (Exception e) {
            prefBudget = 100;
        }

        prefRequiredPlaces = getIntent().getStringExtra("PREF_REQUIRED_PLACES");
        prefPhotoContext = getIntent().getStringExtra("PHOTO_CONTEXT");

        if (prefLocation == null) prefLocation = "";
        if (prefDate == null) {
            prefDate = new java.text.SimpleDateFormat("d MMMM yyyy", new java.util.Locale("fr", "FR")).format(new java.util.Date());
        }
        if (prefActivities == null) prefActivities = "Découverte";
        if (prefDuration == null) prefDuration = "1 jour";
        if (prefProfile == null) prefProfile = "Famille";
        if (prefRequiredPlaces == null) prefRequiredPlaces = "";
        if (prefPhotoContext == null) prefPhotoContext = "";

        // Initialize AI model
        aiModel = GenerativeModelFutures.from(
            FirebaseAI.getInstance(GenerativeBackend.googleAI())
                .generativeModel("gemini-3-flash-preview")
        );

        // Bind views
        loadingContainer = findViewById(R.id.loadingContainer);
        scrollContent = findViewById(R.id.scrollContent);
        tvBannerText = findViewById(R.id.tvBannerText);
        rvItineraries = findViewById(R.id.rvItineraries);
        btnRegenerate = findViewById(R.id.btnRegenerate);
        btnEditPreferences = findViewById(R.id.btnEditPreferences);

        // Setup toolbar
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Setup header
        TextView tvLocationDate = findViewById(R.id.tvLocationDate);
        String locationLabel = prefLocation.isEmpty() ? "Destination IA" : prefLocation;
        tvLocationDate.setText(locationLabel + " • " + prefDate);

        // Setup bottom navigation
        setupBottomNavigation();

        // Setup action buttons
        btnEditPreferences.setOnClickListener(v -> finish());
        btnRegenerate.setOnClickListener(v -> generateItineraries());

        // Kick off AI generation
        generateItineraries();
    }

    private void generateItineraries() {
        showLoading(true);
        new Thread(() -> {
            try {
                String prompt = buildPrompt();
                Content content = new Content.Builder().addText(prompt).build();
                ListenableFuture<GenerateContentResponse> future = aiModel.generateContent(content);
                GenerateContentResponse response = future.get();

                if (response == null) {
                    throw new Exception("API returned null response");
                }

                String text = response.getText();
                if (text == null || text.isEmpty()) {
                    throw new Exception("API returned empty response");
                }

                // Remove markdown code fences
                text = text.trim()
                    .replaceAll("(?s)```[a-z]*\\n?", "")
                    .replace("```", "")
                    .trim();

                List<GeneratedItinerary> result = parseItineraries(text);

                runOnUiThread(() -> {
                    showLoading(false);
                    if (result != null && !result.isEmpty()) {
                        ItineraryCache.set(result);
                        displayItineraries(result);
                    } else {
                        showError("Impossible de générer les parcours. Veuillez réessayer.");
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showLoading(false);
                    showError("Erreur : " + (e.getMessage() != null ? e.getMessage() : "Erreur inconnue"));
                });
            }
        }).start();
    }

    private String buildPrompt() {
        String stepCountRule;
        int exampleSteps;
        switch (prefDuration) {
            case "½ journée":
                stepCountRule = "3 à 4 destinations par itinéraire";
                exampleSteps = 3;
                break;
            case "2 jours+":
                stepCountRule = "10 à 12 destinations par itinéraire";
                exampleSteps = 10;
                break;
            default: // "1 jour"
                stepCountRule = "5 à 6 destinations par itinéraire";
                exampleSteps = 5;
                break;
        }

        String requiredPlacesRule = "";
        if (!prefRequiredPlaces.isEmpty()) {
            requiredPlacesRule = "- Lieux obligatoires à inclure dans chaque itinéraire : " + prefRequiredPlaces + "\n";
        }

        String locationLine = prefLocation.isEmpty()
            ? "- Destination : à toi de choisir une ville intéressante en accord avec les préférences ci-dessous (indique la ville choisie dans les descriptions)\n"
            : "- Destination : " + prefLocation + "\n";

        String photoContextSection = "";
        if (!prefPhotoContext.isEmpty()) {
            photoContextSection = "\nPhotos aimées par le voyageur (utilise ces lieux et trouve des endroits similaires qui correspondent aux mêmes goûts) :\n"
                + prefPhotoContext + "\n";
        }

        return "Tu es un expert en voyages. Génère exactement 3 variantes d'itinéraire de voyage.\n\n" +
            "Préférences du voyageur :\n" +
            locationLine +
            "- Date : " + prefDate + "\n" +
            "- Activités souhaitées : " + prefActivities + "\n" +
            "- Budget maximum : " + prefBudget + "€\n" +
            "- Durée : " + prefDuration + "\n" +
            "- Profil voyageur : " + prefProfile + "\n" +
            (prefRequiredPlaces.isEmpty() ? "" : "- Lieux obligatoires : " + prefRequiredPlaces + "\n") +
            photoContextSection +
            "\nRéponds UNIQUEMENT avec un objet JSON valide, sans texte avant ni après :\n" +
            "{\n" +
            "  \"itineraries\": [\n" +
            "    {\n" +
            "      \"type\": \"ECO\",\n" +
            "      \"destinationCity\": \"Ville et Pays (ex: Barcelone, Espagne)\",\n" +
            "      \"description\": \"...\",\n" +
            "      \"totalBudget\": 45,\n" +
            "      \"estimatedDurationHours\": 6.5,\n" +
            "      \"effort\": \"LOW\",\n" +
            "      \"numberOfSteps\": " + exampleSteps + ",\n" +
            "      \"destinations\": [\n" +
            "        {\n" +
            "          \"name\": \"Nom du lieu\",\n" +
            "          \"description\": \"Description courte\",\n" +
            "          \"reason\": \"Pourquoi ce lieu correspond aux préférences\",\n" +
            "          \"latitude\": 48.8566,\n" +
            "          \"longitude\": 2.3522,\n" +
            "          \"estimatedPriceEuros\": 15,\n" +
            "          \"estimatedDurationMinutes\": 60,\n" +
            "          \"travelDurationMinutes\": 15,\n" +
            "          \"transportationMode\": \"marche\",\n" +
            "          \"type\": \"Culture\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    { \"type\": \"BALANCED\", ... },\n" +
            "    { \"type\": \"COMFORT\", ... }\n" +
            "  ]\n" +
            "}\n\n" +
            "Règles :\n" +
            "- type itinéraire : exactement \"ECO\", \"BALANCED\" ou \"COMFORT\"\n" +
            "- effort : exactement \"LOW\", \"MEDIUM\" ou \"HIGH\"\n" +
            "- type destination : \"Restauration\", \"Loisirs\", \"Découverte\", \"Culture\" ou \"Monument\"\n" +
            "- transportationMode : \"marche\", \"vélo\", \"bus\", \"métro\" ou \"voiture\" selon la distance et le profil\n" +
            "- travelDurationMinutes : temps de trajet depuis l'étape précédente (0 pour la première étape)\n" +
            "- Coordonnées GPS réelles et précises pour la ville choisie\n" +
            "- Budget ECO < " + (prefBudget / 2) + "€, BALANCED entre " + (prefBudget / 2) + "€ et " + (int)(prefBudget * 0.8) + "€, COMFORT jusqu'à " + prefBudget + "€\n" +
            "- Toutes les descriptions en français\n" +
            requiredPlacesRule +
            "- Exactement " + stepCountRule + "\n" +
            "- JSON uniquement, rien d'autre";
    }

    private List<GeneratedItinerary> parseItineraries(String json) {
        try {
            JSONObject root = new JSONObject(json);
            JSONArray itinerariesArray = root.getJSONArray("itineraries");
            List<GeneratedItinerary> itineraries = new ArrayList<>();

            for (int i = 0; i < itinerariesArray.length(); i++) {
                JSONObject itinJson = itinerariesArray.getJSONObject(i);

                GeneratedItinerary itin = new GeneratedItinerary();
                itin.setType(itinJson.getString("type"));
                itin.setDescription(itinJson.optString("description", ""));
                itin.setTotalBudget(itinJson.optInt("totalBudget", 0));
                itin.setEstimatedDurationHours((float) itinJson.optDouble("estimatedDurationHours", 0));
                itin.setEffort(itinJson.optString("effort", "MEDIUM"));
                itin.setNumberOfSteps(itinJson.optInt("numberOfSteps", 0));
                itin.setDestinationCity(itinJson.optString("destinationCity", ""));

                // Parse destinations
                List<TravelDestination> destinations = new ArrayList<>();
                if (itinJson.has("destinations")) {
                    JSONArray destArray = itinJson.getJSONArray("destinations");
                    for (int j = 0; j < destArray.length(); j++) {
                        JSONObject destJson = destArray.getJSONObject(j);
                        TravelDestination dest = new TravelDestination(
                            j + 1, // order (1-indexed)
                            destJson.optString("name", ""),
                            destJson.optString("description", ""),
                            destJson.optString("reason", ""),
                            destJson.optDouble("latitude", 0),
                            destJson.optDouble("longitude", 0),
                            destJson.optInt("estimatedPriceEuros", 0),
                            destJson.optInt("estimatedDurationMinutes", 0),
                            destJson.optString("type", "Découverte")
                        );
                        dest.setTravelDurationMinutes(destJson.optInt("travelDurationMinutes", 0));
                        dest.setTransportationMode(destJson.optString("transportationMode", ""));
                        destinations.add(dest);
                    }
                }
                itin.setDestinations(destinations);
                itineraries.add(itin);
            }

            return itineraries;
        } catch (JSONException e) {
            return null;
        }
    }

    private void showLoading(boolean loading) {
        loadingContainer.setVisibility(loading ? View.VISIBLE : View.GONE);
        scrollContent.setVisibility(loading ? View.GONE : View.VISIBLE);
        btnRegenerate.setEnabled(!loading);
        btnEditPreferences.setEnabled(!loading);
    }

    private void showError(String message) {
        com.google.android.material.snackbar.Snackbar.make(
            findViewById(android.R.id.content),
            message,
            com.google.android.material.snackbar.Snackbar.LENGTH_LONG
        ).setAction("Réessayer", v -> generateItineraries())
         .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REGEN && resultCode == RESULT_FIRST_USER) {
            generateItineraries();
        }
    }

    private void displayItineraries(List<GeneratedItinerary> itineraries) {
        String htmlText = "Nous avons généré <b>" + itineraries.size() + " parcours</b> adaptés à vos préférences et votre budget.";
        tvBannerText.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT));

        rvItineraries.setLayoutManager(new LinearLayoutManager(this));
        rvItineraries.setAdapter(new GeneratedItineraryAdapter(itineraries, this, prefLocation, prefDate));
    }

    private void setupBottomNavigation() {
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.travelPathPreferencesFragment);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.travelPathPreferencesFragment) {
                return true;
            }

            android.content.Intent intent = new android.content.Intent(this, MainActivity.class);
            intent.putExtra("target_fragment_id", itemId);
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            return true;
        });
    }
}
