package com.alphabgammainc.nestfinder.User;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.ProgressBarManager;
import android.widget.Toast;

import com.alphabgammainc.nestfinder.Classes.User;
import com.alphabgammainc.nestfinder.FirebaseConnection.DataBaseConnectionPresenter;
import com.alphabgammainc.nestfinder.MapsActivity;
import com.alphabgammainc.nestfinder.ProgressBarPresenter.ProgresssBarPresenter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by davidhuang on 2017-06-24.
 */

public class AccountLoginModel {
    private String Email;
    private String Password;
    private DataBaseConnectionPresenter dataBaseConnectionPresenter;
    private Activity mActivity;
    private ProgresssBarPresenter progressBarPresenter;
    public AccountLoginModel(String e, String p, DataBaseConnectionPresenter d, Activity activity, ProgresssBarPresenter prog){
        Email=e;
        Password=p;
        dataBaseConnectionPresenter=d;
        mActivity=activity;
        progressBarPresenter=prog;
    }
    protected void Login(){
        try {
            dataBaseConnectionPresenter = DataBaseConnectionPresenter.getInstance(mActivity);
            dataBaseConnectionPresenter.getFireBaseAuth().signInWithEmailAndPassword(Email, Password).
                    addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!(task.isSuccessful())) {
                                Toast.makeText(mActivity, "authentication failed", Toast.LENGTH_SHORT).show();
                                progressBarPresenter.HideProgressDialog();
                            } else {
                                dataBaseConnectionPresenter.setFirebaseUser();

                                getUser();

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBarPresenter.HideProgressDialog();
                    Toast.makeText(mActivity,"Please Check your internet Connetion", Toast.LENGTH_LONG).show();
                }
            });
        }catch (NullPointerException e){
            progressBarPresenter.HideProgressDialog();
            e.printStackTrace();
        }
    }

    private void getUser(){
        String userId = dataBaseConnectionPresenter.getFirebaseUser().getUid().toString();
        dataBaseConnectionPresenter.getDbReference().child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                ((MapsActivity)mActivity).getUserManager().setUser(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mActivity, "Error logging in", Toast.LENGTH_LONG).show();
            }
        });
    }
}
