package com.paullouis.travel.admin;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.paullouis.travel.R;
import com.paullouis.travel.data.DataCallback;
import com.paullouis.travel.data.FirebaseRepository;
import com.paullouis.travel.model.Group;
import com.paullouis.travel.util.ClipboardHelper;

public class AdminSettingsFragment extends Fragment {

    private static final String ARG_GROUP_ID = "group_id";
    private String groupId;
    private Group group;
    private ImageView ivGroupCoverPreview;
    private ImageView ivCoverUploadIcon;
    private Uri selectedCoverUri;

    private final ActivityResultLauncher<String> coverImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedCoverUri = uri;
                    if (ivCoverUploadIcon != null) ivCoverUploadIcon.setVisibility(View.GONE);
                    if (ivGroupCoverPreview != null) {
                        Glide.with(this).load(uri).into(ivGroupCoverPreview);
                    }
                }
            }
    );

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

        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        FirebaseRepository.getInstance().getGroupById(groupId, new DataCallback<Group>() {
            @Override
            public void onSuccess(Group g) {
                if (!isAdded() || getView() == null) return;
                group = g;
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                populateFields(view);
            }

            @Override
            public void onError(Exception e) {
                if (!isAdded() || getView() == null) return;
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Impossible de charger les paramètres", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateFields(View view) {
        // Permission check: only admins and owners can modify settings
        if (group.getRole() != Group.UserRole.ADMIN && group.getRole() != Group.UserRole.OWNER) {
            Toast.makeText(getContext(), "Vous n'avez pas les permissions nécessaires", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return;
        }

        EditText etName = view.findViewById(R.id.etGroupName);
        EditText etDesc = view.findViewById(R.id.etGroupDesc);
        SwitchMaterial switchApprove = view.findViewById(R.id.switchApprove);
        TextView tvCode = view.findViewById(R.id.tvInviteCode);
        TextView tvType = view.findViewById(R.id.tvGroupType);

        ivGroupCoverPreview = view.findViewById(R.id.ivGroupCoverPreview);
        ivCoverUploadIcon = view.findViewById(R.id.ivCoverUploadIcon);

        etName.setText(group.getName());
        etDesc.setText(group.getDescription());
        switchApprove.setChecked(group.isPrivate());
        tvCode.setText(group.getCode());
        tvType.setText(group.isPrivate() ? "Privé" : "Public");

        if (group.getCoverImage() != null && !group.getCoverImage().isEmpty()) {
            if (ivCoverUploadIcon != null) ivCoverUploadIcon.setVisibility(View.GONE);
            Glide.with(this).load(group.getCoverImage()).into(ivGroupCoverPreview);
        }

        view.findViewById(R.id.btnChangeImage).setOnClickListener(v -> coverImageLauncher.launch("image/*"));

        tvCode.setOnClickListener(v -> ClipboardHelper.copyToClipboard(getContext(), group.getCode(), "Code d'invitation"));

        view.findViewById(R.id.btnRegenerateCode).setOnClickListener(v -> {
            group.regenerateCode();
            tvCode.setText(group.getCode());
            FirebaseRepository.getInstance().updateGroup(group, new DataCallback<Void>() {
                @Override public void onSuccess(Void r) {
                    if (isAdded()) Toast.makeText(getContext(), "Nouveau code généré", Toast.LENGTH_SHORT).show();
                }
                @Override public void onError(Exception e) {
                    if (isAdded()) Toast.makeText(getContext(), "Erreur lors de la régénération", Toast.LENGTH_SHORT).show();
                }
            });
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
            group.setName(etName.getText().toString().trim());
            group.setDescription(etDesc.getText().toString().trim());
            group.setPrivate(switchApprove.isChecked());
            if (selectedCoverUri != null) {
                group.setCoverImage(selectedCoverUri.toString());
            }

            view.findViewById(R.id.btnSaveSettings).setEnabled(false);
            FirebaseRepository.getInstance().updateGroup(group, new DataCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    if (!isAdded()) return;
                    view.findViewById(R.id.btnSaveSettings).setEnabled(true);
                    selectedCoverUri = null;
                    Toast.makeText(getContext(), "Paramètres enregistrés", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Exception e) {
                    if (!isAdded()) return;
                    view.findViewById(R.id.btnSaveSettings).setEnabled(true);
                    Toast.makeText(getContext(), "Erreur lors de la sauvegarde", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
