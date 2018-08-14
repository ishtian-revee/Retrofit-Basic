package com.rev.githubrepo.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rev.githubrepo.R;
import com.rev.githubrepo.api.model.GitHubRepo;

import java.util.List;

public class GitHubRepoAdapter extends ArrayAdapter<GitHubRepo> {

    // fields
    private Context context;
    private List<GitHubRepo> values;

    public GitHubRepoAdapter(Context context, List<GitHubRepo> values) {
        super(context, R.layout.list_items, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        if(row == null){
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_items, parent, false);
        }

        TextView txtRepoName = row.findViewById(R.id.txt_repo_name);

        GitHubRepo item = values.get(position);
        String repoName = item.getName();
        txtRepoName.setText(repoName);

        return row;
    }
}
