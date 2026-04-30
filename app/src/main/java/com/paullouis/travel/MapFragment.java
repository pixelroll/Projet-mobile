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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.paullouis.travel.data.DataCallback;
import com.paullouis.travel.data.FirebaseRepository;
import com.paullouis.travel.model.Photo;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment {

    private MapView map;
    private View cardPreview;
    private ImageView ivPreview, btnCloseCard;
    private TextView tvPreviewTitle, tvPreviewLocation, tvPreviewAuthor, tvPreviewLikes;

    // Loaded photos indexed by ID for fast lookup on marker click
    private final Map<String, Photo> photoIndex = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context ctx = requireActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(ctx.getPackageName());
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        map = view.findViewById(R.id.mapView);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        map.setMultiTouchControls(true);

        cardPreview = view.findViewById(R.id.cardMapPreview);
        ivPreview = view.findViewById(R.id.ivPreview);
        tvPreviewTitle = view.findViewById(R.id.tvPreviewTitle);
        tvPreviewLocation = view.findViewById(R.id.tvPreviewLocation);
        tvPreviewAuthor = view.findViewById(R.id.tvPreviewAuthor);
        tvPreviewLikes = view.findViewById(R.id.tvPreviewLikes);
        btnCloseCard = view.findViewById(R.id.btnCloseCard);
        btnCloseCard.setOnClickListener(v -> cardPreview.setVisibility(View.GONE));

        // Default center: Europe
        map.getController().setZoom(5.0);
        map.getController().setCenter(new GeoPoint(48.8566, 2.3522));

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        loadPhotosOnMap();
    }

    private void loadPhotosOnMap() {
        FirebaseRepository.getInstance().getUserPhotos(new DataCallback<List<Photo>>() {
            @Override
            public void onSuccess(List<Photo> photos) {
                if (!isAdded()) return;
                for (Photo photo : photos) {
                    if (photo.getLat() == 0 && photo.getLng() == 0) continue;
                    photoIndex.put(photo.getId(), photo);
                    String imageSource = photo.getImageUrl() != null ? photo.getImageUrl()
                            : (photo.getImageResId() != 0 ? String.valueOf(photo.getImageResId()) : null);
                    if (imageSource == null) continue;

                    GeoPoint point = new GeoPoint(photo.getLat(), photo.getLng());
                    addPhotoMarker(photo.getId(), point, photo.getTitle(),
                            photo.getLocationName(), photo.getLikes() + " j'aime",
                            photo.getAuthorName(), imageSource);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("MapFragment", "Failed to load photos", e);
            }
        });
    }

    private void addPhotoMarker(String photoId, GeoPoint point, String title, String location,
                                String likes, String author, String imageSource) {
        Object loadTarget = imageSource.matches("\\d+") ? Integer.parseInt(imageSource) : imageSource;
        Glide.with(this)
            .asBitmap()
            .load(loadTarget)
            .override(150, 150)
            .centerCrop()
            .into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap photo, @Nullable Transition<? super Bitmap> transition) {
                    if (!isAdded()) return;
                    try {
                        View customMarkerView = getLayoutInflater().inflate(R.layout.item_map_marker, null);
                        ImageView markerImage = customMarkerView.findViewById(R.id.markerImage);
                        markerImage.setImageBitmap(photo);

                        float density = getResources().getDisplayMetrics().density;
                        int widthPx = (int) (60 * density);
                        int heightPx = (int) (72 * density);
                        customMarkerView.measure(
                                View.MeasureSpec.makeMeasureSpec(widthPx, View.MeasureSpec.EXACTLY),
                                View.MeasureSpec.makeMeasureSpec(heightPx, View.MeasureSpec.EXACTLY));
                        customMarkerView.layout(0, 0, widthPx, heightPx);

                        Bitmap bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888);
                        customMarkerView.draw(new Canvas(bitmap));

                        Marker marker = new Marker(map);
                        marker.setPosition(point);
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        marker.setIcon(new BitmapDrawable(getResources(), bitmap));
                        marker.setTitle(title);
                        marker.setInfoWindow(null);
                        marker.setOnMarkerClickListener((m, mapView) -> {
                            showPhotoPreview(photoId, title, location, likes, author, imageSource);
                            mapView.getController().animateTo(m.getPosition());
                            return true;
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

    private void showPhotoPreview(String photoId, String title, String location, String likes,
                                  String author, String imageSource) {
        tvPreviewTitle.setText(title != null ? title : "");
        tvPreviewLocation.setText(location != null ? location : "");
        tvPreviewLikes.setText(likes);
        tvPreviewAuthor.setText(author != null ? "Par " + author : "");

        Object loadTarget = imageSource.matches("\\d+") ? Integer.parseInt(imageSource) : imageSource;
        Glide.with(this).load(loadTarget).centerCrop().into(ivPreview);

        cardPreview.setVisibility(View.VISIBLE);
        cardPreview.setAlpha(0f);
        cardPreview.setTranslationY(100f);
        cardPreview.animate().alpha(1f).translationY(0f).setDuration(300).start();

        cardPreview.setOnClickListener(v -> {
            Photo photo = photoIndex.get(photoId);
            if (photo != null && isAdded()) {
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
