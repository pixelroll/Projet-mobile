package com.paullouis.travel.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.paullouis.travel.R;
import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.Group;
import com.paullouis.travel.util.ClipboardHelper;

public class AdminSettingsFragment extends Fragment {

    private static final String ARG_GROUP_ID = "group_id";
    private String groupId;
    private Group group;

    public static AdminSettingsFragment newInstance(String groupId) {
        AdminSettingsFragment fragment = new AdminSettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupId = getArguments().getString(ARG_GROUP_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find group
        for (Group g : MockDataProvider.getMyGroups()) {
            if (g.getId().equals(groupId)) {
                group = g;
                break;
            }
        }

        if (group == null) return;

        EditText etName = view.findViewById(R.id.etGroupName);
        EditText etDesc = view.findViewById(R.id.etGroupDesc);
        SwitchMaterial switchApprove = view.findViewById(R.id.switchApprove);
        TextView tvCode = view.findViewById(R.id.tvInviteCode);
        TextView tvType = view.findViewById(R.id.tvGroupType);

        etName.setText(group.getName());
        etDesc.setText(group.getDescription());
        switchApprove.setChecked(group.isPrivate()); // Using private as a proxy for approval requirement
        tvCode.setText(group.getCode());
        tvType.setText(group.isPrivate() ? "Privé" : "Public");

        tvCode.setOnClickListener(v -> {
            ClipboardHelper.copyToClipboard(getContext(), group.getCode(), "Code d'invitation");
        });

        view.findViewById(R.id.btnRegenerateCode).setOnClickListener(v -> {
            group.regenerateCode();
            tvCode.setText(group.getCode());
            Toast.makeText(getContext(), "Nouveau code généré", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.btnGroupType).setOnClickListener(v -> {
            android.widget.PopupMenu popup = new android.widget.PopupMenu(getContext(), v);
            popup.getMenu().add("Public");
            popup.getMenu().add("Privé");
            popup.setOnMenuItemClickListener(item -> {
                tvType.setText(item.getTitle());
                if (item.getTitle().equals("Public")) {
                    tvType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_globe, 0, 0, 0);
                    group.setPrivate(false);
                } else {
                    tvType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock, 0, 0, 0);
                    group.setPrivate(true);
                }
                return true;
            });
            popup.show();
        });

        view.findViewById(R.id.btnSaveSettings).setOnClickListener(v -> {
            group.setName(etName.getText().toString());
            group.setDescription(etDesc.getText().toString());
            group.setPrivate(switchApprove.isChecked());
            Toast.makeText(getContext(), "Paramètres enregistrés", Toast.LENGTH_SHORT).show();
        });

        // The mockup doesn't show the Delete button clearly, but I'll keep the logic if needed or hide it if it's too different.
        // Mockup 4 actually doesn't show the Delete button, it shows a large teal Save button.
        // I'll keep the delete logic but it's not in the main scroll view in mockup 4.
    }
}
