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

import com.google.android.material.imageview.ShapeableImageView;
import com.paullouis.travel.R;
import com.paullouis.travel.model.ItineraryStep;

import java.util.List;

public class ItineraryStepAdapter extends RecyclerView.Adapter<ItineraryStepAdapter.ViewHolder> {

    private List<ItineraryStep> steps;
    private Context context;

    public ItineraryStepAdapter(List<ItineraryStep> steps, Context context) {
        this.steps = steps;
        this.context = context;
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

        holder.tvTimeBubble.setText(step.getTime());
        holder.ivStepImage.setImageResource(step.getImageRes());
        holder.ivTypeIcon.setImageResource(step.getTypeIconRes());
        holder.tvTitle.setText(step.getTitle());
        holder.tvDescription.setText(step.getDescription());
        holder.tvHours.setText(step.getHours());
        holder.tvStatus.setText(step.getStatus());
        holder.tvDuration.setText(step.getDuration());
        holder.tvPrice.setText(step.getPrice());
        holder.tvPeriod.setText(step.getPeriod());
        holder.tvMediaInfo.setText(step.getMediaInfo());

        // Hide the last timeline line
        if (position == steps.size() - 1) {
            holder.timelineLine.setVisibility(View.INVISIBLE);
        } else {
            holder.timelineLine.setVisibility(View.VISIBLE);
        }

        // Period Badge coloring
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

        // Expanded photos grid
        if (step.getPhotos() != null && !step.getPhotos().isEmpty()) {
            holder.llMediaInfoToggle.setVisibility(View.VISIBLE);
            
            // Setup horizontal recyclerview
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
        TextView tvTimeBubble, tvTitle, tvDescription, tvHours, tvStatus, tvDuration, tvPrice, tvPeriod, tvMediaInfo;
        ShapeableImageView ivStepImage;
        ImageView ivTypeIcon, ivChevron;
        View timelineLine;
        LinearLayout llMediaInfoToggle;
        RecyclerView rvStepPhotos;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTimeBubble = itemView.findViewById(R.id.tvTimeBubble);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvHours = itemView.findViewById(R.id.tvHours);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvPeriod = itemView.findViewById(R.id.tvPeriod);
            tvMediaInfo = itemView.findViewById(R.id.tvMediaInfo);
            ivStepImage = itemView.findViewById(R.id.ivStepImage);
            ivTypeIcon = itemView.findViewById(R.id.ivTypeIcon);
            ivChevron = itemView.findViewById(R.id.ivChevron);
            timelineLine = itemView.findViewById(R.id.timelineLine);
            llMediaInfoToggle = itemView.findViewById(R.id.llMediaInfoToggle);
            rvStepPhotos = itemView.findViewById(R.id.rvStepPhotos);
        }
    }
}
