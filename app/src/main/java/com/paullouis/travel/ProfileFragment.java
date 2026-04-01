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
import com.paullouis.travel.adapter.ProfilePhotoAdapter;
import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.User;

public class ProfileFragment extends Fragment {

    private TextView tvHeaderName, tvName, tvCountries, tvBio;
    private TextView tvStatPosts, tvStatFollowers, tvStatFollowing;
    private RecyclerView rvProfilePhotos;
    private LinearLayout tabPhotos, tabTrips;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Handle safe area for notch
        View headerLayout = view.findViewById(R.id.headerLayout);
        ViewCompat.setOnApplyWindowInsetsListener(headerLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        // Initialize views
        tvHeaderName = view.findViewById(R.id.tvHeaderName);
        tvName = view.findViewById(R.id.tvName);
        tvCountries = view.findViewById(R.id.tvCountries);
        tvBio = view.findViewById(R.id.tvBio);
        tvStatPosts = view.findViewById(R.id.tvStatPosts);
        tvStatFollowers = view.findViewById(R.id.tvStatFollowers);
        tvStatFollowing = view.findViewById(R.id.tvStatFollowing);
        rvProfilePhotos = view.findViewById(R.id.rvProfilePhotos);
        tabPhotos = view.findViewById(R.id.tabPhotos);
        tabTrips = view.findViewById(R.id.tabTrips);

        // Load Mock Data
        User currentUser = MockDataProvider.getCurrentUser();
        bindUserData(currentUser);

        // Setup Photo Grid
        rvProfilePhotos.setLayoutManager(new GridLayoutManager(getContext(), 3));
        ProfilePhotoAdapter adapter = new ProfilePhotoAdapter(MockDataProvider.getUserPhotos());
        rvProfilePhotos.setAdapter(adapter);

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
        view.findViewById(R.id.btnEditProfile).setOnClickListener(v -> 
            Toast.makeText(getContext(), "Modifier le profil (à implémenter)", Toast.LENGTH_SHORT).show());
        
        view.findViewById(R.id.btnShareProfile).setOnClickListener(v -> 
            Toast.makeText(getContext(), "Partager le profil (à implémenter)", Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.ivAdd).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(getActivity(), PublishPhotoActivity.class);
            startActivity(intent);
        });

        tabPhotos.setOnClickListener(v -> selectTab(true));
        tabTrips.setOnClickListener(v -> selectTab(false));
    }

    private void selectTab(boolean isPhotos) {
        // Toggle visual style of tabs (simplified for mock)
        if (isPhotos) {
            tabPhotos.setBackgroundResource(R.drawable.bg_rounded_white);
            tabPhotos.setElevation(4f);
            tabTrips.setBackground(null);
            tabTrips.setElevation(0f);
            rvProfilePhotos.setVisibility(View.VISIBLE);
        } else {
            tabTrips.setBackgroundResource(R.drawable.bg_rounded_white);
            tabTrips.setElevation(4f);
            tabPhotos.setBackground(null);
            tabPhotos.setElevation(0f);
            rvProfilePhotos.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Onglet Parcours (à implémenter)", Toast.LENGTH_SHORT).show();
        }
    }
}
