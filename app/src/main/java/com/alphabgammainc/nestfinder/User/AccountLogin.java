package com.alphabgammainc.nestfinder.User;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.alphabgammainc.nestfinder.FirebaseConnection.DataBaseConnectionPresenter;
import com.alphabgammainc.nestfinder.MapsActivity;
import com.alphabgammainc.nestfinder.ProgressBarPresenter.ProgresssBarPresenter;
import com.alphabgammainc.nestfinder.R;

/**
 * Created by davidhuang on 2017-06-13.
 */

public class AccountLogin extends Fragment implements View.OnClickListener{
    private View myView;
    private Activity mActivity;
    private String Email;
    private String Password;
    private DataBaseConnectionPresenter dataBaseConnectionPresenter;
    private ProgresssBarPresenter progressBarPresenter;
    private AccountLoginModel loginPresenter;
    private EditText et1;
    private EditText et2;
    private CheckBox rememberMe;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity= getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView= inflater.inflate(R.layout.login,container,false);

        et1 = (EditText)myView.findViewById(R.id.userName);
        et2 = (EditText)myView.findViewById(R.id.userPassword);

        Button signin = (Button)myView.findViewById(R.id.signIn);
        rememberMe = (CheckBox)myView.findViewById(R.id.remember_me);

        sharedPreferences = ((MapsActivity)mActivity).getSharedPreferences(getString(R.string.CREDENTIALS), Context.MODE_PRIVATE);
        if(sharedPreferences!=null){
            rememberMe.setChecked(true);
            _retrieveCredentials(et1,et2,sharedPreferences);
        }

        signin.setOnClickListener(this);


        return myView;
    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signIn:

                if(((MapsActivity)mActivity).isExternalStorageWritable()==false) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);

                }else {
                    Email = et1.getText().toString();
                    Password = et2.getText().toString();

                    progressBarPresenter = new ProgresssBarPresenter(mActivity, "Loging In...");
                    progressBarPresenter.ShowProgressDialog();
                    loginPresenter = new AccountLoginModel(Email, Password, dataBaseConnectionPresenter, mActivity, progressBarPresenter);
                    if (rememberMe.isChecked()) _storeCredentials(Email, Password);
                    else {
                        destroyPreferences(sharedPreferences);
                    }
                    loginPresenter.Login();
                }

                break;
            case R.id.signup:
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new AccountCreation()).commit();
                break;
        }

    }

    public void onDestroy() {
        ViewGroup container = (ViewGroup)mActivity.findViewById(R.id.content_frame);
        container.removeAllViews();
        super.onDestroy();
    }
    /*
     * http://stackoverflow.com/questions/9233035/best-option-to-store-username-and-password-in-android-app
     * refer to the above link when we are ready to provide encryption to our password.
     */
    public void _storeCredentials(String username, String password){
        SharedPreferences sharedPreferences = ((MapsActivity)mActivity).getApplicationContext().getSharedPreferences(getString(R.string.CREDENTIALS), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.USERNAME),username);
        editor.putString(getString(R.string.PASSWORD),password);
        editor.commit();

    }

    public void _retrieveCredentials(EditText username, EditText password,SharedPreferences sharedPreferences){
        username.setText(sharedPreferences.getString(getString(R.string.USERNAME),""));
        password.setText(sharedPreferences.getString(getString(R.string.PASSWORD),""));
    }

    public void destroyPreferences(SharedPreferences sharedPreferences){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(getString(R.string.CREDENTIALS));
        editor.commit();
    }
}
