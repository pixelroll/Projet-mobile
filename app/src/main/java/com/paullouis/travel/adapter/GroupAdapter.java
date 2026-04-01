package com.paullouis.travel.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import androidx.fragment.app.FragmentManager;
import com.paullouis.travel.GroupDetailDialogFragment;
import com.paullouis.travel.R;
import com.paullouis.travel.model.Group;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<Group> groups;
    private FragmentManager fragmentManager;

    public GroupAdapter(List<Group> groups, FragmentManager fragmentManager) {
        this.groups = groups;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groups.get(position);

        holder.tvGroupName.setText(group.getName());
        holder.tvDescription.setText(group.getDescription());
        holder.tvMembers.setText(String.valueOf(group.getMembersCount()));
        holder.tvPhotos.setText(String.valueOf(group.getPhotosCount()));
        
        holder.ivCrown.setVisibility(group.getRole() == Group.UserRole.ADMIN ? View.VISIBLE : View.GONE);

        // Badge
        if (group.isPrivate()) {
            holder.badgeContainer.setBackgroundResource(R.drawable.bg_badge_gray);
            holder.ivBadgeIcon.setImageResource(R.drawable.ic_lock);
            holder.ivBadgeIcon.setColorFilter(holder.itemView.getContext().getColor(R.color.muted_foreground));
            holder.tvBadgeText.setText("Privé");
            holder.tvBadgeText.setTextColor(holder.itemView.getContext().getColor(R.color.muted_foreground));
        } else {
            holder.badgeContainer.setBackgroundResource(R.drawable.bg_badge_teal);
            holder.ivBadgeIcon.setImageResource(R.drawable.ic_globe);
            holder.ivBadgeIcon.setColorFilter(holder.itemView.getContext().getColor(R.color.primary));
            holder.tvBadgeText.setText("Public");
            holder.tvBadgeText.setTextColor(holder.itemView.getContext().getColor(R.color.primary));
        }

        Glide.with(holder.itemView.getContext())
                .load(group.getCoverImage())
                .centerCrop()
                .placeholder(R.drawable.bg_gray_rounded)
                .into(holder.ivCover);

        holder.itemView.setOnClickListener(v -> {
            GroupDetailDialogFragment dialog = GroupDetailDialogFragment.newInstance(group.getId());
            dialog.show(fragmentManager, "GroupDetailDialog");
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover, ivCrown, ivBadgeIcon;
        TextView tvGroupName, tvDescription, tvMembers, tvPhotos, tvBadgeText;
        FrameLayout badgeContainer;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.ivGroupCover);
            ivCrown = itemView.findViewById(R.id.ivOwnerCrown);
            ivBadgeIcon = itemView.findViewById(R.id.ivBadgeIcon);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            tvDescription = itemView.findViewById(R.id.tvGroupDescription);
            tvMembers = itemView.findViewById(R.id.tvMembersCount);
            tvPhotos = itemView.findViewById(R.id.tvPhotosCount);
            tvBadgeText = itemView.findViewById(R.id.tvBadgeText);
            badgeContainer = itemView.findViewById(R.id.badgeContainer);
        }
    }
}
