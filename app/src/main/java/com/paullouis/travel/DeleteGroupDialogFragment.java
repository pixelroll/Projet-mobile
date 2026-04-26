package com.paullouis.travel;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

public class DeleteGroupDialogFragment extends BottomSheetDialogFragment {

    private String groupName;

    public static DeleteGroupDialogFragment newInstance(String groupName) {
        DeleteGroupDialogFragment fragment = new DeleteGroupDialogFragment();
        Bundle args = new Bundle();
        args.putString("group_name", groupName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupName = getArguments().getString("group_name");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_delete_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvInstruction = view.findViewById(R.id.tvInstruction);
        tvInstruction.setText("Veuillez taper '" + groupName + "' pour confirmer :");

        EditText etConfirm = view.findViewById(R.id.etConfirmName);
        MaterialButton btnDelete = view.findViewById(R.id.btnConfirmDelete);

        etConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnDelete.setEnabled(s.toString().equals(groupName));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dismiss());
        btnDelete.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Groupe '" + groupName + "' supprimé", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            dismiss();
        });
    }

    @Override
    public int getTheme() {
        return R.style.CustomBottomSheetDialogTheme;
    }
}
