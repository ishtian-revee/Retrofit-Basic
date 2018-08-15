package com.rev.githubrepo.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.rev.githubrepo.R;

import butterknife.BindView;
import butterknife.OnClick;

public class PostActivity extends AppCompatActivity {

    // fields
    @BindView(R.id.et_email)
    public EditText email;
    @BindView(R.id.et_password)
    public EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
    }

    @OnClick(R.id.btn_create)
    public void onClickCreate(){

    }
}
