package com.paullouis.travel.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paullouis.travel.R;
import com.paullouis.travel.model.ItineraryStep;

import java.util.List;

public class ItineraryStepAdapter extends RecyclerView.Adapter<ItineraryStepAdapter.ViewHolder> {

    public interface OnStepDeleteListener {
        void onDelete(int position);
    }

    private List<ItineraryStep> steps;
    private Context context;
    private boolean editMode = false;
    private OnStepDeleteListener deleteListener;

    public ItineraryStepAdapter(List<ItineraryStep> steps, Context context) {
        this.steps = steps;
        this.context = context;
    }

    public void setEditMode(boolean editMode, OnStepDeleteListener listener) {
        this.editMode = editMode;
        this.deleteListener = listener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_itinerary_step, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItineraryStep step = steps.get(position);
        boolean isLast = position == steps.size() - 1;

        holder.tvTimeBubble.setText(step.getTime());
        holder.ivTypeIcon.setImageResource(step.getTypeIconRes());
        holder.tvTitle.setText(step.getTitle());
        holder.tvDescription.setText(step.getDescription());
        holder.tvDuration.setText(step.getDuration());
        holder.tvPrice.setText(step.getPrice());
        holder.tvPeriod.setText(step.getPeriod());

        // Period badge coloring
        if ("Matin".equals(step.getPeriod())) {
            holder.tvPeriod.setBackgroundResource(R.drawable.bg_badge_primary);
            holder.tvPeriod.setTextColor(Color.parseColor("#1565C0"));
        } else if ("Après-midi".equals(step.getPeriod())) {
            holder.tvPeriod.setBackgroundResource(R.drawable.bg_badge_modere);
            holder.tvPeriod.setTextColor(Color.parseColor("#E65100"));
        } else if ("Soir".equals(step.getPeriod())) {
            holder.tvPeriod.setBackgroundResource(R.drawable.bg_badge_gray);
            holder.tvPeriod.setTextColor(Color.parseColor("#4527A0"));
        }

        // Travel connector between steps
        if (isLast || (step.getTravelDurationMinutes() <= 0 && (step.getTransportationMode() == null || step.getTransportationMode().isEmpty()))) {
            holder.llTravelConnector.setVisibility(View.GONE);
        } else {
            holder.llTravelConnector.setVisibility(View.VISIBLE);
            String travelText = step.getTravelDurationMinutes() + " min";
            if (step.getTransportationMode() != null && !step.getTransportationMode().isEmpty()) {
                travelText += " · " + step.getTransportationMode();
            }
            holder.tvTravelInfo.setText(travelText);
        }

        // Edit mode: delete button
        if (holder.btnDeleteStep != null) {
            holder.btnDeleteStep.setVisibility(editMode ? View.VISIBLE : View.GONE);
            holder.btnDeleteStep.setOnClickListener(v -> {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_ID && deleteListener != null) {
                    deleteListener.onDelete(pos);
                }
            });
        }

        // Photos section
        if (step.getPhotos() != null && !step.getPhotos().isEmpty()) {
            holder.llMediaInfoToggle.setVisibility(View.VISIBLE);
            holder.rvStepPhotos.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            StepPhotoAdapter photoAdapter = new StepPhotoAdapter(step.getPhotos());
            holder.rvStepPhotos.setAdapter(photoAdapter);

            holder.llMediaInfoToggle.setOnClickListener(v -> {
                boolean isExpanded = holder.rvStepPhotos.getVisibility() == View.VISIBLE;
                holder.rvStepPhotos.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
                holder.ivChevron.animate().rotation(isExpanded ? 0 : 180).setDuration(200).start();
            });
        } else {
            holder.llMediaInfoToggle.setVisibility(View.GONE);
            holder.rvStepPhotos.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return steps != null ? steps.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTimeBubble, tvTitle, tvDescription, tvDuration, tvPrice, tvPeriod, tvMediaInfo, tvTravelInfo;
        ImageView ivTypeIcon, ivChevron, btnDeleteStep;
        LinearLayout llTravelConnector, llMediaInfoToggle;
        RecyclerView rvStepPhotos;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTimeBubble = itemView.findViewById(R.id.tvTimeBubble);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvPeriod = itemView.findViewById(R.id.tvPeriod);
            tvMediaInfo = itemView.findViewById(R.id.tvMediaInfo);
            tvTravelInfo = itemView.findViewById(R.id.tvTravelInfo);
            ivTypeIcon = itemView.findViewById(R.id.ivTypeIcon);
            ivChevron = itemView.findViewById(R.id.ivChevron);
            btnDeleteStep = itemView.findViewById(R.id.btnDeleteStep);
            llTravelConnector = itemView.findViewById(R.id.llTravelConnector);
            llMediaInfoToggle = itemView.findViewById(R.id.llMediaInfoToggle);
            rvStepPhotos = itemView.findViewById(R.id.rvStepPhotos);
        }
    }
}
