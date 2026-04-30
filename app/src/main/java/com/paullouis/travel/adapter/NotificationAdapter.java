package com.paullouis.travel.adapter;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.paullouis.travel.R;
import com.paullouis.travel.model.Notification;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notifications;

    public NotificationAdapter(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        // Content with bold user name
        String text = (notification.getUserName() != null ? "<b>" + notification.getUserName() + "</b> " : "") + notification.getContent();
        holder.tvContent.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
        holder.tvTime.setText(notification.getTime());
        holder.vUnreadDot.setVisibility(notification.isRead() ? View.GONE : View.VISIBLE);

        // Avatar vs Icon
        if (notification.getUserAvatar() != null) {
            holder.ivUserAvatar.setVisibility(View.VISIBLE);
            holder.flIconContainer.setVisibility(View.GONE);
            Glide.with(holder.itemView.getContext())
                    .load(notification.getUserAvatar())
                    .centerCrop()
                    .into(holder.ivUserAvatar);
        } else {
            holder.ivUserAvatar.setVisibility(View.GONE);
            holder.flIconContainer.setVisibility(View.VISIBLE);
            int iconRes = R.drawable.ic_notifications;
            int colorRes = R.color.muted_foreground;

            Notification.Type type = notification.getType();
            if (type != null) {
                switch (type) {
                    case ITINERARY:
                        iconRes = R.drawable.ic_location_on;
                        colorRes = R.color.primary;
                        break;
                    case PHOTO_PUBLISHED:
                        iconRes = R.drawable.ic_notifications;
                        colorRes = R.color.primary;
                        break;
                    case SUGGESTION:
                        iconRes = R.drawable.ic_notifications;
                        colorRes = R.color.secondary;
                        break;
                    default:
                        break;
                }
            }
            holder.ivTypeIcon.setImageResource(iconRes);
            holder.ivTypeIcon.setColorFilter(holder.itemView.getContext().getColor(colorRes));
        }

        // Photo Thumbnail
        if (notification.getPhotoUrl() != null) {
            holder.ivPhotoThumbnail.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(notification.getPhotoUrl())
                    .centerCrop()
                    .into(holder.ivPhotoThumbnail);
        } else {
            holder.ivPhotoThumbnail.setVisibility(View.GONE);
        }
        
        // Background for unread
        holder.itemView.setBackgroundColor(notification.isRead() ? 0 : 0x0D000000); // Very light overlay for unread
    }

    @Override
    public int getItemCount() {
        return notifications == null ? 0 : notifications.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserAvatar, ivTypeIcon, ivPhotoThumbnail;
        FrameLayout flIconContainer;
        TextView tvContent, tvTime;
        View vUnreadDot;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            ivTypeIcon = itemView.findViewById(R.id.ivTypeIcon);
            ivPhotoThumbnail = itemView.findViewById(R.id.ivPhotoThumbnail);
            flIconContainer = itemView.findViewById(R.id.flIconContainer);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTime = itemView.findViewById(R.id.tvTime);
            vUnreadDot = itemView.findViewById(R.id.vUnreadDot);
        }
    }
}
