package com.paullouis.travel.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.paullouis.travel.R;
import com.paullouis.travel.model.Group;
import com.paullouis.travel.model.GroupMember;
import java.util.List;

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.MemberViewHolder> {

    private List<GroupMember> members;

    public GroupMemberAdapter(List<GroupMember> members) {
        this.members = members;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        GroupMember member = members.get(position);
        holder.tvName.setText(member.getUser().getName());
        holder.tvStats.setText(member.getPhotosCount() + " photos • Il y a " + member.getLastActivity());
        
        // Avatar
        if (member.getUser().getAvatarUrl() != null) {
            Glide.with(holder.itemView.getContext())
                .load(member.getUser().getAvatarUrl())
                .placeholder(R.drawable.profile_sophie)
                .into(holder.ivAvatar);
        } else {
            holder.ivAvatar.setImageResource(R.drawable.profile_sophie);
        }

        // Role Badge Styling
        int bgRes;
        int textColor;
        int iconRes;
        String roleName;

        switch (member.getRole()) {
            case OWNER:
                bgRes = R.drawable.bg_badge_gold;
                textColor = Color.parseColor("#92400E");
                iconRes = R.drawable.ic_crown;
                roleName = "Propriétaire";
                break;
            case ADMIN:
                bgRes = R.drawable.bg_badge_blue;
                textColor = Color.parseColor("#1E40AF");
                iconRes = R.drawable.ic_shield;
                roleName = "Admin";
                break;
            case MODERATOR:
                bgRes = R.drawable.bg_badge_purple;
                textColor = Color.parseColor("#6B21A8");
                iconRes = R.drawable.ic_shield;
                roleName = "Modérateur";
                break;
            default:
                bgRes = R.drawable.bg_badge_gray;
                textColor = Color.parseColor("#475569");
                iconRes = R.drawable.ic_user;
                roleName = "Membre";
                break;
        }

        holder.tvRoleBadge.setText(roleName);
        holder.tvRoleBadge.setBackgroundResource(bgRes);
        holder.tvRoleBadge.setTextColor(textColor);
        holder.tvRoleBadge.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0);
        holder.tvRoleBadge.setCompoundDrawableTintList(android.content.res.ColorStateList.valueOf(textColor));

        holder.btnOptions.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.getMenu().add(0, 1, 0, "Changer le rôle");
            popup.getMenu().add(0, 2, 1, "Retirer du groupe");
            
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 1) {
                    com.paullouis.travel.ChangeRoleDialogFragment dialog = com.paullouis.travel.ChangeRoleDialogFragment.newInstance(member.getUser().getName());
                    dialog.show(((androidx.appcompat.app.AppCompatActivity)v.getContext()).getSupportFragmentManager(), "ChangeRoleDialog");
                    return true;
                } else if (item.getItemId() == 2) {
                    com.paullouis.travel.RemoveMemberDialogFragment dialog = com.paullouis.travel.RemoveMemberDialogFragment.newInstance(member.getUser().getName());
                    dialog.show(((androidx.appcompat.app.AppCompatActivity)v.getContext()).getSupportFragmentManager(), "RemoveMemberDialog");
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName, tvStats, tvRoleBadge;
        ImageButton btnOptions;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivMemberAvatar);
            tvName = itemView.findViewById(R.id.tvMemberName);
            tvStats = itemView.findViewById(R.id.tvMemberStats);
            tvRoleBadge = itemView.findViewById(R.id.tvRoleBadge);
            btnOptions = itemView.findViewById(R.id.btnMemberOptions);
        }
    }
}
