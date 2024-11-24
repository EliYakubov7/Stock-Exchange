package com.example.stockexchange.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.stockexchange.api.NetworkResult;
import com.example.stockexchange.model.ApiResponse;
import com.example.stockexchange.model.StockResponse;
import com.example.stockexchange.repo.StocksRepo;

import java.util.List;

import javax.inject.Inject;

public class ApiViewModel extends ViewModel {

    private final StocksRepo repository;

    private final MutableLiveData<NetworkResult<List<ApiResponse.Result>>> _stateLiveData = new MutableLiveData<>();
    public LiveData<NetworkResult<List<ApiResponse.Result>>> getStateLiveData() {
        return _stateLiveData;
    }

    private final MutableLiveData<NetworkResult<StockResponse>> stockData = new MutableLiveData<>();
    public LiveData<NetworkResult<StockResponse>> getStockDataLive() {
        return stockData;
    }

    @Inject
    public ApiViewModel(StocksRepo repository) {
        this.repository = repository;
    }

    public void fetchBestMatches(String keywords, String apiKey, Context context) {
        repository.fetchBestMatches(keywords, apiKey, context).observeForever(_stateLiveData::setValue);
    }

    public void getAllStocks(int limit, String apiKey, Context context) {
        repository.getAllStocks(limit, apiKey, context).observeForever(_stateLiveData::setValue);
    }

    public void fetchStockUpdates(String stocksTicker, String from, String to, boolean adjusted, String sort, String apiKey, Context context) {
        repository.fetchStockUpdates(stocksTicker, from, to, adjusted, sort, apiKey, context).observeForever(stockData::setValue);
    }
}