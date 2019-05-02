package com.app.raassoc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity{

    private TextView textViewUsername,textViewUseremail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
        textViewUsername=(TextView)findViewById(R.id.textViewUsername);
        textViewUseremail=(TextView)findViewById(R.id.textViewUseremail);

        textViewUseremail.setText(SharedPrefManager.getInstance(this).getUseremail());
        textViewUsername.setText(SharedPrefManager.getInstance(this).getUsername());

        findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
                SharedPrefManager.getInstance(getApplicationContext()).logout();
            }
        });

    }
}
