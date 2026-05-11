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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.paullouis.travel.adapter.ProfilePhotoAdapter;
import com.paullouis.travel.adapter.ProfileItineraryAdapter;
import com.paullouis.travel.data.DataCallback;
import com.paullouis.travel.data.FirebaseRepository;
import com.paullouis.travel.data.EventBus;
import com.paullouis.travel.model.SavedItinerary;
import com.paullouis.travel.model.User;
import com.paullouis.travel.model.Photo;

public class ProfileFragment extends Fragment implements EventBus.PhotoListener, EventBus.UserListener {

    private TextView tvHeaderName, tvName, tvBio, tvLocation, tvWebsite;
    private TextView tvStatPosts;
    private RecyclerView rvProfilePhotos, rvProfileTrips;
    private LinearLayout tabPhotos, tabTrips, locationContainer, websiteContainer;
    private ProfilePhotoAdapter profilePhotoAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

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
        tvBio = view.findViewById(R.id.tvBio);
        tvLocation = view.findViewById(R.id.tvLocation);
        tvWebsite = view.findViewById(R.id.tvWebsite);
        tvStatPosts = view.findViewById(R.id.tvStatPosts);
        locationContainer = view.findViewById(R.id.locationContainer);
        websiteContainer = view.findViewById(R.id.websiteContainer);
        ImageView ivProfile = view.findViewById(R.id.ivProfile);
        rvProfilePhotos = view.findViewById(R.id.rvProfilePhotos);
        rvProfileTrips = view.findViewById(R.id.rvProfileTrips);
        tabPhotos = view.findViewById(R.id.tabPhotos);
        tabTrips = view.findViewById(R.id.tabTrips);

        // Setup pull-to-refresh
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary);
        swipeRefreshLayout.setOnRefreshListener(this::refreshProfileData);

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
                updatePublicationCount();
            }

            @Override
            public void onError(Exception e) {
                if (!isAdded() || getView() == null) return;
                // Ignore error
            }
        });

        // Setup Trips List with real Firebase data
        rvProfileTrips.setLayoutManager(new LinearLayoutManager(getContext()));
        ProfileItineraryAdapter tripAdapter = new ProfileItineraryAdapter(new java.util.ArrayList<>(), getContext());
        rvProfileTrips.setAdapter(tripAdapter);

        FirebaseRepository.getInstance().getUserItineraries(new DataCallback<java.util.List<SavedItinerary>>() {
            @Override
            public void onSuccess(java.util.List<SavedItinerary> result) {
                if (!isAdded() || getView() == null) return;
                tripAdapter.setItineraries(result);
            }

            @Override
            public void onError(Exception e) {
                android.util.Log.e("ProfileFragment", "Failed to load itineraries", e);
            }
        });

        // Setup Click Listeners
        setupListeners(view);
    }

    private void bindUserData(View view, User user) {
        tvHeaderName.setText(user.getName());
        tvName.setText(user.getName());

        // Handle bio with null safety
        if (user.getBio() != null && !user.getBio().isEmpty()) {
            tvBio.setText(user.getBio());
            tvBio.setVisibility(View.VISIBLE);
        } else {
            tvBio.setVisibility(View.GONE);
        }

        // Handle location with null safety
        if (user.getLocation() != null && !user.getLocation().isEmpty()) {
            tvLocation.setText(user.getLocation());
            locationContainer.setVisibility(View.VISIBLE);
        } else {
            locationContainer.setVisibility(View.GONE);
        }

        // Setup website if available
        if (user.getWebsite() != null && !user.getWebsite().isEmpty()) {
            tvWebsite.setText(user.getWebsite());
            websiteContainer.setVisibility(View.VISIBLE);
            websiteContainer.setOnClickListener(v -> openWebsite(user.getWebsite()));
        } else {
            websiteContainer.setVisibility(View.GONE);
        }

        updatePublicationCount();
        
        ImageView ivProfile = view.findViewById(R.id.ivProfile);
        if (ivProfile != null) {
            String avatarUrl = user.getAvatarUrl();
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                com.bumptech.glide.Glide.with(this)
                        .load(avatarUrl)
                        .circleCrop()
                        .into(ivProfile);
            } else {
                String userName = user.getName();
                String initial = userName != null && !userName.isEmpty() ? userName.substring(0, 1).toUpperCase() : "?";
                ivProfile.setImageDrawable(createInitialDrawable(initial, userName));
            }
        }
    }

    private void updatePublicationCount() {
        if (tvStatPosts != null && profilePhotoAdapter != null) {
            int count = profilePhotoAdapter.getItemCount();
            tvStatPosts.setText(String.valueOf(count));
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

    private void refreshProfileData() {
        // Reload user profile
        FirebaseRepository.getInstance().getCurrentUser(new DataCallback<User>() {
            @Override
            public void onSuccess(User user) {
                if (!isAdded() || getView() == null) {
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }
                bindUserData(getView(), user);
            }

            @Override
            public void onError(Exception e) {
                if (!isAdded() || getView() == null) return;
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Reload photos
        FirebaseRepository.getInstance().getUserPhotos(new DataCallback<java.util.List<Photo>>() {
            @Override
            public void onSuccess(java.util.List<Photo> result) {
                if (!isAdded() || getView() == null) {
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }
                profilePhotoAdapter.setPhotos(result);
                updatePublicationCount();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Exception e) {
                if (!isAdded() || getView() == null) return;
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Reload itineraries
        FirebaseRepository.getInstance().getUserItineraries(new DataCallback<java.util.List<SavedItinerary>>() {
            @Override
            public void onSuccess(java.util.List<SavedItinerary> result) {
                if (!isAdded() || getView() == null) {
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }
                ProfileItineraryAdapter tripAdapter = (ProfileItineraryAdapter) rvProfileTrips.getAdapter();
                if (tripAdapter != null) {
                    tripAdapter.setItineraries(result);
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Exception e) {
                if (!isAdded() || getView() == null) return;
                swipeRefreshLayout.setRefreshing(false);
            }
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

    private void openWebsite(String website) {
        String url = website;
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url));
        startActivity(intent);
    }

    private android.graphics.drawable.Drawable createInitialDrawable(String initial, String userName) {
        android.graphics.drawable.ShapeDrawable drawable = new android.graphics.drawable.ShapeDrawable(new android.graphics.drawable.shapes.OvalShape());
        int[] colors = {0xFF6366F1, 0xFF8B5CF6, 0xFFEC4899, 0xFFF59E0B, 0xFF10B981, 0xFF3B82F6};
        int colorIndex = (userName != null ? userName.hashCode() : 0) % colors.length;
        if (colorIndex < 0) colorIndex = -colorIndex;
        drawable.getPaint().setColor(colors[colorIndex]);

        android.graphics.drawable.LayerDrawable layerDrawable = new android.graphics.drawable.LayerDrawable(new android.graphics.drawable.Drawable[]{drawable});

        android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(200, 200, android.graphics.Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
        layerDrawable.setBounds(0, 0, 200, 200);
        layerDrawable.draw(canvas);

        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setColor(android.graphics.Color.WHITE);
        paint.setTextSize(80);
        paint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD));
        paint.setTextAlign(android.graphics.Paint.Align.CENTER);
        canvas.drawText(initial, 100, 130, paint);

        return new android.graphics.drawable.BitmapDrawable(getResources(), bitmap);
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
            updatePublicationCount();
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
            updatePublicationCount();
        }
    }

    @Override
    public void onUserUpdated(User user) {
        if (isAdded() && getView() != null) {
            bindUserData(getView(), user);
        }
    }
}
