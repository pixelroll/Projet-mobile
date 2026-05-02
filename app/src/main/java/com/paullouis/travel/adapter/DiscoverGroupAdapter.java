package com.paullouis.travel.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.paullouis.travel.GroupDetailDialogFragment;
import com.paullouis.travel.R;
import com.paullouis.travel.model.Group;
import java.util.ArrayList;
import java.util.List;

public class DiscoverGroupAdapter extends RecyclerView.Adapter<DiscoverGroupAdapter.ViewHolder> {

    public interface OnGroupJoinedCallback {
        void onGroupJoined(String groupId);
    }

    private List<Group> allGroups;
    private List<Group> filteredGroups;
    private final FragmentManager fragmentManager;
    private final OnGroupJoinedCallback joinCallback;

    public DiscoverGroupAdapter(List<Group> groups, FragmentManager fragmentManager, OnGroupJoinedCallback joinCallback) {
        this.allGroups = new ArrayList<>(groups);
        this.filteredGroups = new ArrayList<>(groups);
        this.fragmentManager = fragmentManager;
        this.joinCallback = joinCallback;
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

        if (group.getCoverImage() != null && !group.getCoverImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(group.getCoverImage())
                    .into(holder.ivBanner);
        } else {
            String groupName = group.getName();
            String initial = groupName != null && !groupName.isEmpty() ? groupName.substring(0, 1).toUpperCase() : "G";
            holder.ivBanner.setImageDrawable(createGroupInitialDrawable(holder.itemView.getContext(), initial, groupName));
        }

        View.OnClickListener openDialog = v -> {
            GroupDetailDialogFragment dialog = GroupDetailDialogFragment.newInstance(group.getId(), true);
            dialog.setOnGroupJoinedListener(groupId -> {
                // Remove from both lists so discover no longer shows this group
                allGroups.remove(group);
                int filteredPos = filteredGroups.indexOf(group);
                if (filteredPos >= 0) {
                    filteredGroups.remove(filteredPos);
                    notifyItemRemoved(filteredPos);
                }
                if (joinCallback != null) joinCallback.onGroupJoined(groupId);
            });
            dialog.show(fragmentManager, "GroupDetailDialog");
        };
        holder.itemView.setOnClickListener(openDialog);
        if (holder.btnAction != null) holder.btnAction.setOnClickListener(openDialog);
    }

    public void setGroups(List<Group> groups) {
        this.allGroups = new ArrayList<>(groups);
        this.filteredGroups = new ArrayList<>(groups);
        notifyDataSetChanged();
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

    private android.graphics.drawable.Drawable createGroupInitialDrawable(android.content.Context context, String initial, String groupName) {
        int[] colors = {0xFF6366F1, 0xFF8B5CF6, 0xFFEC4899, 0xFFF59E0B, 0xFF10B981, 0xFF3B82F6};
        int colorIndex = (groupName != null ? groupName.hashCode() : 0) % colors.length;
        if (colorIndex < 0) colorIndex = -colorIndex;

        android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(300, 150, android.graphics.Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
        canvas.drawColor(colors[colorIndex]);

        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setColor(android.graphics.Color.WHITE);
        paint.setTextSize(100);
        paint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD));
        paint.setTextAlign(android.graphics.Paint.Align.CENTER);
        canvas.drawText(initial, 150, 100, paint);

        return new android.graphics.drawable.BitmapDrawable(context.getResources(), bitmap);
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
