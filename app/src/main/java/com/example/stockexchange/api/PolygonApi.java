package com.example.stockexchange.api;

import com.example.stockexchange.model.ApiResponse;
import com.example.stockexchange.model.StockResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PolygonApi {
    @GET("v3/reference/tickers")
    Call<ApiResponse> searchSymbol(
            @Query("search") String function,
            @Query("apiKey") String apiKey
    );

    @GET("v3/reference/tickers")
    Call<ApiResponse> getAllStocks(
            @Query("active") Boolean active,
            @Query("limit") int limit,
            @Query("apiKey") String apiKey
    );

    @GET("v2/aggs/ticker/{stocksTicker}/range/1/minute/{from}/{to}")
    Call<StockResponse> getStockData(
            @Path("stocksTicker") String stocksTicker,
            @Path("from") String from,// Dynamic symbol
            @Path("to") String to,
            @Query("adjusted") boolean adjusted,
            @Query("sort") String sort,
            @Query("apiKey") String apiKey
    );
}