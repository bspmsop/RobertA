package com.app.raassoc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.app.raassoc.R.id.textViewBoil;

/**
 * Created by New android on 31-10-2018.
 */

public class BoilerRoom extends AppCompatActivity {
    //Declaring an Spinner
    private TextView tvAddr1,tvAddr2,tvIns;
    private ListView textViewEquip;

    //An ArrayList for Spinner Items
    private ArrayList<String> buildingID;

    //JSON Array
    private JSONArray equip;
    private  String mechID;

    //qr code scanner object
    private IntentIntegrator qrScan;

    //TextViews to display details
    private TextView textViewName,textViewAddress;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boiler);
        Bundle b = this.getIntent().getExtras();
        mechID = b.getString("id");

        //intializing scan object
        qrScan = new IntentIntegrator(this);

        final TextView tvmechID =(TextView) findViewById(R.id.tvmechID);

        tvmechID.setText(mechID);
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
       // Bundle b = this.getIntent().getExtras();
        //name = b.getString("id");

        tvAddr1 = (TextView) findViewById(R.id.tvAddr1);
        tvAddr2 = (TextView) findViewById(R.id.tvAddr2);
        tvIns =(TextView) findViewById(R.id.tvIns);

        textViewEquip = (ListView) findViewById(textViewBoil);

        Button btnPerfInsp =(Button) findViewById(R.id.btnPerfInsp);

        btnPerfInsp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Start your activity here
                Intent intent = new Intent(BoilerRoom.this, Inspection.class);
                startActivity(intent);
            }
        });
        ImageView docLibr =(ImageView) findViewById(R.id.docLibr);
        docLibr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoilerRoom.this, DocumentLibrary.class);
                startActivity(intent);
            }
        });
        ImageView vendRepr = (ImageView) findViewById(R.id.vendRepr);
        vendRepr.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoilerRoom.this, RepairsMain.class);
                startActivity(intent);
            }
        });
        ImageView btnBack = (ImageView) findViewById(R.id.btnback);
        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoilerRoom.this, MechRoom.class);
                startActivity(intent);
            }
        });
        Button btnScan = (Button) findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
               // setContentView(R.layout.scan_equipment);
                //initiating the qr code scan
               // qrScan.initiateScan();
                Intent intent = new Intent(BoilerRoom.this, ScanQR.class);
               //intent.putExtra( "id", tvmechID.getText().toString() );
                startActivity(intent);
            }
        });
        getData();
    }
    private void getData(){

        //Creating a string request

        //  Toast.makeText(getApplicationContext(),mechID,Toast.LENGTH_LONG).show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Constants.BOILER_URL+mechID+"/"+SharedPrefManager.getInstance(this).getUsertype(),

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);

                            //Storing the Array of JSON String to our JSON Array
                            JSONObject result = j.getJSONObject("response");

                            String  address1=result.getString("address1");
                            String  address2=result.getString("address2");
                            String  inspname=result.getString("insname");
                            tvAddr1.setText(address1);
                            tvAddr2.setText(address2);
                            tvIns.setText(inspname);

                            //Toast.makeText(getApplicationContext(),result.getString("equipments"), Toast.LENGTH_LONG).show();
                            // tvAddr2.setText(equipmentID);
                             equip = result.getJSONArray(Constants.BOILER_ARRAY);

                            //Calling method getEquipment to get the Equipments from the JSON Array
                            getEquipment(equip);
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
    private void getEquipment(JSONArray j)throws JSONException {

        final String[] heroes = new String[j.length()];
        final String[] models = new String[j.length()];
        final String[] serial =new String[j.length()];
        final String[] effiName = new String[j.length()];
        for (int i = 0; i < j.length(); i++) {
            JSONObject obj = j.getJSONObject(i);
            //getting the username from the json object and putting it inside string array
            heroes[i] = obj.getString("title");
            models[i] = obj.getString("model");
            serial[i] = obj.getString("serial");
            effiName[i] = obj.getString("effname");
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, heroes);
        textViewEquip.setAdapter(arrayAdapter);
        textViewEquip.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String item = ((TextView)view).getText().toString();
               // Toast.makeText(getBaseContext(),item,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(BoilerRoom.this, ScanEquipment.class);
                //intent.putExtra( "id", tvID.getText().toString() );
                Bundle data = new Bundle();
                data.putString("title",heroes[position]);
                data.putString("model",models[position]);
                data.putString("serial",serial[position]);
                data.putString("effname",effiName[position]);

                intent.putExtras(data);
                startActivity(intent);

            }
        });
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
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                    textViewName.setText(obj.getString("name"));
                    textViewAddress.setText(obj.getString("address"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



}
