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
import com.paullouis.travel.adapter.ReportedPhotoAdapter;
import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.ReportedPhoto;
import java.util.List;

public class AdminModerationFragment extends Fragment {

    private static final String ARG_GROUP_ID = "group_id";
    private String groupId;

    public static AdminModerationFragment newInstance(String groupId) {
        AdminModerationFragment fragment = new AdminModerationFragment();
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
        return inflater.inflate(R.layout.fragment_admin_moderation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvReports = view.findViewById(R.id.rvReports);
        View llEmpty = view.findViewById(R.id.llEmptyModeration);

        rvReports.setLayoutManager(new LinearLayoutManager(getContext()));
        List<ReportedPhoto> reports = MockDataProvider.getReportedPhotos(groupId);
        
        if (reports.isEmpty()) {
            rvReports.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
        } else {
            rvReports.setVisibility(View.VISIBLE);
            llEmpty.setVisibility(View.GONE);
            rvReports.setAdapter(new ReportedPhotoAdapter(reports));
        }
    }
}
