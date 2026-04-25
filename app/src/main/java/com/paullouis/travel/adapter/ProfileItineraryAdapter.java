package com.paullouis.travel.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.paullouis.travel.ItineraryDetailActivity;
import com.paullouis.travel.R;
import com.paullouis.travel.model.ProfileItinerary;

import java.util.List;

public class ProfileItineraryAdapter extends RecyclerView.Adapter<ProfileItineraryAdapter.ViewHolder> {

    private List<ProfileItinerary> items;
    private Context context;

    public ProfileItineraryAdapter(List<ProfileItinerary> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_itinerary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProfileItinerary item = items.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvDuration.setText(item.getDuration());
        holder.tvLocation.setText(item.getLocation());
        holder.ivItineraryImage.setImageResource(item.getImageRes());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItineraryDetailActivity.class);
            // TODO backend: passer le vrai id du parcours et charger ses données
            intent.putExtra("ITINERARY_TITLE", "Équilibré");
            intent.putExtra("EXTRA_CITY", "Paris");
            intent.putExtra("EXTRA_DATE", "17 Mars 2026");
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDuration, tvLocation;
        ShapeableImageView ivItineraryImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            ivItineraryImage = itemView.findViewById(R.id.ivItineraryImage);
        }
    }
}
