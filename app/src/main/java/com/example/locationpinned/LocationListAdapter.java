/*
 * Nathanael Selvaraj
 * 100783830
 * 2023-11-08
 */
package com.example.locationpinned;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.ViewHolder> {
    // adapter for displaying the locations on screen in a list format
    private LayoutInflater inflater;
    private List<LocationModel> locationModels;

    public LocationListAdapter(Context context, List<LocationModel> locationModels) {
        this.inflater = LayoutInflater.from(context);
        this.locationModels = locationModels;
    }

    // filters the locations on screen for searching
    public void filterList(List<LocationModel> filteredList) {
        locationModels = filteredList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.location_view, parent, false);
        return new ViewHolder(view);
    }

    // displays each location's values from the database in its respective list card
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocationModel location = locationModels.get(position);
        holder.address.setText(location.getLocationAddress());
        holder.latitude.setText("Latitude: " + location.getLocationLatitude());
        holder.longitude.setText("Longitude: " + location.getLocationLongitude());
    }

    // gets size of the database
    @Override
    public int getItemCount() {
        return locationModels.size();
    }


    // displays the data on screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView address, latitude, longitude;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.lAddress);
            latitude = itemView.findViewById(R.id.lLatitude);
            longitude = itemView.findViewById(R.id.lLongitude);
            cardView = itemView.findViewById(R.id.cardView);

            // access each listed note's details when clicked on
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle the click event to show location details, if needed.
                    Intent intent = new Intent(v.getContext(), LocationDetails.class);
                    intent.putExtra("address", locationModels.get(getAdapterPosition()).getLocationAddress());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
