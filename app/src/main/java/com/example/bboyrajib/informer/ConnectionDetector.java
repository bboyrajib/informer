package com.example.bboyrajib.informer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by bboyrajib on 15/04/17.
 */

public class ConnectionDetector {


        public static boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

}
