package com.rev.githubrepo.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rev.githubrepo.R;
import com.rev.githubrepo.api.ServiceGenerator;
import com.rev.githubrepo.api.model.AccessToken;
import com.rev.githubrepo.api.model.GitHubRepo;
import com.rev.githubrepo.api.service.GitHubClient;
import com.rev.githubrepo.ui.adapter.GitHubRepoAdapter;

import java.text.DateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RepositoryListActivity extends AppCompatActivity {

    // fields
    @BindView(R.id.repo_list_view)
    public ListView listView;

    // oAuth credentials
    // it should either define client id and secret as constants or in string resources
    private static final String API_BASE_URL = "https://example.com/oauthloginpage";
    private static final String API_OAUTH_CLIENTID = "replace-me";
    private static final String API_OAUTH_CLIENTSECRET = "replace-me";
    private static final String API_OAUTH_REDIRECT = "nl.jpelgrm.retrofit2oauthrefresh://oauth";


    private Retrofit retrofit;
    private Retrofit.Builder builder;
    private GitHubClient client;
    private Call<List<GitHubRepo>> call;
    private Call<AccessToken> accessTokenCall;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repository_list);
        ButterKnife.bind(this);

        initRetrofit();

        // for oAuth authentication
//        Intent intent = new Intent(
//                Intent.ACTION_VIEW,
//                Uri.parse(API_BASE_URL + "/login" + "?client_id=" + API_OAUTH_CLIENTID + "&redirect_uri=" + API_OAUTH_REDIRECT));
//        startActivity(intent);
    }

    // for oAuth authentication
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Uri uri = getIntent().getData();
//
//        if(uri != null && uri.toString().startsWith(API_OAUTH_REDIRECT)){
//            String code = uri.getQueryParameter("code");
//
//            // initializing builder, declaring base url add adding converter factory
//            builder = new Retrofit.Builder()
//                    .baseUrl("https://github.com/")
//                    .addConverterFactory(GsonConverterFactory.create());
//            retrofit = builder.build();
//
//            client = retrofit.create(GitHubClient.class);
//            accessTokenCall = client.getAccessToken(API_OAUTH_CLIENTID, API_OAUTH_CLIENTSECRET, code);
//
//            accessTokenCall.enqueue(new Callback<AccessToken>() {
//                @Override
//                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
//                    showMessage("success!");
//                }
//
//                @Override
//                public void onFailure(Call<AccessToken> call, Throwable t) {
//                    showMessage("error!");
//                }
//            });
//        }
//    }

    public void initRetrofit(){
        // customizing gson
//        Gson gson = new GsonBuilder().serializeNulls().setDateFormat(DateFormat.LONG).create();
//        builder = new Retrofit.Builder()
//                .baseUrl("https://api.github.com/")
//                .addConverterFactory(GsonConverterFactory.create(gson));
//        retrofit = builder.build();

        // initializing builder, declaring base url add adding converter factory
//        builder = new Retrofit.Builder()
//                .baseUrl("https://api.github.com/")
//                .addConverterFactory(GsonConverterFactory.create());
//        retrofit = builder.build();
//
//        client = retrofit.create(GitHubClient.class);

        // for sustainable client
        client = ServiceGenerator.createService(GitHubClient.class);
        call = client.reposForUser("ishtian-revee");

        // for dynamic url: base url + end point
//        url = "https://api.github.com/ishtian-revee";
//        call = client.reposForUser(url);

        call.enqueue(new Callback<List<GitHubRepo>>() {
            @Override
            public void onResponse(Call<List<GitHubRepo>> call, Response<List<GitHubRepo>> response) {
                List<GitHubRepo> repos = response.body();
                listView.setAdapter(new GitHubRepoAdapter(RepositoryListActivity.this, repos));
            }

            @Override
            public void onFailure(Call<List<GitHubRepo>> call, Throwable t) {
                showMessage("error: ");
            }
        });
    }

    public void showMessage(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
