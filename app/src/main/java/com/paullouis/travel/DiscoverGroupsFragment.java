package com.paullouis.travel;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    private View emptyState;
    private TextView tvEmptyTitle;
    private TextView tvEmptySubtitle;
    private ProgressBar progressBar;
    private String currentQuery = "";

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
        emptyState = view.findViewById(R.id.emptyState);
        tvEmptyTitle = view.findViewById(R.id.tvEmptyTitle);
        tvEmptySubtitle = view.findViewById(R.id.tvEmptySubtitle);
        progressBar = view.findViewById(R.id.progressBar);

        rvGroups.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DiscoverGroupAdapter(new ArrayList<>(), getParentFragmentManager(), this::onGroupJoined);
        rvGroups.setAdapter(adapter);

        loadGroups();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = s.toString();
                adapter.filter(currentQuery);
                updateEmptyState(adapter.getItemCount() == 0);
            }

            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void loadGroups() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        if (emptyState != null) emptyState.setVisibility(View.GONE);

        FirebaseRepository.getInstance().getDiscoverGroups(new DataCallback<List<Group>>() {
            @Override
            public void onSuccess(List<Group> groups) {
                if (!isAdded() || getView() == null) return;
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                adapter.setGroups(groups);
                updateEmptyState(groups.isEmpty());
            }

            @Override
            public void onError(Exception e) {
                if (!isAdded() || getView() == null) return;
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                updateEmptyState(true);
            }
        });
    }

    private void onGroupJoined(String groupId) {
        // Group was joined from dialog; adapter already removed it — just check empty state
        updateEmptyState(adapter.getItemCount() == 0);
    }

    private void updateEmptyState(boolean isEmpty) {
        if (emptyState == null) return;
        if (isEmpty) {
            if (!currentQuery.isEmpty()) {
                tvEmptyTitle.setText("Aucun résultat");
                tvEmptySubtitle.setText("Aucun groupe ne correspond à \"" + currentQuery + "\"");
            } else {
                tvEmptyTitle.setText("Aucun groupe disponible");
                tvEmptySubtitle.setText("Il n'y a pas encore de groupes publics à rejoindre");
            }
            emptyState.setVisibility(View.VISIBLE);
        } else {
            emptyState.setVisibility(View.GONE);
        }
    }
}
