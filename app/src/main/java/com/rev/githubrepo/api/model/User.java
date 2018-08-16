package com.rev.githubrepo.api.model;

public class User {

    // fields
    private String username;
    private String job;
    private int id;
    private String createdAt;

    public User(String username, String job){
        this.username = username;
        this.job = job;
    }

    // getters
    public String getUsername(){ return this.username; }
    public String getJob(){ return this.job; }
    public int getId(){ return this.id; }
    public String getCreatedAt(){ return this.createdAt; }
}
