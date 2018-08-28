package com.rev.githubrepo.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.rev.githubrepo.R;
import com.rev.githubrepo.api.model.GitHubRepo;
import com.rev.githubrepo.api.service.GitHubClient;
import com.rev.githubrepo.ui.adapter.GitHubRepoAdapter;

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

    private Retrofit retrofit;
    private Retrofit.Builder builder;
    private GitHubClient client;
    private Call<List<GitHubRepo>> call;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repository_list);
        ButterKnife.bind(this);

        initRetrofit();
    }

    public void initRetrofit(){
        // initializing builder, declaring base url add adding converter factory
        builder = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();

        client = retrofit.create(GitHubClient.class);
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
