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
import android.text.format.DateUtils;
import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.User;
import android.content.Intent;
import com.paullouis.travel.PhotoDetailActivity;

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
        
        User user = MockDataProvider.getUserById(photo.getUserId());
        String authorName = (user != null) ? user.getName() : "Voyageur " + photo.getUserId().replace("u", "");
        
        holder.tvUserName.setText(authorName);
        holder.tvAvatarInitials.setText(authorName.substring(0, 1).toUpperCase());
        holder.tvLocationHeader.setText(photo.getLocationName());
        
        // Relative time formatting
        CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(
                photo.getTimestamp(), 
                System.currentTimeMillis(), 
                DateUtils.MINUTE_IN_MILLIS);
        holder.tvDate.setText(relativeTime);
        
        holder.tvLikesCount.setText(String.valueOf(photo.getLikes()));
        holder.tvCommentsCount.setText(String.valueOf(photo.getComments()));
        holder.tvLocationChip.setText(photo.getLocationName());

        // Inline description with bold author name and bold title
        String title = (photo.getTitle() != null) ? photo.getTitle() : "";
        String desc = (photo.getDescription() != null) ? photo.getDescription() : "";
        
        String fullText = authorName + " " + title + " - " + desc;
        SpannableString spannable = new SpannableString(fullText);
        
        // Bold Author
        spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, authorName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        // Bold Title
        if (!title.isEmpty()) {
            int titleStart = authorName.length() + 1;
            int titleEnd = titleStart + title.length();
            spannable.setSpan(new StyleSpan(Typeface.BOLD), titleStart, titleEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        
        holder.tvInlineDesc.setText(spannable);

        // Load image using Glide
        Glide.with(holder.itemView.getContext())
                .load(photo.getImageUrl())
                .centerCrop()
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.ivPhoto);
                
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PhotoDetailActivity.class);
            intent.putExtra("photo", photo);
            v.getContext().startActivity(intent);
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
