package com.gra.converters.money.services;

import com.gra.converters.money.services.dto.RateResponse;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface CurrencyService {

    @GET("/latest.json")
    public void getRates(@Query("app_id") String key, Callback<RateResponse> callback);

    @GET("/currencies.json")
    public void getCurrencyMappings(@Query("app_id") String key, Callback<HashMap<String, String>> callback);
}