package com.rev.githubrepo.api.model;

public class ApiError {

    // fields
    private int statusCode;
    private String endPoint;
    private String message = "Unknown error.";

    public int getStatusCode(){ return this.statusCode; }
    public String getEndPoint(){ return this.endPoint; }
    public String getMessage(){ return this.message; }
}
