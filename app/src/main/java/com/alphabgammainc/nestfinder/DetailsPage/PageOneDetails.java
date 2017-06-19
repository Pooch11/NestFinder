package com.alphabgammainc.nestfinder.DetailsPage;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alphabgammainc.nestfinder.Classes.Locations;
import com.alphabgammainc.nestfinder.GoogleService.Caching.PlacesCachingManager;
import com.alphabgammainc.nestfinder.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by davidhuang on 2017-05-20.
 */

public class PageOneDetails extends Fragment implements OnMapReadyCallback, DetailsPageInterface, View.OnClickListener {

    private Activity mActivity;
    private View mView;
    private HashMap<String,List<HashMap<String, String>>> nearbyPlacesList;
    private GoogleMap map;
    private Locations locations;
    private String currentPlace;

    /**
     * this is going to be  dynamic later
     */
    private LinearLayout schools;
    private LinearLayout convenience_store;
    private LinearLayout gym;
    private LinearLayout bus_stops;
    private LinearLayout grocery_or_supermarket;
    private LinearLayout gas_station;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.details_page_one, container ,false);
        nearbyPlacesList = new HashMap<>();
        MapView mMapView = (MapView) mView.findViewById(R.id.map);

        mMapView.onCreate(savedInstanceState);
        locations = ((DetailsPage)mActivity).getLocations();

        schools = (LinearLayout)mView.findViewById(R.id.schools);
        convenience_store = (LinearLayout)mView.findViewById(R.id.convenience_store);
        gym = (LinearLayout)mView.findViewById(R.id.gym);
        bus_stops = (LinearLayout)mView.findViewById(R.id.bus_stop);
        grocery_or_supermarket = (LinearLayout)mView.findViewById(R.id.grocery_store);
        gas_station = (LinearLayout)mView.findViewById(R.id.gas_station);

        schools.setOnClickListener(this);
        convenience_store.setOnClickListener(this);
        gym.setOnClickListener(this);
        bus_stops.setOnClickListener(this);
        grocery_or_supermarket.setOnClickListener(this);
        gas_station.setOnClickListener(this);

        TextView ad_title = (TextView)mView.findViewById(R.id.ad_title);
        TextView ad_location = (TextView)mView.findViewById(R.id.ad_location);
        TextView ad_price = (TextView)mView.findViewById(R.id.ad_price);
        TextView additional_info = (TextView)mView.findViewById(R.id.additional_info);

        ad_title.setText(locations.getAdTitle());
        ad_location.setText(locations.getAddress());
        ad_price.setText(locations.getPrice() + "/month");
        additional_info.setText(locations.getDescription());


        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(mActivity.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }


        mMapView.getMapAsync(this);



        return mView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(locations.getLat() , locations.getLon()), 12);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            MapStyleOptions style =MapStyleOptions.loadRawResourceStyle(this.getContext(),R.raw.mapstyle);
            googleMap.setMapStyle(style);
        }

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(locations.getLat(), locations.getLon()))
                .title(locations.getAddress()));

        map = googleMap;

        googleMap.animateCamera(cameraUpdate);
    }

    @Override
    public void setPlaces(List<HashMap<String, String>> nearbyPlacesList) {
        this.nearbyPlacesList.put(currentPlace,nearbyPlacesList);

        if(nearbyPlacesList.size() > 0) {
            PlacesCachingManager placesCachingManager = PlacesCachingManager.getmInstance(mActivity);
            double locationId = placesCachingManager.cacheLocation(locations.getLat(), locations.getLon(), this.currentPlace);

            for (int i = 0; i < nearbyPlacesList.size(); i++) {
                Log.d("onPostExecute", "Entered into showing locations");
                MarkerOptions markerOptions = new MarkerOptions();
                HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
                double lat = Double.parseDouble(googlePlace.get("lat"));
                double lng = Double.parseDouble(googlePlace.get("lng"));
                String placeName = googlePlace.get("place_name");
                String vicinity = googlePlace.get("vicinity");
                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(placeName + " : " + vicinity);
                map.addMarker(markerOptions);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                placesCachingManager.cachePlace(googlePlace, locationId);
                //move map camera
                // mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                // mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
            }
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.schools:
                populatePlaces(getResources().getString(R.string.school));
                break;
            case R.id.convenience_store:
                populatePlaces(getResources().getString(R.string.convenience_store));
                break;
            case R.id.gym:
                populatePlaces(getResources().getString(R.string.gym));
                break;
            case R.id.bus_stop:
               // populatePlaces(getResources().getString(R.string.bu));
                break;
            case R.id.grocery_store:
                populatePlaces(getResources().getString(R.string.grocery_or_supermarket));
                break;
            case R.id.gas_station:
                populatePlaces(getResources().getString(R.string.gas_station));
                break;
        }
    }

    public void populatePlaces(String place) {
        if(this.nearbyPlacesList.get(place)==null) {
            this.currentPlace = place;
            map.clear();
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(locations.getLat(), locations.getLon()))
                    .title(locations.getAddress()));
            // also place back the original marker
            NearbyPlaces myPlaces = new NearbyPlaces();
            /**
             * this is where you pass the latitude and longitude along with the search type
             */
            myPlaces.getPlaces(locations.getLat(), locations.getLon(), place, map, this, mActivity);
        } else {
            setPlaces(this.nearbyPlacesList.get(place));
        }
    }

}
