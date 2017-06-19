package com.alphabgammainc.nestfinder.DetailsPage;

import android.app.Activity;

import com.alphabgammainc.nestfinder.GoogleService.Caching.FindLocationCallBack;
import com.alphabgammainc.nestfinder.GoogleService.Caching.PlacesCachingManager;
import com.alphabgammainc.nestfinder.GoogleService.GetNearbyPlacesData;
import com.google.android.gms.maps.GoogleMap;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.alphabgammainc.nestfinder.R.string.google_maps_key;

/**
 * Created by soutrikbarua on 2017-05-21.
 */

public class NearbyPlaces implements FindLocationCallBack{
    private int PROXIMITY_RADIUS = 5000;
    private String URL;
    private String store;
    private String nearByPlace;

    private double latitude;
    private double longitude;
    private GoogleMap googleMap;
    private DetailsPageInterface callback;

    private String getURL(double latitude,double longitude,String nearbyPlaces){
        this.nearByPlace = nearbyPlaces;

        StringBuilder googlePlacesURL =
                new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesURL.append("location=" +latitude+","+longitude);
        googlePlacesURL.append("&radius=" +PROXIMITY_RADIUS );
        googlePlacesURL.append("&type=" + nearbyPlaces);
        googlePlacesURL.append("&sensor=true");
        googlePlacesURL.append("&key=AIzaSyCb14xML7qnQ4AXGQ5ymUzgQwSmvcGa3IE");

        return (googlePlacesURL.toString());
    }

    public void getPlaces(double latitude, double longitude, String store, GoogleMap googleMap, DetailsPageInterface callback, Activity activity){
        this.store = store;
        this.latitude = latitude;
        this.longitude = longitude;
        this.googleMap = googleMap;
        this.callback = callback;


        PlacesCachingManager placesCachingManager = PlacesCachingManager.getmInstance(activity);

        placesCachingManager.getPlaces(this,latitude,longitude,store);

    }


    @Override
    public void locationResult(List<HashMap<String, String>> places) {

        if(places.size() == 0) {
            URL = getURL(latitude, longitude, store);
            Object[] dataTransfer = new Object[7];
            dataTransfer[0] = googleMap;
            dataTransfer[1] = URL;
            dataTransfer[2] = callback;
            dataTransfer[3] = latitude;
            dataTransfer[4] = longitude;
            dataTransfer[5] = nearByPlace;
            GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
            getNearbyPlacesData.execute(dataTransfer);
            //call the get GetNearbyPlacesData class
        } else {
            callback.setPlaces(places);
        }
    }
}
