package com.paullouis.travel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapFragment extends Fragment {

    private MapView map;
    private View cardPreview;
    private ImageView ivPreview, btnCloseCard;
    private TextView tvPreviewTitle, tvPreviewLocation, tvPreviewAuthor, tvPreviewLikes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        
        // Initialize osmdroid configuration
        Context ctx = requireActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(ctx.getPackageName());
        
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        map = view.findViewById(R.id.mapView);
        map.setTileSource(TileSourceFactory.MAPNIK); // OpenStreetMap style
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        map.setMultiTouchControls(true);

        // Initialize Card Views
        cardPreview = view.findViewById(R.id.cardMapPreview);
        ivPreview = view.findViewById(R.id.ivPreview);
        tvPreviewTitle = view.findViewById(R.id.tvPreviewTitle);
        tvPreviewLocation = view.findViewById(R.id.tvPreviewLocation);
        tvPreviewAuthor = view.findViewById(R.id.tvPreviewAuthor);
        tvPreviewLikes = view.findViewById(R.id.tvPreviewLikes);
        btnCloseCard = view.findViewById(R.id.btnCloseCard);

        btnCloseCard.setOnClickListener(v -> cardPreview.setVisibility(View.GONE));

        // Center on Europe
        map.getController().setZoom(5.0);
        GeoPoint startPoint = new GeoPoint(48.8566, 2.3522); // Paris
        map.getController().setCenter(startPoint);

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

        // Add real mock markers from MockDataProvider
        addMockMarker(
            "up0",
            new GeoPoint(48.8584, 2.2945),
            "Tour Eiffel",
            "Paris, France",
            "342 j'aime",
            "https://images.unsplash.com/photo-1511739001486-6bfe10ce785f?w=400"
        );
        addMockMarker(
            "up1",
            new GeoPoint(41.8902, 12.4922),
            "Colisée",
            "Rome, Italie",
            "521 j'aime",
            "https://images.unsplash.com/photo-1552832230-c0197dd311b5?w=400"
        );
        addMockMarker(
            "up2",
            new GeoPoint(41.3851, 2.1734),
            "Sagrada Familia",
            "Barcelone, Espagne",
            "289 j'aime",
            "https://images.unsplash.com/photo-1583778175739-9994ed09c965?w=400"
        );
    }
    
    private void addMockMarker(String photoId, GeoPoint point, String title, String location, String likes, String imageUrl) {
        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .override(150, 150)
            .centerCrop()
            .into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap photo, @Nullable Transition<? super Bitmap> transition) {
                    try {
                        View customMarkerView = getLayoutInflater().inflate(R.layout.item_map_marker, null);
                        ImageView markerImage = customMarkerView.findViewById(R.id.markerImage);
                        markerImage.setImageBitmap(photo);

                        float density = getResources().getDisplayMetrics().density;
                        int widthPx  = (int) (60 * density);
                        int heightPx = (int) (72 * density);

                        int wSpec = View.MeasureSpec.makeMeasureSpec(widthPx, View.MeasureSpec.EXACTLY);
                        int hSpec = View.MeasureSpec.makeMeasureSpec(heightPx, View.MeasureSpec.EXACTLY);
                        customMarkerView.measure(wSpec, hSpec);
                        customMarkerView.layout(0, 0, widthPx, heightPx);

                        Bitmap returnedBitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(returnedBitmap);
                        customMarkerView.draw(canvas);

                        Marker marker = new Marker(map);
                        marker.setPosition(point);
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        marker.setIcon(new BitmapDrawable(getResources(), returnedBitmap));
                        marker.setTitle(title);
                        
                        // Disable default InfoWindow
                        marker.setInfoWindow(null);
                        
                        marker.setOnMarkerClickListener((m, mapView) -> {
                            showPhotoPreview(photoId, title, location, likes, imageUrl);
                            mapView.getController().animateTo(m.getPosition());
                            return true; // Consume the click
                        });

                        map.getOverlays().add(marker);
                        map.invalidate();

                    } catch (Exception e) {
                        Log.e("MapFragment", "Error generating marker icon", e);
                    }
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {}
            });
    }

    private void showPhotoPreview(String photoId, String title, String location, String likes, String imageUrl) {
        tvPreviewTitle.setText(title);
        tvPreviewLocation.setText(location);
        tvPreviewLikes.setText(likes);
        tvPreviewAuthor.setText("Par Sophie Martin");
        
        Glide.with(this).load(imageUrl).centerCrop().into(ivPreview);
        
        cardPreview.setVisibility(View.VISIBLE);
        cardPreview.setAlpha(0f);
        cardPreview.setTranslationY(100f);
        cardPreview.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(300)
            .start();

        cardPreview.setOnClickListener(v -> {
            com.paullouis.travel.model.Photo photo = null;
            for (com.paullouis.travel.model.Photo p : com.paullouis.travel.data.MockDataProvider.getMockPhotos()) {
                if (p.getId().equals(photoId)) {
                    photo = p;
                    break;
                }
            }
            if (photo != null) {
                android.content.Intent intent = new android.content.Intent(getActivity(), PhotoDetailActivity.class);
                intent.putExtra("photo", photo);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (map != null) map.onPause();
    }
}
