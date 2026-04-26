package com.paullouis.travel.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.paullouis.travel.R;
import com.paullouis.travel.model.SearchNavigationOption;
import java.util.List;

public class SearchNavigationAdapter extends RecyclerView.Adapter<SearchNavigationAdapter.ViewHolder> {

    private final List<SearchNavigationOption> options;
    private final OnOptionClickListener listener;

    public interface OnOptionClickListener {
        void onOptionClick(SearchNavigationOption option);
    }

    public SearchNavigationAdapter(List<SearchNavigationOption> options, OnOptionClickListener listener) {
        this.options = options;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_navigation_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchNavigationOption option = options.get(position);
        holder.tvTitle.setText(option.getTitle());
        holder.tvSubtitle.setText(option.getSubtitle());
        holder.ivIcon.setImageResource(option.getIconResId());

        if (option.isSelected()) {
            holder.itemView.setBackgroundResource(R.drawable.bg_search_option_selected);
        } else {
            holder.itemView.setBackgroundResource(android.R.color.white);
        }

        holder.itemView.setOnClickListener(v -> {
            // Update selection locally
            for (SearchNavigationOption o : options) {
                o.setSelected(false);
            }
            option.setSelected(true);
            notifyDataSetChanged();
            
            if (listener != null) {
                listener.onOptionClick(option);
            }
        });
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle, tvSubtitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
        }
    }
}
