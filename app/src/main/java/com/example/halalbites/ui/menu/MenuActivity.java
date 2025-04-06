package com.example.halalbites.ui.menu;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.halalbites.R;
import com.example.halalbites.adapters.MenuAdapter;
import com.example.halalbites.models.MenuItem;
import com.example.halalbites.network.RetrofitClient;
import com.example.halalbites.network.UserService;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MenuAdapter menuAdapter;
    private Button orderButton;
    private long restaurantId;
    private String restaurantName; // optional if needed for ordering
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Initialize UI elements
        recyclerView = findViewById(R.id.recyclerViewMenu);
        orderButton = findViewById(R.id.btnOrder);

        // Retrieve restaurant details from Intent extras
        restaurantId = getIntent().getLongExtra("restaurantId", -1);
        restaurantName = getIntent().getStringExtra("restaurantName");

        // Initialize Retrofit service
        userService = RetrofitClient.getInstance().create(UserService.class);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        menuAdapter = new MenuAdapter();
        recyclerView.setAdapter(menuAdapter);

        // Fetch the menu items from the backend
        fetchMenuItems();

        // Set up order button listener
        orderButton.setOnClickListener(v -> {
            // Implement your order logic here (record order using restaurantId/restaurantName)
            recordOrder();
        });
    }

    private void fetchMenuItems() {
        userService.getMenuItems(restaurantId).enqueue(new Callback<List<MenuItem>>() {
            @Override
            public void onResponse(Call<List<MenuItem>> call, Response<List<MenuItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MenuItem> menuItems = response.body();
                    menuAdapter.setMenuItems(menuItems);
                } else {
                    Toast.makeText(MenuActivity.this, "Failed to load menu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MenuItem>> call, Throwable t) {
                Toast.makeText(MenuActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void recordOrder() {
        // Here you can call your existing order API logic.
        // For example, if you already have a method in UserService:
        // userService.recordOrder(userId, restaurantName).enqueue(...);
        Toast.makeText(this, "Order placed for " + restaurantName, Toast.LENGTH_SHORT).show();
    }
}
