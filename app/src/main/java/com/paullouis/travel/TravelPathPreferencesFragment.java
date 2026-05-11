package com.paullouis.travel;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.paullouis.travel.data.FirebaseRepository;

import java.util.ArrayList;
import java.util.List;

public class TravelPathPreferencesFragment extends Fragment {
    private String selectedDuration = "1 jour";
    private String selectedProfile = "Famille";
    private com.google.android.material.slider.Slider sliderBudget;
    private ChipGroup cgRequiredPlaces;
    private final List<String> requiredPlaces = new ArrayList<>();

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

        // Required places chip group
        cgRequiredPlaces = view.findViewById(R.id.cgRequiredPlaces);
        view.findViewById(R.id.btnAddPlace).setOnClickListener(v -> showAddPlaceDialog());

        // Budget slider (Material Slider)
        sliderBudget = view.findViewById(R.id.sliderBudget);
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

        // Generate button
        EditText etCity = view.findViewById(R.id.etCity);
        view.findViewById(R.id.btnGenerate).setOnClickListener(v -> {
            if (FirebaseRepository.getInstance().isUserLoggedIn()) {
                // Collect activity preferences
                CheckBox cbRestauration = view.findViewById(R.id.cbRestauration);
                CheckBox cbLoisirs = view.findViewById(R.id.cbLoisirs);
                CheckBox cbDecouverte = view.findViewById(R.id.cbDecouverte);
                CheckBox cbCulture = view.findViewById(R.id.cbCulture);

                List<String> activities = new ArrayList<>();
                if (cbRestauration.isChecked()) activities.add("Restauration");
                if (cbLoisirs.isChecked()) activities.add("Loisirs");
                if (cbDecouverte.isChecked()) activities.add("Découverte");
                if (cbCulture.isChecked()) activities.add("Culture");

                String activitiesStr = activities.isEmpty() ? "Découverte" : String.join(",", activities);
                int budget = (int) sliderBudget.getValue();

                String city = etCity.getText().toString().trim();
                String locationName = city.isEmpty() ? "" : city;

                String today = new SimpleDateFormat("d MMMM yyyy", new Locale("fr", "FR")).format(new Date());

                android.content.Intent intent = new android.content.Intent(getActivity(), GeneratedItinerariesActivity.class);
                intent.putExtra("LOCATION_NAME", locationName);
                intent.putExtra("LOCATION_DATE", today);
                intent.putExtra("PREF_ACTIVITIES", activitiesStr);
                intent.putExtra("PREF_BUDGET", String.valueOf(budget));
                intent.putExtra("PREF_DURATION", selectedDuration);
                intent.putExtra("PREF_PROFILE", selectedProfile);
                intent.putExtra("PREF_REQUIRED_PLACES", String.join(",", requiredPlaces));
                startActivity(intent);
            } else {
                LoginRequiredDialogFragment.newInstance().show(getChildFragmentManager(), "login_required");
            }
        });

        // Try Now button — opens liked photos selection for AI-powered parcours generation
        view.findViewById(R.id.btnTryNow).setOnClickListener(v -> {
            if (FirebaseRepository.getInstance().isUserLoggedIn()) {
                android.content.Intent intent = new android.content.Intent(getActivity(), LikedPhotosSelectionActivity.class);
                startActivity(intent);
            } else {
                LoginRequiredDialogFragment.newInstance().show(getChildFragmentManager(), "login_required");
            }
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
                selectedDuration = chip.getText().toString();
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
                selectedProfile = chip.getText().toString();
            });
        }
    }

    private void showAddPlaceDialog() {
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        input.setHint("Nom du lieu");
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        input.setPadding(pad, pad, pad, pad);

        new AlertDialog.Builder(requireContext())
            .setTitle("Ajouter un lieu obligatoire")
            .setView(input)
            .setPositiveButton("Ajouter", (dialog, which) -> {
                String place = input.getText().toString().trim();
                if (!place.isEmpty()) {
                    requiredPlaces.add(place);
                    addPlaceChip(place);
                }
            })
            .setNegativeButton("Annuler", null)
            .show();
    }

    private void addPlaceChip(String place) {
        Chip chip = new Chip(requireContext());
        chip.setText(place);
        chip.setTextColor(0xFFFFFFFF);
        chip.setTextSize(13f);
        chip.setChipBackgroundColorResource(android.R.color.transparent);
        chip.setChipStrokeWidth(0f);
        chip.setCloseIconVisible(true);
        chip.setCloseIconTintResource(android.R.color.white);
        chip.setCheckedIconVisible(false);
        chip.setChipCornerRadius(20f * getResources().getDisplayMetrics().density);

        android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
        bg.setColor(0xFF0891b2);
        bg.setCornerRadius(20f * getResources().getDisplayMetrics().density);
        chip.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(0xFF0891b2));

        chip.setOnCloseIconClickListener(v -> {
            requiredPlaces.remove(place);
            cgRequiredPlaces.removeView(chip);
        });
        cgRequiredPlaces.addView(chip);
    }
}
