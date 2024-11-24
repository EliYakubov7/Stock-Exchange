package com.example.stockexchange.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockexchange.R;
import com.example.stockexchange.model.StockResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {

    private final List<StockResult> stockList;

    public StockAdapter(List<StockResult> stockList) {
        this.stockList = stockList;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_stock_update, parent, false);
        return new StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        StockResult stock = stockList.get(position);
        holder.tvOpen.setText("Open price: " + stock.getOpenPrice());
        holder.tvClose.setText("Close price: " + stock.getClosePrice());
        holder.tvHigh.setText("High price: " + stock.getHighPrice());
        holder.tvTimestamp.setText("Last updated: " + convertTimestampToReadable(stock.getTimestamp()));
        holder.tvLow.setText("Low price: " + stock.getLowPrice());
        holder.tvVolume.setText("Volume: " + stock.getVolume());
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

    public static class StockViewHolder extends RecyclerView.ViewHolder {
        TextView tvTimestamp, tvVolume,tvOpen,tvClose,tvHigh,tvLow;
        public StockViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTimestamp = itemView.findViewById(R.id.tvTimeStamp);
            tvVolume = itemView.findViewById(R.id.tvVolume);
            tvOpen = itemView.findViewById(R.id.tvOpenPrice);
            tvClose = itemView.findViewById(R.id.tvClosePrice);
            tvHigh = itemView.findViewById(R.id.tvHighPrice);
            tvLow = itemView.findViewById(R.id.tvLowPrice);
        }
    }
    public static String convertTimestampToReadable(long timestamp) {
        // Create a Date object from the timestamp
        Date date = new Date(timestamp);

        // Define the output date format
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        formatter.setTimeZone(TimeZone.getDefault()); // Set to the local time zone

        // Format the date and return it
        return formatter.format(date);
    }
}