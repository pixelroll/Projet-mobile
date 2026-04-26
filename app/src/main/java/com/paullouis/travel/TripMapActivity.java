package com.paullouis.travel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.ItineraryStep;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

public class TripMapActivity extends AppCompatActivity {

    private MapView map;
    private String tripTitle;
    private String destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize osmdroid configuration
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(ctx.getPackageName());

        setContentView(R.layout.activity_trip_map);

        // Get intent data
        tripTitle = getIntent().getStringExtra("TRIP_TITLE");
        destination = getIntent().getStringExtra("TRIP_DESTINATION");
        if (tripTitle == null) tripTitle = "Parcours Équilibré";
        if (destination == null) destination = "Paris, France";

        // Handle safe area
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.header), (v, insets) -> {
            Insets statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(0, statusBars.top, 0, 0);
            return insets;
        });

        // Setup Header
        ((TextView) findViewById(R.id.tvTripTitle)).setText(tripTitle);
        ((TextView) findViewById(R.id.tvDestination)).setText(destination);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Setup Map
        map = findViewById(R.id.mapView);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        map.setMultiTouchControls(true);

        loadTripData();
    }

    private void loadTripData() {
        List<ItineraryStep> steps = MockDataProvider.getItinerarySteps();
        List<GeoPoint> points = new ArrayList<>();

        for (ItineraryStep step : steps) {
            GeoPoint gp = new GeoPoint(step.getLatitude(), step.getLongitude());
            points.add(gp);
            addStepMarker(gp, step);
        }

        if (!points.isEmpty()) {
            drawPath(points);
            centerMap(points);
        }
        
        // Update stats
        ((TextView) findViewById(R.id.tvPlacesCount)).setText(String.valueOf(steps.size()));
        int photoCount = 0;
        for (ItineraryStep s : steps) if (s.getPhotos() != null) photoCount += s.getPhotos().size();
        ((TextView) findViewById(R.id.tvPhotosCount)).setText(String.valueOf(photoCount));
    }

    private void drawPath(List<GeoPoint> points) {
        Polyline line = new Polyline(map);
        line.setPoints(points);
        line.getOutlinePaint().setColor(Color.parseColor("#0891b2"));
        line.getOutlinePaint().setStrokeWidth(8.0f);
        line.getOutlinePaint().setStrokeCap(Paint.Cap.ROUND);
        
        // Optional: Dashed effect for "future" or "suggested" path
        // line.getOutlinePaint().setPathEffect(new DashPathEffect(new float[]{20, 20}, 0));
        
        map.getOverlays().add(line);
    }

    private void centerMap(List<GeoPoint> points) {
        if (points.isEmpty()) return;
        
        // Simple centering on the first point for now, or use bounding box
        map.getController().setZoom(14.5);
        map.getController().setCenter(points.get(0));
    }

    private void addStepMarker(GeoPoint point, ItineraryStep step) {
        // Use a default marker first while loading image
        Marker marker = new Marker(map);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(step.getTitle());
        
        // Load step image (or first photo) into marker
        int imageRes = step.getImageRes();
        
        Glide.with(this)
            .asBitmap()
            .load(imageRes)
            .override(120, 120)
            .centerCrop()
            .into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap photo, @Nullable Transition<? super Bitmap> transition) {
                    try {
                        View customMarkerView = getLayoutInflater().inflate(R.layout.item_map_marker, null);
                        ImageView markerImage = customMarkerView.findViewById(R.id.markerImage);
                        markerImage.setImageBitmap(photo);

                        float density = getResources().getDisplayMetrics().density;
                        int widthPx  = (int) (54 * density);
                        int heightPx = (int) (64 * density);

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

                        marker.setIcon(new BitmapDrawable(getResources(), returnedBitmap));
                        map.getOverlays().add(marker);
                        map.invalidate();

                    } catch (Exception e) {
                        Log.e("TripMapActivity", "Error generating marker icon", e);
                    }
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) { }
            });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (map != null) map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (map != null) map.onPause();
    }
}
