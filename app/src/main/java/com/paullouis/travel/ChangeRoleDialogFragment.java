package com.paullouis.travel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.paullouis.travel.model.Group;

public class ChangeRoleDialogFragment extends BottomSheetDialogFragment {

    private String memberName;

    public static ChangeRoleDialogFragment newInstance(String memberName) {
        ChangeRoleDialogFragment fragment = new ChangeRoleDialogFragment();
        Bundle args = new Bundle();
        args.putString("member_name", memberName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            memberName = getArguments().getString("member_name");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_change_role, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvName = view.findViewById(R.id.tvTargetMemberName);
        tvName.setText("Changer le rôle de " + memberName);

        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.btnConfirmRole).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Rôle mis à jour pour " + memberName, Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }

    @Override
    public int getTheme() {
        return R.style.CustomBottomSheetDialogTheme;
    }
}
