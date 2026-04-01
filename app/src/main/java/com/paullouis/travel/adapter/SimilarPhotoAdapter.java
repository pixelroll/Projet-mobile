package com.paullouis.travel.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.paullouis.travel.PhotoDetailActivity;
import com.paullouis.travel.R;
import com.paullouis.travel.model.Photo;
import java.util.List;

public class SimilarPhotoAdapter extends RecyclerView.Adapter<SimilarPhotoAdapter.PhotoViewHolder> {

    private List<Photo> similarPhotos;

    public SimilarPhotoAdapter(List<Photo> similarPhotos) {
        this.similarPhotos = similarPhotos;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_similar_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Photo photo = similarPhotos.get(position);
        holder.tvTitle.setText(photo.getTitle());

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

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PhotoDetailActivity.class);
            intent.putExtra("photo", photo);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return similarPhotos.size();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        TextView tvTitle;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivSimilarPhoto);
            tvTitle = itemView.findViewById(R.id.tvSimilarPhotoTitle);
        }
    }
}
