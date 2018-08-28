package com.rev.githubrepo.api.service;

import com.rev.githubrepo.api.model.GitHubRepo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface GitHubClient {

    @GET("/users/{user}/repos")
    Call<List<GitHubRepo>> reposForUser(@Path("user") String user);

    // using dynamic url
//    @GET
//    Call<List<GitHubRepo>> reposForUser(@Url String url);
}
