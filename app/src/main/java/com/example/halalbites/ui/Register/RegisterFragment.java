package com.example.halalbites.ui.Register;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.halalbites.R;
import com.example.halalbites.models.Users;
import com.example.halalbites.network.RetrofitClient;
import com.example.halalbites.network.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    private EditText etUsername, etPassword, etEmail, etPhone;
    private Button btnRegister;
    private ProgressDialog progressDialog;
    private UserService userService;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Initialize Views
        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        btnRegister = view.findViewById(R.id.btnRegister);

        // Initialize Retrofit Service
        userService = RetrofitClient.getInstance().create(UserService.class);

        // Progress Dialog
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Registering...");

        // Register Button Click
        btnRegister.setOnClickListener(v -> registerUser());

        return view;
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(requireContext(), "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        // Create User Object
        Users newUser = new Users(username, email, password, phone); //

        // Call API
        Call<Users> call = userService.createUser(newUser);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API_TEST", "User registered: " + response.body().getUsername());
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Registration Successful!", Toast.LENGTH_SHORT).show()
                    );
                } else {
                    Log.e("API_TEST", "Registration failed. Response Code: " + response.code());
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Registration Failed! Try Again.", Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("API_TEST", "API request failed: " + t.getMessage());
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        });
    }
}
