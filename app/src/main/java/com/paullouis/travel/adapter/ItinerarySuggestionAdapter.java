package com.paullouis.travel.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.paullouis.travel.R;
import com.paullouis.travel.model.ItinerarySuggestion;

import java.util.List;

public class ItinerarySuggestionAdapter extends RecyclerView.Adapter<ItinerarySuggestionAdapter.SuggestionViewHolder> {

    private final List<ItinerarySuggestion> suggestions;
    private final OnSuggestionClickListener listener;

    public interface OnSuggestionClickListener {
        void onCustomizeClick(ItinerarySuggestion suggestion);
    }

    public ItinerarySuggestionAdapter(List<ItinerarySuggestion> suggestions, OnSuggestionClickListener listener) {
        this.suggestions = suggestions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_itinerary_suggestion, parent, false);
        return new SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        ItinerarySuggestion suggestion = suggestions.get(position);
        holder.tvName.setText(suggestion.getName());
        holder.tvDuration.setText(suggestion.getDuration());
        holder.tvBudget.setText(suggestion.getBudget());

        holder.chipGroup.removeAllViews();
        for (String place : suggestion.getPlaces()) {
            Chip chip = new Chip(holder.itemView.getContext());
            chip.setText(place);
            chip.setChipIconResource(R.drawable.ic_map_pin);
            chip.setChipIconSize(32f); // 12dp equivalent approx
            chip.setChipIconVisible(true);
            chip.setChipBackgroundColorResource(R.color.background); // Light teal-ish if possible
            chip.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.primary));
            chip.setChipStartPadding(8f);
            holder.chipGroup.addView(chip);
        }

        holder.btnCustomize.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCustomizeClick(suggestion);
            }
        });
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    static class SuggestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDuration, tvBudget;
        ChipGroup chipGroup;
        View btnCustomize;

        public SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItineraryName);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvBudget = itemView.findViewById(R.id.tvBudget);
            chipGroup = itemView.findViewById(R.id.chipGroupPlaces);
            btnCustomize = itemView.findViewById(R.id.btnCustomize);
        }
    }
}
