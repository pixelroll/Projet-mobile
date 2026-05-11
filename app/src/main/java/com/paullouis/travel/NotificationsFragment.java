package com.paullouis.travel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.paullouis.travel.adapter.NotificationAdapter;
import com.paullouis.travel.data.DataCallback;
import com.paullouis.travel.data.FirebaseRepository;
import com.paullouis.travel.model.Notification;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationsFragment extends Fragment {

    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private List<Notification> allNotifications = new ArrayList<>();
    private TextView tvUnreadCount, tvTabAllLabel, tvTabUnreadLabel, badgeAll, badgeUnread;
    private View indicatorAll, indicatorUnread;
    private FrameLayout tabAll, tabUnread;
    private ProgressBar progressBar;
    private boolean showingAll = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View headerLayout = view.findViewById(R.id.headerLayout);
        ViewCompat.setOnApplyWindowInsetsListener(headerLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        tvUnreadCount = view.findViewById(R.id.tvUnreadCount);
        tvTabAllLabel = view.findViewById(R.id.tvTabAllLabel);
        tvTabUnreadLabel = view.findViewById(R.id.tvTabUnreadLabel);
        badgeAll = view.findViewById(R.id.badgeAll);
        badgeUnread = view.findViewById(R.id.badgeUnread);
        indicatorAll = view.findViewById(R.id.indicatorAll);
        indicatorUnread = view.findViewById(R.id.indicatorUnread);
        tabAll = view.findViewById(R.id.tabAll);
        tabUnread = view.findViewById(R.id.tabUnread);
        rvNotifications = view.findViewById(R.id.rvNotifications);
        progressBar = view.findViewById(R.id.progressBar);

        adapter = new NotificationAdapter(allNotifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNotifications.setAdapter(adapter);

        view.findViewById(R.id.btnBack).setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        view.findViewById(R.id.btnMarkAll).setOnClickListener(v -> markAllAsRead());
        view.findViewById(R.id.btnSettings).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(getActivity(), NotificationSettingsActivity.class);
            startActivity(intent);
        });

        tabAll.setOnClickListener(v -> switchTab(true));
        tabUnread.setOnClickListener(v -> switchTab(false));

        loadNotifications();
    }

    private void loadNotifications() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        FirebaseRepository.getInstance().getNotifications(new DataCallback<List<Notification>>() {
            @Override
            public void onSuccess(List<Notification> notifications) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                allNotifications.clear();
                allNotifications.addAll(notifications);
                updateCounts();
                switchTab(showingAll);
            }

            @Override
            public void onError(Exception e) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                updateCounts();
            }
        });
    }

    private void updateCounts() {
        long unread = allNotifications.stream().filter(n -> !n.isRead()).count();
        tvUnreadCount.setText(unread + " non lue" + (unread > 1 ? "s" : ""));
        badgeAll.setText(String.valueOf(allNotifications.size()));
        badgeUnread.setText(String.valueOf(unread));
    }

    private void switchTab(boolean all) {
        showingAll = all;
        indicatorAll.setVisibility(all ? View.VISIBLE : View.GONE);
        indicatorUnread.setVisibility(all ? View.GONE : View.VISIBLE);

        int primary = requireContext().getColor(R.color.primary);
        int muted = requireContext().getColor(R.color.muted_foreground);

        tvTabAllLabel.setTextColor(all ? primary : muted);
        tvTabUnreadLabel.setTextColor(all ? muted : primary);

        if (all) {
            adapter.setNotifications(allNotifications);
        } else {
            List<Notification> filtered = allNotifications.stream()
                    .filter(n -> !n.isRead())
                    .collect(Collectors.toList());
            adapter.setNotifications(filtered);
        }
    }

    private void markAllAsRead() {
        for (Notification n : allNotifications) {
            if (!n.isRead()) {
                n.setRead(true);
                if (n.getNotificationId() != null) {
                    FirebaseRepository.getInstance().markNotificationRead(n.getNotificationId(), new DataCallback<Void>() {
                        @Override public void onSuccess(Void r) {}
                        @Override public void onError(Exception e) {}
                    });
                }
            }
        }
        updateCounts();
        if (showingAll) {
            adapter.notifyDataSetChanged();
        } else {
            adapter.setNotifications(new ArrayList<>());
        }
    }
}
