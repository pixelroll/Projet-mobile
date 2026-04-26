package com.paullouis.travel.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.paullouis.travel.R;
import com.paullouis.travel.adapter.TopContributorAdapter;
import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.GroupMember;
import java.util.List;
import java.util.Map;

public class AdminStatsFragment extends Fragment {

    private static final String ARG_GROUP_ID = "group_id";
    private String groupId;

    public static AdminStatsFragment newInstance(String groupId) {
        AdminStatsFragment fragment = new AdminStatsFragment();
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
        return inflater.inflate(R.layout.fragment_admin_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Map<String, Integer> stats = MockDataProvider.getGroupStats(groupId);

        ((TextView)view.findViewById(R.id.tvStatActiveMembers)).setText(String.valueOf(stats.get("active_members")));
        ((TextView)view.findViewById(R.id.tvStatPhotosShared)).setText(String.valueOf(stats.get("photos_shared")));
        ((TextView)view.findViewById(R.id.tvStatNewMembers)).setText("+" + stats.get("new_members_7d"));
        
        int totalInteractions = stats.get("total_interactions");
        String interactionStr = totalInteractions >= 1000 ? (totalInteractions / 1000) + "k" : String.valueOf(totalInteractions);
        ((TextView)view.findViewById(R.id.tvStatInteractions)).setText(interactionStr);

        RecyclerView rvTop = view.findViewById(R.id.rvTopContributors);
        rvTop.setLayoutManager(new LinearLayoutManager(getContext()));
        List<GroupMember> members = MockDataProvider.getGroupMembers(groupId);
        // Sort by photosCount descending for top contributors
        members.sort((m1, m2) -> Integer.compare(m2.getPhotosCount(), m1.getPhotosCount()));
        
        rvTop.setAdapter(new TopContributorAdapter(members.subList(0, Math.min(members.size(), 3))));
    }
}
