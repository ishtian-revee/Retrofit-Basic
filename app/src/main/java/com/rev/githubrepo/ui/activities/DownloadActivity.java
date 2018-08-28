package com.rev.githubrepo.ui.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.rev.githubrepo.R;
import com.rev.githubrepo.api.service.FileDownloadClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

        downloadFile();
    }

    @OnClick(R.id.btn_start_file_download)
    public void onClickStartDownload(){
        // for some reason it's not working for button click
//        downloadFile();
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
                boolean success = writeResponseBodyToDisk(response.body());
                showMessage("Download successful: " + success);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showMessage("Error!");
            }
        });
    }

    private boolean writeResponseBodyToDisk(ResponseBody body){
        try {
            File futureStudioFile = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "Future Studio Icon.png"
            );

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try{
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioFile);

                while(true){
                    int read = inputStream.read(fileReader);
                    if(read == -1){
                        break;
                    }

                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d("Future Studio", "file upload: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();
                return true;
            } catch (IOException e){
                return false;
            } finally {
                if(inputStream != null){
                    inputStream.close();
                }

                if(outputStream != null){
                    outputStream.close();
                }
            }
        } catch (IOException e){
            return false;
        }
    }

    // TODO: override onRequestPermissionsResult()

    public void showMessage(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
