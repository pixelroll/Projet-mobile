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
import com.paullouis.travel.data.DataCallback;
import com.paullouis.travel.data.FirebaseRepository;
import com.paullouis.travel.model.Group;

public class RemoveMemberDialogFragment extends BottomSheetDialogFragment {

    private static final String ARG_MEMBER_NAME = "member_name";
    private static final String ARG_GROUP_ID = "group_id";
    private String memberName;
    private String groupId;

    public static RemoveMemberDialogFragment newInstance(String memberName) {
        RemoveMemberDialogFragment fragment = new RemoveMemberDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MEMBER_NAME, memberName);
        fragment.setArguments(args);
        return fragment;
    }

    public static RemoveMemberDialogFragment newInstance(String memberName, String groupId) {
        RemoveMemberDialogFragment fragment = new RemoveMemberDialogFragment();
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
        return inflater.inflate(R.layout.dialog_remove_member, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvWarning = view.findViewById(R.id.tvRemoveWarning);
        tvWarning.setText("Êtes-vous sûr de vouloir retirer " + memberName + " du groupe ? Il ne pourra plus voir les photos privées.");

        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.btnConfirmRemove).setOnClickListener(v -> {
            // Check permissions before removing member
            if (groupId != null && !groupId.isEmpty()) {
                FirebaseRepository.getInstance().getGroupById(groupId, new DataCallback<Group>() {
                    @Override
                    public void onSuccess(Group group) {
                        if (!isAdded()) return;

                        // Only admins and owners can remove members
                        if (group.getRole() != Group.UserRole.ADMIN && group.getRole() != Group.UserRole.OWNER) {
                            Toast.makeText(getContext(), "Vous n'avez pas les permissions nécessaires", Toast.LENGTH_SHORT).show();
                            dismiss();
                            return;
                        }

                        Toast.makeText(getContext(), memberName + " a été retiré du groupe", Toast.LENGTH_SHORT).show();
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
