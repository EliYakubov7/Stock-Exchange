package com.example.stockexchange.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StockResponse {
    @SerializedName("ticker")
    private String ticker;

    @SerializedName("queryCount")
    private int queryCount;

    @SerializedName("resultsCount")
    private int resultsCount;

    @SerializedName("adjusted")
    private boolean adjusted;

    @SerializedName("results")
    private List<StockResult> results;

    // Getters and Setters
    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public int getQueryCount() {
        return queryCount;
    }

    public void setQueryCount(int queryCount) {
        this.queryCount = queryCount;
    }

    public int getResultsCount() {
        return resultsCount;
    }

    public void setResultsCount(int resultsCount) {
        this.resultsCount = resultsCount;
    }

    public boolean isAdjusted() {
        return adjusted;
    }

    public void setAdjusted(boolean adjusted) {
        this.adjusted = adjusted;
    }

    public List<StockResult> getResults() {
        return results;
    }

    public void setResults(List<StockResult> results) {
        this.results = results;
    }
}