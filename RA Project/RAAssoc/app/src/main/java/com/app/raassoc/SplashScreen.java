package com.app.raassoc;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import gr.net.maroulis.library.EasySplashScreen;

/**
 * Created by New android on 26-10-2018.
 */

public class SplashScreen extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EasySplashScreen config= new EasySplashScreen(SplashScreen.this)
                .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(2500)
                .withBackgroundColor(Color.parseColor("#E4E2E3"))
                .withLogo(R.mipmap.logo1)
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
