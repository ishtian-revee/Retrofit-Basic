package com.rev.githubrepo.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.rev.githubrepo.R;
import com.rev.githubrepo.api.model.ApiError;
import com.rev.githubrepo.api.model.User2;
import com.rev.githubrepo.api.service.UserClient;
import com.rev.githubrepo.helpers.ErrorUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetUserIdActivity extends AppCompatActivity {

    // fields
    @BindView(R.id.et_user_id)
    public EditText userId;

    private UserClient client;
    private Call<User2> call;
    private Call<ResponseBody> multipleQueryCall;
    private String url;

    // create retrofit instance
//    private static Retrofit.Builder builder = new Retrofit.Builder()
//            .baseUrl("https://reqres.in/api/")
//            .addConverterFactory(GsonConverterFactory.create());
    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create());

    public static Retrofit retrofit = builder.build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user_id);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_get_user)
    public void onClickGetUser(){
        executeUserRequest(userId.getText().toString());
    }

    private void executeUserRequest(String userId){
        client = retrofit.create(UserClient.class);
        call = client.getUserById(userId);

        call.enqueue(new Callback<User2>() {
            @Override
            public void onResponse(Call<User2> call, Response<User2> response) {
                // this means at least we got the response
                if (response.isSuccessful()) {
                    showMessage("server returned user: " + response.body());
                }else{      // server overloaded type errors, incorrect input errors
                    // we can handle it like this in simple way
//                    switch (response.code()) {
//                        case 404:
//                            showMessage("server returned error: user not found!");
//                            break;
//                        case 500:
//                            showMessage("server returned error: server is broken!");
//                            break;
//                        default:
//                            showMessage("server returned error: unknown error!");
//                    }

                    // we can also display error body
//                    try {
//                        showMessage("server returned error: " + response.errorBody().string());
//                    } catch (IOException e) {
//                        showMessage("Unknown error!");
//                        e.printStackTrace();
//                    }

                    // the best way to simple error handling
                    ApiError apiError = ErrorUtils.parseError(response);
                    showMessage(apiError.getMessage());
                }
            }

            // this happens when errors like device doesn't have any internet connection
            @Override
            public void onFailure(Call<User2> call, Throwable t) {
                showMessage("On failure: ");
            }
        });

        // for multiple query parts
        multipleQueryCall = client.searchForUsers(11, "asc", 1);

        // for optional parameters
        multipleQueryCall = client.searchForUsers(11, null, null);

        // for multiple ids
        multipleQueryCall = client.searchForUsers(Arrays.asList(11, 12, 13), null, 1);

        // asynchronous call
//        multipleQueryCall.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//            }
//        });

        // creating a map fot dynamic query parameter
        Map<String, Object> map = new HashMap<>();
        map.put("id", 11);
        map.put("sort", "asc");
        map.put("page", 1);
        Call<ResponseBody> dynamicQueryCall = client.searchForUsers(map);
    }

    public void showMessage(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
