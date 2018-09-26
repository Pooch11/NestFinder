package com.alphabgammainc.nestfinder.User;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.ProgressBarManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alphabgammainc.nestfinder.FirebaseConnection.DataBaseConnectionPresenter;
import com.alphabgammainc.nestfinder.ProgressBarPresenter.ProgresssBarPresenter;
import com.alphabgammainc.nestfinder.R;

/**
 * Created by davidhuang on 2017-06-13.
 */

public class AccountCreation extends Fragment implements View.OnClickListener{
    private View myView;
    private Activity mActivity;
    private String Email;
    private String Password;
    private String ConfirmPassword;
    private ProgresssBarPresenter progressBarPresenter;
    private DataBaseConnectionPresenter dataBaseConnectionPresenter;
    private AccountCreationModel signUpPresenter;

    private EditText et1;
    private EditText et2;
    private EditText et3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity=getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.sign_up,container,false);
        mActivity.setTitle("Sign Up");

        et1= (EditText)myView.findViewById(R.id.userName);
        et2 =(EditText)myView.findViewById(R.id.userPassword);
        et3 =(EditText)myView.findViewById(R.id.confirmPasword);

        Button signup = (Button)myView.findViewById(R.id.signup);
        signup.setOnClickListener(this);



        return  myView;
    }

    public boolean checkpassword(){
        return (Password.equals(ConfirmPassword))?true:false;
    }

    public void onClick(View v) {
        if(v.getId()==R.id.signup) {
            Email= et1.getText().toString();
            Password= et2.getText().toString();
            ConfirmPassword=et3.getText().toString();

            if(checkpassword()) {
                progressBarPresenter = new ProgresssBarPresenter(mActivity,"Creating Account...");
                progressBarPresenter.ShowProgressDialog();
                signUpPresenter = new AccountCreationModel(Email,Password,dataBaseConnectionPresenter,progressBarPresenter,mActivity);
                signUpPresenter.createNewAccount();
            }else Toast.makeText(mActivity,"Password do not match",Toast.LENGTH_LONG).show();
        }
    }
    public void onDestroy() {
        ViewGroup container = (ViewGroup)mActivity.findViewById(R.id.content_frame);
        container.removeAllViews();
        super.onDestroy();
    }
}
