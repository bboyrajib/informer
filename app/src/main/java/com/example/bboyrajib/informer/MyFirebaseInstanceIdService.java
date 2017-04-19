package com.example.bboyrajib.informer;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by bboyrajib on 12/04/17.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    String TAG="Service";
    @Override
    public void onTokenRefresh() {
        String Token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,"Token: "+Token.toString());
        //FirebaseMessaging.getInstance().subscribeToTopic("news");
    }
}
