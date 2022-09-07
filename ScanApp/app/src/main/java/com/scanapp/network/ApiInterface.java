package com.scanapp.network;


import com.scanapp.network.remote.TokenResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface
{

    @POST("identity/connect/token")
    @FormUrlEncoded
    Call<TokenResponse> getToken(@Field("grant_type") String grantType,
            @Field("client_id") String client_id,@Field("client_secret") String client_secret);
    @POST("identity/connect/token")
    @FormUrlEncoded
    Call<TokenResponse> submitSupply(@Field("grant_type") String grantType,
            @Field("client_id") String client_id,@Field("client_secret") String client_secret);

}


