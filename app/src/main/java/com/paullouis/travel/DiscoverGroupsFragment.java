package com.paullouis.travel;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.paullouis.travel.adapter.DiscoverGroupAdapter;
import com.paullouis.travel.data.DataCallback;
import com.paullouis.travel.data.FirebaseRepository;
import com.paullouis.travel.model.Group;
import java.util.ArrayList;
import java.util.List;

public class DiscoverGroupsFragment extends Fragment {

    private DiscoverGroupAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover_groups, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvGroups = view.findViewById(R.id.rvDiscoverGroups);
        EditText etSearch = view.findViewById(R.id.etSearch);

        rvGroups.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DiscoverGroupAdapter(new ArrayList<>(), getParentFragmentManager());
        rvGroups.setAdapter(adapter);

        FirebaseRepository.getInstance().getDiscoverGroups(new DataCallback<List<Group>>() {
            @Override
            public void onSuccess(List<Group> groups) {
                if (!isAdded() || getView() == null) return;
                adapter.setGroups(groups);
            }

            @Override
            public void onError(Exception e) {
                // Leave list empty on error
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}
