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
import com.paullouis.travel.adapter.PhotoAdapter;
import com.paullouis.travel.adapter.SearchNavigationAdapter;
import com.paullouis.travel.data.DataCallback;
import com.paullouis.travel.data.FirebaseRepository;
import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.Photo;
import com.paullouis.travel.model.SearchNavigationOption;
import java.util.ArrayList;
import java.util.List;

public class AdvancedSearchFragment extends Fragment {

    private View containerFiltres;
    private RecyclerView rvNavigation;
    private RecyclerView rvSearchResults;
    private View indicatorFiltres, indicatorNavigation;
    private TextView tvTabFiltres, tvTabNavigation;
    private SearchNavigationOption selectedOption;
    private EditText etSearchQuery, etAuthorFilter;
    private PhotoAdapter searchResultsAdapter;

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
                tvPeriodValue.setText(item.getTitle());
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
                    for (com.paullouis.travel.model.Group g : groups) {
                        popup.getMenu().add(g.getName());
                    }
                }
                @Override
                public void onError(Exception e) {}
            });
            popup.setOnMenuItemClickListener(item -> {
                tvGroupFilter.setText(item.getTitle());
                tvGroupFilter.setTextColor(Color.parseColor("#0F172A"));
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
    }

    private void setupTabs(View view) {
        View tabFiltres = view.findViewById(R.id.tabFiltres);
        View tabNavigation = view.findViewById(R.id.tabNavigation);
        tabFiltres.setOnClickListener(v -> switchTab(true));
        tabNavigation.setOnClickListener(v -> switchTab(false));
        switchTab(true);
    }

    private void switchTab(boolean showFiltres) {
        // Hide results when switching tabs
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
        String query = etSearchQuery != null ? etSearchQuery.getText().toString().trim() : "";
        String author = etAuthorFilter != null ? etAuthorFilter.getText().toString().trim() : "";

        String searchTerm = !query.isEmpty() ? query : author;
        if (searchTerm.isEmpty()) {
            Toast.makeText(getContext(), "Saisissez un terme de recherche", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseRepository.getInstance().searchPhotos(searchTerm, new DataCallback<List<Photo>>() {
            @Override
            public void onSuccess(List<Photo> photos) {
                containerFiltres.setVisibility(View.GONE);
                rvNavigation.setVisibility(View.GONE);
                rvSearchResults.setVisibility(View.VISIBLE);
                searchResultsAdapter = new PhotoAdapter(new ArrayList<>(photos));
                rvSearchResults.setAdapter(searchResultsAdapter);
                if (photos.isEmpty()) {
                    Toast.makeText(getContext(), "Aucune photo trouvée pour \"" + searchTerm + "\"", Toast.LENGTH_SHORT).show();
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
                        // Shuffle for random discovery
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
                        FirebaseRepository.getInstance().searchPhotos(author, new DataCallback<List<Photo>>() {
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
