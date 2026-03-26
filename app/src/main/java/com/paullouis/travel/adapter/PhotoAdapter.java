package com.paullouis.travel.adapter;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
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
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private List<Photo> photoList;

    public PhotoAdapter(List<Photo> photoList) {
        this.photoList = photoList;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Photo photo = photoList.get(position);
        
        String authorName = "Voyageur " + photo.getUserId().replace("u", "");
        holder.tvUserName.setText(authorName);
        holder.tvAvatarInitials.setText(authorName.substring(0, 1).toUpperCase());
        holder.tvLocationHeader.setText(photo.getLocationName());
        holder.tvDate.setText("Janvier 2026"); // Mock static date
        holder.tvLikesCount.setText(String.valueOf(photo.getLikesCount()));
        holder.tvCommentsCount.setText(String.valueOf(photo.getLikesCount() / 10)); // Arbitrary for mock
        holder.tvLocationChip.setText(photo.getLocationName());

        // Inline description with bold author name
        String descText = authorName + " " + photo.getDescription();
        SpannableString spannable = new SpannableString(descText);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, authorName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.tvInlineDesc.setText(spannable);

        // Load image using Glide
        Glide.with(holder.itemView.getContext())
                .load(photo.getImageUrl())
                .centerCrop()
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.ivPhoto);
                
        holder.itemView.setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(v)
                .navigate(R.id.photoDetailFragment);
        });
    }

    @Override
    public int getItemCount() {
        return photoList == null ? 0 : photoList.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatarInitials, tvUserName, tvLocationHeader, tvDate, tvLikesCount, tvCommentsCount, tvLocationChip, tvInlineDesc;
        ImageView ivPhoto, ivLike, ivComment, ivShare, ivBookmark;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatarInitials = itemView.findViewById(R.id.tvAvatarInitials);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvLocationHeader = itemView.findViewById(R.id.tvLocationHeader);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvLikesCount = itemView.findViewById(R.id.tvLikesCount);
            tvCommentsCount = itemView.findViewById(R.id.tvCommentsCount);
            tvLocationChip = itemView.findViewById(R.id.tvLocationChip);
            tvInlineDesc = itemView.findViewById(R.id.tvInlineDesc);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivComment = itemView.findViewById(R.id.ivComment);
            ivShare = itemView.findViewById(R.id.ivShare);
            ivBookmark = itemView.findViewById(R.id.ivBookmark);
        }
    }
}
