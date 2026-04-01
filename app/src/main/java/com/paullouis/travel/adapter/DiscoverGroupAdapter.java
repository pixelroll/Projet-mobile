package com.paullouis.travel.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.paullouis.travel.GroupDetailDialogFragment;
import com.paullouis.travel.R;
import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.Group;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class DiscoverGroupAdapter extends RecyclerView.Adapter<DiscoverGroupAdapter.ViewHolder> {

    private List<Group> allGroups;
    private List<Group> filteredGroups;
    private FragmentManager fragmentManager;

    public DiscoverGroupAdapter(List<Group> groups, FragmentManager fragmentManager) {
        this.allGroups = new ArrayList<>(groups);
        this.filteredGroups = new ArrayList<>(groups);
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_discover, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group group = filteredGroups.get(position);

        holder.tvGroupName.setText(group.getName());
        holder.tvDescription.setText(group.getDescription());
        holder.tvMembers.setText(group.getMembersCount() + " membres");
        holder.tvPhotos.setText(group.getPhotosCount() + " photos");

        Glide.with(holder.itemView.getContext())
                .load(group.getCoverImage())
                .placeholder(R.drawable.bg_group_placeholder)
                .into(holder.ivBanner);

        updateActionButton(holder.btnAction, group);

        holder.btnAction.setOnClickListener(v -> {
            if (!group.isJoined()) {
                MockDataProvider.joinGroup(group.getId());
                updateActionButton(holder.btnAction, group);
                holder.tvMembers.setText(group.getMembersCount() + " membres");
                Toast.makeText(holder.itemView.getContext(), "Vous avez rejoint " + group.getName() + " !", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener(v -> {
            GroupDetailDialogFragment dialog = GroupDetailDialogFragment.newInstance(group.getId());
            dialog.show(fragmentManager, "GroupDetailDialog");
        });
    }

    private void updateActionButton(Button btn, Group group) {
        MaterialButton mBtn = (MaterialButton) btn;
        if (group.isJoined()) {
            mBtn.setText("Rejoint");
            mBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(btn.getContext().getColor(android.R.color.transparent)));
            mBtn.setTextColor(btn.getContext().getColor(R.color.muted_foreground));
            mBtn.setIconResource(R.drawable.ic_check);
            mBtn.setIconTint(android.content.res.ColorStateList.valueOf(btn.getContext().getColor(R.color.muted_foreground)));
            mBtn.setAlpha(0.7f);
            mBtn.setEnabled(false);
            // Manual border
            android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
            gd.setCornerRadius(18 * btn.getContext().getResources().getDisplayMetrics().density);
            gd.setStroke((int)(1 * btn.getContext().getResources().getDisplayMetrics().density), 0xFFBDBDBD);
            mBtn.setBackground(gd);
        } else {
            mBtn.setText("Rejoindre");
            mBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(btn.getContext().getColor(R.color.primary)));
            mBtn.setTextColor(btn.getContext().getColor(R.color.white));
            mBtn.setIconResource(R.drawable.ic_user_plus);
            mBtn.setIconTint(android.content.res.ColorStateList.valueOf(btn.getContext().getColor(R.color.white)));
            mBtn.setAlpha(1.0f);
            mBtn.setEnabled(true);
            mBtn.setBackgroundResource(R.drawable.btn_turquoise_rounded);
            mBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(btn.getContext().getColor(R.color.primary)));
        }
    }

    public void filter(String query) {
        filteredGroups.clear();
        if (query.isEmpty()) {
            filteredGroups.addAll(allGroups);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (Group group : allGroups) {
                if (group.getName().toLowerCase().contains(lowerCaseQuery) || 
                    group.getDescription().toLowerCase().contains(lowerCaseQuery)) {
                    filteredGroups.add(group);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return filteredGroups.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBanner;
        TextView tvGroupName, tvDescription, tvMembers, tvPhotos;
        Button btnAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBanner = itemView.findViewById(R.id.ivBanner);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvMembers = itemView.findViewById(R.id.tvMembersCount);
            tvPhotos = itemView.findViewById(R.id.tvPhotosCount);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }
}
