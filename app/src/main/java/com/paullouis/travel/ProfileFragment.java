package com.paullouis.travel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.paullouis.travel.adapter.ProfilePhotoAdapter;
import com.paullouis.travel.adapter.ProfileItineraryAdapter;
import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.User;

public class ProfileFragment extends Fragment {

    private TextView tvHeaderName, tvName, tvCountries, tvBio;
    private TextView tvStatPosts, tvStatFollowers, tvStatFollowing;
    private RecyclerView rvProfilePhotos, rvProfileTrips;
    private LinearLayout tabPhotos, tabTrips;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (!MockDataProvider.isUserLoggedIn()) {
            return inflater.inflate(R.layout.fragment_profile_anonymous, container, false);
        }
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!MockDataProvider.isUserLoggedIn()) {
            setupAnonymousView(view);
            return;
        }

        // Handle safe area for notch is already handled by MainActivity.
        View headerLayout = view.findViewById(R.id.headerLayout);

        // Initialize views
        tvHeaderName = view.findViewById(R.id.tvHeaderName);
        tvName = view.findViewById(R.id.tvName);
        tvCountries = view.findViewById(R.id.tvCountries);
        tvBio = view.findViewById(R.id.tvBio);
        tvStatPosts = view.findViewById(R.id.tvStatPosts);
        tvStatFollowers = view.findViewById(R.id.tvStatFollowers);
        tvStatFollowing = view.findViewById(R.id.tvStatFollowing);
        rvProfilePhotos = view.findViewById(R.id.rvProfilePhotos);
        rvProfileTrips = view.findViewById(R.id.rvProfileTrips);
        tabPhotos = view.findViewById(R.id.tabPhotos);
        tabTrips = view.findViewById(R.id.tabTrips);

        // Load Mock Data
        User currentUser = MockDataProvider.getCurrentUser();
        bindUserData(currentUser);

        // Setup Photo Grid
        rvProfilePhotos.setLayoutManager(new GridLayoutManager(getContext(), 3));
        ProfilePhotoAdapter adapter = new ProfilePhotoAdapter(MockDataProvider.getUserPhotos());
        rvProfilePhotos.setAdapter(adapter);

        // Setup Trips List
        rvProfileTrips.setLayoutManager(new LinearLayoutManager(getContext()));
        ProfileItineraryAdapter tripAdapter = new ProfileItineraryAdapter(MockDataProvider.getProfileItineraries(), getContext());
        rvProfileTrips.setAdapter(tripAdapter);

        // Setup Click Listeners
        setupListeners(view);
    }

    private void bindUserData(User user) {
        tvHeaderName.setText(user.getName());
        tvName.setText(user.getName());
        tvCountries.setText(user.getCountriesVisited() + " pays visités");
        tvBio.setText(user.getBio());
        tvStatPosts.setText(String.valueOf(user.getPostsCount()));
        tvStatFollowers.setText(String.valueOf(user.getFollowersCount()));
        tvStatFollowing.setText(String.valueOf(user.getFollowingCount()));
    }

    private void setupListeners(View view) {
        // Account Switcher
        view.findViewById(R.id.llTitle).setOnClickListener(v -> 
            SwitchAccountDialogFragment.newInstance().show(getChildFragmentManager(), "switch_account"));

        view.findViewById(R.id.btnEditProfile).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });
        
        view.findViewById(R.id.btnShareProfile).setOnClickListener(v -> 
            ShareProfileDialogFragment.newInstance().show(getChildFragmentManager(), "share_profile"));

        view.findViewById(R.id.ivAdd).setOnClickListener(v -> {
            if (MockDataProvider.isUserLoggedIn()) {
                android.content.Intent intent = new android.content.Intent(getActivity(), PublishPhotoActivity.class);
                startActivity(intent);
            } else {
                LoginRequiredDialogFragment.newInstance().show(getChildFragmentManager(), "login_required");
            }
        });

        view.findViewById(R.id.ivMenu).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });

        tabPhotos.setOnClickListener(v -> selectTab(true));
        tabTrips.setOnClickListener(v -> selectTab(false));
    }

    private void setupAnonymousView(View view) {
        view.findViewById(R.id.btnLogin).setOnClickListener(v -> {
            MockDataProvider.setUserLoggedIn(true);
            if (getActivity() != null) getActivity().recreate();
        });

        view.findViewById(R.id.btnExplore).setOnClickListener(v -> {
            // Navigate to Home
            com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = 
                getActivity().findViewById(R.id.bottom_navigation);
            bottomNav.setSelectedItemId(R.id.homeFragment);
        });
    }

    private void selectTab(boolean isPhotos) {
        // Toggle visual style of tabs (simplified for mock)
        if (isPhotos) {
            tabPhotos.setBackgroundResource(R.drawable.bg_rounded_white);
            tabPhotos.setElevation(4f);
            tabTrips.setBackground(null);
            tabTrips.setElevation(0f);
            rvProfilePhotos.setVisibility(View.VISIBLE);
            if (rvProfileTrips != null) rvProfileTrips.setVisibility(View.GONE);
        } else {
            tabTrips.setBackgroundResource(R.drawable.bg_rounded_white);
            tabTrips.setElevation(4f);
            tabPhotos.setBackground(null);
            tabPhotos.setElevation(0f);
            rvProfilePhotos.setVisibility(View.GONE);
            if (rvProfileTrips != null) rvProfileTrips.setVisibility(View.VISIBLE);
        }
    }
}
