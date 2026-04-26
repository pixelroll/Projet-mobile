package com.paullouis.travel;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.paullouis.travel.adapter.SearchNavigationAdapter;
import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.SearchNavigationOption;
import java.util.List;

public class AdvancedSearchFragment extends Fragment {

    private View containerFiltres;
    private RecyclerView rvNavigation;
    private View indicatorFiltres, indicatorNavigation;
    private TextView tvTabFiltres, tvTabNavigation;
    private SearchNavigationOption selectedOption;

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
        
        // Back button
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }

        // Search button
        View btnSubmit = view.findViewById(R.id.btnSubmit);
        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> {
                if (rvNavigation.getVisibility() == View.VISIBLE) {
                    handleNavigationSearch();
                } else {
                    Toast.makeText(getContext(), "Recherche par filtres lancée", Toast.LENGTH_SHORT).show();
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
            popup.getMenu().add("Personnalisé...");
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
            popup.getMenu().add("Voyage Paris 2026");
            popup.getMenu().add("Vacances Bretagne");
            popup.getMenu().add("Famille");
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
        indicatorFiltres = view.findViewById(R.id.indicatorFiltres);
        indicatorNavigation = view.findViewById(R.id.indicatorNavigation);
        tvTabFiltres = view.findViewById(R.id.tvTabFiltres);
        tvTabNavigation = view.findViewById(R.id.tvTabNavigation);
    }

    private void setupTabs(View view) {
        View tabFiltres = view.findViewById(R.id.tabFiltres);
        View tabNavigation = view.findViewById(R.id.tabNavigation);

        tabFiltres.setOnClickListener(v -> switchTab(true));
        tabNavigation.setOnClickListener(v -> switchTab(false));
        
        // Default tab (Filtres is already set as visible in XML, but let's be explicit)
        switchTab(true);
    }

    private void switchTab(boolean showFiltres) {
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
        
        // Select first by default if nothing selected
        if (options != null && !options.isEmpty()) {
            boolean hasSelection = false;
            for (SearchNavigationOption o : options) {
                if (o.isSelected()) {
                    selectedOption = o;
                    hasSelection = true;
                    break;
                }
            }
            if (!hasSelection) {
                options.get(0).setSelected(true);
                selectedOption = options.get(0);
            }
        }

        SearchNavigationAdapter adapter = new SearchNavigationAdapter(options, option -> {
            selectedOption = option;
        });
        
        rvNavigation.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNavigation.setAdapter(adapter);
    }

    private void handleNavigationSearch() {
        if (selectedOption == null) {
            Toast.makeText(getContext(), "Veuillez choisir une option de navigation", Toast.LENGTH_SHORT).show();
            return;
        }

        String message;
        switch (selectedOption.getId()) {
            case "random":
                message = "Recherche aléatoire lancée";
                break;
            case "location":
                message = "Ouverture du sélecteur de lieu...";
                break;
            case "period":
                message = "Ouverture du calendrier...";
                break;
            case "author":
                message = "Recherche par auteur lancée";
                break;
            case "similar":
                message = "Recherche par similarité IA lancée";
                break;
            default:
                message = "Action lancée pour : " + selectedOption.getTitle();
                break;
        }
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
