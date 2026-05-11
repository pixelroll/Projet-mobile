package com.paullouis.travel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.paullouis.travel.data.DataCallback;
import com.paullouis.travel.data.FirebaseRepository;
import com.paullouis.travel.model.Group;

public class ChangeRoleDialogFragment extends BottomSheetDialogFragment {

    private static final String ARG_MEMBER_NAME = "member_name";
    private static final String ARG_GROUP_ID = "group_id";
    private String memberName;
    private String groupId;
    private MaterialRadioButton rbAdmin;
    private MaterialRadioButton rbMember;

    public static ChangeRoleDialogFragment newInstance(String memberName) {
        ChangeRoleDialogFragment fragment = new ChangeRoleDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MEMBER_NAME, memberName);
        fragment.setArguments(args);
        return fragment;
    }

    public static ChangeRoleDialogFragment newInstance(String memberName, String groupId) {
        ChangeRoleDialogFragment fragment = new ChangeRoleDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MEMBER_NAME, memberName);
        args.putString(ARG_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            memberName = getArguments().getString(ARG_MEMBER_NAME);
            groupId = getArguments().getString(ARG_GROUP_ID);
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

        rbAdmin = view.findViewById(R.id.rbAdmin);
        rbMember = view.findViewById(R.id.rbMember);
        rbMember.setChecked(true);

        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.btnConfirmRole).setOnClickListener(v -> {
            // Check permissions before changing role
            if (groupId != null && !groupId.isEmpty()) {
                FirebaseRepository.getInstance().getGroupById(groupId, new DataCallback<Group>() {
                    @Override
                    public void onSuccess(Group group) {
                        if (!isAdded()) return;

                        // Only admins and owners can change roles
                        if (group.getRole() != Group.UserRole.ADMIN && group.getRole() != Group.UserRole.OWNER) {
                            Toast.makeText(getContext(), "Vous n'avez pas les permissions nécessaires", Toast.LENGTH_SHORT).show();
                            dismiss();
                            return;
                        }

                        String selectedRole = rbAdmin.isChecked() ? "Administrateur" : "Membre";
                        Toast.makeText(getContext(), "Rôle mis à jour pour " + memberName, Toast.LENGTH_SHORT).show();
                        dismiss();
                    }

                    @Override
                    public void onError(Exception e) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(), "Impossible de vérifier les permissions", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
            } else {
                // Fallback for backward compatibility (shouldn't happen with proper usage)
                Toast.makeText(getContext(), "Erreur: ID du groupe manquant", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

    @Override
    public int getTheme() {
        return R.style.CustomBottomSheetDialogTheme;
    }
}
