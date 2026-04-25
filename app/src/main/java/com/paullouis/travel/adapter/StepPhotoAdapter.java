package com.paullouis.travel.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paullouis.travel.R;
import com.paullouis.travel.model.StepPhoto;

import java.util.List;

public class StepPhotoAdapter extends RecyclerView.Adapter<StepPhotoAdapter.ViewHolder> {

    private List<StepPhoto> photos;

    public StepPhotoAdapter(List<StepPhoto> photos) {
        this.photos = photos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_step_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StepPhoto photo = photos.get(position);
        holder.ivThumbnail.setImageResource(photo.getDrawableRes());
        holder.tvLabel.setText(photo.getLabel());
        holder.videoOverlay.setVisibility(photo.isVideo() ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            if (v.getContext() instanceof androidx.appcompat.app.AppCompatActivity) {
                androidx.appcompat.app.AppCompatActivity activity = (androidx.appcompat.app.AppCompatActivity) v.getContext();
                com.paullouis.travel.MediaViewerDialogFragment dialog = com.paullouis.travel.MediaViewerDialogFragment.newInstance(
                        photo.getDrawableRes(), photo.getLabel(), photo.isVideo()
                );
                dialog.show(activity.getSupportFragmentManager(), "media_viewer");
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos != null ? photos.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        FrameLayout videoOverlay;
        TextView tvLabel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            videoOverlay = itemView.findViewById(R.id.videoOverlay);
            tvLabel = itemView.findViewById(R.id.tvLabel);
        }
    }
}
