package com.paullouis.travel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.paullouis.travel.adapter.GatewayPhotoAdapter;
import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.Photo;

import java.util.List;

public class GatewayActivity extends AppCompatActivity {

    private RecyclerView rvPhotos;
    private TextView tvSelectionCount, tvSuggestionsSubtitle;
    private MaterialButton btnGenerate;
    private GatewayPhotoAdapter photoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway);

        initViews();
        setupToolbar();
        setupPhotosGrid();
        setupHowItWorks();
        
        // Handle incoming extra for pre-selection
        String preselectedLocation = getIntent().getStringExtra("location");
        if (preselectedLocation != null) {
            photoAdapter.selectPhoto(preselectedLocation);
        } else {
            updateSelectionUI(0); // Set initial state
        }
    }

    private void initViews() {
        rvPhotos = findViewById(R.id.rvGatewayPhotos);
        tvSelectionCount = findViewById(R.id.tvSelectionCount);
        tvSuggestionsSubtitle = findViewById(R.id.tvSuggestionsSubtitle);
        btnGenerate = findViewById(R.id.btnGenerate);

        btnGenerate.setOnClickListener(v -> generateItinerary());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
        ((TextView) findViewById(R.id.toolbar).findViewById(R.id.toolbar_title)).setText("Créer un parcours depuis vos photos");
    }

    private void setupPhotosGrid() {
        List<Photo> photos = MockDataProvider.getGatewayPhotos();
        photoAdapter = new GatewayPhotoAdapter(photos, count -> {
            updateSelectionUI(count);
        });
        rvPhotos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvPhotos.setAdapter(photoAdapter);
    }

    private void updateSelectionUI(int count) {
        tvSelectionCount.setText(count + " sélectionnée" + (count > 1 ? "s" : ""));
        
        // Update button appearance
        int color = count > 0 ? 0xFF0891B2 : 0xFFBDBDBD;
        btnGenerate.setBackgroundColor(color);
        
        // Update button text with plural logic
        String text = "Générer un parcours complet (" + count + " lieu" + (count > 1 ? "s" : "") + ")";
        btnGenerate.setText(text);
        
        // Update subtitle if suggestions are visible
        tvSuggestionsSubtitle.setText("Basé sur vos " + count + " photo" + (count > 1 ? "s" : "") + " sélectionnée" + (count > 1 ? "s" : ""));
    }

    private void setupHowItWorks() {
        setupStep(R.id.step1, "1", "Sélectionnez vos lieux préférés", "Parcourez vos photos aimées et choisissez celles qui vous intéressent");
        setupStep(R.id.step2, "2", "L'IA génère des parcours", "Nous créons automatiquement des itinéraires optimisés");
        setupStep(R.id.step3, "3", "Personnalisez et partez", "Ajustez selon vos préférences et commencez votre aventure");
    }

    private void setupStep(int viewId, String num, String title, String desc) {
        View view = findViewById(viewId);
        ((TextView) view.findViewById(R.id.tvStepNumber)).setText(num);
        ((TextView) view.findViewById(R.id.tvStepTitle)).setText(title);
        ((TextView) view.findViewById(R.id.tvStepDescription)).setText(desc);
    }


    private void generateItinerary() {
        if (photoAdapter.getSelectedCount() == 0) {
            Toast.makeText(this, "Veuillez sélectionner au moins un lieu", Toast.LENGTH_SHORT).show();
            return;
        }
        // Navigate directly to the generated itineraries page
        startActivity(new Intent(this, GeneratedItinerariesActivity.class));
    }
}
