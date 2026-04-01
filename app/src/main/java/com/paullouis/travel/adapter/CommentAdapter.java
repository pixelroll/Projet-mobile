package com.paullouis.travel.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.paullouis.travel.R;
import com.paullouis.travel.model.Comment;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> commentList;

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
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
        holder.tvDate.setText(comment.getDate());
        holder.tvText.setText(comment.getText());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvInitial, tvAuthor, tvDate, tvText;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInitial = itemView.findViewById(R.id.tvCommentInitial);
            tvAuthor = itemView.findViewById(R.id.tvCommentAuthor);
            tvDate = itemView.findViewById(R.id.tvCommentDate);
            tvText = itemView.findViewById(R.id.tvCommentText);
        }
    }
}
