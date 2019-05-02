package com.app.raassoc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by New android on 22-11-2018.
 */

public class ScanQR extends AppCompatActivity implements View.OnClickListener {
    //View Objects
    private Button buttonScan;
    private TextView tvEqui, textViewAddress,textViewName;

    //qr code scanner object
    private IntentIntegrator qrScan;
    private String mechID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanqr);

        //View objects
        buttonScan = (Button) findViewById(R.id.buttonScan);
        tvEqui = (TextView) findViewById(R.id.tvEqui);
        textViewName =(TextView) findViewById(R.id.textViewName);
        textViewAddress = (TextView) findViewById(R.id.textViewAddress);

        //intializing scan object
        qrScan = new IntentIntegrator(this);
       // Bundle b = this.getIntent().getExtras();
      //  mechID = b.getString("id");

        //intializing scan object
        qrScan = new IntentIntegrator(this);

       // final TextView tvmechID =(TextView) findViewById(R.id.tvmechID);

       // tvmechID.setText(mechID);
        //attaching onclick listener
        buttonScan.setOnClickListener(this);
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                   // Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                    String resultData=result.getContents();
                    Toast.makeText(this, resultData, Toast.LENGTH_LONG).show();
                    // List<String> equipmentList = Arrays.asList(resultData.split(","));
                    String[] arrayList = resultData.split(",");
                    //ArrayList list = new ArrayList<String>();

                    for (int i = 0; i < arrayList.length; i++) {
                       // Toast.makeText(this, arrayList[i], Toast.LENGTH_LONG).show();
                       // String mechID= arrayList[0].split(":");
                        String equiID=arrayList[1];
                        textViewAddress.setText(mechID);
                    }

                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());

                } catch (JSONException e) {
                    e.printStackTrace();
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                   // Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
        //initiating the qr code scan
        qrScan.initiateScan();
    }
}
