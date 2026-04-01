package com.paullouis.travel;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.Group;
import java.util.Random;

public class CreateGroupDialogFragment extends DialogFragment {

    public interface OnGroupCreatedListener {
        void onGroupCreated();
    }

    private EditText etName, etDescription;
    private TextView tvErrorName;
    private LinearLayout llPublic, llPrivate;
    private ImageView ivPublic, ivPrivate;
    private boolean isPrivate = false;
    private OnGroupCreatedListener listener;

    public void setOnGroupCreatedListener(OnGroupCreatedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return inflater.inflate(R.layout.dialog_create_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etName = view.findViewById(R.id.etGroupName);
        etDescription = view.findViewById(R.id.etGroupDescription);
        tvErrorName = view.findViewById(R.id.tvErrorName);
        llPublic = view.findViewById(R.id.llOptionPublic);
        llPrivate = view.findViewById(R.id.llOptionPrivate);
        ivPublic = view.findViewById(R.id.ivIconPublic);
        ivPrivate = view.findViewById(R.id.ivIconPrivate);

        view.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dismiss());

        llPublic.setOnClickListener(v -> updateVisibility(false));
        llPrivate.setOnClickListener(v -> updateVisibility(true));

        view.findViewById(R.id.btnCreate).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                tvErrorName.setVisibility(View.VISIBLE);
                return;
            }

            String desc = etDescription.getText().toString().trim();
            String code = null;
            if (isPrivate) {
                code = generateRandomCode();
            }

            Group newGroup = new Group(
                String.valueOf(System.currentTimeMillis()),
                name,
                desc.isEmpty() ? "Pas de description" : desc,
                1, 0, isPrivate, true, true,
                "https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=400",
                code
            );

            MockDataProvider.addGroup(newGroup);

            String message = "Groupe cree !";
            if (code != null) {
                message += " \nCode d'invitation : " + code;
            }
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

            if (listener != null) {
                listener.onGroupCreated();
            }
            dismiss();
        });
    }

    private void updateVisibility(boolean isPrivate) {
        this.isPrivate = isPrivate;
        int activeBg = R.drawable.bg_radio_option_active;
        int inactiveBg = R.drawable.bg_radio_option_inactive;
        int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary);
        int mutedColor = ContextCompat.getColor(requireContext(), R.color.muted_foreground);

        llPublic.setBackgroundResource(isPrivate ? inactiveBg : activeBg);
        llPrivate.setBackgroundResource(isPrivate ? activeBg : inactiveBg);

        ivPublic.setColorFilter(isPrivate ? mutedColor : primaryColor);
        ivPrivate.setColorFilter(isPrivate ? primaryColor : mutedColor);
    }

    private String generateRandomCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
