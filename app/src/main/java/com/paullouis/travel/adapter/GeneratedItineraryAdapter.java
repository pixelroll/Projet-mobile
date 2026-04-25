package com.paullouis.travel.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paullouis.travel.ItineraryDetailActivity;
import com.paullouis.travel.R;
import com.paullouis.travel.model.GeneratedItinerary;

import java.util.List;

public class GeneratedItineraryAdapter extends RecyclerView.Adapter<GeneratedItineraryAdapter.ViewHolder> {

    private List<GeneratedItinerary> items;
    private Context context;

    public GeneratedItineraryAdapter(List<GeneratedItinerary> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_generated_itinerary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GeneratedItinerary item = items.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvDescription.setText(item.getDescription());
        holder.tvBudget.setText(item.getBudget());
        holder.tvDuration.setText(item.getDuration());
        holder.tvStops.setText(item.getStops());
        holder.tvEffort.setText(item.getEffort());

        // Effort Badge styling
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(50f);
        switch (item.getEffort()) {
            case "Facile":
                bg.setColor(Color.parseColor("#E8F5E9"));
                holder.tvEffort.setTextColor(Color.parseColor("#388E3C"));
                break;
            case "Modéré":
                bg.setColor(Color.parseColor("#FFF8E1"));
                holder.tvEffort.setTextColor(Color.parseColor("#F57F17"));
                break;
            case "Difficile":
                bg.setColor(Color.parseColor("#FFEBEE"));
                holder.tvEffort.setTextColor(Color.parseColor("#C62828"));
                break;
            default:
                bg.setColor(Color.parseColor("#E0F7FA"));
                holder.tvEffort.setTextColor(Color.parseColor("#0891b2"));
                break;
        }
        holder.tvEffort.setBackground(bg);

        // Like button
        updateLikeIcon(holder.ivLikeBottom, item.isLiked());

        View.OnClickListener toggleLike = v -> {
            item.setLiked(!item.isLiked());
            updateLikeIcon(holder.ivLikeBottom, item.isLiked());
        };
        holder.btnLikeBottom.setOnClickListener(toggleLike);

        // Detail button
        holder.btnDetail.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItineraryDetailActivity.class);
            intent.putExtra("ITINERARY_TITLE", item.getTitle());
            context.startActivity(intent);
        });
    }

    private void updateLikeIcon(ImageView ivBottom, boolean isLiked) {
        int color = isLiked ? Color.parseColor("#EF4444") : Color.parseColor("#BDBDBD");
        int resId = isLiked ? R.drawable.ic_heart_filled : R.drawable.ic_heart_lucide;
        
        ivBottom.setImageResource(resId);
        ivBottom.setColorFilter(color);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvBudget, tvDuration, tvEffort, tvStops;
        ImageView ivLikeBottom;
        View btnDetail, btnLikeBottom;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvBudget = itemView.findViewById(R.id.tvBudget);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvEffort = itemView.findViewById(R.id.tvEffort);
            tvStops = itemView.findViewById(R.id.tvStops);
            ivLikeBottom = itemView.findViewById(R.id.ivLikeBottom);
            btnDetail = itemView.findViewById(R.id.btnDetail);
            btnLikeBottom = itemView.findViewById(R.id.btnLikeBottom);
        }
    }
}
