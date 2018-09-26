package com.alphabgammainc.nestfinder.User;

import android.app.Activity;
import android.app.FragmentManager;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.alphabgammainc.nestfinder.Classes.User;
import com.alphabgammainc.nestfinder.FirebaseConnection.DataBaseConnectionPresenter;
import com.alphabgammainc.nestfinder.MapsActivity;
import com.alphabgammainc.nestfinder.ProgressBarPresenter.ProgresssBarPresenter;
import com.alphabgammainc.nestfinder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by davidhuang on 2017-06-24.
 */

public class AccountCreationModel {
    private String Email;
    private String Password;
    private DataBaseConnectionPresenter dataBaseConnectionPresenter;
    private ProgresssBarPresenter progressBarPresenter;
    private Activity mActivity;


    public AccountCreationModel(String e, String p, DataBaseConnectionPresenter d, ProgresssBarPresenter prog, Activity activity){
        Email=e;
        Password=p;
        dataBaseConnectionPresenter=d;
        progressBarPresenter=prog;
        mActivity=activity;
    }

    protected void createNewAccount(){
        try {
            dataBaseConnectionPresenter =  DataBaseConnectionPresenter.getInstance(mActivity);
            dataBaseConnectionPresenter.getFireBaseAuth().createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBarPresenter.HideProgressDialog();
                    if (!(task.isSuccessful())) {
                        Toast.makeText(mActivity, "Something went wrong", Toast.LENGTH_SHORT).show();
                        System.out.println(task.getException().toString());
                    } else {
                        User user = new User(Email,dataBaseConnectionPresenter.getFirebaseUser().getUid(),null,null);
                        DatabaseReference reference = dataBaseConnectionPresenter.getDbReference().child("Users").push();
                        reference.setValue(user);

                        FragmentManager fragmentManager = ((MapsActivity) mActivity).getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, new AccountLogin()).commit();

                    }
                }
            });
        }catch (NullPointerException e){
            progressBarPresenter.HideProgressDialog();
            e.printStackTrace();
        }
    }
}
