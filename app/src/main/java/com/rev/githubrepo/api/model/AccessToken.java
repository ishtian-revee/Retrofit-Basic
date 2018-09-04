package com.rev.githubrepo.api.model;

import com.google.gson.annotations.SerializedName;

public class AccessToken {

    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("token_type")
    private String tokenType;

    public String getAccessToken(){ return accessToken; }
    public String getTokenType(){ return tokenType; }
}
