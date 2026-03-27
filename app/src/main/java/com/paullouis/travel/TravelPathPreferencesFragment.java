package com.paullouis.travel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class TravelPathPreferencesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_travelpath_preferences, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back button
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        // Budget slider (Material Slider)
        com.google.android.material.slider.Slider sliderBudget = view.findViewById(R.id.sliderBudget);
        TextView tvBudgetValue = view.findViewById(R.id.tvBudgetValue);
        sliderBudget.addOnChangeListener((slider, value, fromUser) -> {
            tvBudgetValue.setText((int) value + "€");
        });

        // Duration chip selection
        TextView chipHalfDay = view.findViewById(R.id.chipHalfDay);
        TextView chipOneDay = view.findViewById(R.id.chipOneDay);
        TextView chipMultiDay = view.findViewById(R.id.chipMultiDay);
        setupDurationChips(chipHalfDay, chipOneDay, chipMultiDay);

        // Profile chip selection
        TextView chipFamille = view.findViewById(R.id.chipFamille);
        TextView chipSeniors = view.findViewById(R.id.chipSeniors);
        TextView chipSportif = view.findViewById(R.id.chipSportif);
        TextView chipAventurier = view.findViewById(R.id.chipAventurier);
        setupProfileChips(chipFamille, chipSeniors, chipSportif, chipAventurier);

        // Meteo chip toggle
        setupToggleChip(view.findViewById(R.id.chipFroid));
        setupToggleChip(view.findViewById(R.id.chipChaleur));
        setupToggleChip(view.findViewById(R.id.chipHumidite));
        setupToggleChip(view.findViewById(R.id.chipEnsoleille));

        // Generate button (placeholder)
        view.findViewById(R.id.btnGenerate).setOnClickListener(v -> {
            // TODO: Collect preferences and navigate to results
        });
    }

    private void setupDurationChips(TextView... chips) {
        for (TextView chip : chips) {
            chip.setOnClickListener(v -> {
                for (TextView c : chips) {
                    c.setBackground(getResources().getDrawable(R.drawable.chip_outline, null));
                    c.setTextColor(0xFF4A5568);
                    c.setTypeface(null, android.graphics.Typeface.NORMAL);
                }
                chip.setBackground(getResources().getDrawable(R.drawable.chip_outline_selected, null));
                chip.setTextColor(0xFF0891b2);
                chip.setTypeface(null, android.graphics.Typeface.BOLD);
            });
        }
    }

    private void setupProfileChips(TextView... chips) {
        for (TextView chip : chips) {
            chip.setOnClickListener(v -> {
                for (TextView c : chips) {
                    c.setBackground(getResources().getDrawable(R.drawable.chip_outline, null));
                    c.setTextColor(0xFF4A5568);
                }
                chip.setBackground(getResources().getDrawable(R.drawable.chip_filled_turquoise, null));
                chip.setTextColor(0xFFFFFFFF);
            });
        }
    }

    private void setupToggleChip(TextView chip) {
        chip.setOnClickListener(v -> {
            boolean isSelected = chip.isSelected();
            chip.setSelected(!isSelected);
            if (!isSelected) {
                chip.setBackground(getResources().getDrawable(R.drawable.chip_filled_turquoise, null));
                chip.setTextColor(0xFFFFFFFF);
            } else {
                chip.setBackground(getResources().getDrawable(R.drawable.chip_outline, null));
                chip.setTextColor(0xFF4A5568);
            }
        });
    }
}
