package com.paullouis.travel;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.paullouis.travel.adapter.PhotoAdapter;
import com.paullouis.travel.adapter.SearchNavigationAdapter;
import com.paullouis.travel.data.DataCallback;
import com.paullouis.travel.data.FirebaseRepository;
import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.Photo;
import com.paullouis.travel.model.SearchFilters;
import com.paullouis.travel.model.SearchNavigationOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvancedSearchFragment extends Fragment {

    private View containerFiltres;
    private RecyclerView rvNavigation;
    private RecyclerView rvSearchResults;
    private View indicatorFiltres, indicatorNavigation;
    private TextView tvTabFiltres, tvTabNavigation;
    private SearchNavigationOption selectedOption;
    private EditText etSearchQuery, etAuthorFilter, etLocationFilter, etTagsFilter;
    private PhotoAdapter searchResultsAdapter;

    // Place type filter
    private String selectedPlaceType = null;
    private MaterialCardView cardNature, cardMusee, cardRue, cardMagasin, cardRestaurant, cardMonument;

    // Moment of day filter
    private String selectedMomentOfDay = null;
    private TextView btnMatin, btnMidi, btnApresmidi, btnSoir, btnNuit;

    // Period filter
    private String selectedPeriod = null;

    // Group filter
    private String selectedGroupId = null;
    private final Map<String, String> groupNameToId = new HashMap<>();

    private static final String COLOR_SELECTED_STROKE = "#0891b2";
    private static final String COLOR_UNSELECTED_STROKE = "#E2E8F0";
    private static final String COLOR_SELECTED_TEXT = "#0891b2";
    private static final String COLOR_UNSELECTED_TEXT = "#0F172A";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_advanced_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupTabs(view);
        setupRecyclerView();
        setupSearchResults();
        setupPlaceTypeCards();
        setupMomentButtons();

        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) getActivity().onBackPressed();
            });
        }

        View btnSubmit = view.findViewById(R.id.btnSubmit);
        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> {
                if (rvNavigation.getVisibility() == View.VISIBLE) {
                    handleNavigationSearch();
                } else {
                    handleFilterSearch();
                }
            });
        }

        setupDropdowns(view);
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

    private void initViews(View view) {
        containerFiltres = view.findViewById(R.id.containerFiltres);
        rvNavigation = view.findViewById(R.id.rvNavigation);
        rvSearchResults = view.findViewById(R.id.rvSearchResults);
        indicatorFiltres = view.findViewById(R.id.indicatorFiltres);
        indicatorNavigation = view.findViewById(R.id.indicatorNavigation);
        tvTabFiltres = view.findViewById(R.id.tvTabFiltres);
        tvTabNavigation = view.findViewById(R.id.tvTabNavigation);
        etSearchQuery = view.findViewById(R.id.etSearchQuery);
        etAuthorFilter = view.findViewById(R.id.etAuthorFilter);
        etLocationFilter = view.findViewById(R.id.etLocationFilter);
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

    private void setupTabs(View view) {
        View tabFiltres = view.findViewById(R.id.tabFiltres);
        View tabNavigation = view.findViewById(R.id.tabNavigation);
        tabFiltres.setOnClickListener(v -> switchTab(true));
        tabNavigation.setOnClickListener(v -> switchTab(false));
        switchTab(true);
    }

    private void switchTab(boolean showFiltres) {
        rvSearchResults.setVisibility(View.GONE);

        if (showFiltres) {
            containerFiltres.setVisibility(View.VISIBLE);
            rvNavigation.setVisibility(View.GONE);
            tvTabFiltres.setTextColor(Color.parseColor("#002137"));
            indicatorFiltres.setBackgroundColor(Color.parseColor("#0891b2"));
            indicatorFiltres.getLayoutParams().height = (int) (2 * getResources().getDisplayMetrics().density);
            tvTabNavigation.setTextColor(Color.parseColor("#4A5568"));
            indicatorNavigation.setBackgroundColor(Color.parseColor("#E2E8F0"));
            indicatorNavigation.getLayoutParams().height = (int) (1 * getResources().getDisplayMetrics().density);
        } else {
            containerFiltres.setVisibility(View.GONE);
            rvNavigation.setVisibility(View.VISIBLE);
            tvTabNavigation.setTextColor(Color.parseColor("#002137"));
            indicatorNavigation.setBackgroundColor(Color.parseColor("#0891b2"));
            indicatorNavigation.getLayoutParams().height = (int) (2 * getResources().getDisplayMetrics().density);
            tvTabFiltres.setTextColor(Color.parseColor("#4A5568"));
            indicatorFiltres.setBackgroundColor(Color.parseColor("#E2E8F0"));
            indicatorFiltres.getLayoutParams().height = (int) (1 * getResources().getDisplayMetrics().density);
        }
        indicatorFiltres.requestLayout();
        indicatorNavigation.requestLayout();
    }

    private void setupRecyclerView() {
        List<SearchNavigationOption> options = MockDataProvider.getSearchNavigationOptions();
        if (options != null && !options.isEmpty()) {
            boolean hasSelection = false;
            for (SearchNavigationOption o : options) {
                if (o.isSelected()) { selectedOption = o; hasSelection = true; break; }
            }
            if (!hasSelection) { options.get(0).setSelected(true); selectedOption = options.get(0); }
        }
        SearchNavigationAdapter adapter = new SearchNavigationAdapter(options, option -> selectedOption = option);
        rvNavigation.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNavigation.setAdapter(adapter);
    }

    private void setupSearchResults() {
        searchResultsAdapter = new PhotoAdapter(new ArrayList<>());
        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSearchResults.setAdapter(searchResultsAdapter);
    }

    private void handleFilterSearch() {
        SearchFilters filters = new SearchFilters();

        String query = etSearchQuery != null ? etSearchQuery.getText().toString().trim() : "";
        if (!query.isEmpty()) filters.setQuery(query);

        if (selectedPlaceType != null) filters.setPlaceType(selectedPlaceType);
        if (selectedMomentOfDay != null) filters.setMomentOfDay(selectedMomentOfDay);
        if (selectedPeriod != null) filters.setPeriod(selectedPeriod);

        String location = etLocationFilter != null ? etLocationFilter.getText().toString().trim() : "";
        if (!location.isEmpty()) filters.setLocation(location);

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
                containerFiltres.setVisibility(View.GONE);
                rvNavigation.setVisibility(View.GONE);
                rvSearchResults.setVisibility(View.VISIBLE);
                searchResultsAdapter = new PhotoAdapter(new ArrayList<>(photos));
                rvSearchResults.setAdapter(searchResultsAdapter);
                if (photos.isEmpty()) {
                    Toast.makeText(getContext(), "Aucune photo trouvée pour ces critères", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Erreur de recherche", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleNavigationSearch() {
        if (selectedOption == null) {
            Toast.makeText(getContext(), "Veuillez choisir une option de navigation", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (selectedOption.getId()) {
            case "random":
                FirebaseRepository.getInstance().getUserPhotos(new DataCallback<List<Photo>>() {
                    @Override
                    public void onSuccess(List<Photo> photos) {
                        containerFiltres.setVisibility(View.GONE);
                        rvNavigation.setVisibility(View.GONE);
                        rvSearchResults.setVisibility(View.VISIBLE);
                        java.util.Collections.shuffle(photos);
                        searchResultsAdapter = new PhotoAdapter(new ArrayList<>(photos));
                        rvSearchResults.setAdapter(searchResultsAdapter);
                    }
                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getContext(), "Erreur de chargement", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case "author":
                showAuthorSearchDialog();
                break;
            case "similar":
                Toast.makeText(getContext(), "Recherche par similarité IA : fonctionnalité avancée", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getContext(), "Utilisez l'onglet Filtres pour filtrer par " + selectedOption.getTitle(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showAuthorSearchDialog() {
        EditText input = new EditText(getContext());
        input.setHint("Nom de l'auteur");
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Rechercher par auteur")
                .setView(input)
                .setPositiveButton("Rechercher", (dialog, which) -> {
                    String author = input.getText().toString().trim();
                    if (!author.isEmpty()) {
                        SearchFilters filters = new SearchFilters();
                        filters.setAuthor(author);
                        FirebaseRepository.getInstance().searchPhotosWithFilters(filters, new DataCallback<List<Photo>>() {
                            @Override
                            public void onSuccess(List<Photo> photos) {
                                containerFiltres.setVisibility(View.GONE);
                                rvNavigation.setVisibility(View.GONE);
                                rvSearchResults.setVisibility(View.VISIBLE);
                                searchResultsAdapter = new PhotoAdapter(new ArrayList<>(photos));
                                rvSearchResults.setAdapter(searchResultsAdapter);
                                if (photos.isEmpty()) {
                                    Toast.makeText(getContext(), "Aucun auteur trouvé : " + author, Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(getContext(), "Erreur de recherche", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

}
