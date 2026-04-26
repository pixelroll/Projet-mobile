package com.paullouis.travel;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.paullouis.travel.util.ClipboardHelper;

public class InviteMemberDialogFragment extends DialogFragment {

    private static final String ARG_CODE = "code";
    private String code;

    public static InviteMemberDialogFragment newInstance(String code) {
        InviteMemberDialogFragment fragment = new InviteMemberDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CODE, code);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            code = getArguments().getString(ARG_CODE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_invite_member, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvCode = view.findViewById(R.id.tvInviteCode);
        TextView tvLink = view.findViewById(R.id.tvInviteLink);
        
        tvCode.setText(code);
        String link = "traveling.app/groups/join/" + code;
        tvLink.setText(link);

        view.findViewById(R.id.btnCopyCode).setOnClickListener(v -> {
            ClipboardHelper.copyToClipboard(getContext(), code, "Code");
        });

        view.findViewById(R.id.btnCopyLink).setOnClickListener(v -> {
            ClipboardHelper.copyToClipboard(getContext(), link, "Lien");
        });

        view.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.btnXClose).setOnClickListener(v -> dismiss());
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}
