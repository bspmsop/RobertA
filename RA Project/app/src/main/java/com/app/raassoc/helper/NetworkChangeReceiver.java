package com.app.raassoc.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class NetworkChangeReceiver extends BroadcastReceiver {

    public static final String NETWORK_AVAILABLE_ACTION = "com.offline.action";
    public static final String IS_NETWORK_AVAILABLE = "isNetworkAvailable";

    public static  Boolean isNetworkAvailable = false;






    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("msg", "recieved reciejlk ");


        NetworkChangeReceiver.isNetworkAvailable = isConnectedToInternet(context);


       if(isConnectedToInternet(context))
       {
           Log.i("msg", "got netowkr");
           GlobalClass.CallbackgroundSyncAPI(context);


       }
       else
       {
           Log.i("msg", "no network");
           GlobalClass.istransfersync = true;
           GlobalClass.isBackgroundApiRunning = false;
           GlobalClass.isFromMainClass = false;

       }







    }

    public boolean isConnectedToInternet(Context context) {
        try {
            if (context != null) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
            return false;
        } catch (Exception e) {
            Log.i("msg", "got exception " + e);
            return false;
        }
    }
}
