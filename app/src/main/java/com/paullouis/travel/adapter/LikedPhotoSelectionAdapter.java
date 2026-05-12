package com.paullouis.travel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paullouis.travel.R;
import com.paullouis.travel.model.Photo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LikedPhotoSelectionAdapter extends RecyclerView.Adapter<LikedPhotoSelectionAdapter.ViewHolder> {

    private List<Photo> photos;
    private final Set<String> selectedIds = new HashSet<>();
    private final OnSelectionChangedListener listener;

    public interface OnSelectionChangedListener {
        void onSelectionChanged(int count);
    }

    public LikedPhotoSelectionAdapter(List<Photo> photos, OnSelectionChangedListener listener) {
        this.photos = photos;
        this.listener = listener;
    }

    public void updatePhotos(List<Photo> newPhotos) {
        this.photos = newPhotos;
        notifyDataSetChanged();
    }

    public List<Photo> getSelectedPhotos() {
        List<Photo> selected = new ArrayList<>();
        for (Photo p : photos) {
            if (selectedIds.contains(p.getId())) selected.add(p);
        }
        return selected;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_liked_photo_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Photo photo = photos.get(position);
        boolean selected = selectedIds.contains(photo.getId());

        if (photo.getImageUrl() != null && !photo.getImageUrl().isEmpty()) {
            Glide.with(holder.ivPhoto.getContext())
                    .load(photo.getImageUrl())
                    .centerCrop()
                    .into(holder.ivPhoto);
        } else if (photo.getImageResId() != 0) {
            holder.ivPhoto.setImageResource(photo.getImageResId());
        }

        holder.ivCheckmark.setVisibility(selected ? View.VISIBLE : View.GONE);
        holder.viewOverlay.setVisibility(selected ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            String id = photo.getId();
            if (selectedIds.contains(id)) {
                selectedIds.remove(id);
            } else {
                selectedIds.add(id);
            }
            notifyItemChanged(position);
            listener.onSelectionChanged(selectedIds.size());
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        ImageView ivCheckmark;
        View viewOverlay;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            ivCheckmark = itemView.findViewById(R.id.ivCheckmark);
            viewOverlay = itemView.findViewById(R.id.viewOverlay);
        }
    }
}
