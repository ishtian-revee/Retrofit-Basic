package com.rev.githubrepo.api.service;

import com.rev.githubrepo.api.model.User;
import com.rev.githubrepo.api.model.User2;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserClient {

    @POST("users")
    Call<User> createAccount(@Body User user);

//    @GET("users/{id}")
//    Call<User2> getUserById(@Path("id") String user);

    @GET("posts/{id}")
    Call<User2> getUserById(@Path("id") String user);

    // with custom header requests
    @Headers({
            "Cache-Control: max-age=3600",
            "User-Agent: Android"
    }) // this is static header declaration
    @POST("users")
    Call<User> createSomething(
            @Header("Username") String userName,    // this is dynamic header declaration
            @Body User user
    );

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

    // for multiple query parameters
    // multiple query parameters with @Query
    @GET("user")
    Call<ResponseBody> searchForUsers(
            @Query("id") int id,
            @Query("sort") String order,
            @Query("page") int page
    );

    // optional
    // Integer, String these are nullable types
    // multiple query parameters by passing null
    @GET("user")
    Call<ResponseBody> searchForUsers(
            @Query("id") Integer id,
            @Query("sort") String order,
            @Query("page") Integer page
    );

    // for searching multiple attributes at the same time
    // // multiple query parameters with List<>
    @GET("user")
    Call<ResponseBody> searchForUsers(
            @Query("id") List<Integer> id,
            @Query("sort") String order,
            @Query("page") Integer page
    );
}
