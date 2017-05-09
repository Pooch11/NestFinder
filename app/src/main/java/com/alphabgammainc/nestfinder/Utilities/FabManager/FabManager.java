package com.alphabgammainc.nestfinder.Utilities.FabManager;

import android.app.Activity;

import android.app.FragmentManager;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import com.alphabgammainc.nestfinder.FrontPage.FrontPage;
import com.alphabgammainc.nestfinder.Landlord.AdPostingPage;
import com.alphabgammainc.nestfinder.R;
import java.util.ArrayList;

/**
 * Created by davidhuang on 2017-05-06.
 */

public class FabManager implements View.OnClickListener {
    private FloatingActionButton generalTooling;
    private FloatingActionButton createListing;
    private FloatingActionButton centerLocation;
    private static FabManager fabManager;
    private Activity mActivity;


    private FabManager(
                       FloatingActionButton generalTooling,
                       FloatingActionButton createListing,

                       FloatingActionButton centerLocation){
        this.generalTooling = generalTooling;
        this.createListing = createListing;
        this.centerLocation = centerLocation;

                       FloatingActionButton centerLocation,
                       Activity mActivity){
        this.generalTooling = generalTooling;
        this.createListing = createListing;
        this.centerLocation = centerLocation;
        this.mActivity=mActivity;


    }

    /**
     * consider accepting an array of fab buttons if we want to expand this manager to all general fab buttons
     * if we want to manager all buttons then we should probably do that and have a pointer to the main one
     * so we will know which ones to show and such.
     * @param generalTooling
     * @param createListing
     * @param centerLocation
     * @return
     */
    public static synchronized FabManager getInstance(
                                                      FloatingActionButton generalTooling,
                                                      FloatingActionButton createListing,

                                                      FloatingActionButton centerLocation,
                                                      Activity mActivity){
        if(fabManager == null) {
            fabManager = new FabManager(generalTooling,createListing,centerLocation,mActivity);
        }
        return fabManager;
    }

    // just incase we dont want to set the listeners in the constructor.
    public void setListeners(){
        generalTooling.setOnClickListener(this);
        createListing.setOnClickListener(this);
        centerLocation.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.generalTool:
                if(createListing.getVisibility() == v.VISIBLE && centerLocation.getVisibility() == v.VISIBLE){
                    createListing.hide();
                    centerLocation.hide();
                } else {
                    centerLocation.show();
                    createListing.show();
                }

                break;
            case R.id.createListing:
                //set up a fragment kind of thing here so we can change the view also make sure to close all the fab buttons
                FragmentManager fragmentManager = mActivity.getFragmentManager();
                fragmentManager.beginTransaction().add(R.id.content_frame,new AdPostingPage()).commit();
                // addpostingFragment.
                break;
            case R.id.centerLocation:
                // same thing here
                break;

        }
    }

    public void _showGeneralFabButton(){
        generalTooling.show();
    }

    /**
     * when ever we switch fragments or something we want to hide all the fabs
     */
    public void _hideAllFabButtons(){
        createListing.hide();
        centerLocation.hide();
        generalTooling.hide();
    }

    /**
     * this is going to be called in the beggening
     */
    public void _hideAllNonGeneralButtons(){
        createListing.hide();
        centerLocation.hide();
    }
}
