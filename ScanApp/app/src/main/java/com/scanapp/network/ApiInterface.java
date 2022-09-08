package com.scanapp.network;


import com.scanapp.network.remote.SupplyModel;
import com.scanapp.network.remote.TokenResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @POST("identity/connect/token")
    @FormUrlEncoded
    Call<TokenResponse> getToken(@Field("grant_type") String grantType,
                                 @Field("client_id") String client_id, @Field("client_secret") String client_secret);

    @POST("identity/connect/token")
    @FormUrlEncoded
    Call<TokenResponse> submitSupply(@Field("grant_type") String grantType,
                                     @Field("client_id") String client_id, @Field("client_secret") String client_secret);

    @GET("/verification/product/gs1/{PRODUCT_CODE}/pack/{SERIAL_NUMBER}")
    Call<SupplyModel> getSupplyRequest(@Path("PRODUCT_CODE") String productCode, @Path("SERIAL_NUMBER") String serialNumber, @Query("batch") String batch, @Query("expiry") String expiry,
                                       @HeaderMap Map<String, String> header);
}


