/*
 * Nathanael Selvaraj
 * 100783830
 * 2023-11-08
 */
package com.example.locationpinned;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;
import java.util.Locale;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.Button;
import java.io.BufferedReader;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import java.io.InputStreamReader;
import java.util.logging.Handler;

import android.content.ContentValues;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity {

    // search bar
    EditText searchAddress;

    // buttons
    ImageButton refreshButton;
    private FloatingActionButton addButton;

    // display database size
    TextView dbSize;
    int itemCount = 0;

    private Geocoder geocoder;



    // to list the locations on screen
    RecyclerView recyclerView;
    LocationListAdapter adapter;
    List<LocationModel> locationModelList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // search bar and buttons
        searchAddress = findViewById(R.id.searchAddress);
        refreshButton = findViewById(R.id.refreshButton);
        addButton = findViewById(R.id.addLocation);

        // geocoder calculator and database access
        geocoder = new Geocoder(this, Locale.getDefault());
        Database db = new Database(MainActivity.this);

        // to display the size of te database
        dbSize = findViewById(R.id.dbSize);
        dbSize.setText(Integer.toString(itemCount));



        // refreshes the database
        // deletes everything in it first
        // then reloads it by reading the txt file's lat and lon values and gets the location from them, and storing them in the database
        // and displays the size of the database
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Database db = new Database(MainActivity.this);
                db.deleteAll();
                refreshDatabase(MainActivity.this, "locations.txt");
                locationModelList = db.getLocations(); // Update the locationModelList
                itemCount = locationModelList.size();
                adapter.filterList(locationModelList); // Notify the adapter
                Toast.makeText(getApplicationContext(), "Database Size = " + itemCount, Toast.LENGTH_SHORT).show();
                // displays the # of locations on screen
                dbSize.setText(Integer.toString(itemCount));
            }
        });





        // navigates to add a new location activity
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddLocation.class);
                startActivity(intent);
            }
        });





        // displays the locations from the database listed on screen and filtered by the search bar
        recyclerView = findViewById(R.id.addRecyclerView);
        Database database = new Database(this);
        locationModelList = database.getLocations();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new LocationListAdapter(this, locationModelList);
        recyclerView.setAdapter(adapter);





        // Search bar to search and filter locations on screen as the input search text changes
        searchAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });



    }


    // filters the locations on screen by the searched query
    private void filter(String query){
        List<LocationModel> filteredList = new ArrayList<>();
        for(LocationModel location : locationModelList){
            if(location.getLocationAddress().toLowerCase().contains(query.toLowerCase())){
                filteredList.add(location);
            }
        }
        adapter.filterList(filteredList);
    }


    // reads the txt file's contents line by line and determines the address of them before storing them in the DB
    private void refreshDatabase(Context context, String fileName){
        Database D = new Database(context);
        SQLiteDatabase db = D.getWritableDatabase();
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            int id = 1; // Initialize the ID counter.
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                if (parts.length == 2) {
                    double latitude = Double.parseDouble(parts[0]);
                    double longitude = Double.parseDouble(parts[1]);
                    LocationModel locationModel = new LocationModel(
                            /*In case i need to, convert the id here and in Locationmodel to a string, as per the database*/
                            id,
                            getAddressFromLocation(latitude, longitude), //calls function to get address from lat and lon values
                            parts[0],
                            parts[1]
                    );
                    D.AddLocation(locationModel);
                    id++; // Increment the ID counter for the next entry.
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // function that takes a latitude and longitude values, and uses geocoding to calculate and return the address of them
    public String getAddressFromLocation(double latitude, double longitude) {
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                // You can extract various address components like street, city, postal code, etc.
                String fullAddress = address.getAddressLine(0);
                return fullAddress;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Address not found"; // Provide a fallback value for failed geocoding.
    }



}