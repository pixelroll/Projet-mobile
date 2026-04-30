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
import com.paullouis.travel.data.DataCallback;
import com.paullouis.travel.data.FirebaseRepository;
import com.paullouis.travel.data.EventBus;
import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.User;
import com.paullouis.travel.model.Photo;

public class ProfileFragment extends Fragment implements EventBus.PhotoListener, EventBus.UserListener {

    private TextView tvHeaderName, tvName, tvCountries, tvBio;
    private TextView tvStatPosts, tvStatFollowers, tvStatFollowing;
    private RecyclerView rvProfilePhotos, rvProfileTrips;
    private LinearLayout tabPhotos, tabTrips;
    private ProfilePhotoAdapter profilePhotoAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (!FirebaseRepository.getInstance().isUserLoggedIn()) {
            return inflater.inflate(R.layout.fragment_profile_anonymous, container, false);
        }
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!FirebaseRepository.getInstance().isUserLoggedIn()) {
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
        ImageView ivProfile = view.findViewById(R.id.ivProfile); // Assuming it's ivProfile
        rvProfilePhotos = view.findViewById(R.id.rvProfilePhotos);
        rvProfileTrips = view.findViewById(R.id.rvProfileTrips);
        tabPhotos = view.findViewById(R.id.tabPhotos);
        tabTrips = view.findViewById(R.id.tabTrips);

        // Load Data asynchronously from Repository
        FirebaseRepository.getInstance().getCurrentUser(new DataCallback<User>() {
            @Override
            public void onSuccess(User user) {
                if (!isAdded() || getView() == null) return;
                bindUserData(view, user);
            }

            @Override
            public void onError(Exception e) {
                if (!isAdded() || getView() == null) return;
                Toast.makeText(getContext(), "Erreur de chargement du profil", Toast.LENGTH_SHORT).show();
            }
        });

        // Setup Photo Grid — Keep a single adapter instance to preserve optimistic items
        rvProfilePhotos.setLayoutManager(new GridLayoutManager(getContext(), 3));
        profilePhotoAdapter = new ProfilePhotoAdapter(new java.util.ArrayList<>());
        rvProfilePhotos.setAdapter(profilePhotoAdapter);

        // Load photos and update the existing adapter in-place (not replace it)
        FirebaseRepository.getInstance().getUserPhotos(new DataCallback<java.util.List<Photo>>() {
            @Override
            public void onSuccess(java.util.List<Photo> result) {
                if (!isAdded() || getView() == null) return;
                profilePhotoAdapter.setPhotos(result);
            }

            @Override
            public void onError(Exception e) {
                if (!isAdded() || getView() == null) return;
                // Ignore error
            }
        });

        // Setup Trips List
        rvProfileTrips.setLayoutManager(new LinearLayoutManager(getContext()));
        ProfileItineraryAdapter tripAdapter = new ProfileItineraryAdapter(MockDataProvider.getProfileItineraries(), getContext());
        rvProfileTrips.setAdapter(tripAdapter);

        // Setup Click Listeners
        setupListeners(view);
    }

    private void bindUserData(View view, User user) {
        tvHeaderName.setText(user.getName());
        tvName.setText(user.getName());
        tvCountries.setText(user.getCountriesVisited() + " pays visités");
        tvBio.setText(user.getBio());
        tvStatPosts.setText(String.valueOf(user.getPostsCount()));
        tvStatFollowers.setText(String.valueOf(user.getFollowersCount()));
        tvStatFollowing.setText(String.valueOf(user.getFollowingCount()));
        
        ImageView ivProfile = view.findViewById(R.id.ivProfile);
        if (ivProfile != null && user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
            com.bumptech.glide.Glide.with(this)
                    .load(user.getAvatarUrl())
                    .circleCrop()
                    .placeholder(R.drawable.profile_sophie)
                    .into(ivProfile);
        }
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
            if (FirebaseRepository.getInstance().isUserLoggedIn()) {
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
            LoginRequiredDialogFragment.newInstance().show(getChildFragmentManager(), "login_required");
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

    @Override
    public void onStart() {
        super.onStart();
        EventBus.registerPhotoListener(this);
        EventBus.registerUserListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.unregisterPhotoListener(this);
        EventBus.unregisterUserListener(this);
    }

    @Override
    public void onPhotoAdded(Photo photo) {
        if (profilePhotoAdapter != null) {
            profilePhotoAdapter.addPhoto(photo);
            if (rvProfilePhotos != null) {
                rvProfilePhotos.scrollToPosition(0);
            }
        }
    }

    @Override
    public void onPhotoUpdated(Photo photo) {
        if (profilePhotoAdapter != null) {
            profilePhotoAdapter.updatePhoto(photo);
        }
    }

    @Override
    public void onPhotoRemoved(String photoId) {
        if (profilePhotoAdapter != null) {
            profilePhotoAdapter.removePhoto(photoId);
        }
    }

    @Override
    public void onUserUpdated(User user) {
        if (isAdded() && getView() != null) {
            bindUserData(getView(), user);
        }
    }
}
