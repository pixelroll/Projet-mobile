package com.paullouis.travel.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.paullouis.travel.R;
import com.paullouis.travel.model.Photo;
import java.util.List;
import android.content.Intent;
import com.paullouis.travel.PhotoDetailActivity;

public class ProfilePhotoAdapter extends RecyclerView.Adapter<ProfilePhotoAdapter.PhotoViewHolder> {

    private List<Photo> photos;

    public ProfilePhotoAdapter(List<Photo> photos) {
        this.photos = photos;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Photo photo = photos.get(position);
        Glide.with(holder.itemView.getContext())
                .load(photo.getImageUrl())
                .centerCrop()
                .into(holder.ivPhoto);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PhotoDetailActivity.class);
            intent.putExtra("photo", photo);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return photos == null ? 0 : photos.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
        }
    }
}
