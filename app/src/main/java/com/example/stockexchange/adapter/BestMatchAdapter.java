package com.example.stockexchange.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockexchange.R;
import com.example.stockexchange.StockUpdateActivity;
import com.example.stockexchange.model.ApiResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class BestMatchAdapter extends RecyclerView.Adapter<BestMatchAdapter.ViewHolder> {

    private List<ApiResponse.Result> matches;
    private Context context;

    public BestMatchAdapter(Context context, List<ApiResponse.Result> matches) {
        this.matches = matches;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_best_match, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the match object at the current position
        ApiResponse.Result match = matches.get(position);

        // Bind the data to the view
        holder.symbolTextView.setText("Symbol: " + match.getTicker());
        holder.nameTextView.setText("Name: " + match.getName());
        holder.localeTextView.setText("Locale: "+match.getLocale());

        String formattedDate = convertUtcToLocal(match.getLast_updated_utc());

        holder.lastUpdatedTextView.setText("Last Updated: "+formattedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("selectedDate", formattedDate);
                Intent intent=new Intent(context, StockUpdateActivity.class);
                intent.putExtra("symbol",match.getTicker());
                intent.putExtra("lastUpdatedDate",match.getLast_updated_utc());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return matches != null ? matches.size() : 0;
    }

    // ViewHolder class to bind individual items
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView symbolTextView;
        private TextView nameTextView;
        private TextView localeTextView;
        private TextView lastUpdatedTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            symbolTextView = itemView.findViewById(R.id.symbolTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            localeTextView=itemView.findViewById(R.id.localeTextView);
            lastUpdatedTextView=itemView.findViewById(R.id.lastUpdatedTextView);
        }
    }

    // Method to update data in the adapter
    public void setMatches(List<ApiResponse.Result> matches) {
        this.matches = matches;
    }
    public static String convertUtcToLocal(String utcDateTime) {
        if (utcDateTime == null) {
            return "Invalid date format: input is null";
        }

        String[] possibleFormats = {
                "yyyy-MM-dd'T'HH:mm:ss'Z'", // Standard ISO 8601 with 'Z'
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", // ISO 8601 with milliseconds
                "yyyy-MM-dd'T'HH:mm:ssXXX", // ISO 8601 with timezone offset
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" // ISO 8601 with milliseconds and offset
        };

        for (String format : possibleFormats) {
            try {
                // Define the input format
                SimpleDateFormat utcFormat = new SimpleDateFormat(format);
                utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                // Parse the date
                Date date = utcFormat.parse(utcDateTime);

                // Define the output format
                SimpleDateFormat localFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                localFormat.setTimeZone(TimeZone.getDefault());

                // Return the formatted date
                return localFormat.format(date);
            } catch (ParseException e) {
                // Try the next format
            }
        }

        // If no format works, return an error message
        return "Invalid date format";
    }
}