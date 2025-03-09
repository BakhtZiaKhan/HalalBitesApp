package com.example.halalbites.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.halalbites.R;
import com.example.halalbites.ui.Signin.SignInActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private PlacesClient placesClient;

    // Permission launcher for runtime permissions
    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    moveToCurrentLocationAndShowHalalRestaurants();
                } else {
                    Toast.makeText(requireContext(), "Location permission is required to use this feature.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Google Places API
        if (!Places.isInitialized()) {
            Places.initialize(requireContext().getApplicationContext(), getString(R.string.google_maps_key));
        }

        // Initialize Location & Places API clients
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        placesClient = Places.createClient(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up Toolbar
        setupToolbar(view);

        // Set up Google Places Autocomplete
        setupPlacesAutocomplete();

        // Set up Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Initializes and sets up the Toolbar.
     */
    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (toolbar == null) {
            Log.e("HomeFragment", "Toolbar is null! Check fragment_home.xml.");
            return;
        }

        toolbar.inflateMenu(R.menu.toolbar_menu);

        // Handle Profile Icon Click
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_profile) {
                // Launch SignInActivity
                startActivity(new Intent(requireContext(), SignInActivity.class));
                return true;
            }
            return false;
        });
    }

    /**
     * Sets up Google Places Autocomplete Fragment.
     */
    private void setupPlacesAutocomplete() {
        AutocompleteSupportFragment autocompleteFragment =
                (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        if (autocompleteFragment != null) {
            autocompleteFragment.setOnPlaceSelectedListener(null);
            autocompleteFragment.setHint("Search for a place");
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    LatLng placeLatLng = place.getLatLng();
                    if (placeLatLng != null) {
                        googleMap.clear();
                        googleMap.addMarker(new MarkerOptions()
                                .position(placeLatLng)
                                .title(place.getName()));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 15));
                    }
                }

                @Override
                public void onError(@NonNull Status status) {
                    Toast.makeText(requireContext(), "Error selecting place: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Log.e("HomeFragment", "Error: Autocomplete fragment is NULL. Check fragment_home.xml.");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Check for location permissions before enabling location features
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            moveToCurrentLocationAndShowHalalRestaurants();
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void moveToCurrentLocationAndShowHalalRestaurants() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                googleMap.addMarker(new MarkerOptions()
                        .position(userLocation)
                        .title("Your Location"));

                fetchNearbyHalalRestaurants(userLocation);
            } else {
                Toast.makeText(requireContext(), "Could not retrieve current location.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Failed to get current location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchNearbyHalalRestaurants(LatLng userLocation) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("Permissions", "Location permission denied! Cannot fetch places.");
            return;
        }

        List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID,
                Place.Field.LAT_LNG,
                Place.Field.NAME,
                Place.Field.TYPES
        );

        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

        try {
            placesClient.findCurrentPlace(request).addOnSuccessListener(response -> {
                for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                    Place place = placeLikelihood.getPlace();

                    if (place.getTypes() != null && place.getTypes().contains(Place.Type.RESTAURANT)) {
                        if (place.getName() != null && place.getName().toLowerCase().contains("halal")) {
                            LatLng placeLatLng = place.getLatLng();
                            if (placeLatLng != null) {
                                googleMap.addMarker(new MarkerOptions()
                                        .position(placeLatLng)
                                        .title(place.getName() + " (Halal)")
                                        .snippet("Halal-certified restaurant"));
                            }
                        }
                    }
                }
            }).addOnFailureListener(e -> {
                Log.e("PlacesAPI", "Failed to fetch places: " + e.getMessage());
            });
        } catch (SecurityException e) {
            Log.e("Permissions", "SecurityException: " + e.getMessage());
        }
    }
}
