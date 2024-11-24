package com.example.stockexchange;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.stockexchange.adapter.BestMatchAdapter;
import com.example.stockexchange.api.NetworkResult;
import com.example.stockexchange.databinding.ActivityMainBinding;
import com.example.stockexchange.model.ApiResponse;
import com.example.stockexchange.viewmodel.ApiViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    @Inject
    ApiViewModel apiViewModel;

    private BestMatchAdapter adapter;
    private List<ApiResponse.Result> allMatches = new ArrayList<>();
    private final List<ApiResponse.Result> filteredMatches = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.searchView.setIconifiedByDefault(false);
        setupSpinner();

        initRecyclerView();
        observeViewModel();
        setUpSearchView();

        apiViewModel.getAllStocks(1000, "ymr8Kd8GpgPpGVi6IQJYSY4AB97H9cmt",MainActivity.this);
    }

    private void initRecyclerView() {
        adapter = new BestMatchAdapter(MainActivity.this,filteredMatches);
        binding.resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.resultsRecyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        apiViewModel.getStateLiveData().observe(this, new Observer<NetworkResult<List<ApiResponse.Result>>>() {
            @Override
            public void onChanged(NetworkResult<List<ApiResponse.Result>> result) {
                if (result == null || result.getData() == null) return;

                handleApiResponse(result);
            }
        });
    }

    private void handleApiResponse(NetworkResult<List<ApiResponse.Result>> result) {
        switch (result.getStatus()) {
            case SUCCESS:
            hideProgressBar();
            updateMatches(result.getData());
            break;
            case ERROR:
            hideProgressBar();
            break;
            case LOADING:
            showProgressBar();
            break;
        }
    }

    private void hideProgressBar() {
        binding.progressBar.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    private void updateMatches(List<ApiResponse.Result> matches) {
        allMatches = matches;
        filteredMatches.clear();
        filteredMatches.addAll(allMatches);  // Show all initially
        adapter.setMatches(filteredMatches);
        adapter.notifyDataSetChanged();
    }

    private void setUpSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterResults(newText);
                return true;
            }
        });
    }

    private void filterResults(String query) {
        filteredMatches.clear();
        if (query.isEmpty()) {
            filteredMatches.addAll(allMatches); // Show all if search is empty
        } else {
            for (ApiResponse.Result match : allMatches) {
                if (matchesQuery(match, query)) {
                    filteredMatches.add(match);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private boolean matchesQuery(ApiResponse.Result match, String query) {
        String lowerQuery = query.toLowerCase();
        return match.getName().toLowerCase().contains(lowerQuery) ||
                match.getTicker().toLowerCase().contains(lowerQuery);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
        R.array.list_companies,
        android.R.layout.simple_spinner_item
        );
        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        binding.spinnerStocks.setAdapter(adapter);
        binding.spinnerStocks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Perform actions when an item is selected
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (!selectedItem.equals("Select company")) {
                    apiViewModel.fetchBestMatches(selectedItem, "ymr8Kd8GpgPpGVi6IQJYSY4AB97H9cmt",MainActivity.this);
                } else {
                    apiViewModel.getAllStocks(1000, "ymr8Kd8GpgPpGVi6IQJYSY4AB97H9cmt",MainActivity.this);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Perform actions when no item is selected, if needed
            }
        });
    }
}