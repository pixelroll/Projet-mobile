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
import android.content.Intent;
import com.paullouis.travel.PhotoDetailActivity;
import android.content.Intent;
import com.paullouis.travel.PhotoDetailActivity;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private List<Photo> photoList;

    public PhotoAdapter(List<Photo> photoList) {
        this.photoList = photoList;
    }

    public void setPhotos(List<Photo> photoList) {
        this.photoList = photoList;
        notifyDataSetChanged();
    }

    public void addPhoto(Photo photo) {
        this.photoList.add(0, photo);
        notifyItemInserted(0);
    }

    public void updatePhoto(Photo updatedPhoto) {
        for (int i = 0; i < photoList.size(); i++) {
            if (photoList.get(i).getId().equals(updatedPhoto.getId())) {
                photoList.set(i, updatedPhoto);
                notifyItemChanged(i);
                return;
            }
        }
    }

    public void removePhoto(String photoId) {
        for (int i = 0; i < photoList.size(); i++) {
            if (photoList.get(i).getId().equals(photoId)) {
                photoList.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
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
        
        String authorName = (photo.getAuthorName() != null && !photo.getAuthorName().isEmpty()) ? photo.getAuthorName() : "Voyageur " + photo.getUserId().replace("u", "");
        String initial = (photo.getAuthorInitial() != null && !photo.getAuthorInitial().isEmpty()) ? photo.getAuthorInitial() : authorName.substring(0, 1).toUpperCase();
        
        holder.tvUserName.setText(authorName);
        holder.tvAvatarInitials.setText(initial);
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

        // Group Badge (Disabled as per user request)
        holder.tvGroupBadge.setVisibility(View.GONE);

        // Load image using Glide
        Glide.with(holder.itemView.getContext())
                .load(photo.getImageUrl())
                .centerCrop()
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.ivPhoto);

        // Load author avatar using Glide
        if (photo.getAuthorAvatarUrl() != null && !photo.getAuthorAvatarUrl().isEmpty()) {
            holder.ivUserAvatar.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(photo.getAuthorAvatarUrl())
                    .circleCrop()
                    .into(holder.ivUserAvatar);
        } else {
            holder.ivUserAvatar.setVisibility(View.GONE);
        }

        // Show mic icon if there's an audio note
        if (photo.getAudioUrl() != null && !photo.getAudioUrl().isEmpty()) {
            holder.ivAudioMic.setVisibility(View.VISIBLE);
        } else {
            holder.ivAudioMic.setVisibility(View.GONE);
        }
                
        // Handle Loading State
        if (photo.isLoading()) {
            holder.pbLoading.setVisibility(View.VISIBLE);
            holder.vLoadingOverlay.setVisibility(View.VISIBLE);
            holder.itemView.setAlpha(0.7f);
        } else {
            holder.pbLoading.setVisibility(View.GONE);
            holder.vLoadingOverlay.setVisibility(View.GONE);
            holder.itemView.setAlpha(1.0f);
        }

        // Like button — optimistic with Firebase persistence
        updateLikeIcon(holder.ivLike, photo.isLiked());
        holder.ivLike.setOnClickListener(v -> {
            if (photo.isLoading()) return; // Prevent double-tap

            boolean wasLiked = photo.isLiked();
            int prevLikes = photo.getLikes();

            // Optimistic toggle
            boolean nowLiked = !wasLiked;
            int newLikes = nowLiked ? prevLikes + 1 : prevLikes - 1;
            photo.setLiked(nowLiked);
            photo.setLikes(newLikes);
            photo.setLoading(true); // Show loading
            holder.tvLikesCount.setText(String.valueOf(newLikes));
            updateLikeIcon(holder.ivLike, nowLiked);
            notifyItemChanged(position);

            // Persist to Firebase
            com.paullouis.travel.data.FirebaseRepository.getInstance().toggleLike(photo.getId(), nowLiked, new com.paullouis.travel.data.DataCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    photo.setLoading(false);
                    notifyItemChanged(position);
                    // Broadcast to other screens
                    com.paullouis.travel.data.EventBus.notifyPhotoUpdated(photo);
                }

                @Override
                public void onError(Exception e) {
                    // Rollback
                    photo.setLiked(wasLiked);
                    photo.setLikes(prevLikes);
                    photo.setLoading(false);
                    holder.tvLikesCount.setText(String.valueOf(prevLikes));
                    updateLikeIcon(holder.ivLike, wasLiked);
                    notifyItemChanged(position);
                    android.widget.Toast.makeText(holder.itemView.getContext(),
                        "Like failed, try again", android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        });
                
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PhotoDetailActivity.class);
            intent.putExtra("photo", photo);
            v.getContext().startActivity(intent);
        });
    }

    private void updateLikeIcon(ImageView ivLike, boolean liked) {
        if (liked) {
            ivLike.setImageResource(R.drawable.ic_heart_filled);
            ivLike.setColorFilter(androidx.core.content.ContextCompat.getColor(ivLike.getContext(), R.color.error));
        } else {
            ivLike.setImageResource(R.drawable.ic_favorite_border);
            ivLike.setColorFilter(androidx.core.content.ContextCompat.getColor(ivLike.getContext(), R.color.on_background));
        }
    }

    @Override
    public int getItemCount() {
        return photoList == null ? 0 : photoList.size();
    }

    public List<Photo> getPhotos() {
        return photoList;
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatarInitials, tvUserName, tvLocationHeader, tvDate, tvLikesCount, tvCommentsCount, tvLocationChip, tvInlineDesc, tvGroupBadge;
        ImageView ivPhoto, ivLike, ivComment, ivShare, ivBookmark, ivUserAvatar, ivAudioMic;
        android.widget.ProgressBar pbLoading;
        View vLoadingOverlay;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatarInitials = itemView.findViewById(R.id.tvAvatarInitials);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvGroupBadge = itemView.findViewById(R.id.tvGroupBadge);
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
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            ivAudioMic = itemView.findViewById(R.id.ivAudioMic);
            pbLoading = itemView.findViewById(R.id.pbLoading);
            vLoadingOverlay = itemView.findViewById(R.id.vLoadingOverlay);
        }
    }
}
