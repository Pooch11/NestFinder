package com.alphabgammainc.nestfinder.Landlord;

import android.app.Activity;
import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.alphabgammainc.nestfinder.Classes.Address;
import com.alphabgammainc.nestfinder.FirebaseConnection.DataBaseConnectionPresenter;
import com.alphabgammainc.nestfinder.GoogleService.LatLong;
import com.alphabgammainc.nestfinder.R;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;

/**
 * Created by soutrikbarua on 2017-04-26.
 */

public class AdPostingPage extends Fragment{


    /**
     * All the form variable exists here
     */
    private EditText adTitle;
    private EditText streetNumber;
    private EditText streetName;
    private EditText postalCode;
    private EditText city;
    private EditText country;
    private EditText province;
    private EditText bedRooms;
    private EditText bathRooms;
    private CheckBox isFurnished;
    private CheckBox pets;
    private EditText price;
    private Button postAd;


    /**
     * placeholders for  the  above variables converted to their respected data types
     */
    private String madTitle;
    private String mstreetNumber;
    private String mstreetName;
    private String mCity;
    private String mCountry;
    private String mProvince;
    private String mpostalCode;
    private int mbedRooms;
    private int mbathRooms;
    private boolean misFurnished;
    private boolean mPets;
    private Double mPrice;
    /**
     * All other variable for this fragment exists here
     */
    private View mView;
    private Activity mActivity;
    private Location adLocation;
    private Address mAddress;
    private LatLong mLatLong;
    private Double mLat;
    private Double mLng;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity =getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.ad_posting_page,container,false);

        /**
         * Get the respective views
         */

        adTitle =(EditText)mView.findViewById(R.id.title);
        streetNumber=(EditText)mView.findViewById(R.id.streetNumber);
        streetName=(EditText)mView.findViewById(R.id.streetName);
        postalCode=(EditText)mView.findViewById(R.id.postalCode);
        city=(EditText)mView.findViewById(R.id.city);
        country=(EditText)mView.findViewById(R.id.country);
        province =(EditText)mView.findViewById(R.id.province);
        bedRooms=(EditText)mView.findViewById(R.id.bedrooms);
        bathRooms=(EditText)mView.findViewById(R.id.bathrooms);
        isFurnished=(CheckBox)mView.findViewById(R.id.furnished);
        pets=(CheckBox)mView.findViewById(R.id.pets);
        price=(EditText)mView.findViewById(R.id.price);



        postAd =(Button)mView.findViewById(R.id.postAd);


        // adLocation = new Location() instantiate the new location here

        /**
         * Submit button;
         */

        postAd.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {
                /**
                 * Convert the fetched data to be passed in the Locations object
                 */
                madTitle= adTitle.getText().toString();
                mstreetName=streetName.getText().toString();
                mstreetNumber=streetNumber.getText().toString();
                mpostalCode = postalCode.getText().toString();
                mCity =city.getText().toString();
                mCountry=country.getText().toString();
                mProvince=province.getText().toString();

                /**
                 * Additional information about the apartment
                 */


                mbedRooms =Integer.parseInt(bedRooms.getText().toString());
                mbathRooms = Integer.parseInt(bathRooms.getText().toString());
                misFurnished=Boolean.valueOf(isFurnished.getText().toString());
                mPets =Boolean.valueOf(pets.getText().toString());
                mPrice =Double.parseDouble(price.getText().toString());


                /**
                 * send the data into an address object
                 */

                mAddress =new Address(mstreetNumber,mstreetName,mCity,mProvince,mpostalCode,mCountry);


                mLatLong = new LatLong(mAddress);
                mLatLong.createAddress();
                try {
                   mLatLong.execute();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                mLat = mLatLong.latitude();
                mLng = mLatLong.longitude();

                //Something goes wrong here
               /* adLocation = new Location(mLng,mLat,madTitle,mAddress,mbedRooms,mbathRooms,
                        misFurnished,mPets,mPrice);
                 */

                post_ad_to_database();
            }
        });

        return mView;
    }

    /**
     * Helper methods
     */
    public void post_ad_to_database()
    {
        // Adds all the information to firebase here

    }


}
// Add check to use