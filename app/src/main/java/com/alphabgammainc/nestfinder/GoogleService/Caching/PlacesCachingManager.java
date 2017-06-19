package com.alphabgammainc.nestfinder.GoogleService.Caching;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by davidhuang on 2017-06-12.
 */

public class PlacesCachingManager {

    private Activity mActivity;
    private static PlacesCachingManager placesCachingManager;
    private SQLiteDatabase db;
    private FeedReaderDbHelper mDbHelper;

    private PlacesCachingManager(Activity activity) {
        mActivity = activity;
    }


    /* Inner class that defines the table contents */
    public static class Location implements BaseColumns {
        public static final String TABLE_NAME = "location";
        public static final String COLUMN_LAT = "lat";
        public static final String COLUMN_LON = "lng";
        public static final String COLUMN_PLACE = "placeName";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }

    public static class Place implements BaseColumns {
        public static final String TABLE_NAME = "place";
        public static final String COLUMN_PLACE_NAME = "name";
        public static final String COLUMN_LAT = "lat";
        public static final String COLUMN_LON = "lng";
        public static final String COLUMN_VICINITY = "vicinity";
        public static final String LOCATION_ID = "locationId";

    }

    private static final String SQL_CREATE_LOCATION =
            "CREATE TABLE " + Location.TABLE_NAME + " (" +
                    Location._ID + " INTEGER PRIMARY KEY," +
                    Location.COLUMN_LAT + " REAL," +
                    Location.COLUMN_LON + " REAL," +
                    Location.COLUMN_PLACE + " TEXT," +
                    Location.COLUMN_TIMESTAMP + " INTEGER)";

    private static final String SQL_CREATE_PLACE =
            "CREATE TABLE " + Place.TABLE_NAME + " (" +
                    Place._ID + " INTEGER PRIMARY KEY," +
                    Place.COLUMN_LAT + " REAL," +
                    Place.COLUMN_LON + " REAL," +
                    Place.LOCATION_ID + " INTEGER," +
                    Place.COLUMN_VICINITY + " TEXT," +
                    Place.COLUMN_PLACE_NAME + " TEXT)";

    private static final String SQL_DELETE_LOCATION =
            "DROP TABLE IF EXISTS " + Location.TABLE_NAME;

    private static final String SQL_DELETE_PLACE =
            "DROP TABLE IF EXISTS " + Place.TABLE_NAME;

    public class FeedReaderDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "FeedReader.db";

        public FeedReaderDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_LOCATION);
            db.execSQL(SQL_DELETE_PLACE);

            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(SQL_CREATE_LOCATION);
            db.execSQL(SQL_CREATE_PLACE);
        }


    }


    public static PlacesCachingManager getmInstance(Activity activity){
        if(placesCachingManager == null) {
            placesCachingManager = new PlacesCachingManager(activity);
        }

        return placesCachingManager;
    }


    public void getPlaces(final FindLocationCallBack callback, final double lat, final double lon, final String place){

        initDB();

        new AsyncTask<Void, Void, Void>() {
            ArrayList<HashMap<String, String>> nearbyPlacesList;

            private void deleteRow(long id){

                String placeWhereClause = Place.LOCATION_ID + " =?";
                String locationWhereClause = Location._ID + " =?";
                String[] whereArgs = { Long.toString(id) };
                db.delete(Place.TABLE_NAME, placeWhereClause, whereArgs);
                db.delete(Location.TABLE_NAME, locationWhereClause, whereArgs);
            }

            @Override
            protected Void doInBackground( final Void ... params ) {
                // something you know that will take a few seconds
                // Define a projection that specifies which columns from the database
                // you will actually use after this query.
                String[] projection = {
                        Location._ID,
                        Location.COLUMN_TIMESTAMP
                };

                // Filter results WHERE "title" = 'My Title'
                String selection = Location.COLUMN_LAT + " = ? AND " + Location.COLUMN_LON + " = ? AND " +
                         Location.COLUMN_PLACE + " = ? ";
                String[] selectionArgs = { Double.toString(lat), Double.toString(lon) , place};

                // How you want the results sorted in the resulting Cursor
                // String sortOrder = FeedEntry.COLUMN_NAME_SUBTITLE + " DESC";

                Cursor cursor = db.query(
                        Location.TABLE_NAME,                     // The table to query
                        projection,                               // The columns to return
                        selection,                                // The columns for the WHERE clause
                        selectionArgs,                            // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        null                                    // The sort order
                );

                long LocationId = 0;
                long timestamp = 0;
                while(cursor.moveToNext()) {

                    int column_id = cursor.getColumnIndexOrThrow(Location._ID);
                    int column_timestamp = cursor.getColumnIndexOrThrow(Location.COLUMN_TIMESTAMP);


                    LocationId = cursor.getLong(column_id);
                    timestamp = cursor.getLong(column_timestamp);
                }
                cursor.close();


                String[] placeProjection = {
                        Place.COLUMN_LAT,
                        Place.COLUMN_LON,
                        Place.COLUMN_PLACE_NAME,
                        Place.COLUMN_VICINITY
                };

                String placeSelection = Place.LOCATION_ID + " =?";
                String[] placeSelectionArgs = {Long.toString(LocationId)};

                Cursor placeCursor = db.query(
                        Place.TABLE_NAME,
                        placeProjection,
                        placeSelection,
                        placeSelectionArgs,
                        null,
                        null,
                        null
                );

                nearbyPlacesList = new ArrayList<HashMap<String, String>>();
                while(placeCursor.moveToNext()) {
                    int LAT_INDEX = placeCursor.getColumnIndexOrThrow(Place.COLUMN_LAT);
                    int LON_INDEX = placeCursor.getColumnIndexOrThrow(Place.COLUMN_LON);
                    int PLACE_INDEX = placeCursor.getColumnIndexOrThrow(Place.COLUMN_PLACE_NAME);
                    int VICINITY = placeCursor.getColumnIndexOrThrow(Place.COLUMN_VICINITY);

                    double column_lat = placeCursor.getDouble(LAT_INDEX);
                    double column_lon = placeCursor.getDouble(LON_INDEX);
                    String column_place = placeCursor.getString(PLACE_INDEX);
                    String column_vicinity = placeCursor.getString(VICINITY);

                    HashMap<String, String> place = new HashMap<String, String>();

                    place.put("lat", Double.toString(column_lat));
                    place.put("lng", Double.toString(column_lon));
                    place.put("place_name", column_place);
                    place.put("vicinity", column_vicinity);

                    nearbyPlacesList.add(place);
                }


                if((timestamp - 2592000) > 0){
                    deleteRow(LocationId);
                }
                return null;
            }

            @Override
            protected void onPostExecute( final Void result ) {
                // continue what you are doing...
                callback.locationResult(nearbyPlacesList);
            }
        }.execute();

    }

    public long cacheLocation(double lat, double lon, String place){

        Long timestamp = System.currentTimeMillis()/1000;

        initDB();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Location.COLUMN_LAT, lat);
        values.put(Location.COLUMN_LON, lon);
        values.put(Location.COLUMN_PLACE,place);
        values.put(Location.COLUMN_TIMESTAMP,timestamp);

        // Insert the new row, returning the primary key value of the new row
        return db.insert(Location.TABLE_NAME, null, values);
    }

    public void cachePlace(HashMap<String, String> place, double locationId){

        initDB();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Place.COLUMN_LAT, place.get("lat"));
        values.put(Place.COLUMN_LON, place.get("lng"));
        values.put(Place.COLUMN_PLACE_NAME, place.get("place_name"));
        values.put(Place.COLUMN_VICINITY, place.get("vicinity"));
        values.put(Place.LOCATION_ID, locationId);

        // Insert the new row, returning the primary key value of the new row
        db.insert(Place.TABLE_NAME, null, values);
    }

    public void initDB(){
        if(mDbHelper == null){

            mDbHelper = new FeedReaderDbHelper(mActivity);
        }
        if(db == null){
            db = mDbHelper.getWritableDatabase();
            //mDbHelper.onUpgrade(db, 0, 0); //reset db.
        }

    }



}
