package com.rev.githubrepo.api.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface FileDownloadClient {

    @GET("images/futurestudio-university-logo.png")
    Call<ResponseBody> downloadFile();
}
