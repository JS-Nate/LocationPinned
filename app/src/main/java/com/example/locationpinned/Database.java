/*
 * Nathanael Selvaraj
 * 100783830
 * 2023-11-08
 */
package com.example.locationpinned;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {
    public static final int DB_VERSION = 2;

    // database variables
    public static String DB_NAME = "LocationsDB.db";
    public static String DB_TABLE = "LocationsTable";
    public static String COLUMN_ID = "LocationsId";
    public static String COLUMN_ADDRESS = "LocationsAddress";
    public static String COLUMN_LATITUDE = "LocationLatitude";
    public static String COLUMN_LONGITUDE = "LocationLongitude";
    public Database(@Nullable Context context) {super(context, DB_NAME, null, DB_VERSION); }

    // database table schema
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + DB_TABLE +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ADDRESS + " TEXT," +
                COLUMN_LATITUDE + " TEXT," +
                COLUMN_LONGITUDE + " TEXT" + ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion >= newVersion)
            return;
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        onCreate(db);
    }

    // add a location's location model to the database with a unique id
    public long AddLocation(LocationModel locationModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ADDRESS, locationModel.getLocationAddress());
        contentValues.put(COLUMN_LATITUDE, locationModel.getLocationLatitude());
        contentValues.put(COLUMN_LONGITUDE, locationModel.getLocationLongitude());
        long ID = db.insert(DB_TABLE, null, contentValues);
        Log.d("Inserted", "id ->" + ID);
        return ID;
    }



    // gets and returns a location's location model by its address
    public LocationModel getLocationByAddress(String address){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] query = new String[]{COLUMN_ID, COLUMN_ADDRESS, COLUMN_LATITUDE, COLUMN_LONGITUDE};
        Cursor cursor = db.query(DB_TABLE, query, COLUMN_ADDRESS + "=?", new String[]{address}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            return new LocationModel(
                    Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
            );
        } else {
            return null; // User with the specified password not found.
        }
    }


    // updates a location in the database based on its id with new values
    public int updateLocation(int id, LocationModel updatedLocation){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(COLUMN_ADDRESS, updatedLocation.getLocationAddress());
        updatedValues.put(COLUMN_LATITUDE, updatedLocation.getLocationLatitude());
        updatedValues.put(COLUMN_LONGITUDE, updatedLocation.getLocationLongitude());
        return db.update(
                DB_TABLE,
                updatedValues,
                COLUMN_ID + " =?",
                new String[]{String.valueOf(id)}
        );

    }






    // Returns a list of entries in the database to display on the main page
    public List<LocationModel> getLocations() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<LocationModel> allLocations = new ArrayList<>();
        String queryStatement = "SELECT * FROM " + DB_TABLE;
        Cursor cursor = db.rawQuery(queryStatement, null);

        if (cursor.moveToFirst()) {
            do {
                LocationModel locationModel = new LocationModel();
                locationModel.setId(cursor.getInt(0));
                locationModel.setLocationAddress(cursor.getString(1));
                locationModel.setLocationLatitude(cursor.getString(2));
                locationModel.setLocationLongitude(cursor.getString(3));
                allLocations.add(locationModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return allLocations;
    }








    // deletes a given location from the database
    public void deleteLocation(int ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_TABLE, COLUMN_ID + " = ?", new String[]{String.valueOf(ID)});
        db.close();
    }

    // deletes all locations in the database (for reloading from the txt file)
    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_TABLE, null, null);
        db.close();
    }



}
