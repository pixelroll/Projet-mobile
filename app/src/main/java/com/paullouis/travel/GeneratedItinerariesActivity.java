package com.paullouis.travel;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paullouis.travel.adapter.GeneratedItineraryAdapter;
import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.GeneratedItinerary;

import java.util.List;

public class GeneratedItinerariesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generated_itineraries);

        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), systemBars.bottom);
            return insets;
        });

        // Get intent extras
        String location = getIntent().getStringExtra("LOCATION_NAME");
        String date = getIntent().getStringExtra("LOCATION_DATE");
        if (location == null) location = "Paris, France";
        if (date == null) date = "17 Mars 2026";

        // Setup Toolbar
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Setup Header Data
        TextView tvLocationDate = findViewById(R.id.tvLocationDate);
        tvLocationDate.setText(location + " • " + date);

        // Setup Banner
        TextView tvBannerText = findViewById(R.id.tvBannerText);
        List<GeneratedItinerary> itineraries = MockDataProvider.getGeneratedItineraries();
        String htmlText = "Nous avons généré <b>" + itineraries.size() + " parcours</b> adaptés à vos préférences et votre budget.";
        tvBannerText.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT));

        // Setup RecyclerView
        RecyclerView rv = findViewById(R.id.rvItineraries);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new GeneratedItineraryAdapter(itineraries, this));

        setupActionButtons();
        setupBottomNavigation();
    }

    private void setupActionButtons() {
        findViewById(R.id.btnEditPreferences).setOnClickListener(v -> finish());
        findViewById(R.id.btnRegenerate).setOnClickListener(v -> {
            com.google.android.material.snackbar.Snackbar.make(
                findViewById(android.R.id.content),
                "Regénération en cours...",
                com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
            ).show();
        });
    }

    private void setupBottomNavigation() {
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.travelPathPreferencesFragment);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.travelPathPreferencesFragment) {
                return true;
            }
            
            // Go back to MainActivity with the target fragment
            android.content.Intent intent = new android.content.Intent(this, MainActivity.class);
            intent.putExtra("target_fragment_id", itemId);
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            return true;
        });
    }
}
