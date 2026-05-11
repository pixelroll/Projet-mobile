package com.paullouis.travel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.paullouis.travel.data.ItineraryCache;
import com.paullouis.travel.model.GeneratedItinerary;
import com.paullouis.travel.model.TravelDestination;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
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

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(ctx.getPackageName());

        setContentView(R.layout.activity_trip_map);

        tripTitle = getIntent().getStringExtra("TRIP_TITLE");
        destination = getIntent().getStringExtra("TRIP_DESTINATION");
        if (tripTitle == null) tripTitle = "Parcours";
        if (destination == null) destination = "";

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.header), (v, insets) -> {
            Insets statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(0, statusBars.top, 0, 0);
            return insets;
        });

        ((TextView) findViewById(R.id.tvTripTitle)).setText(tripTitle);
        ((TextView) findViewById(R.id.tvDestination)).setText(destination);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        map = findViewById(R.id.mapView);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        map.setMultiTouchControls(true);

        loadTripData();
    }

    private void loadTripData() {
        GeneratedItinerary itinerary = ItineraryCache.getSelected();
        if (itinerary == null) {
            finish();
            return;
        }

        ((TextView) findViewById(R.id.tvDuration)).setText(itinerary.getDuration());
        ((TextView) findViewById(R.id.tvBudget)).setText(itinerary.getBudget());

        List<TravelDestination> destinations = itinerary.getDestinations();
        if (destinations == null || destinations.isEmpty()) {
            finish();
            return;
        }

        List<GeoPoint> points = new ArrayList<>();
        for (int i = 0; i < destinations.size(); i++) {
            TravelDestination dest = destinations.get(i);
            GeoPoint gp = new GeoPoint(dest.getLatitude(), dest.getLongitude());
            points.add(gp);
            addNumberedMarker(gp, dest.getName(), i + 1);
        }

        drawPath(points);
        centerMapOnPoints(points);

        ((TextView) findViewById(R.id.tvPlacesCount)).setText(String.valueOf(destinations.size()));
        ((TextView) findViewById(R.id.tvPhotosCount)).setText(itinerary.getBudget());
    }

    private void drawPath(List<GeoPoint> points) {
        Polyline line = new Polyline(map);
        line.setPoints(points);
        line.getOutlinePaint().setColor(Color.parseColor("#0891b2"));
        line.getOutlinePaint().setStrokeWidth(8.0f);
        line.getOutlinePaint().setStrokeCap(Paint.Cap.ROUND);
        map.getOverlays().add(line);
    }

    private void centerMapOnPoints(List<GeoPoint> points) {
        if (points.isEmpty()) return;
        if (points.size() == 1) {
            map.getController().setZoom(15.0);
            map.getController().setCenter(points.get(0));
            return;
        }

        double minLat = points.get(0).getLatitude();
        double maxLat = points.get(0).getLatitude();
        double minLon = points.get(0).getLongitude();
        double maxLon = points.get(0).getLongitude();

        for (GeoPoint gp : points) {
            if (gp.getLatitude() < minLat) minLat = gp.getLatitude();
            if (gp.getLatitude() > maxLat) maxLat = gp.getLatitude();
            if (gp.getLongitude() < minLon) minLon = gp.getLongitude();
            if (gp.getLongitude() > maxLon) maxLon = gp.getLongitude();
        }

        final BoundingBox bbox = new BoundingBox(maxLat, maxLon, minLat, minLon);
        map.post(() -> map.zoomToBoundingBox(bbox, true, 80));
    }

    private void addNumberedMarker(GeoPoint point, String title, int number) {
        float density = getResources().getDisplayMetrics().density;
        int circleDiameter = (int) (36 * density);
        int tipHeight = (int) (10 * density);
        int totalHeight = circleDiameter + tipHeight;

        Bitmap bmp = Bitmap.createBitmap(circleDiameter, totalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.parseColor("#0891b2"));
        float radius = circleDiameter / 2f;
        canvas.drawCircle(radius, radius, radius, circlePaint);

        Path tip = new Path();
        tip.moveTo(radius - 6 * density, circleDiameter - 4 * density);
        tip.lineTo(radius + 6 * density, circleDiameter - 4 * density);
        tip.lineTo(radius, totalHeight);
        tip.close();
        canvas.drawPath(tip, circlePaint);

        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(14 * density);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        float textY = radius - (textPaint.descent() + textPaint.ascent()) / 2f;
        canvas.drawText(String.valueOf(number), radius, textY, textPaint);

        Marker marker = new Marker(map);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title);
        marker.setIcon(new BitmapDrawable(getResources(), bmp));
        map.getOverlays().add(marker);
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
