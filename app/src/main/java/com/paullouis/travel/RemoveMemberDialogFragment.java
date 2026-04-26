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

public class RemoveMemberDialogFragment extends BottomSheetDialogFragment {

    private String memberName;

    public static RemoveMemberDialogFragment newInstance(String memberName) {
        RemoveMemberDialogFragment fragment = new RemoveMemberDialogFragment();
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
        return inflater.inflate(R.layout.dialog_remove_member, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvWarning = view.findViewById(R.id.tvRemoveWarning);
        tvWarning.setText("Êtes-vous sûr de vouloir retirer " + memberName + " du groupe ? Il ne pourra plus voir les photos privées.");

        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.btnConfirmRemove).setOnClickListener(v -> {
            Toast.makeText(getContext(), memberName + " a été retiré du groupe", Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }

    @Override
    public int getTheme() {
        return R.style.CustomBottomSheetDialogTheme;
    }
}
