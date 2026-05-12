package com.paullouis.travel;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paullouis.travel.adapter.LikedPhotoSelectionAdapter;
import com.paullouis.travel.data.FirebaseRepository;
import com.paullouis.travel.model.Photo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LikedPhotosSelectionActivity extends AppCompatActivity {

    private LikedPhotoSelectionAdapter adapter;
    private List<Photo> allPhotos = new ArrayList<>();
    private TextView btnGenerate;
    private TextView tvSelectionCount;
    private View progressBar;
    private View emptyState;
    private RecyclerView rvPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_photos_selection);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), systemBars.bottom);
            return insets;
        });

        btnGenerate = findViewById(R.id.btnGenerate);
        tvSelectionCount = findViewById(R.id.tvSelectionCount);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);
        rvPhotos = findViewById(R.id.rvPhotos);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        adapter = new LikedPhotoSelectionAdapter(new ArrayList<>(), this::onSelectionChanged);
        rvPhotos.setLayoutManager(new GridLayoutManager(this, 3));
        rvPhotos.setAdapter(adapter);

        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPhotos(s.toString().trim());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnGenerate.setOnClickListener(v -> launchGeneration());

        loadLikedPhotos();
    }

    private void loadLikedPhotos() {
        progressBar.setVisibility(View.VISIBLE);
        rvPhotos.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);

        FirebaseRepository.getInstance().getLikedPhotos(new com.paullouis.travel.data.DataCallback<List<Photo>>() {
            @Override
            public void onSuccess(List<Photo> photos) {
                progressBar.setVisibility(View.GONE);
                allPhotos = photos;
                if (photos.isEmpty()) {
                    emptyState.setVisibility(View.VISIBLE);
                } else {
                    rvPhotos.setVisibility(View.VISIBLE);
                    adapter.updatePhotos(photos);
                }
            }

            @Override
            public void onError(Exception e) {
                progressBar.setVisibility(View.GONE);
                emptyState.setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.tvEmptyTitle)).setText("Erreur de chargement");
                ((TextView) findViewById(R.id.tvEmptySubtitle)).setText("Impossible de charger vos photos aimées");
            }
        });
    }

    private void filterPhotos(String query) {
        if (query.isEmpty()) {
            adapter.updatePhotos(allPhotos);
            return;
        }
        String lower = query.toLowerCase();
        List<Photo> filtered = new ArrayList<>();
        for (Photo p : allPhotos) {
            boolean matchesTitle = p.getTitle() != null && p.getTitle().toLowerCase().contains(lower);
            boolean matchesDesc = p.getDescription() != null && p.getDescription().toLowerCase().contains(lower);
            boolean matchesLocation = p.getLocationName() != null && p.getLocationName().toLowerCase().contains(lower);
            if (matchesTitle || matchesDesc || matchesLocation) filtered.add(p);
        }
        adapter.updatePhotos(filtered);
        emptyState.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        rvPhotos.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
        if (filtered.isEmpty()) {
            ((TextView) findViewById(R.id.tvEmptyTitle)).setText("Aucun résultat");
            ((TextView) findViewById(R.id.tvEmptySubtitle)).setText("Aucune photo ne correspond à \"" + query + "\"");
        }
    }

    private void onSelectionChanged(int count) {
        if (count == 0) {
            tvSelectionCount.setText("Sélectionnez des photos pour créer votre parcours");
            btnGenerate.setAlpha(0.5f);
            btnGenerate.setEnabled(false);
        } else {
            tvSelectionCount.setText(count + " photo" + (count > 1 ? "s" : "") + " sélectionnée" + (count > 1 ? "s" : ""));
            btnGenerate.setAlpha(1f);
            btnGenerate.setEnabled(true);
        }
    }

    private void launchGeneration() {
        List<Photo> selected = adapter.getSelectedPhotos();
        if (selected.isEmpty()) return;

        // Build required places from photo locations
        List<String> requiredPlaces = new ArrayList<>();
        StringBuilder photoContext = new StringBuilder();
        String primaryLocation = "";

        for (Photo p : selected) {
            if (p.getLocationName() != null && !p.getLocationName().isEmpty()) {
                if (!requiredPlaces.contains(p.getLocationName())) {
                    requiredPlaces.add(p.getLocationName());
                }
                if (primaryLocation.isEmpty()) primaryLocation = p.getLocationName();
            }
            photoContext.append("- ");
            if (p.getTitle() != null && !p.getTitle().isEmpty()) photoContext.append(p.getTitle());
            if (p.getLocationName() != null && !p.getLocationName().isEmpty()) {
                photoContext.append(" (").append(p.getLocationName()).append(")");
            }
            if (p.getDescription() != null && !p.getDescription().isEmpty()) {
                photoContext.append(": ").append(p.getDescription());
            }
            if (p.getTags() != null && !p.getTags().isEmpty()) {
                photoContext.append(" [").append(String.join(", ", p.getTags())).append("]");
            }
            photoContext.append("\n");
        }

        String today = new SimpleDateFormat("d MMMM yyyy", new Locale("fr", "FR")).format(new Date());

        Intent intent = new Intent(this, GeneratedItinerariesActivity.class);
        intent.putExtra("LOCATION_NAME", primaryLocation);
        intent.putExtra("LOCATION_DATE", today);
        intent.putExtra("PREF_ACTIVITIES", "Découverte,Culture,Loisirs");
        intent.putExtra("PREF_BUDGET", "150");
        intent.putExtra("PREF_DURATION", "1 jour");
        intent.putExtra("PREF_PROFILE", "Famille");
        intent.putExtra("PREF_REQUIRED_PLACES", String.join(",", requiredPlaces));
        intent.putExtra("PHOTO_CONTEXT", photoContext.toString());
        startActivity(intent);
    }
}
