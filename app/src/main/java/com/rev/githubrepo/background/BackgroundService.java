package com.rev.githubrepo.background;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.rev.githubrepo.api.model.User;
import com.rev.githubrepo.api.service.UserClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BackgroundService extends IntentService {

    public BackgroundService() {
        super("Background Service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        User user = new User("Revee", "Engineer");

        // create retrofit instance
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://reqres.in/api/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        // get client and call object for the request
        UserClient client = retrofit.create(UserClient.class);
        Call<User> call = client.createAccount(user);

        // synchronous way of network request
        try {
            Response<User> result = call.execute();
            Log.i("Retrofit", "success!");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("Retrofit", "failure!");
        }
    }
}
