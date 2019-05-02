package com.app.raassoc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;

import static com.app.raassoc.R.id.btnback;

/**
 * Created by New android on 03-11-2018.
 */

public class VendorRepair extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_repair);
        ImageView back = (ImageView) findViewById(btnback);
        final Spinner spinner = (Spinner) findViewById(R.id.spinJobStatus);

        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Start your activity here
                Intent intent = new Intent(VendorRepair.this, ScanEquipment.class);
                startActivity(intent);
            }
        });

    }
}
