/*
 * Nathanael Selvaraj
 * 100783830
 * 2023-11-08
 */
package com.example.locationpinned;

public class LocationModel {

    // the storage form of locations in the database, holding and retrieving each stored data
    int id;
    String locationAddress;
    String locationLatitude;
    String locationLongitude;



    // forms a new location model with the required values, with or without an id
    public LocationModel(String locationAddress, String locationLatitude, String locationLongitude){
        this.locationAddress = locationAddress;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
    }

    public LocationModel(int id, String locationAddress, String locationLatitude, String locationLongitude){
        this.id = id;
        this.locationAddress = locationAddress;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
    }

    // even forms a location model for a blank call
    public LocationModel() {

    }

    // gets and returns the id
    public int getId() {
        return id;}

    public void setId(int id) {
        this.id = id;
    }

    // gets and returns the address
    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    // gets and returns the latitude
    public String getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(String locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    // gets and returns the longitude
    public String getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(String locationLongitude) {
        this.locationLongitude = locationLongitude;
    }
}
