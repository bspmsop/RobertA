package com.app.raassoc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.app.raassoc.R.id.btnback;

/**
 * Created by New android on 02-11-2018.
 */

public class ScanEquipment extends AppCompatActivity {
    //JSON Array
    private JSONArray equipData;
    private TextView tvModel,serialNum,tvEffTest,tvTitl;
    private String mechID;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_equipment);
        Button btnScan = (Button) findViewById(R.id.btnScan);
        Button btnEffiency = (Button) findViewById(R.id.btnEfficiency);
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        tvModel =(TextView) findViewById(R.id.tvModel);
        serialNum = (TextView) findViewById(R.id.serialnum);
        tvEffTest = (TextView) findViewById(R.id.tvEffTest);
        tvTitl  = (TextView) findViewById(R.id.tvTitl);
        TextView tvEqui = (TextView) findViewById(R.id.tvEqui);
       // tvEqui.setText(getIntent().getStringExtra("title"));
        Bundle data = getIntent().getExtras();
        Bundle datas = getIntent().getExtras();

            String equipment = datas.getString("equiName1");
            String model = datas.getString("model1");
            String serial = datas.getString("serial1");
            String effiName = datas.getString("add1");
            tvEqui.setText(equipment);
            tvModel.setText(model);
            serialNum.setText(serial);
            tvEffTest.setText(effiName);
            tvTitl.setText(equipment);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start your activity here
                Intent intent = new Intent(ScanEquipment.this, VendorRepair.class);
                startActivity(intent);
            }
        });
        btnEffiency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start your activity here
                Intent intent = new Intent(ScanEquipment.this, EfficiencyTest.class);
                startActivity(intent);
            }
        });
        ImageView back = (ImageView) findViewById(btnback);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start your activity here
                Intent intent = new Intent(ScanEquipment.this, BoilerRoom.class);
                startActivity(intent);
            }
        });
      //  getData();
    }

    private void getData() {

        //Creating a string request

        //  Toast.makeText(getApplicationContext(),mechID,Toast.LENGTH_LONG).show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Constants.BOILER_URL+"2"+"/"+SharedPrefManager.getInstance(this).getUsertype(),

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);

                            //Storing the Array of JSON String to our JSON Array
                            JSONObject result = j.getJSONObject("response");
                            equipData = result.getJSONArray(Constants.BOILER_ARRAY);
                            getEquipmentData(equipData);
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);

    }
    private void getEquipmentData(JSONArray j) {
        //Traversing through all the items in the json array
        String equipmentID ="";
        for (int i = 0; i < j.length(); i++) {
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                equipmentID = json.getString("model");
                String equipmentTitle = json.getString("model");
               // Toast.makeText(getApplicationContext(),equipmentID, Toast.LENGTH_LONG).show();
                // textViewEquip.setText(equipmentID);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // tvAddr2.setText(equipmentID);


    }
}
