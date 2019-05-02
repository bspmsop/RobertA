package com.app.raassoc;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.app.raassoc.helper.NetworkChangeReceiver;

import gr.net.maroulis.library.EasySplashScreen;

/**
 * Created by New android on 26-10-2018.
 */

public class SplashScreen extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        IntentFilter filter = new IntentFilter();
//        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//
//        NetworkChangeReceiver myReceiver = new NetworkChangeReceiver();
//        registerReceiver(myReceiver, filter);








        EasySplashScreen config= new EasySplashScreen(SplashScreen.this)
                .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(2500)
                .withBackgroundColor(Color.parseColor("#E4E2E3"))
                .withLogo(R.mipmap.bets360)
                .withHeaderText("")
                .withFooterText("Copy Rights @2018")
                .withBeforeLogoText("")
                .withAfterLogoText("");
        //Set Color to Text
        config.getHeaderTextView().setTextColor(Color.BLACK);
        config.getFooterTextView().setTextColor(Color.WHITE);
        config.getBeforeLogoTextView().setTextColor(Color.WHITE);
        config.getAfterLogoTextView().setTextColor(Color.WHITE);
        //Set to View
        View view = config.create();

        //Set view to contentView
        setContentView(view);
    }
}
