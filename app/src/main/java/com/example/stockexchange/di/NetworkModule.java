package com.example.stockexchange.di;

import com.example.stockexchange.api.PolygonApi;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    @Provides
    public Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("https://api.polygon.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    @Provides
    public PolygonApi provideAlphaVantageApi(Retrofit retrofit) {
        return retrofit.create(PolygonApi.class);
    }
}
