/*
* Nathanael Selvaraj
* 100783830
* 2023-11-08
*/
package com.example.locationpinned;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddLocation extends AppCompatActivity {

    private Geocoder geocoder;

    // different inputs, results and buttons whether the user enters lat and lon values or an address
    EditText enterLat, enterLon, enterAdd;
    TextView getLat, getLon, getAdd;
    Button calculateAddress, calculateCoordinates, submitFromCoordinates, submitFromAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        Database db = new Database(AddLocation.this);
        geocoder = new Geocoder(this, Locale.getDefault());






        /* Method 1: Entering Coordinates, receiving Address */
        enterLat = findViewById(R.id.enterLat);
        enterLon = findViewById(R.id.enterLon);
        getAdd = findViewById(R.id.getAdd);
        calculateAddress = findViewById(R.id.calculateAddress);
        submitFromCoordinates = findViewById(R.id.submitFromCoordinates);


        // calculates the address of what the user entered for latitude and longitude
        calculateAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ensures the user enters a valid number and not letters or a blank
                try{
                    double latitude = Double.parseDouble(enterLat.getText().toString().trim());
                    double longitude = Double.parseDouble(enterLon.getText().toString().trim());
                    String address = getAddressFromCoordinates(latitude, longitude);
                    getAdd.setText(address);
                } catch (NumberFormatException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Invalid latitude or longitude entry", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // recalculates (in case of changes without clicking calculate) and submits changes and add new location to database
        submitFromCoordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ensures the user enters a valid number and not letters or a blank
                try{
                    double latitude = Double.parseDouble(enterLat.getText().toString().trim());
                    double longitude = Double.parseDouble(enterLon.getText().toString().trim());
                    String address = getAddressFromCoordinates(latitude, longitude);

                    if(address != null){
                        LocationModel locationModel = new LocationModel(
                                address,
                                enterLat.getText().toString().trim(),
                                enterLon.getText().toString().trim()
                        );

                        db.AddLocation(locationModel);
                        Intent intent = new Intent(AddLocation.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    // doesnt save to the database in case the lat and lon values dont have an address
                    else{
                        Toast.makeText(getApplicationContext(), "No calculatable address found", Toast.LENGTH_SHORT).show();
                    }

                } catch (NumberFormatException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Invalid latitude or longitude entry", Toast.LENGTH_SHORT).show();
                }


            }
        });












        /* Method 2: Entering Address, receiving Coordinates */
        enterAdd = findViewById(R.id.enterAdd);
        getLat = findViewById(R.id.getLat);
        getLon = findViewById(R.id.getLon);
        calculateCoordinates = findViewById(R.id.calculateCoordinates);
        submitFromAddress = findViewById(R.id.submitFromAddress);


        // when the button is pressed, calculates the latitude and longitude of what the user entered for address
        calculateCoordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // converts the entered into a string
                // uses LatLng to store the latitude and longitude values from calling the function
                LatLng coordinates = getCoordinatesFromAddress(AddLocation.this, enterAdd.getText().toString());

                // checks if the returned coordinates are valid or not
                boolean invalid = coordinates.latitude == 0 || coordinates.longitude == 0;

                // informs the user if the address doesnt have lat and long values (highly unlikely, but just being safe)
                if(invalid){
                    Toast.makeText(getApplicationContext(), "Invalid address entry", Toast.LENGTH_SHORT).show();
                }

                // otherwise, displays the lat and long values of the address on the TextViews on screen
                else{
                    getLat.setText(Double.toString(coordinates.latitude));
                    getLon.setText(Double.toString(coordinates.longitude));
                }

            }
        });


        // recalculates (in case of changes without clicking calculate) and submits changes and add new location to database
        submitFromAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng coordinates = getCoordinatesFromAddress(AddLocation.this, enterAdd.getText().toString());
                boolean invalid = coordinates.latitude == 0 || coordinates.longitude == 0;

                // ensures the address has lat and long values before accessing the database
                if(invalid){
                    Toast.makeText(getApplicationContext(), "Invalid address entry", Toast.LENGTH_SHORT).show();
                }

                else{
                    getLat.setText(Double.toString(coordinates.latitude));
                    getLon.setText(Double.toString(coordinates.longitude));
                    LocationModel locationModel = new LocationModel(
                            enterAdd.getText().toString().trim(),
                            getLat.getText().toString().trim(),
                            getLon.getText().toString().trim()
                    );

                    db.AddLocation(locationModel);
                    Intent intent = new Intent(AddLocation.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                }
            }
        });

    }






    // Method 1's function that finds the address from the given latitude and longitude values
    public String getAddressFromCoordinates(double latitude, double longitude) {
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);

                String fullAddress = address.getAddressLine(0);
                return fullAddress;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    // Method 2's function that finds the latitude and longitude values from the given the address
    public static LatLng getCoordinatesFromAddress(Context context, String address) {
        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address coordinates = addresses.get(0);
                double latitude = coordinates.getLatitude();
                double longitude = coordinates.getLongitude();
                return new LatLng(latitude, longitude);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Return null if geocoding fails.
    }
}