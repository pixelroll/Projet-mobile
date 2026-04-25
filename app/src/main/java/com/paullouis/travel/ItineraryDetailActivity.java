package com.paullouis.travel;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.paullouis.travel.adapter.ItineraryStepAdapter;
import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.ItineraryStep;

import java.util.List;

public class ItineraryDetailActivity extends AppCompatActivity {

    private boolean isLiked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), systemBars.bottom);
            return insets;
        });

        // Get intent extras
        String title = getIntent().getStringExtra("ITINERARY_TITLE");
        String city = getIntent().getStringExtra("EXTRA_CITY");
        String date = getIntent().getStringExtra("EXTRA_DATE");

        if (title == null) title = "Équilibré";
        if (city == null) city = "Paris";
        if (date == null) date = "17 Mars 2026";

        // TODO backend: charger le vrai parcours selon l'id

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("Parcours " + title);
        
        TextView tvSubtitle = findViewById(R.id.tvSubtitle);
        tvSubtitle.setText(city + " • " + date);

        // Header Like button
        ImageView ivLikeHeader = findViewById(R.id.ivLike);
        ivLikeHeader.setOnClickListener(v -> toggleLike(ivLikeHeader));

        // Bloc 1: Navigation FAB
        findViewById(R.id.fabNavigation).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:48.8566,2.3522"));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "Aucune application de cartographie trouvée", Toast.LENGTH_SHORT).show();
            }
        });

        // Bloc 4: Offline Save
        findViewById(R.id.btnOfflineSave).setOnClickListener(v -> {
            // TODO: Implémenter la sauvegarde hors-ligne
        });

        // Bloc 5: Steps Timeline
        RecyclerView rvStepsTimeline = findViewById(R.id.rvStepsTimeline);
        rvStepsTimeline.setLayoutManager(new LinearLayoutManager(this));
        List<ItineraryStep> steps = MockDataProvider.getItinerarySteps();
        ItineraryStepAdapter stepAdapter = new ItineraryStepAdapter(steps, this);
        rvStepsTimeline.setAdapter(stepAdapter);

        // Footer Actions
        findViewById(R.id.btnRegenerate).setOnClickListener(v -> {
            // TODO: Implémenter la regénération
        });

        findViewById(R.id.btnSave).setOnClickListener(v -> {
            toggleLike(ivLikeHeader);
        });

        String finalTitle = title;
        String finalCity = city;

        findViewById(R.id.btnShare).setOnClickListener(v -> {
            ShareProfileDialogFragment dialog = ShareProfileDialogFragment.newInstanceForItinerary(finalTitle, finalCity);
            dialog.show(getSupportFragmentManager(), "share_itinerary");
        });

        findViewById(R.id.btnExportPdf).setOnClickListener(v -> {
            PdfReadyDialogFragment dialog = PdfReadyDialogFragment.newInstance(finalTitle, finalCity);
            dialog.show(getSupportFragmentManager(), "pdf_ready");
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.travelPathPreferencesFragment);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.travelPathPreferencesFragment) {
                // If we click the same tab, just go back to the list
                finish();
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

    private void toggleLike(ImageView ivLikeHeader) {
        isLiked = !isLiked;
        
        // Update header icon
        ivLikeHeader.setImageResource(isLiked ? R.drawable.ic_heart_filled : R.drawable.ic_heart_lucide);
        ivLikeHeader.setColorFilter(isLiked ? Color.parseColor("#EF4444") : Color.parseColor("#BDBDBD"));
        
        // Update bottom button icon and text (optional feedback)
        ImageView ivSaveIcon = findViewById(R.id.ivSaveIcon);
        ivSaveIcon.setImageResource(isLiked ? R.drawable.ic_heart_filled : R.drawable.ic_heart_lucide);
    }
}
