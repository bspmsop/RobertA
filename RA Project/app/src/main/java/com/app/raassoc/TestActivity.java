package com.app.raassoc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        final RelativeLayout relativeLayout =(RelativeLayout) findViewById(R.id.rl_layout);
        Button btnClick =(Button) findViewById(R.id.btnClick);
        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.getLayoutParams().height=0;
                relativeLayout.requestLayout();
            }
        });
        Toast.makeText(this,"Button Clicked",Toast.LENGTH_LONG).show();

    }
}
