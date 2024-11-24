package com.example.stockexchange.repo;

import android.content.Context;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.stockexchange.api.PolygonApi;
import com.example.stockexchange.api.NetworkResult;
import com.example.stockexchange.api.NetworkUtil;
import com.example.stockexchange.model.ApiResponse;
import com.example.stockexchange.model.StockResponse;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StocksRepo {
    private final PolygonApi api;
    @Inject
    public StocksRepo(PolygonApi api) {
        this.api = api;
    }

    public LiveData<NetworkResult<List<ApiResponse.Result>>> fetchBestMatches(String keywords, String apiKey, Context context) {
        MutableLiveData<NetworkResult<List<ApiResponse.Result>>> liveData = new MutableLiveData<>();
        liveData.setValue(NetworkResult.loading());

        if (NetworkUtil.isNetworkAvailable(context)) {
            api.searchSymbol(keywords, apiKey).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        saveBestMatchesToFirebase(response.body().getResults(), keywords);
                        liveData.setValue(NetworkResult.success(response.body().getResults()));
                    } else {
                        liveData.setValue(NetworkResult.error("Failed to fetch data"));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                    liveData.setValue(NetworkResult.error(t.getMessage()));
                }
            });
        } else {
            fetchBestMatchesFromFirebase(keywords, liveData);
        }

        return liveData;
    }

    public LiveData<NetworkResult<List<ApiResponse.Result>>> getAllStocks(int limit, String apiKey, Context context) {
        MutableLiveData<NetworkResult<List<ApiResponse.Result>>> liveData = new MutableLiveData<>();
        liveData.setValue(NetworkResult.loading());

        if (NetworkUtil.isNetworkAvailable(context)) {
            api.getAllStocks(true, limit, apiKey).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        saveToFirebase(response.body().getResults());
                        liveData.setValue(NetworkResult.success(response.body().getResults()));
                    } else {
                        liveData.setValue(NetworkResult.error("Failed to fetch data"));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                    liveData.setValue(NetworkResult.error(t.getMessage()));
                }
            });
        } else {
            fetchFromFirebase(liveData);
        }

        return liveData;
    }

    public LiveData<NetworkResult<StockResponse>> fetchStockUpdates(String stocksTicker, String from, String to, boolean adjusted, String sort, String apiKey, Context context) {
        MutableLiveData<NetworkResult<StockResponse>> liveData = new MutableLiveData<>();

        if (NetworkUtil.isNetworkAvailable(context)) {
            liveData.setValue(NetworkResult.loading());
            api.getStockData(stocksTicker, from, to, adjusted, sort, apiKey).enqueue(new Callback<StockResponse>() {
                @Override
                public void onResponse(@NonNull Call<StockResponse> call, @NonNull Response<StockResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        saveStockUpdatesToFirebase(stocksTicker, response.body());
                        liveData.setValue(NetworkResult.success(response.body()));
                    } else {
                        liveData.setValue(NetworkResult.error("Failed to fetch data"));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<StockResponse> call, @NonNull Throwable t) {
                    liveData.setValue(NetworkResult.error(t.getMessage()));
                }
            });
        } else {
            fetchStockUpdatesFromFirebase(context,stocksTicker, liveData);
        }
        return liveData;
    }

    private void saveToFirebase(List<ApiResponse.Result> results) {
        DatabaseReference resultsRef = FirebaseDatabase.getInstance().getReference("results");
        resultsRef.keepSynced(true);
        resultsRef.setValue(results);
    }

    private void saveBestMatchesToFirebase(List<ApiResponse.Result> results, String keyword) {
        DatabaseReference resultsRef = FirebaseDatabase.getInstance().getReference("bestMatches").child(keyword.replace(".",""));
        resultsRef.keepSynced(true);
        resultsRef.setValue(results);
    }

    private void fetchBestMatchesFromFirebase(String keyword, MutableLiveData<NetworkResult<List<ApiResponse.Result>>> liveData) {
        DatabaseReference resultsRef = FirebaseDatabase.getInstance().getReference("bestMatches").child(keyword.replace(".",""));
        resultsRef.keepSynced(true);
        resultsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ApiResponse.Result> resultList = new ArrayList<>();
                for (DataSnapshot resultSnapshot : snapshot.getChildren()) {
                    ApiResponse.Result result = resultSnapshot.getValue(ApiResponse.Result.class);
                    if (result != null) {
                        resultList.add(result);
                    }
                }
                liveData.setValue(NetworkResult.success(resultList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                liveData.setValue(NetworkResult.error(error.getMessage()));
            }
        });
    }

    private void fetchFromFirebase(MutableLiveData<NetworkResult<List<ApiResponse.Result>>> liveData) {
        DatabaseReference resultsRef = FirebaseDatabase.getInstance().getReference("results");
        resultsRef.keepSynced(true);
        resultsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ApiResponse.Result> resultList = new ArrayList<>();
                for (DataSnapshot resultSnapshot : snapshot.getChildren()) {
                    ApiResponse.Result result = resultSnapshot.getValue(ApiResponse.Result.class);
                    if (result != null) {
                        resultList.add(result);
                    }
                }
                liveData.setValue(NetworkResult.success(resultList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                liveData.setValue(NetworkResult.error(error.getMessage()));
            }
        });
    }

    private void saveStockUpdatesToFirebase(String stocksTicker, StockResponse stockResponse) {
        DatabaseReference stockUpdatesRef = FirebaseDatabase.getInstance().getReference("stockUpdates").child(stocksTicker.replace(".",""));
        stockUpdatesRef.keepSynced(true);
        stockUpdatesRef.setValue(stockResponse);
    }

    private void fetchStockUpdatesFromFirebase(Context context,String stocksTicker, MutableLiveData<NetworkResult<StockResponse>> liveData) {
        DatabaseReference stockUpdatesRef = FirebaseDatabase.getInstance()
                .getReference("stockUpdates")
                .child(stocksTicker.replace(".", ""));

        stockUpdatesRef.keepSynced(true);

        stockUpdatesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    StockResponse stockResponse = snapshot.getValue(StockResponse.class);
                    if(stockResponse == null || stockResponse.getResults() == null||
                            stockResponse.getResults().get(0) == null ) {
                        Toast.makeText(context, "No data available in Firebase", Toast.LENGTH_SHORT).show();

                        liveData.setValue(NetworkResult.error("No data available in Firebase"));
                    } else {
                        liveData.setValue(NetworkResult.success(stockResponse));
                    }
                } else {
                    Toast.makeText(context, "No data available in Firebase", Toast.LENGTH_SHORT).show();

                    liveData.setValue(NetworkResult.error("No cached data available in Firebase"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                liveData.setValue(NetworkResult.error(error.getMessage()));
            }
        });
    }
}