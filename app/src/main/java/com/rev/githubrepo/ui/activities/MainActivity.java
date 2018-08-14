package com.rev.githubrepo.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.rev.githubrepo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    // fields
    @BindView(R.id.repository_list_layout)
    public LinearLayout repositoryListLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.repository_list_layout)
    public void onClickRepositoryLayout(){
        Intent main = new Intent(MainActivity.this, RepositoryListActivity.class);
        startActivity(main);
    }
}
