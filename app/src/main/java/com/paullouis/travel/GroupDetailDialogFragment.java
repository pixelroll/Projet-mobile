package com.paullouis.travel;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import com.bumptech.glide.Glide;
import com.paullouis.travel.data.DataCallback;
import com.paullouis.travel.data.FirebaseRepository;
import com.paullouis.travel.model.Group;

public class GroupDetailDialogFragment extends DialogFragment {

    public interface OnGroupJoinedListener {
        void onGroupJoined(String groupId);
    }

    private static final String ARG_GROUP_ID = "group_id";
    private static final String ARG_FROM_DISCOVER = "from_discover";

    private OnGroupJoinedListener joinListener;

    public static GroupDetailDialogFragment newInstance(String groupId, boolean fromDiscover) {
        GroupDetailDialogFragment fragment = new GroupDetailDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP_ID, groupId);
        args.putBoolean(ARG_FROM_DISCOVER, fromDiscover);
        fragment.setArguments(args);
        return fragment;
    }

    public static GroupDetailDialogFragment newInstance(String groupId) {
        return newInstance(groupId, false);
    }

    public void setOnGroupJoinedListener(OnGroupJoinedListener listener) {
        this.joinListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return inflater.inflate(R.layout.dialog_group_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String groupId = getArguments() != null ? getArguments().getString(ARG_GROUP_ID) : null;
        boolean fromDiscover = getArguments() != null && getArguments().getBoolean(ARG_FROM_DISCOVER, false);

        if (groupId == null) {
            dismiss();
            return;
        }

        view.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());

        FirebaseRepository.getInstance().getGroupById(groupId, new DataCallback<Group>() {
            @Override
            public void onSuccess(Group group) {
                if (!isAdded() || getView() == null) return;
                bindGroup(view, group, fromDiscover);
            }

            @Override
            public void onError(Exception e) {
                if (!isAdded()) return;
                dismiss();
            }
        });
    }

    private void bindGroup(View view, Group group, boolean fromDiscover) {
        ImageView ivBanner = view.findViewById(R.id.ivBanner);
        TextView tvGroupName = view.findViewById(R.id.tvGroupName);
        ImageView ivGroupTypeIcon = view.findViewById(R.id.ivGroupTypeIcon);
        TextView tvDescription = view.findViewById(R.id.tvDescription);
        TextView tvMembersCount = view.findViewById(R.id.tvMembersCount);
        TextView tvPhotosCount = view.findViewById(R.id.tvPhotosCount);
        TextView tvTypeTitle = view.findViewById(R.id.tvTypeTitle);
        ConstraintLayout clInvitationCode = view.findViewById(R.id.clInvitationCode);
        TextView tvInviteCode = view.findViewById(R.id.tvInviteCode);
        LinearLayout llMembersList = view.findViewById(R.id.llMembersList);
        Button btnManage = view.findViewById(R.id.btnManage);
        Button btnViewPhotos = view.findViewById(R.id.btnViewPhotos);
        Button btnJoin = view.findViewById(R.id.btnJoin);

        if (group.getCoverImage() != null && !group.getCoverImage().isEmpty()) {
            Glide.with(this).load(group.getCoverImage()).into(ivBanner);
        } else {
            String groupName = group.getName();
            String initial = groupName != null && !groupName.isEmpty() ? groupName.substring(0, 1).toUpperCase() : "G";
            ivBanner.setImageDrawable(createGroupInitialDrawable(initial, groupName));
        }
        tvGroupName.setText(group.getName());
        tvDescription.setText(group.getDescription());
        tvMembersCount.setText(String.valueOf(group.getMembersCount()));
        tvPhotosCount.setText(String.valueOf(group.getPhotosCount()));
        tvTypeTitle.setText(group.isPrivate() ? "Privé" : "Public");
        ivGroupTypeIcon.setImageResource(group.isPrivate() ? R.drawable.ic_lock : R.drawable.ic_globe);

        if (fromDiscover) {
            clInvitationCode.setVisibility(View.GONE);
            btnManage.setVisibility(View.GONE);
            btnViewPhotos.setVisibility(View.GONE);
            btnJoin.setVisibility(View.VISIBLE);

            btnJoin.setOnClickListener(v -> {
                btnJoin.setEnabled(false);
                btnJoin.setText("Rejoindre...");
                FirebaseRepository.getInstance().joinGroup(group.getId(), new DataCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(), "Vous avez rejoint " + group.getName() + " !", Toast.LENGTH_SHORT).show();
                        if (joinListener != null) joinListener.onGroupJoined(group.getId());
                        dismiss();
                    }

                    @Override
                    public void onError(Exception e) {
                        if (!isAdded()) return;
                        btnJoin.setEnabled(true);
                        btnJoin.setText("Rejoindre");
                        Toast.makeText(getContext(), "Impossible de rejoindre le groupe", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        } else {
            Group.UserRole role = group.getRole();
            if (role == Group.UserRole.ADMIN || role == Group.UserRole.OWNER) {
                clInvitationCode.setVisibility(View.VISIBLE);
                btnManage.setVisibility(View.VISIBLE);
                tvInviteCode.setText(group.getCode());
            } else if (role == Group.UserRole.MEMBER_WITH_CODE) {
                clInvitationCode.setVisibility(View.VISIBLE);
                btnManage.setVisibility(View.GONE);
                tvInviteCode.setText(group.getCode());
            } else {
                clInvitationCode.setVisibility(View.GONE);
                btnManage.setVisibility(View.GONE);
            }

            view.findViewById(R.id.btnCopy).setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Group Code", group.getCode());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Code copié !", Toast.LENGTH_SHORT).show();
            });

            btnManage.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), GroupAdminActivity.class);
                intent.putExtra(GroupAdminActivity.EXTRA_GROUP_ID, group.getId());
                startActivity(intent);
                dismiss();
            });

            btnViewPhotos.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), GroupFeedActivity.class);
                intent.putExtra(GroupFeedActivity.EXTRA_GROUP_ID, group.getId());
                intent.putExtra(GroupFeedActivity.EXTRA_GROUP_NAME, group.getName());
                startActivity(intent);
                dismiss();
            });
        }

        setupMembersList(llMembersList, group.getMembersCount());
    }

    private void setupMembersList(LinearLayout container, int count) {
        container.removeAllViews();
        int maxDisplay = 6;
        String[] initials = {"A", "B", "C", "D", "E", "F", "G", "H"};
        int toDisplay = Math.min(count, maxDisplay);
        for (int i = 0; i < toDisplay; i++) {
            container.addView(createMemberAvatar(initials[i % initials.length]));
        }
        if (count > maxDisplay) {
            container.addView(createMemberAvatar("+" + (count - maxDisplay)));
        }
    }

    private View createMemberAvatar(String text) {
        TextView tv = new TextView(getContext());
        int size = (int) (36 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.setMargins(0, 0, (int) (4 * getResources().getDisplayMetrics().density), 0);
        tv.setLayoutParams(params);
        tv.setGravity(Gravity.CENTER);
        tv.setText(text);
        tv.setTextSize(12);
        tv.setTextColor(Color.parseColor("#0891B2"));
        tv.setBackgroundResource(R.drawable.circle_avatar_bg);
        return tv;
    }

    private android.graphics.drawable.Drawable createGroupInitialDrawable(String initial, String groupName) {
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

        return new android.graphics.drawable.BitmapDrawable(getResources(), bitmap);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
