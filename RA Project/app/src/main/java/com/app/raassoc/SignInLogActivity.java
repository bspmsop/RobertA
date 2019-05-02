package com.app.raassoc;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by New android on 05-11-2018.
 */

public class SignInLogActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signinlog);
        ImageView imgPop =(ImageView) findViewById(R.id.imgPop);
        imgPop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                final Dialog dialog = new Dialog(SignInLogActivity.this);
                dialog.setContentView(R.layout.activity_popup);
                dialog.setTitle("DatePick");

                //Her add your textView and ImageView if you want

                Button dialogButton = (Button) dialog.findViewById(R.id.bt1_popUP);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        ImageView docLibr =(ImageView) findViewById(R.id.docLibr);
        docLibr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInLogActivity.this, DocumentLibrary.class);
                startActivity(intent);
            }
        });
        ImageView vendRep = (ImageView) findViewById(R.id.vendRep);
        vendRep.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInLogActivity.this, RepairsMain.class);
                startActivity(intent);
            }
        });
        ImageView btnback = (ImageView) findViewById(R.id.btnback);
        btnback.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInLogActivity.this, MechanicalRoom.class);
                startActivity(intent);
            }
        });

    }

}
