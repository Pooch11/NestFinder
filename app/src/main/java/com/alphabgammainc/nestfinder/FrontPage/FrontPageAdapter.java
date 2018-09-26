package com.alphabgammainc.nestfinder.FrontPage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;

import com.alphabgammainc.nestfinder.Classes.Locations;
import com.alphabgammainc.nestfinder.DetailsPage.DetailsPage;
import com.alphabgammainc.nestfinder.Landlord.AdPostingManager;
import com.alphabgammainc.nestfinder.MapsActivity;
import com.alphabgammainc.nestfinder.R;
import com.alphabgammainc.nestfinder.Utilities.ImageManager;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

/**
 * Created by davidhuang on 2017-04-25.
 */

public class FrontPageAdapter  extends RecyclerView.Adapter<FrontPageAdapter.ViewHolder> {
    private Activity mActivity;
    private ArrayList<Locations> locationses;
    private ManageMap callback;
    private RecyclerView WrapperView;

    /**
     *
     * @param activity
     * @param locationses
     */
    public FrontPageAdapter(Activity activity, ArrayList<Locations> locationses, ManageMap callback,
                            RecyclerView recyclerView){
        this.mActivity = activity;
        this.locationses =locationses;
        this.callback = callback;
        this.WrapperView = recyclerView;
    }

    @Override
    public FrontPageAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.front_page_listview_item, parent, false);

        ViewHolder holder = new ViewHolder(v);

     //   v.setOnClickListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(final FrontPageAdapter.ViewHolder holder, final int position) {

        ImageManager.downloadImage(mActivity,holder.image, locationses.get(position).getRentImage().get(0));
        holder.address.setText(this.locationses.get(position).getAddress());
        holder.price.setText("Price : $"+Double.toString((this.locationses.get(position).getPrice())));
        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUp(holder.details, position);
            }
        });
        holder.showMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // int itemPosition = WrapperView.getChildLayoutPosition(v);
                callback.setMarkerFocusCallback(locationses.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return locationses.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView address;
        TextView price;
        ImageView image;
        ImageView details;
        LinearLayout showMarker;
        public ViewHolder(View itemView) {
            super(itemView);
            address = (TextView)itemView.findViewById(R.id.location);
            price = (TextView)itemView.findViewById(R.id.price);
            image = (ImageView)itemView.findViewById(R.id.image);
            details = (ImageView)itemView.findViewById(R.id.show_detils);
            showMarker = (LinearLayout)itemView.findViewById(R.id.show_marker);
        }

    }

    /**
     *
     * @param v the specific view of the list item
     * @param position position item
     * @todo consider removing the three dots and adding learn more on the bottom instead.
     */
    public void showPopUp(View v, final int position) {

        PopupMenu popup = new PopupMenu(mActivity, v, Gravity.TOP);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getTitle().toString()){
                    case "Details":

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("location",locationses.get(position));

                        Intent myIntent = new Intent(mActivity, DetailsPage.class);
                        myIntent.putExtra("bundle", bundle);
                        mActivity.startActivityForResult(myIntent, 2);

                        break;

                }


                return false;
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.details, popup.getMenu());

        popup.show();
    }



}
