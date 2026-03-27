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

        // Center on Greece area to see both markers
        map.getController().setZoom(4.0);
        GeoPoint startPoint = new GeoPoint(38.0, 50.0);
        map.getController().setCenter(startPoint);

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

        // Add mock markers matching MockDataProvider locations with real Unsplash URLs
        addMockMarker(
            new GeoPoint(36.3932, 25.4615),
            "Santorin",
            "https://images.unsplash.com/photo-1613395877344-13d4a8e0d49e?auto=format&fit=crop&q=80&w=200"
        );
        addMockMarker(
            new GeoPoint(35.0116, 135.7681),
            "Kyoto",
            "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?auto=format&fit=crop&q=80&w=200"
        );
    }
    
    private void addMockMarker(GeoPoint point, String title, String imageUrl) {
        // First load the image with Glide, then build the marker bitmap
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
                        Drawable bgDrawable = customMarkerView.getBackground();
                        if (bgDrawable != null) {
                            bgDrawable.draw(canvas);
                        }
                        customMarkerView.draw(canvas);

                        Marker marker = new Marker(map);
                        marker.setPosition(point);
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        marker.setIcon(new BitmapDrawable(getResources(), returnedBitmap));
                        marker.setTitle(title);
                        map.getOverlays().add(marker);
                        map.invalidate(); // Refresh the map to show the new marker

                    } catch (Exception e) {
                        Log.e("MapFragment", "Error generating marker icon", e);
                    }
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                    // No-op
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
