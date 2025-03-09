package com.example.halalbites.ui.network;

import com.example.halalbites.models.Users;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface UserService {

    // Get all users
    @GET("api/users")
    Call<List<Users>> getAllUsers();

    // Get user by username
    @GET("api/users/{username}")
    Call<Users> getUserByUsername(@Path("username") String username);

    // Create a new user
    @POST("api/users")
    Call<Users> createUser(@Body Users user);
}
