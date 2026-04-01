package com.paullouis.travel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.paullouis.travel.adapter.GroupAdapter;
import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.Group;
import java.util.List;

public class GroupListFragment extends Fragment {

    private static final String ARG_TYPE = "type";
    public static final int TYPE_MY_GROUPS = 0;
    public static final int TYPE_DISCOVER = 1;

    public static GroupListFragment newInstance(int type) {
        GroupListFragment fragment = new GroupListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        RecyclerView rvGroups = view.findViewById(R.id.rvGroups);
        rvGroups.setLayoutManager(new LinearLayoutManager(getContext()));

        int type = getArguments().getInt(ARG_TYPE);
        List<Group> groups;
        if (type == TYPE_MY_GROUPS) {
            groups = MockDataProvider.getMyGroups();
        } else {
            groups = MockDataProvider.getDiscoverGroups();
        }

        rvGroups.setAdapter(new GroupAdapter(groups, getParentFragmentManager()));
    }
}
