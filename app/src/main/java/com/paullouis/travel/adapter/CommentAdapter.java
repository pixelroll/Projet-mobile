package com.paullouis.travel.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.paullouis.travel.R;
import com.paullouis.travel.model.Comment;
import com.bumptech.glide.Glide;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.widget.ImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> commentList;

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public void addComment(Comment comment) {
        this.commentList.add(comment);
        notifyItemInserted(this.commentList.size() - 1);
    }

    public void updateComment(Comment comment) {
        // Find comment by timestamp or object reference
        for (int i = 0; i < commentList.size(); i++) {
            if (commentList.get(i).getTimestamp() == comment.getTimestamp()) {
                commentList.set(i, comment);
                notifyItemChanged(i);
                return;
            }
        }
    }

    public void removeComment(long timestamp) {
        for (int i = 0; i < commentList.size(); i++) {
            if (commentList.get(i).getTimestamp() == timestamp) {
                commentList.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.tvInitial.setText(comment.getUserInitial());
        holder.tvAuthor.setText(comment.getUserName());
        holder.tvDate.setText(formatRelativeTime(comment.getTimestamp()));
        holder.tvText.setText(comment.getText());

        if (comment.getUserAvatarUrl() != null && !comment.getUserAvatarUrl().isEmpty()) {
            holder.ivCommentAvatar.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(comment.getUserAvatarUrl())
                    .circleCrop()
                    .into(holder.ivCommentAvatar);
        } else {
            holder.ivCommentAvatar.setVisibility(View.GONE);
        }

        if (comment.isLoading()) {
            holder.itemView.setAlpha(0.6f);
            holder.pbCommentLoading.setVisibility(View.VISIBLE);
        } else {
            holder.itemView.setAlpha(1.0f);
            holder.pbCommentLoading.setVisibility(View.GONE);
        }
    }

    private static String formatRelativeTime(long timestamp) {
        if (timestamp <= 0) return "à l'instant";
        long diff = System.currentTimeMillis() - timestamp;
        if (diff < 60_000) return "à l'instant";
        if (diff < 3_600_000) {
            long minutes = diff / 60_000;
            return "il y a " + minutes + " min";
        }
        if (diff < 86_400_000) {
            long hours = diff / 3_600_000;
            return "il y a " + hours + " h";
        }
        long days = diff / 86_400_000;
        if (days < 7) return "il y a " + days + " j";
        return new SimpleDateFormat("d MMM yyyy, HH:mm", Locale.FRENCH).format(new Date(timestamp));
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvInitial, tvAuthor, tvDate, tvText;
        ImageView ivCommentAvatar;
        android.widget.ProgressBar pbCommentLoading;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInitial = itemView.findViewById(R.id.tvCommentInitial);
            tvAuthor = itemView.findViewById(R.id.tvCommentAuthor);
            tvDate = itemView.findViewById(R.id.tvCommentDate);
            tvText = itemView.findViewById(R.id.tvCommentText);
            ivCommentAvatar = itemView.findViewById(R.id.ivCommentAvatar);
            pbCommentLoading = itemView.findViewById(R.id.pbCommentLoading);
        }
    }
}
