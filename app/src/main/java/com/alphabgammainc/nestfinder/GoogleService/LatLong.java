package com.alphabgammainc.nestfinder.GoogleService;

import android.os.AsyncTask;
import android.util.Log;

import com.alphabgammainc.nestfinder.Classes.Address;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import static com.alphabgammainc.nestfinder.R.string.google_maps_key;

/**
 * Created by soutrikbarua on 2017-05-01.
 */

public class LatLong {
    private final static String geocode ="https://maps.googleapis.com/maps/api/geocode/json?" +
            "address=";

    private String address;
    private Address myAddress;
    private ConversionCallback conversionCallback;
    public LatLong(){

    }
    public LatLong(Address myAddress){
        this.myAddress=myAddress;
    }
    public void createAddress(){

        address = myAddress.getAddress() + ",+"
                + myAddress.getCity()+ ",+"+ myAddress.getProvince();

    }

    public String createURL() throws UnsupportedEncodingException{
        String urladdress = URLEncoder.encode(address, "utf-8");
        return geocode + urladdress + "&key"+google_maps_key;
    }

    public void execute(ConversionCallback conversionCallback) throws UnsupportedEncodingException {
        this.conversionCallback = conversionCallback;
        new requestAddressToGoogle().execute(createURL());

    }


    /**
     * @DownloadRawData : this class is used to send the HTTPS URL request to google
     * to fetch the JSON file for the specific ad posting address the Landlord inputs
     */

    private class requestAddressToGoogle extends AsyncTask<String,Void,String> {
        private LatLongObj obj;
        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                URL url = new URL(link);
                InputStream inputStream =url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = reader.readLine()) != null)
                {
                    buffer.append(line+"\n");
                }
                //Log.e(TAG, "Response from url: " + buffer.toString());
                obj = JSONParser(buffer.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            conversionCallback.setlatlongobj(obj);

        }
    }
    private LatLongObj JSONParser (String s) throws JSONException {

        JSONObject myJSONObject = new JSONObject(s);
        JSONObject mylocation = myJSONObject.getJSONArray("results").getJSONObject(0).
                getJSONObject("geometry").getJSONObject("location");


        return new LatLongObj(mylocation.getDouble("lat"), mylocation.getDouble("lng"));

    }



}




