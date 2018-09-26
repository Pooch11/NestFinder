package com.alphabgammainc.nestfinder.User;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TabHost;
import android.widget.Toast;

import com.alphabgammainc.nestfinder.R;

/**
 * Created by davidhuang on 2017-07-02.
 */

public class AccountProfile extends AppCompatActivity{
    private View mView;
    private TabHost tabHost;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_profile);
        tabHost = (TabHost) findViewById(R.id.Tabhost);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

    }

}
