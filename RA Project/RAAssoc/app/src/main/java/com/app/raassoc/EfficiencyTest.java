package com.app.raassoc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import static com.app.raassoc.R.id.btnback;

/**
 * Created by New android on 03-11-2018.
 */

public class EfficiencyTest extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.efficiency_test);
        ImageView back = (ImageView) findViewById(btnback);

        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Start your activity here
                Intent intent = new Intent(EfficiencyTest.this, ScanEquipment.class);
                startActivity(intent);
            }
        });
    }
}
