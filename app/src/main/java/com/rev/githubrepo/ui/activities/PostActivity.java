package com.rev.githubrepo.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.rev.githubrepo.R;
import com.rev.githubrepo.api.model.User;
import com.rev.githubrepo.api.service.UserClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        // initializing builder, declaring base url add adding converter factory
        // creating retrofit instance
        builder = new Retrofit.Builder()
                .baseUrl("https://reqres.in/api/")
                .addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();

        client = retrofit.create(UserClient.class);
        call = client.createAccount(user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                showMessage("Successfully done.\n User id: " + response.body().getId());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                showMessage("Something went wrong...!!!");
            }
        });
    }

    public void showMessage(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
