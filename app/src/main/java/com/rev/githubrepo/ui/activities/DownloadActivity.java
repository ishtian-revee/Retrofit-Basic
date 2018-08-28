package com.rev.githubrepo.ui.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.rev.githubrepo.R;
import com.rev.githubrepo.api.service.FileDownloadClient;

import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DownloadActivity extends AppCompatActivity {

    // fields
    private static final int MY_PERMISSIONS_REQUEST = 100;
    private Retrofit retrofit;
    private Retrofit.Builder builder;
    private FileDownloadClient client;
    private Call<ResponseBody> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        if(ContextCompat.checkSelfPermission(DownloadActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(DownloadActivity.this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST);
        }
    }

    @OnClick(R.id.btn_start_file_download)
    public void onClickStartDownload(){
        downloadFile();
    }

    public void downloadFile(){
        // create retrofit instance
        builder = new Retrofit.Builder().baseUrl("https://futurestud.io/");
        retrofit = builder.build();

        client = retrofit.create(FileDownloadClient.class);
        call = client.downloadFile();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                showMessage("Success!");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showMessage("Error!");
            }
        });
    }

    public void showMessage(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
