package com.alphabgammainc.nestfinder.FrontPage;

import android.Manifest;
import android.app.Activity;

import android.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.alphabgammainc.nestfinder.Classes.Locations;
//import com.alphabgammainc.nestfinder.FilterPage.FilterFragment1;
import com.alphabgammainc.nestfinder.MapsActivity;
import com.alphabgammainc.nestfinder.R;
import com.alphabgammainc.nestfinder.SearchFilter.FilterClass;
import com.alphabgammainc.nestfinder.Utilities.FabManager.FabManager;
import com.alphabgammainc.nestfinder.Utilities.PermissionsManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.ContentValues.TAG;

/**
 * Created by davidhuang on 2017-04-25.
 */

public class FrontPage extends Fragment implements OnMapReadyCallback, ManageMap,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener,SearchView.OnQueryTextListener {

    private View mView;
    private Activity mActivity;
    private MapManager mapManager;
    private GoogleMap mMap;
    private RecyclerView listView;
    private ArrayList<Locations> locations;
    private FrontPageAdapter adapter;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FabManager fabManager;
    private SearchView mSearchView;
    // Temporary until we actually start pulling data
    private AppBarLayout mAppBarLayout;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    LocationManager mLocationManager;
    LocationListener mLocationListener;
    LocationRequest mLocationRequest;
    Marker mCurrLocationMarker;


    /**
     * Get the instance of activity here if we do it somewhere else then we might run into issues
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mapManager = new MapManager();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(mActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(mActivity, "permission denied", Toast.LENGTH_LONG).show();
                }

            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.front_page, container, false);

        MapView mMapView = (MapView) mView.findViewById(R.id.map);

        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        FloatingActionButton generalTooling = (FloatingActionButton) mView.findViewById(R.id.generalTool);
        FloatingActionButton createListing = (FloatingActionButton) mView.findViewById(R.id.createListing);
        FloatingActionButton centerLocation = (FloatingActionButton) mView.findViewById(R.id.centerLocation);

        fabManager = FabManager.getInstance(generalTooling, createListing, centerLocation, mActivity); // calling get instance automically shows the fab
        fabManager.setListeners();
        fabManager._hideAllNonGeneralButtons();

        mAppBarLayout = (AppBarLayout) mView.findViewById(R.id.app_bar);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(AppBarLayout appBarLayout) {
                return false;
            }
        });
        params.setBehavior(behavior);

        swipeRefreshLayout = (SwipeRefreshLayout)mView.findViewById(R.id.swipe);
       // swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setEnabled(false);

        locations = new ArrayList<>();

        listView = (RecyclerView) mView.findViewById(R.id.rview);

        layoutManager = new LinearLayoutManager(mActivity);

        listView.setLayoutManager(layoutManager);

        locations = ((MapsActivity)mActivity).getLocationses();

        adapter = new FrontPageAdapter(mActivity, locations, this, listView);

        listView.setAdapter(adapter);

        /**
         * Search feature starts here.
         */
        final  Button filterButton = (Button) mView.findViewById(R.id.filter);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Open filter page
                 */
                Intent filterIntent = new Intent(mActivity,FilterClass.class);
                mActivity.startActivity(filterIntent);

            }
        });
        mSearchView = (SearchView) mView.findViewById(R.id.searchView);
        setupSearchView();



        return mView;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //final MarkerManager markerManager = new MarkerManager(mActivity , this, locations, adapter);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(mActivity,Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
            else {
                checkLocationPermissions();
            }
            MapStyleOptions style =MapStyleOptions.loadRawResourceStyle(this.getContext(),R.raw.mapstyle);
            mMap.setMapStyle(style);
        }



      //  LatLng location = new LatLng(43.887501 , -79.428406);
       // markerManager.fetchMarkers(43.887501 , -79.428406);

        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.887501 , -79.428406), 10);
        mMap.animateCamera(cameraUpdate);
        LoadMap(locations);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Locations location = mapManager.getListMarker(marker);

                // swap the two location objects in the array list so that it can be displayed at the top of the list
                // when the user selects a marker.
                Collections.swap(locations, 0, locations.indexOf(location));
                adapter.notifyDataSetChanged();

                return false;
            }
        });

    }

    /**
     * callback once we fetch all the locations on the given marker
     * @param locations
     */
    @Override
    public void LoadMap(ArrayList<Locations> locations) {
        //swipeRefreshLayout.setEnabled(false);
        // Add a marker in Sydney and move the camera
        for(Locations location : locations){
            LatLng position = new LatLng(location.getLat(),location.getLon());
            Marker marker = mMap.addMarker(new MarkerOptions().position(position).title(location.getAddress()));
            mapManager.addMapMarker(marker, location);
        }
    //    swipeRefreshLayout.setRefreshing(false);
    }


    /**
     * we probably dont need this function but just leave it here for now.
     * right now it is used for callback function for the adapter since we cann set a overall list listener
     *
     * @param location the key object that is used to search the map for the corresponding value.
     */
    @Override
    public void setMarkerFocusCallback(Locations location) {
        Marker marker = mapManager.getMapmarker(location);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),14));
        marker.showInfoWindow();
    }


    /**
     * User's location journey begins here
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onStart() {
        super.onStart();
        if(mGoogleApiClient != null){
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        //Stop Location updates when activity is no longer active
        if(mGoogleApiClient != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest =new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if(ContextCompat.checkSelfPermission(mActivity,Manifest.permission.ACCESS_FINE_LOCATION)
                ==PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
        }


    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if(mCurrLocationMarker != null){
            mCurrLocationMarker.remove();
        }
        //Place Current  Lcoation Marker
        LatLng latLng =new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Postion");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map Camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));

    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermissions(){
        if(ContextCompat.checkSelfPermission(mActivity,Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(mActivity)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(mActivity,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();
            }
            else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    /**
     * Helper Functions for search feature
     */
    private void setupSearchView(){
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Search Here");
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        String location = query;
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
