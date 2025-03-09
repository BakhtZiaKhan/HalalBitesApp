package com.example.halalbites.ui.Signin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.halalbites.MainActivity;
import com.example.halalbites.R;
import com.example.halalbites.models.Users;
import com.example.halalbites.network.RetrofitClient;
import com.example.halalbites.network.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button signInButton, registerButton;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize UI elements
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signInButton = findViewById(R.id.signInButton);
        registerButton = findViewById(R.id.btnRegister);

        // Initialize Retrofit Service
        userService = RetrofitClient.getInstance().create(UserService.class);

        // Sign-In Button Logic
        signInButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(SignInActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            authenticateUser(email, password);
        });

        // Register Button Click (Navigate to RegisterFragment)
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            intent.putExtra("navigateToRegister", true); // Pass data to open RegisterFragment
            startActivity(intent);
        });
    }

    // 🔹 Function to authenticate user via Retrofit API call
    private void authenticateUser(String email, String password) {
        Log.d("API_TEST", "Starting API call for user: " + email);

        Call<Users> call = userService.getUserByUsername(email);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Users user = response.body();
                    Log.d("API_TEST", "User found: " + user.getUsername());

                    // Validate password (Check for null values)
                    if (user.getPassword() != null && user.getPassword().equals(password)) {
                        Log.d("API_TEST", "Login successful!");
                        Toast.makeText(SignInActivity.this, "Sign-in successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignInActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Log.e("API_TEST", "Incorrect password entered");
                        Toast.makeText(SignInActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("API_TEST", "User not found. Response Code: " + response.code());
                    Toast.makeText(SignInActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Log.e("API_TEST", "API call failed: " + t.getMessage());
                Toast.makeText(SignInActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
