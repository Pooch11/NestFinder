package com.alphabgammainc.nestfinder.ProgressBarPresenter;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * Created by davidhuang on 2017-06-24.
 */

public class ProgresssBarPresenter {
    private ProgressDialog pDialog;
    private Activity mActivity;
    private String Message;
    public ProgresssBarPresenter(Activity activity,String m){
        Message=m;
        mActivity=activity;
    }

    public void ShowProgressDialog() { // progress
        if (pDialog == null) {
            pDialog = new ProgressDialog(mActivity);
            pDialog.setMessage(Message);
            pDialog.setIndeterminate(true);
        }
        pDialog.show();
    }
    public void HideProgressDialog() {
        if(pDialog!=null && pDialog.isShowing()){
            pDialog.dismiss();
            pDialog.cancel();
        }
    }

}
