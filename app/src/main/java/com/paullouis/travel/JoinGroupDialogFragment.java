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
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.paullouis.travel.data.DataCallback;
import com.paullouis.travel.data.FirebaseRepository;
import com.paullouis.travel.model.Group;

public class JoinGroupDialogFragment extends DialogFragment {

    private EditText etJoinCode;
    private Button btnJoin;
    private TextView tvError;
    private ProgressBar progressBar;

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
        progressBar = view.findViewById(R.id.progressBar);

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
            setLoading(true);

            FirebaseRepository.getInstance().findGroupByCode(code, new DataCallback<Group>() {
                @Override
                public void onSuccess(Group group) {
                    FirebaseRepository.getInstance().joinGroup(group.getId(), new DataCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            if (!isAdded()) return;
                            setLoading(false);
                            Intent intent = new Intent(getActivity(), GroupFeedActivity.class);
                            intent.putExtra(GroupFeedActivity.EXTRA_GROUP_ID, group.getId());
                            intent.putExtra(GroupFeedActivity.EXTRA_GROUP_NAME, group.getName());
                            startActivity(intent);
                            dismiss();
                        }

                        @Override
                        public void onError(Exception e) {
                            if (!isAdded()) return;
                            setLoading(false);
                            tvError.setVisibility(View.VISIBLE);
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    if (!isAdded()) return;
                    setLoading(false);
                    tvError.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    private void setLoading(boolean loading) {
        btnJoin.setEnabled(!loading);
        if (progressBar != null) {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
