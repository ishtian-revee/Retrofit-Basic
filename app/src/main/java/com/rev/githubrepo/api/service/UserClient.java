package com.rev.githubrepo.api.service;

import com.rev.githubrepo.api.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserClient {

    @POST("users")
    Call<User> createAccount(@Body User user);
}
