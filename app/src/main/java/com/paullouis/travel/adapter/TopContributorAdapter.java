package com.paullouis.travel.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.paullouis.travel.R;
import com.paullouis.travel.model.Group;
import com.paullouis.travel.model.GroupMember;
import java.util.List;

public class TopContributorAdapter extends RecyclerView.Adapter<TopContributorAdapter.ContributorViewHolder> {

    private List<GroupMember> contributors;

    public TopContributorAdapter(List<GroupMember> contributors) {
        this.contributors = contributors;
    }

    @NonNull
    @Override
    public ContributorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_contributor, parent, false);
        return new ContributorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContributorViewHolder holder, int position) {
        GroupMember member = contributors.get(position);
        holder.tvRank.setText("#" + (position + 1));
        holder.tvName.setText(member.getUser().getName());
        holder.tvPhotoCount.setText(member.getPhotosCount() + " photos");

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
            holder.ivAvatar.setImageDrawable(createInitialDrawable(holder.itemView.getContext(), initial, userName));
        }

        // Role Badge Styling (same as GroupMemberAdapter)
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
    }

    @Override
    public int getItemCount() {
        return contributors.size();
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

    static class ContributorViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvName, tvPhotoCount, tvRoleBadge;
        ImageView ivAvatar;

        public ContributorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhotoCount = itemView.findViewById(R.id.tvPhotoCount);
            tvRoleBadge = itemView.findViewById(R.id.tvRoleBadge);
        }
    }
}
