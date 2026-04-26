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
import com.paullouis.travel.adapter.PhotoAdapter;
import com.paullouis.travel.data.MockDataProvider;

public class HomeFragment extends Fragment {

    private RecyclerView rvPhotos;
    private PhotoAdapter photoAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        rvPhotos = view.findViewById(R.id.rvPhotos);
        rvPhotos.setLayoutManager(new LinearLayoutManager(getContext()));
        
        photoAdapter = new PhotoAdapter(MockDataProvider.getMockPhotos());
        rvPhotos.setAdapter(photoAdapter);
        
        // Navigation from top bar
        view.findViewById(R.id.ivAdd).setOnClickListener(v -> {
            if (MockDataProvider.isUserLoggedIn()) {
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
            if (MockDataProvider.isUserLoggedIn()) {
                android.content.Intent intent = new android.content.Intent(getActivity(), GroupsActivity.class);
                startActivity(intent);
            } else {
                LoginRequiredDialogFragment.newInstance().show(getChildFragmentManager(), "login_required");
            }
        });
        
        return view;
    }
}
