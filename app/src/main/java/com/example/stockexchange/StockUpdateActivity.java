package com.example.stockexchange;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.stockexchange.adapter.StockAdapter;
import com.example.stockexchange.model.StockResult;
import com.example.stockexchange.viewmodel.ApiViewModel;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StockUpdateActivity extends AppCompatActivity {

    private StockAdapter adapter;
    private final List<StockResult> stockList = new ArrayList<>();
    @Inject
    ApiViewModel apiViewModel;
    private com.example.stockexchange.databinding.ActivityStockUpdateBinding binding;
    private ProgressBar progressBar;
    private TextView tvNoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = com.example.stockexchange.databinding.ActivityStockUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tvNoData = findViewById(R.id.tvNoData);

        Intent intent=getIntent();
        String symbol= intent.getStringExtra("symbol");
        String lastUpdatedDate=intent.getStringExtra("lastUpdatedDate");
        String[] dateRange = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dateRange = getDateRange(lastUpdatedDate);
        }

        binding.tvTitle.setText("Ticker: "+symbol);
        progressBar = findViewById(R.id.pb_stock);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        adapter = new StockAdapter(stockList);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        viewVisibility(stockList);

        apiViewModel.getStockDataLive().observe(this, result -> {
            switch (result.getStatus()) {
                case LOADING:
                    // Show loading state (optional)
                    progressBar.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    // Update RecyclerView data
                    progressBar.setVisibility(View.GONE);
                    if (result.getData() != null && result.getData().getResults() != null) {
                        stockList.clear();
                        stockList.addAll(result.getData().getResults());
                        adapter.notifyDataSetChanged();
                    }
                    break;

                case ERROR:
                    // Handle error
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(StockUpdateActivity.this,"Failed to fetch data. Something went wrong",Toast.LENGTH_SHORT).show();
                    System.out.println("Error: " + result.getMessage());
                    break;
            }
        });

        // Fetch stock data dynamically
        if (dateRange != null) {
            String from = dateRange[0];
            String to = dateRange[1];
            // Pass these values to your API method
            System.out.println("From: " + from);
            System.out.println("To: " + to);
            assert symbol != null;
            apiViewModel.fetchStockUpdates(symbol, from, to, true, "asc", "ymr8Kd8GpgPpGVi6IQJYSY4AB97H9cmt",StockUpdateActivity.this);
        } else {
            Toast.makeText(StockUpdateActivity.this,"Invalid date format",Toast.LENGTH_SHORT).show();
            System.out.println("Invalid date format");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String[] getDateRange(String lastUpdatedDate) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(lastUpdatedDate);

                // Format the "to" date
                String toDate = zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                // Calculate the "from" date
                ZonedDateTime fromDateTime = zonedDateTime.minusDays(2);
                String fromDate = fromDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                return new String[]{fromDate, toDate};
            } else {
                // Handle the case for older Android versions
                // You might want to provide a fallback implementation or throw an exception
                return null; // Or return a default value
            }
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            // Handle the exception as needed, e.g., log the error, show a user message
            return null;
        }
    }
    private void viewVisibility(List<StockResult> stockResponses) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (stockResponses == null || stockResponses.isEmpty()) {
                binding.recyclerView.setVisibility(View.GONE);
                tvNoData.setVisibility(View.VISIBLE);
            } else {
                binding.recyclerView.setVisibility(View.VISIBLE);
                tvNoData.setVisibility(View.GONE);
            }
        }, 3000); // 3000 milliseconds = 3 seconds
    }
}