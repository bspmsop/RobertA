package com.app.raassoc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by New android on 31-12-2018.
 */

public class ConnectivityHelper {
    /**
     * Checking for all possible internet providers
     **/
    public static boolean isConnectingToInternet(Context mContext) {
        final ConnectivityManager connectivity = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
            if (activeNetwork != null) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    return true;
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }
}