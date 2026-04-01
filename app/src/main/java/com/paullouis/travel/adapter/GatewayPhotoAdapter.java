package com.paullouis.travel.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paullouis.travel.R;
import com.paullouis.travel.model.Photo;

import android.graphics.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GatewayPhotoAdapter extends RecyclerView.Adapter<GatewayPhotoAdapter.GatewayViewHolder> {

    private final List<Photo> photos;
    private final Set<String> selectedPhotoIds = new HashSet<>();
    private final OnSelectionChangedListener listener;

    public interface OnSelectionChangedListener {
        void onSelectionChanged(int count);
    }

    public GatewayPhotoAdapter(List<Photo> photos, OnSelectionChangedListener listener) {
        this.photos = photos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GatewayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gateway_photo, parent, false);
        return new GatewayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GatewayViewHolder holder, int position) {
        Photo photo = photos.get(position);
        holder.tvLocationName.setText(photo.getTitle());
        holder.tvCity.setText(photo.getLocationName());

        if (photo.getImageResId() != 0) {
            Glide.with(holder.itemView.getContext())
                    .load(photo.getImageResId())
                    .centerCrop()
                    .into(holder.ivPhoto);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(photo.getImageUrl())
                    .centerCrop()
                    .into(holder.ivPhoto);
        }

        boolean isSelected = selectedPhotoIds.contains(photo.getId());
        holder.ivHeart.setImageResource(isSelected ? R.drawable.ic_heart_filled : R.drawable.ic_favorite_border);
        holder.ivHeart.setColorFilter(isSelected ? Color.RED : Color.WHITE);

        holder.itemView.setOnClickListener(v -> {
            if (isSelected) {
                selectedPhotoIds.remove(photo.getId());
            } else {
                selectedPhotoIds.add(photo.getId());
            }
            notifyItemChanged(position);
            if (listener != null) {
                listener.onSelectionChanged(selectedPhotoIds.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public void selectPhoto(String locationName) {
        for (int i = 0; i < photos.size(); i++) {
            if (photos.get(i).getTitle().equalsIgnoreCase(locationName)) {
                selectedPhotoIds.add(photos.get(i).getId());
                notifyItemChanged(i);
                if (listener != null) {
                    listener.onSelectionChanged(selectedPhotoIds.size());
                }
                break;
            }
        }
    }

    public int getSelectedCount() {
        return selectedPhotoIds.size();
    }

    static class GatewayViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto, ivHeart;
        TextView tvLocationName, tvCity;

        public GatewayViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            ivHeart = itemView.findViewById(R.id.ivHeart);
            tvLocationName = itemView.findViewById(R.id.tvLocationName);
            tvCity = itemView.findViewById(R.id.tvCity);
        }
    }
}
