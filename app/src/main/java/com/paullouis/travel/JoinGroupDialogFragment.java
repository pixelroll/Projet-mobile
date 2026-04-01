package com.paullouis.travel;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.Group;

public class JoinGroupDialogFragment extends DialogFragment {

    private EditText etJoinCode;
    private Button btnJoin;
    private TextView tvError;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return inflater.inflate(R.layout.dialog_join_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etJoinCode = view.findViewById(R.id.etJoinCode);
        btnJoin = view.findViewById(R.id.btnJoin);
        tvError = view.findViewById(R.id.tvError);

        view.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dismiss());

        etJoinCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnJoin.setEnabled(s.length() > 0);
                tvError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String filtered = s.toString().toUpperCase();
                if (!s.toString().equals(filtered)) {
                    etJoinCode.setText(filtered);
                    etJoinCode.setSelection(filtered.length());
                }
            }
        });

        btnJoin.setOnClickListener(v -> {
            String code = etJoinCode.getText().toString().trim();
            Group group = MockDataProvider.findGroupByCode(code);
            
            if (group != null) {
                Toast.makeText(getActivity(), "Groupe " + group.getName() + " rejoint !", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), GroupDetailActivity.class);
                intent.putExtra("GROUP_ID", group.getId());
                startActivity(intent);
                dismiss();
            } else {
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
