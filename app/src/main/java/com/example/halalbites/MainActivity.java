package com.example.halalbites;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.Button;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.halalbites.databinding.ActivityMainBinding;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1; // Request code for Autocomplete widget

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Using ViewBinding to inflate the layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Google Places SDK
        initializePlacesSDK();

        // Set up the Toolbar
        Toolbar toolbar = binding.toolbar; // Use binding to access the toolbar
        setSupportActionBar(toolbar);

        // Find the BottomNavigationView
        BottomNavigationView navView = binding.navView;

        // Find the NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);

        // Ensure the NavHostFragment is not null
        if (navHostFragment == null) {
            throw new IllegalStateException("NavHostFragment not found. Check your layout XML.");
        }

        // Retrieve the NavController from the NavHostFragment
        NavController navController = navHostFragment.getNavController();

        // Set up the AppBarConfiguration to include the top-level destinations
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_delivery, R.id.navigation_scanner)
                .build();

        // Set up ActionBar with NavController for back navigation support
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Link the BottomNavigationView with the NavController
        NavigationUI.setupWithNavController(navView, navController);


    }

    /**
     * Initializes the Google Places SDK with the API key.
     */
    private void initializePlacesSDK() {
        // Replace "YOUR_API_KEY" with your actual API key
        String apiKey = "AIzaSyCym8qS83CDhwqncpPCZBtUc9YYlpmM54M";

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        // Create a PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);
    }

    /**
     * Launches the Google Places Autocomplete Widget.
     */
    private void launchAutocompleteWidget() {
        // Define the fields to retrieve
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);

        // Build the intent to launch the Autocomplete widget
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);

        // Start the activity for result
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    /**
     * Handles the result from the Autocomplete widget.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Retrieve the selected place
                Place place = Autocomplete.getPlaceFromIntent(data);
                Toast.makeText(this, "Place: " + place.getName() + ", " + place.getAddress(), Toast.LENGTH_LONG).show();
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // Handle the error
                Toast.makeText(this, "Error: Could not fetch place details.", Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // User canceled the operation
                Toast.makeText(this, "Autocomplete canceled.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handle up navigation
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            return navController.navigateUp() || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }
}
