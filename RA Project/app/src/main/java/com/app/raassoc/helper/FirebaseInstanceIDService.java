package com.app.raassoc.helper;

import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.events.Subscriber;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.zzan;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.concurrent.Executor;

public class FirebaseInstanceIDService extends FirebaseMessagingService {


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.i("msg", "got token here");
    }
}
