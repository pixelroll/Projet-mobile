package com.paullouis.travel;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LocationPickerDialogFragment extends DialogFragment {

    private EditText etLocationSearch;
    private ListView lvResults;
    private ImageButton btnCurrentLocation, btnMapPicker, btnClose;
    private ProgressBar pbLoading;
    private SimpleAdapter adapter;
    private List<Map<String, String>> resultsList = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;
    private Geocoder geocoder;
    private LocationSelectedListener listener;

    public interface LocationSelectedListener {
        void onLocationSelected(String locationName, double lat, double lng);
    }

    public static LocationPickerDialogFragment newInstance(LocationSelectedListener listener) {
        LocationPickerDialogFragment fragment = new LocationPickerDialogFragment();
        fragment.listener = listener;
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_location_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etLocationSearch = view.findViewById(R.id.etLocationSearch);
        lvResults = view.findViewById(R.id.lvLocationResults);
        btnCurrentLocation = view.findViewById(R.id.btnCurrentLocation);
        btnMapPicker = view.findViewById(R.id.btnMapPicker);
        btnClose = view.findViewById(R.id.btnClose);
        pbLoading = view.findViewById(R.id.pbLoading);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        geocoder = new Geocoder(requireContext(), Locale.getDefault());

        setupAdapter();
        setupListeners();
    }

    private void setupAdapter() {
        adapter = new SimpleAdapter(
            requireContext(),
            resultsList,
            android.R.layout.simple_list_item_2,
            new String[]{"name", "address"},
            new int[]{android.R.id.text1, android.R.id.text2}
        );
        lvResults.setAdapter(adapter);
        lvResults.setOnItemClickListener((parent, view, position, id) -> {
            Map<String, String> item = resultsList.get(position);
            String name = item.get("name");
            double lat = Double.parseDouble(item.get("lat"));
            double lng = Double.parseDouble(item.get("lng"));
            if (listener != null) {
                listener.onLocationSelected(name, lat, lng);
            }
            dismiss();
        });
    }

    private void setupListeners() {
        etLocationSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    searchLocations(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnCurrentLocation.setOnClickListener(v -> getCurrentLocation());
        btnMapPicker.setOnClickListener(v -> openMapPicker());
        btnClose.setOnClickListener(v -> dismiss());
    }

    private void searchLocations(String query) {
        pbLoading.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                List<Address> addresses = geocoder.getFromLocationName(query, 10);
                resultsList.clear();
                if (addresses != null) {
                    for (Address address : addresses) {
                        Map<String, String> item = new HashMap<>();
                        item.put("name", address.getAddressLine(0) != null ? address.getAddressLine(0) : address.getCountryName());
                        item.put("address", address.getCountryName());
                        item.put("lat", String.valueOf(address.getLatitude()));
                        item.put("lng", String.valueOf(address.getLongitude()));
                        resultsList.add(item);
                    }
                }
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                        pbLoading.setVisibility(View.GONE);
                    });
                }
            } catch (IOException e) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        pbLoading.setVisibility(View.GONE);
                        Toast.makeText(requireContext(), "Erreur de recherche", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            pbLoading.setVisibility(View.VISIBLE);
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        pbLoading.setVisibility(View.GONE);
                        if (addresses != null && !addresses.isEmpty()) {
                            Address address = addresses.get(0);
                            String locationName = address.getAddressLine(0) != null ? address.getAddressLine(0) : address.getCountryName();
                            if (listener != null) {
                                listener.onLocationSelected(locationName, location.getLatitude(), location.getLongitude());
                            }
                            dismiss();
                        }
                    } catch (IOException e) {
                        pbLoading.setVisibility(View.GONE);
                        Toast.makeText(requireContext(), "Erreur de géocodage", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    pbLoading.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Localisation non disponible", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(requireContext(), "Permission de localisation requise", Toast.LENGTH_SHORT).show();
        }
    }

    private void openMapPicker() {
        dismiss();
        // This will be handled by MapPickerFragment, triggered from activity
    }
}
