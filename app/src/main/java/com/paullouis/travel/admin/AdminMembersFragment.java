package com.paullouis.travel.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.paullouis.travel.InviteMemberDialogFragment;
import com.paullouis.travel.R;
import com.paullouis.travel.adapter.GroupMemberAdapter;
import com.paullouis.travel.data.DataCallback;
import com.paullouis.travel.data.FirebaseRepository;
import com.paullouis.travel.model.Group;
import com.paullouis.travel.model.GroupMember;
import java.util.ArrayList;
import java.util.List;

public class AdminMembersFragment extends Fragment {

    private static final String ARG_GROUP_ID = "group_id";
    private String groupId;

    public static AdminMembersFragment newInstance(String groupId) {
        AdminMembersFragment fragment = new AdminMembersFragment();
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
        return inflater.inflate(R.layout.fragment_admin_members, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvMembers = view.findViewById(R.id.rvMembers);
        rvMembers.setLayoutManager(new LinearLayoutManager(getContext()));
        GroupMemberAdapter adapter = new GroupMemberAdapter(new ArrayList<>());
        rvMembers.setAdapter(adapter);

        // Load group info for permission checks
        FirebaseRepository.getInstance().getGroupById(groupId, new DataCallback<Group>() {
            @Override
            public void onSuccess(Group group) {
                if (!isAdded()) return;

                // Permission check: only admins and owners can access this
                if (group.getRole() != Group.UserRole.ADMIN && group.getRole() != Group.UserRole.OWNER) {
                    Toast.makeText(getContext(), "Vous n'avez pas les permissions nécessaires", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                    return;
                }

                adapter.setGroupAndPermissions(group);
                adapter.setGroupId(groupId);

                // Load members after setting permissions
                FirebaseRepository.getInstance().getGroupMembers(groupId, new DataCallback<List<GroupMember>>() {
                    @Override
                    public void onSuccess(List<GroupMember> members) {
                        if (!isAdded() || getView() == null) return;
                        adapter.setMembers(members);
                    }

                    @Override
                    public void onError(Exception e) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(), "Impossible de charger les membres", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Impossible de charger le groupe", Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.btnInvite).setOnClickListener(v -> {
            FirebaseRepository.getInstance().getGroupById(groupId, new DataCallback<Group>() {
                @Override
                public void onSuccess(Group group) {
                    if (!isAdded()) return;

                    // Permission check before inviting
                    if (group.getRole() != Group.UserRole.ADMIN && group.getRole() != Group.UserRole.OWNER) {
                        Toast.makeText(getContext(), "Vous n'avez pas les permissions nécessaires", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String code = group.getCode() != null ? group.getCode() : "";
                    InviteMemberDialogFragment dialog = InviteMemberDialogFragment.newInstance(code);
                    dialog.show(getChildFragmentManager(), "InviteMemberDialog");
                }

                @Override
                public void onError(Exception e) {
                    if (!isAdded()) return;
                    Toast.makeText(getContext(), "Impossible de récupérer le code", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
