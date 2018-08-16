package com.rev.githubrepo.api.service;

import com.rev.githubrepo.api.model.User;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UserClient {

    @POST("users")
    Call<User> createAccount(@Body User user);

    @Multipart          // need to describe endpoint as a multipart request otherwise retrofit will use regular HTTP request encoding
    @POST("photos")     // even if it's files then it needs to be POST method or can be PUT method
    Call<ResponseBody> uploadPhoto(             // as multipart we need to describe each part as @Part annotation
            @Part("description") RequestBody description,   // in this @Part annotation offers a name where Multipart.part class doesn't need that
            @Part MultipartBody.Part photo      // Multipart.Part class is for multiple request part which needs to describe photo
    );

    // base: https://jsonplaceholder.typicode.com/
    @Multipart
    @POST("photos")
    Call<ResponseBody> sendPhoto(
            @Part("albumId") RequestBody albumId,
            @Part("id") RequestBody id,
            @Part("title") RequestBody title,
            @Part MultipartBody.Part url,
            @Part MultipartBody.Part thumbnailUrl
    );
}
