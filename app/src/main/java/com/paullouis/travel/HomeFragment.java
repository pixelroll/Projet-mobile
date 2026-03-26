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
            androidx.navigation.Navigation.findNavController(v).navigate(R.id.publishPhotoFragment);
        });
        
        return view;
    }
}
