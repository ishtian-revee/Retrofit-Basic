package com.rev.githubrepo.ui.activities;

import android.app.IntentService;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.rev.githubrepo.BuildConfig;
import com.rev.githubrepo.R;
import com.rev.githubrepo.api.model.User;
import com.rev.githubrepo.api.service.UserClient;
import com.rev.githubrepo.background.BackgroundService;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class PostActivity extends AppCompatActivity {

    // fields
    @BindView(R.id.et_user_name)
    public EditText etUsername;
    @BindView(R.id.et_job)
    public EditText etJob;

    private Retrofit retrofit;
    private Retrofit.Builder builder;
    private UserClient client;
    private Call<User> call;
    private OkHttpClient.Builder okHttpClientBuilder;
    private HttpLoggingInterceptor interceptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_send)
    public void onClickSend(){
        // retrieving input data
        final String username = etUsername.getText().toString();
        final String job = etJob.getText().toString();

        User user = new User(username, job);
        sendNetworkingRequest(user);
    }

    public void sendNetworkingRequest(User user){
        // select any of this 2 function and uncomment the another one
//        sendWithDefaultRetrofit();
        sendWithOkHttp();

        client = retrofit.create(UserClient.class);
        call = client.createAccount(user);

        // there are two ways of network requests
        // asynchronous way
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                showMessage("Successfully done.\nUser id: " + response.body().getId());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                showMessage("Something went wrong...!!!");
            }
        });

        /*
        if we run a synchronous network request on the UI thread, it will hold the entire UI until
        the request is done. that is why it makes the app crashes every time whenever we run a
        synchronous network request in UI thread. so make sure we always run synchronous network
        request on the thread which is not the UI thread
         */

        Intent intent = new Intent(PostActivity.this, BackgroundService.class);
        startActivity(intent);
    }

    // generally retrofit uses the default instance okhttp as the network layer
    public void sendWithDefaultRetrofit(){
        // initializing builder, declaring base url add adding converter factory
        // creating retrofit instance
        builder = new Retrofit.Builder()
                .baseUrl("https://reqres.in/api/")
                .addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();

        // we can use scalar converter factory to to make a plain tex request
//        builder = new Retrofit.Builder()
//                .baseUrl("https://reqres.in/api/")
//                .addConverterFactory(ScalarsConverterFactory.create());
//        retrofit = builder.build();
    }

    // this method is to make a custom okhttp instance as the network layer for retrofit
    public void sendWithOkHttp(){
        // create OkHttp client
        okHttpClientBuilder = new OkHttpClient.Builder();
        interceptor = new HttpLoggingInterceptor();
        // to have all information such as request lines, headers and body
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // managing request headers in okhttp interceptor
        // the advantage of this is that the interceptor will be called for every single request we are doing
//        okHttpClientBuilder.addInterceptor(new Interceptor() {
//            @Override
//            public okhttp3.Response intercept(Chain chain) throws IOException {
//                // getting the chain request
//                Request request = chain.request();
//                // adding header to the new request
//                Request.Builder newRequest = request.newBuilder().header("Authorization", "secret-key");
//                return chain.proceed(newRequest.build());
//            }
//        });

        // the app will only log if we are in development mode
        if (BuildConfig.DEBUG){
            okHttpClientBuilder.addInterceptor(interceptor);
        }

        // creating retrofit instance
        builder = new Retrofit.Builder()
                .baseUrl("https://reqres.in/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClientBuilder.build());
        retrofit = builder.build();
    }

    public void showMessage(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
