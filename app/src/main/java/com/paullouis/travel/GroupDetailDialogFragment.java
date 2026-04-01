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
import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.Group;

public class GroupDetailDialogFragment extends DialogFragment {

    private static final String ARG_GROUP_ID = "group_id";

    public static GroupDetailDialogFragment newInstance(String groupId) {
        GroupDetailDialogFragment fragment = new GroupDetailDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
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
        Group group = MockDataProvider.getGroupById(groupId);

        if (group == null) {
            dismiss();
            return;
        }

        // Views
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

        // Bind data
        Glide.with(this).load(group.getCoverImage()).placeholder(R.drawable.bg_group_placeholder).into(ivBanner);
        tvGroupName.setText(group.getName());
        tvDescription.setText(group.getDescription());
        tvMembersCount.setText(String.valueOf(group.getMembersCount()));
        tvPhotosCount.setText(String.valueOf(group.getPhotosCount()));
        tvTypeTitle.setText(group.isPrivate() ? "Prive" : "Public");

        // Role Logic
        Group.UserRole role = group.getRole();
        if (role == Group.UserRole.ADMIN) {
            clInvitationCode.setVisibility(View.VISIBLE);
            btnManage.setVisibility(View.VISIBLE);
            tvInviteCode.setText(group.getCode());
            ivGroupTypeIcon.setImageResource(R.drawable.ic_crown);
        } else if (role == Group.UserRole.MEMBER_WITH_CODE) {
            clInvitationCode.setVisibility(View.VISIBLE);
            btnManage.setVisibility(View.GONE);
            tvInviteCode.setText(group.getCode());
            ivGroupTypeIcon.setImageResource(group.isPrivate() ? R.drawable.ic_lock : R.drawable.ic_globe);
        } else {
            clInvitationCode.setVisibility(View.GONE);
            btnManage.setVisibility(View.GONE);
            ivGroupTypeIcon.setImageResource(group.isPrivate() ? R.drawable.ic_lock : R.drawable.ic_globe);
        }

        // Members List (Simulated)
        setupMembersList(llMembersList, group.getMembersCount());

        // Listeners
        view.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());
        
        view.findViewById(R.id.btnCopy).setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Group Code", group.getCode());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Code copie !", Toast.LENGTH_SHORT).show();
        });

        btnManage.setOnClickListener(v -> {
            // GroupManageActivity stub
            Toast.makeText(getContext(), "Ouverture de la gestion : " + group.getName(), Toast.LENGTH_SHORT).show();
            dismiss();
        });

        btnViewPhotos.setOnClickListener(v -> {
            // PhotoActivity stub
            Toast.makeText(getContext(), "Ouverture des photos : " + group.getName(), Toast.LENGTH_SHORT).show();
            dismiss();
        });
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

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
