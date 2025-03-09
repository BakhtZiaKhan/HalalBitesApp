package com.example.halalbites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.halalbites.databinding.ActivityMainBinding;
import com.example.halalbites.ui.Register.RegisterFragment;
import com.example.halalbites.ui.Signin.SignInActivity;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.gms.common.api.Status;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PlacesClient placesClient; // Places API Client

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Google Places SDK
        initializePlacesSDK();

        // Set up the Toolbar
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        // Set up Profile Icon Click Listener
        ImageView profileImage = toolbar.findViewById(R.id.profile_image);
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
        });

        // Set up Google Places Autocomplete Search Bar
        setupPlacesAutocomplete();

        // Set up BottomNavigationView and NavController
        setupNavigation();

        // Handle Intent if coming from SignInActivity
        handleIntent();
    }

    /**
     * Handles the Intent from SignInActivity to navigate to RegisterFragment
     */
    private void handleIntent() {
        if (getIntent().getBooleanExtra("navigateToRegister", false)) {
            getIntent().removeExtra("navigateToRegister"); // Remove the flag to avoid re-triggering

            // Navigate to RegisterFragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, new RegisterFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * Initializes the Google Places SDK securely using BuildConfig.
     */
    private void initializePlacesSDK() {
        if (!Places.isInitialized()) {
            // Securely retrieve the API Key from BuildConfig
            Places.initialize(getApplicationContext(), BuildConfig.GOOGLE_MAPS_API_KEY);
        }
        placesClient = Places.createClient(this);
    }

    /**
     * Sets up the Google Places Autocomplete Fragment without duplicates.
     */
    private void setupPlacesAutocomplete() {
        // Get reference to the existing AutocompleteSupportFragment from XML
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        if (autocompleteFragment != null) {
            // Ensure the fragment view exists before setting properties
            if (autocompleteFragment.getView() == null) {
                Log.e("Places", "Autocomplete fragment view is null. It may not be attached yet.");
                return;
            }

            // Prevent duplicate setup by clearing previous listeners
            autocompleteFragment.setOnPlaceSelectedListener(null);

            // Set hint to differentiate this search bar
            autocompleteFragment.setHint("Search for a place");

            // Define the place fields you want
            autocompleteFragment.setPlaceFields(Arrays.asList(
                    Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

            // Set up a listener for the selected place
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    Toast.makeText(MainActivity.this,
                            "Selected: " + place.getName(),
                            Toast.LENGTH_LONG).show();
                    Log.i("Places", "Place selected: " + place.getName() + " - " + place.getAddress());
                }

                @Override
                public void onError(@NonNull Status status) {
                    Log.e("Places", "Error selecting place: " + status.getStatusMessage());
                }
            });

        } else {
            Log.e("Places", "Autocomplete fragment is NULL. Check activity_main.xml.");
        }
    }

    /**
     * Sets up the BottomNavigationView and links it with the NavController.
     */
    private void setupNavigation() {
        BottomNavigationView navView = binding.navView;

        // Get NavHostFragment and NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);

        if (navHostFragment == null) {
            throw new IllegalStateException("NavHostFragment not found. Check your layout XML.");
        }

        NavController navController = navHostFragment.getNavController();

        // Configure top-level destinations
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_delivery, R.id.navigation_scanner)
                .build();

        // Link BottomNavigationView with NavController
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            return navController.navigateUp() || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }
}
