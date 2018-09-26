package com.alphabgammainc.nestfinder;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.alphabgammainc.nestfinder.Classes.Locations;
import com.alphabgammainc.nestfinder.DetailsPage.DetailsPage;
import com.alphabgammainc.nestfinder.FirebaseConnection.DataBaseConnectionPresenter;
import com.alphabgammainc.nestfinder.FrontPage.FrontPage;
import com.alphabgammainc.nestfinder.Loading.InitialStartUp;
import com.alphabgammainc.nestfinder.User.UserManager;
import com.alphabgammainc.nestfinder.Utilities.FabManager.FabManager;
import com.alphabgammainc.nestfinder.Utilities.FragmentCallback;
import com.alphabgammainc.nestfinder.Utilities.ImageCallBack;
import com.alphabgammainc.nestfinder.Utilities.ImageManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements ImageCallBack{
    private FragmentCallback fragmentCallback;
    private ArrayList<Locations> locationses;
    private UserManager userManager;
    FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().add(R.id.content_frame,new InitialStartUp()).commit();
    }

    /*
        Permissions
     */

    public void showFrontPage(ArrayList<Locations> locationses){
        this.locationses = locationses;
        if(fragmentManager == null)fragmentManager = getFragmentManager();

        FragmentManager manager = getFragmentManager();
        manager.beginTransaction().replace(R.id.content_frame, new FrontPage()).commit();

    }

    public ArrayList<Locations> getLocationses(){
        return locationses;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            //resume tasks needing this permission
        }
    }
    public boolean isExternalStorageWritable() {
        String permission = "android.permission.WRITE_EXTERNAL_STORAGE"; // get permissions
        int res= this.checkCallingOrSelfPermission(permission);
        return (res== PackageManager.PERMISSION_GRANTED);
    }
    public boolean checkReadExternalPermission(){
        String permission = "android.permission.READ_EXTERNAL_STORAGE"; // get permissions
        int res= this.checkCallingOrSelfPermission(permission);
        return (res== PackageManager.PERMISSION_GRANTED);
    }
    public void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
    }

    public void setFragmentCallback(FragmentCallback fragmentCallback){
        this.fragmentCallback = fragmentCallback;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK) {
            switch (requestCode){
                case 1:
                    String path = ImageManager.getPath(this, data.getData());

                    DatabaseReference databaseReference = DataBaseConnectionPresenter.getInstance(this).getDbReference();

                    if(databaseReference != null) {
                        DatabaseReference reference = databaseReference.child("temp").push();

                        ImageManager.uploadImage(path, reference.getKey(), this);
                        fragmentCallback.callback(reference.getKey());
                    }
                    break;

                case 2:

                    break;
            }
        }
    }

    @Override
    public void ImageOnReady(Uri downloadUrl) {
        // we dont need to display it
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
}
