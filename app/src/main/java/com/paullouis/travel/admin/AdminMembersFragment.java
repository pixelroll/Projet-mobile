package com.paullouis.travel.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.paullouis.travel.R;
import com.paullouis.travel.adapter.GroupMemberAdapter;
import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.GroupMember;
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
        
        List<GroupMember> members = MockDataProvider.getGroupMembers(groupId);
        rvMembers.setAdapter(new GroupMemberAdapter(members));

        view.findViewById(R.id.btnInvite).setOnClickListener(v -> {
            // Find group code
            String code = "CODE123";
            for (com.paullouis.travel.model.Group g : MockDataProvider.getMyGroups()) {
                if (g.getId().equals(groupId)) {
                    code = g.getCode();
                    break;
                }
            }
            com.paullouis.travel.InviteMemberDialogFragment dialog = com.paullouis.travel.InviteMemberDialogFragment.newInstance(code);
            dialog.show(getChildFragmentManager(), "InviteMemberDialog");
        });
    }
}
