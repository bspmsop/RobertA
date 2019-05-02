package com.app.raassoc;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.raassoc.helper.GlobalClass;
import com.app.raassoc.helper.NetworkChangeReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

/**
 * Created by New android on 31-10-2018.
 */

public class BoilerRoom extends AppCompatActivity {
    //Declaring an Spinner
    private TextView tvAddr1,tvAddr2,tvIns,tvRHead,tvTxForm;
    private ListView textViewEquip;

    //An ArrayList for Spinner Items
    private ArrayList<String> buildingID;

    //JSON Array
    private JSONArray equip;
    private  String mechID,mechTitl, insId;

    //TextViews to display details
    private TextView textViewName,textViewAddress;
    private ImageView ivBack;

    android.app.AlertDialog ale;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boiler);


      GlobalClass.registerNotifier(this);



        Bundle datas = getIntent().getExtras();
        mechID =datas.getString("id");
        final String equipment = datas.getString("title");
        Constants.GMechanical_Title = equipment;
        Constants.GMechanical_ID = mechID;
        TextView tvmechID =(TextView) findViewById(R.id.tvmechID);
        tvmechID.setText(mechID);
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        tvAddr1 = (TextView) findViewById(R.id.tvAddr1);
        tvAddr2 = (TextView) findViewById(R.id.tvAddr2);
        tvRHead = (TextView) findViewById(R.id.tvRHead);
        tvTxForm= (TextView) findViewById(R.id.tvTxForm);
       // tvIns =(TextView) findViewById(R.id .tvIns);
        tvRHead.setText(equipment);

        textViewEquip = (ListView) findViewById(R.id.textViewBoil);

        LinearLayout btnPerfInsp =(LinearLayout) findViewById(R.id.btnPerfInsp);

        btnPerfInsp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                if(insId.equals("-1")) {

                    Intent innn = new Intent(BoilerRoom.this, defaultInspectionSheet.class);
                    innn.putExtra("mechDID", mechID.toString());
                    startActivity(innn);
                    overridePendingTransition(R.xml.enter, R.xml.exit);





                }else{
                    Intent intent = new Intent(BoilerRoom.this, Inspection.class);
                    intent.putExtra("idIns", insId);
                    startActivity(intent);
                    overridePendingTransition(R.xml.enter, R.xml.exit);
                }
            }
        });

        ImageView docLibr =(ImageView) findViewById(R.id.docLibr);
        docLibr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoilerRoom.this, DocumentLibrary.class);

                    Bundle datas = new Bundle();
                    datas.putString("titlMech", tvRHead.getText().toString());
                    datas.putString("mechDID", mechID);

                    intent.putExtras(datas);

                 startActivity(intent);
                 overridePendingTransition(0, 0);

            }
        });
        ImageView vendRepr = (ImageView) findViewById(R.id.vendRepr);
        vendRepr.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(BoilerRoom.this, RepairsMain.class);
                intent.putExtra("idVen", mechID);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });


        Button btnScan = (Button) findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //initiating the qr code scan
                Intent intent = new Intent(BoilerRoom.this, ScanQR.class);
                if(equip != null) {
                    intent.putExtra("overalljson", equip.toString());
                }
                else {

                    intent.putExtra("overalljson", "[]");
                }
                intent.putExtra("idm", mechID);
                startActivity(intent);
            }
        });

       Button btnSignoutM = (Button) findViewById(R.id.btnSignoutM);
        btnSignoutM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder build = new AlertDialog.Builder(BoilerRoom.this);
                build.setTitle("Alert!");
                build.setCancelable(false);
                build.setMessage("Would you like to sign out from mechanical room");
                build.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        overridePendingTransition(R.xml.benter, R.xml.bexit);
                    }
                });
                build.setNegativeButton("Cancel", null);
                AlertDialog dia = build.create();
                dia.show();


            }
        });
        ale = new SpotsDialog(this,"Loading",R.style.Customdmax);

        ale.setCancelable(false);
        ale.show();

        Boolean isnet =  new NetworkChangeReceiver().isConnectedToInternet(BoilerRoom.this);
        if (isnet)
        {

            getData();


        }
        else
        {

            if(!GlobalClass.isnonetworknotifier)
            {
                android.app.AlertDialog.Builder build = new android.app.AlertDialog.Builder(BoilerRoom.this);
                build.setTitle("Alert!");
                build.setMessage("No network connection would you like to use app in offline mode.");
                build.setNegativeButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getofflineMechdata();
                    }
                });
                build.setPositiveButton("Cancel", null );

                android.app.AlertDialog dia = build.create();
                dia.show();
                GlobalClass.isnonetworknotifier =true;

            }
            else
            {
                getofflineMechdata();

            }

            ale.dismiss();
        }


    }






    public void getofflineMechdata()
    {
        try {


            String offlinestr = SharedPrefManager.getInstance(getApplicationContext()).getData("offlinedata");
           JSONArray result = new JSONArray(offlinestr);


            for(int i=0; i<result.length(); i++)
            {

                JSONObject jb = result.getJSONObject(i);
                if(jb.has("mechanicals")) {

                    JSONArray jam = jb.getJSONArray("mechanicals");

                    for (int j = 0; j < jam.length(); j++) {
                        JSONObject jad = jam.getJSONObject(j);
                            String jmechid = jad.getString("id");
                        Log.i("msg","mechid is " + mechID);
                        Log.i("msg","offlinemechid is " + jmechid);




                            if(mechID.equals(jmechid))
                            {

                                Log.i("msg", "match found ");

                               String addr1 =  jad.getString("address1");
                                String addr2 =  jad.getString("address2");



                                tvAddr1.setText(addr1);
                                tvAddr2.setText(addr2);






                                   if(jad.has("inspection")) {
                                       JSONObject inspecionarray = jad.getJSONObject("inspection");
                                       if (inspecionarray.has("insid")) {
                                           String inspname = inspecionarray.getString("insname");
                                           tvTxForm.setText(inspname);
                                           insId = inspecionarray.getString("insid");
                                       } else {
                                           tvTxForm.setText("Default Inspection Form");
                                           insId = "-1";
                                       }
                                   }
                                   else
                                   {
                                       tvTxForm.setText("Default Inspection Form");
                                       insId = "-1";

                                   }

                                if(jad.has((Constants.BOILER_ARRAY)))
                                {
                                    equip = jad.getJSONArray(Constants.BOILER_ARRAY);
                                    if(equip != null) {

                                        if (equip.length() <1)
                                        {
                                            Log.i("msg", "no equipmetnbs");
                                            int lht = Constants.convertDp(BoilerRoom.this, 130);

                                            LinearLayout ll = (LinearLayout)findViewById(R.id.nodataavialabletext);
                                            ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, lht  ));
                                            ll.requestLayout();

                                        }
                                        else
                                        {
                                            getEquipment(equip);
                                        }
                                          Log.i("msg", "got offline equips");
                                        //Calling method getEquipment to get the Equipments from the JSON Array

                                    }

                                }
                                else
                                {

                                    Log.i("msg", "no equipmetnbs");
                                    int lht = Constants.convertDp(BoilerRoom.this, 130);

                                    LinearLayout ll = (LinearLayout)findViewById(R.id.nodataavialabletext);
                                    ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, lht  ));
                                    ll.requestLayout();






                                }



                                break;
                            }

                    }

                }}









        }catch (Exception e)
        {

            Log.i("msg", "got json exception "   +e);


        }










    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setTitle("Alert!");
        build.setCancelable(false);
        build.setMessage("Would you like to sign out from mechanical room");
        build.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                overridePendingTransition(R.xml.benter, R.xml.bexit);
            }
        });
        build.setNegativeButton("Cancel", null);
        AlertDialog dia = build.create();
        dia.show();
    }

    private void getData(){

        //Creating a string request

        //Toast.makeText(getApplicationContext(),mechID,Toast.LENGTH_LONG).show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Constants.BOILER_URL+mechID+"/"+SharedPrefManager.getInstance(this).getUsertype(),

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        GlobalClass.isnonetworknotifier =false;
                        JSONObject j = null;
                        try {
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);
                            Log.i("msg", "no equipmetnbs" + j);
                            //Storing the Array of JSON String to our JSON Array
                            JSONObject result = j.getJSONObject("response");

                            String  address1=result.getString("address1");
                            String  address2=result.getString("address2");
                            Log.i("msg", "got equipments " + response);


                            tvAddr1.setText(address1);
                            tvAddr2.setText(address2);



                            if(result.has("insid"))
                            {
                                String  inspname=result.getString("insname");
                                tvTxForm.setText(inspname);
                                insId =  result.getString("insid");
                            }
                            else
                            {
                                tvTxForm.setText("Default Inspection Form");
                                insId   = "-1";
                            }


                            if(result.has((Constants.BOILER_ARRAY)))
                            {
                                equip = result.getJSONArray(Constants.BOILER_ARRAY);
                                if(equip != null) {


                                    //Calling method getEquipment to get the Equipments from the JSON Array
                                    getEquipment(equip);
                                }

                            }
                            else
                            {

                                Log.i("msg", "no equipmetnbs");
                                int lht = Constants.convertDp(BoilerRoom.this, 130);

                                LinearLayout ll = (LinearLayout)findViewById(R.id.nodataavialabletext);
                                ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, lht  ));
                                ll.requestLayout();






                            }








                        } catch (JSONException e) {

                            e.printStackTrace();
                            Log.i("msg", "got json error " + e);
                        }
                        ale.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        ale.dismiss();

                        AlertDialog.Builder bd = new AlertDialog.Builder(BoilerRoom.this);
                        bd.setTitle("Alert!");
                        bd.setCancelable(false);
                        bd.setMessage("Please check your network connection and try again.");
                        bd.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ale.show();
                                getData();
                            }
                        });
                        bd.setNegativeButton("Cancel", null);
                        AlertDialog dia = bd.create();
                        dia.show();

                    }
                });

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);

    }
    private void getEquipment(JSONArray j)throws JSONException {

        final String[] ids   = new String[j.length()];
        final String[] heroes = new String[j.length()];
        final String[] models = new String[j.length()];
        final String[] serial =new String[j.length()];
        final String[] effiName = new String[j.length()];
        final String[] effids = new String[j.length()];
        final String[] eqids = new String[j.length()];

        for (int i = 0; i < j.length(); i++) {
            JSONObject obj = j.getJSONObject(i);
            //getting the username from the json object and putting it inside string array
            ids[i]   = obj.getString("id");
            heroes[i] = obj.getString("title");
            models[i] = obj.getString("model");
            serial[i] = obj.getString("serial");
            effiName[i] = obj.getString("effname");
            effids[i] = obj.getString("effid");
            eqids[i] = obj.getString("id");

        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.list_text, heroes);
        Log.i("msg","go tlenth " + j.length());

        final float scale = this.getResources().getDisplayMetrics().density;
        int pixels = (int) (40 * scale + 2.5f);
        Log.i("msg", "got lent " + pixels);



        textViewEquip.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,  pixels* j.length()));
        textViewEquip.requestLayout();

        textViewEquip.setAdapter(arrayAdapter);
      //  Utility.setListViewHeightBasedOnChildren(textViewEquip);
        //  ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, heroes);
       // textViewEquip.setAdapter(arrayAdapter);
        //setListViewHeightBasedOnChildren(textViewEquip);

        textViewEquip.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String item = ((TextView)view).getText().toString();
               // Toast.makeText(getBaseContext(),item,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(BoilerRoom.this, EquipmentData.class);
                //intent.putExtra( "id", tvID.getText().toString());
                Bundle data = new Bundle();
                data.putString("id",ids[position]);
                data.putString("title",heroes[position]);
                data.putString("model",models[position]);
                data.putString("serial",serial[position]);
                 data.putString("effname",effiName[position]);

                data.putString("effid",effids[position]);
                data.putString("eqid",eqids[position]);

                intent.putExtras(data);
                startActivity(intent);
                overridePendingTransition(R.xml.enter, R.xml.exit);

            }
        });
    }

}
