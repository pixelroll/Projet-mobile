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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.paullouis.travel.adapter.PhotoAdapter;
import com.paullouis.travel.data.DataCallback;
import com.paullouis.travel.data.FirebaseRepository;
import com.paullouis.travel.data.EventBus;
import com.paullouis.travel.model.Photo;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements EventBus.PhotoListener, EventBus.PhotoLikeListener {

    private RecyclerView rvPhotos;
    private PhotoAdapter photoAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isDataLoaded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        rvPhotos = view.findViewById(R.id.rvPhotos);
        rvPhotos.setLayoutManager(new LinearLayoutManager(getContext()));
        
        photoAdapter = new PhotoAdapter(new ArrayList<>());
        rvPhotos.setAdapter(photoAdapter);

        // Pull-to-refresh
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary);
        swipeRefreshLayout.setOnRefreshListener(this::loadPhotosFromServer);

        // Load data only once (not on every tab switch)
        if (!isDataLoaded) {
            loadPhotosFromServer();
        }
        
        // Navigation from top bar
        view.findViewById(R.id.ivAdd).setOnClickListener(v -> {
            if (FirebaseRepository.getInstance().isUserLoggedIn()) {
                android.content.Intent intent = new android.content.Intent(getActivity(), PublishPhotoActivity.class);
                startActivity(intent);
            } else {
                LoginRequiredDialogFragment.newInstance().show(getChildFragmentManager(), "login_required");
            }
        });

        view.findViewById(R.id.flNotification).setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(v).navigate(R.id.notificationsFragment);
        });

        view.findViewById(R.id.ivGroup).setOnClickListener(v -> {
            if (FirebaseRepository.getInstance().isUserLoggedIn()) {
                android.content.Intent intent = new android.content.Intent(getActivity(), GroupsActivity.class);
                startActivity(intent);
            } else {
                LoginRequiredDialogFragment.newInstance().show(getChildFragmentManager(), "login_required");
            }
        });
        
        return view;
    }

    /**
     * Fetches photos from Firebase. Called on first load and on pull-to-refresh.
     */
    private void loadPhotosFromServer() {
        FirebaseRepository.getInstance().getFeedPhotos(new DataCallback<List<Photo>>() {
            @Override
            public void onSuccess(List<Photo> result) {
                if (!isAdded() || getView() == null) return;
                photoAdapter.setPhotos(result);
                isDataLoaded = true;
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Exception e) {
                if (!isAdded() || getView() == null) return;
                isDataLoaded = true; // prevent infinite retry
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.registerPhotoListener(this);
        EventBus.registerPhotoLikeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.unregisterPhotoListener(this);
        EventBus.unregisterPhotoLikeListener(this);
    }

    @Override
    public void onPhotoAdded(Photo photo) {
        if (photoAdapter != null) {
            photoAdapter.addPhoto(photo);
            if (rvPhotos != null && isAdded()) {
                rvPhotos.scrollToPosition(0);
            }
        }
    }

    @Override
    public void onPhotoUpdated(Photo photo) {
        if (photoAdapter != null && isAdded()) {
            photoAdapter.updatePhoto(photo);
        }
    }

    @Override
    public void onPhotoRemoved(String photoId) {
        if (photoAdapter != null && isAdded()) {
            photoAdapter.removePhoto(photoId);
        }
    }

    @Override
    public void onPhotoLiked(String photoId, boolean liked) {
        if (photoAdapter != null && isAdded()) {
            for (Photo p : photoAdapter.getPhotos()) {
                if (p.getId().equals(photoId)) {
                    p.setLiked(liked);
                    photoAdapter.updatePhoto(p);
                    break;
                }
            }
        }
    }
}
