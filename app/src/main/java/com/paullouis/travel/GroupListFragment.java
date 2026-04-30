package com.paullouis.travel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.paullouis.travel.adapter.GroupAdapter;
import com.paullouis.travel.data.DataCallback;
import com.paullouis.travel.data.FirebaseRepository;
import com.paullouis.travel.model.Group;
import java.util.List;

public class GroupListFragment extends Fragment {

    private static final String ARG_TYPE = "type";
    public static final int TYPE_MY_GROUPS = 0;
    public static final int TYPE_DISCOVER = 1;

    private RecyclerView rvGroups;
    private ProgressBar progressGroups;
    private TextView tvEmptyGroups;

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

        rvGroups = view.findViewById(R.id.rvGroups);
        progressGroups = view.findViewById(R.id.progressGroups);
        tvEmptyGroups = view.findViewById(R.id.tvEmptyGroups);

        rvGroups.setLayoutManager(new LinearLayoutManager(getContext()));

        int type = getArguments().getInt(ARG_TYPE);
        showLoading();

        DataCallback<List<Group>> callback = new DataCallback<List<Group>>() {
            @Override
            public void onSuccess(List<Group> groups) {
                if (!isAdded()) return;
                if (groups == null || groups.isEmpty()) {
                    showEmpty();
                } else {
                    showList(groups);
                }
            }

            @Override
            public void onError(Exception e) {
                if (!isAdded()) return;
                showEmpty();
                Toast.makeText(getContext(), "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        if (type == TYPE_MY_GROUPS) {
            FirebaseRepository.getInstance().getMyGroups(callback);
        } else {
            FirebaseRepository.getInstance().getDiscoverGroups(callback);
        }
    }

    private void showLoading() {
        progressGroups.setVisibility(View.VISIBLE);
        rvGroups.setVisibility(View.GONE);
        tvEmptyGroups.setVisibility(View.GONE);
    }

    private void showEmpty() {
        progressGroups.setVisibility(View.GONE);
        rvGroups.setVisibility(View.GONE);
        tvEmptyGroups.setVisibility(View.VISIBLE);
    }

    private void showList(List<Group> groups) {
        progressGroups.setVisibility(View.GONE);
        tvEmptyGroups.setVisibility(View.GONE);
        rvGroups.setVisibility(View.VISIBLE);
        rvGroups.setAdapter(new GroupAdapter(groups, getParentFragmentManager()));
    }
}
