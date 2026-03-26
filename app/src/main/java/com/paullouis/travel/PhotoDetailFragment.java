package com.paullouis.travel;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.bumptech.glide.Glide;
import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.Photo;
import com.google.android.material.button.MaterialButton;

public class PhotoDetailFragment extends Fragment {

    private ImageView ivDetailPhoto, btnBack;
    private TextView tvDetailTitle, tvDetailDescription;
    private MaterialButton btnNavigateMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_detail, container, false);
        
        ivDetailPhoto = view.findViewById(R.id.ivDetailPhoto);
        btnBack = view.findViewById(R.id.btnBack);
        tvDetailTitle = view.findViewById(R.id.tvDetailTitle);
        tvDetailDescription = view.findViewById(R.id.tvDetailDescription);
        btnNavigateMap = view.findViewById(R.id.btnNavigateMap);
        
        // Simuler la récupération de la première photo pour l'instant
        Photo currentPhoto = MockDataProvider.getMockPhotos().get(0);
        
        tvDetailTitle.setText(currentPhoto.getTitle());
        tvDetailDescription.setText(currentPhoto.getDescription());
        
        Glide.with(this)
             .load(currentPhoto.getImageUrl())
             .into(ivDetailPhoto);
             
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        
        btnNavigateMap.setOnClickListener(v -> {
            // Intent Google Maps
            Uri gmmIntentUri = Uri.parse("geo:" + currentPhoto.getLat() + "," + currentPhoto.getLng() + "?q=" + Uri.encode(currentPhoto.getLocationName()));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                // Fallback sans package
                Intent genericMapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                startActivity(genericMapIntent);
            }
        });
        
        return view;
    }
}
