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
    private Group currentGroup;
    private boolean isAdminOrOwner;
    private String groupId;

    public GroupMemberAdapter(List<GroupMember> members) {
        this.members = members;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setMembers(List<GroupMember> members) {
        this.members = members;
        notifyDataSetChanged();
    }

    public void setGroupAndPermissions(Group group) {
        this.currentGroup = group;
        this.isAdminOrOwner = group != null && (group.getRole() == Group.UserRole.ADMIN || group.getRole() == Group.UserRole.OWNER);
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

        // Avatar - use real profile picture or show initial
        String avatarUrl = member.getUser().getAvatarUrl();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                .load(avatarUrl)
                .circleCrop()
                .into(holder.ivAvatar);
        } else {
            String userName = member.getUser().getName();
            String initial = userName != null && !userName.isEmpty() ? userName.substring(0, 1).toUpperCase() : "?";
            holder.ivAvatar.setImageDrawable(createInitialDrawable(holder.itemView.getContext(), initial, member.getUser().getName()));
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

        if (isAdminOrOwner) {
            holder.btnOptions.setVisibility(View.VISIBLE);
            holder.btnOptions.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.getMenu().add(0, 1, 0, "Changer le rôle");
                popup.getMenu().add(0, 2, 1, "Retirer du groupe");

                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == 1) {
                        com.paullouis.travel.ChangeRoleDialogFragment dialog = com.paullouis.travel.ChangeRoleDialogFragment.newInstance(member.getUser().getName(), groupId);
                        dialog.show(((androidx.appcompat.app.AppCompatActivity)v.getContext()).getSupportFragmentManager(), "ChangeRoleDialog");
                        return true;
                    } else if (item.getItemId() == 2) {
                        com.paullouis.travel.RemoveMemberDialogFragment dialog = com.paullouis.travel.RemoveMemberDialogFragment.newInstance(member.getUser().getName(), groupId);
                        dialog.show(((androidx.appcompat.app.AppCompatActivity)v.getContext()).getSupportFragmentManager(), "RemoveMemberDialog");
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        } else {
            holder.btnOptions.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    private android.graphics.drawable.Drawable createInitialDrawable(android.content.Context context, String initial, String userName) {
        android.graphics.drawable.ShapeDrawable drawable = new android.graphics.drawable.ShapeDrawable(new android.graphics.drawable.shapes.OvalShape());
        int[] colors = {0xFF6366F1, 0xFF8B5CF6, 0xFFEC4899, 0xFFF59E0B, 0xFF10B981, 0xFF3B82F6};
        int colorIndex = (userName != null ? userName.hashCode() : 0) % colors.length;
        if (colorIndex < 0) colorIndex = -colorIndex;
        drawable.getPaint().setColor(colors[colorIndex]);

        android.graphics.drawable.LayerDrawable layerDrawable = new android.graphics.drawable.LayerDrawable(new android.graphics.drawable.Drawable[]{drawable});

        android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(200, 200, android.graphics.Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
        layerDrawable.setBounds(0, 0, 200, 200);
        layerDrawable.draw(canvas);

        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setColor(android.graphics.Color.WHITE);
        paint.setTextSize(80);
        paint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD));
        paint.setTextAlign(android.graphics.Paint.Align.CENTER);
        canvas.drawText(initial, 100, 130, paint);

        return new android.graphics.drawable.BitmapDrawable(context.getResources(), bitmap);
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
