package com.app.raassoc;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.raassoc.helper.GlobalClass;
import com.app.raassoc.helper.NetworkChangeReceiver;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

import static com.app.raassoc.R.id.btnback;

/**
 * Created by New android on 29-11-2018.
 */

public class EquipmentData extends AppCompatActivity implements Spinner.OnItemSelectedListener {
    //JSON Array
    private JSONArray equipData;
    private JSONArray demodata;
    private TextView tvModel,serialNum,tvEffTest,tvTitl;
    private String equiID;
    private ImageView backbt;
    private Button cancelBtn;
    TextView docsView ;
    ArrayAdapter<String> spinnerArrayAdapter;
    Spinner docsSpinner ;
    ImageView doclib;
    ImageView vendorlist;
    TextView subTitle;
    LinearLayout ly;
    Integer docCoutn = 0;
    SpotsDialog ale;
    String efficiencyid;
    EditText searchbox;

    ListView doclist;
    LinearLayout dropview, spacelayout;
    FrameLayout parentview;

    private ArrayList<String> buildings;
Button dropbtn;





    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.equipment_data);

        GlobalClass.registerNotifier(this);
        ale = new SpotsDialog(this,"Loading",R.style.Customdmax);
        ale.setCancelable(false);

        ale.show();

        docsView = (TextView) findViewById(R.id.textViewInspect);
       // docsSpinner = (Spinner) findViewById(R.id.docspinner);


        dropview = new LinearLayout(this);






        LinearLayout.LayoutParams dvl = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        // dvl.setMargins(Constants.convertDp(this,10),Constants.convertDp(this,145),Constants.convertDp(this,10),0);
        dropview.setLayoutParams(dvl);
        // dropview.setBackgroundColor(getResources().getColor(R.color.nav_font));
        parentview = findViewById(R.id.framers);
        parentview.addView(dropview);
        dropview.setVisibility(View.GONE);
        dropview.setClickable(true);

        View child = getLayoutInflater().inflate(R.layout.jobstatusdroplist, null);
        dropview.addView(child);


        LinearLayout.LayoutParams d2l = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        // dvl.setMargins(Constants.convertDp(this,10),Constants.convertDp(this,145),Constants.convertDp(this,10),0);
        child.setLayoutParams(d2l);

        doclist = child.findViewById(R.id.listdrop);
        spacelayout = child.findViewById(R.id.spaceid);
        doclist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                calldocLibrary(position);




            }
        });





        searchbox = child.findViewById(R.id.searchid);




        LinearLayout backlayut = child.findViewById(R.id.backid);
        backlayut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidekeyboardonclick(null);
                hidekeyboardonclick(dropview);
                dropview.setVisibility(View.GONE);
            }
        });
        dropbtn = findViewById(R.id.docspinner);

        dropbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                int test1[] = new int[2];
                View viewer = findViewById(R.id.view6);
                viewer.getLocationInWindow(test1);

                Log.i("msg", "got coordinates as " +test1[1]);
                Integer egap =  Constants.convertDp(EquipmentData.this,30);

                hidekeyboardonclick(null);
                LinearLayout.LayoutParams layer = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, test1[1] - egap);
                spacelayout.setLayoutParams(layer);

                if(dropview.getVisibility() == View.GONE)
                {

                    dropview.setVisibility(View.VISIBLE);
                }
                else{
                    dropview.setVisibility(View.GONE);


                }





            }
        });







          doclib = (ImageView)findViewById(R.id.docLibrary);
          ly = (LinearLayout)findViewById(R.id.totlaprofncy);
          vendorlist = (ImageView)findViewById(R.id.vendRepaire) ;



          doclib.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent intent = new Intent(EquipmentData.this, DocumentLibrary.class);
                  Bundle datas = new Bundle();


                  intent.putExtras(datas);

                  startActivity(intent);
                  overridePendingTransition(0, 0);

              }
          });

        vendorlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EquipmentData.this, RepairsMain.class);
               // intent.putExtra("idVen", mechID);
                startActivity(intent);
                overridePendingTransition( 0,0);
            }
        });
        backbt = (ImageView)findViewById(R.id.btnback);
        backbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("msg","backtapped");
                finish();
                overridePendingTransition(R.xml.benter, R.xml.bexit);
            }
        });

        cancelBtn = (Button) findViewById(R.id.btnLogout);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.xml.benter, R.xml.bexit);
            }
        });
        Button btnScan = (Button) findViewById(R.id.btnScan);
        TextView btnEffiency = (TextView) findViewById(R.id.performefficiencyBtn);
        subTitle = (TextView) findViewById(R.id.subperfomtitle);


        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        tvModel =(TextView) findViewById(R.id.tvModel);
        serialNum = (TextView) findViewById(R.id.serialnum);
        tvEffTest = (TextView) findViewById(R.id.tvEffTest);
        tvTitl  = (TextView) findViewById(R.id.tvTitl);
        TextView tvEqui = (TextView) findViewById(R.id.tvEqui);


        Bundle datas = getIntent().getExtras();
         equiID =datas.getString("id");


        String equipment = datas.getString("title");
        String model = datas.getString("model");
        String serial = datas.getString("serial");
        String effiName = datas.getString("effname");

        efficiencyid = datas.getString("effid");


        if (effiName.length() > 0)
        {
            subTitle.setText(effiName);
        }
        else
        {
            LinearLayout.LayoutParams hidell = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
            ly.setLayoutParams(hidell);
            ly.requestLayout();
        }

        tvEqui.setText(equipment);
        tvModel.setText(model);
        serialNum.setText(serial);
        //tvEffTest.setText(effiName);
        tvTitl.setText(Constants.GMechanical_Title);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start your activity here
                Intent intent = new Intent(EquipmentData.this, VendorRepair.class);
                intent.putExtra("equiID",equiID);
                startActivity(intent);
                overridePendingTransition(R.xml.enter, R.xml.exit);
            }
        });
       btnEffiency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(EquipmentData.this, EfficiencyTest.class);
                intent.putExtra("idIns", efficiencyid);
                intent.putExtra("equiID",equiID);
                Log.i("msg", "idins " + efficiencyid);
                Log.i("msg", "equi id " + equiID);
                startActivity(intent);
                overridePendingTransition(R.xml.enter, R.xml.exit);
            }
        });
       subTitle.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(EquipmentData.this, EfficiencyTest.class);
               intent.putExtra("idIns", efficiencyid);
               intent.putExtra("equiID",equiID);
               Log.i("msg", "idins " + efficiencyid);
               Log.i("msg", "equi id " + equiID);
               startActivity(intent);
               overridePendingTransition(R.xml.enter, R.xml.exit);
           }
       });



        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                buildings = new ArrayList<String>();

                if(s.length() > 0 && equipData.length() > 0)
                {
                    try {


                        demodata = new JSONArray();
                        for (int i = 0; i < equipData.length(); i++) {
                            String title = equipData.getJSONObject(i).getString("file_name").toLowerCase();

                            if(title.contains(s))
                            {
                                demodata.put(equipData.getJSONObject(i));
                            }

                        }
                        for (int l=0;l<demodata.length();l++)
                        {
                            JSONObject jobj = demodata.getJSONObject(l);
                            String docName =  jobj.getString("file_name");
                            buildings.add(docName);


                        }

                        spinnerArrayAdapter = new ArrayAdapter<String>(EquipmentData.this,R.layout.spincleartext,buildings);
                        doclist.setAdapter(spinnerArrayAdapter);

                            spinnerArrayAdapter.notifyDataSetChanged();



                    }
                    catch(Exception e)
                    {
                        Log.i("msg", "got json exception");
                    }


                }
                else
                {
                    try {


                        demodata = equipData;
                        for (int l = 0; l < demodata.length(); l++) {
                            JSONObject jobj = demodata.getJSONObject(l);
                            String docName = jobj.getString("file_name");
                            buildings.add(docName);


                        }
                        spinnerArrayAdapter = new ArrayAdapter<String>(EquipmentData.this,R.layout.spincleartext,buildings);
                        doclist.setAdapter(spinnerArrayAdapter);
                        spinnerArrayAdapter.notifyDataSetChanged();

                    }catch (Exception e)
                    {

                    }
                    Log.i("msg", "got hre no text ");
                }

                Log.i("msg", "text get change d " + s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        searchbox.addTextChangedListener(textWatcher);

        Boolean isnet =  new NetworkChangeReceiver().isConnectedToInternet(EquipmentData.this);
        if (isnet)
        {

            getData();


        }
        else
        {

            if(!GlobalClass.isnonetworknotifier)
            {
                android.app.AlertDialog.Builder build = new android.app.AlertDialog.Builder(EquipmentData.this);
                build.setTitle("Alert!");
                build.setMessage("No network connection would you like to use app in offline mode.");
                build.setNegativeButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getofflineDocs();
                    }
                });
                build.setPositiveButton("Cancel", null );

                android.app.AlertDialog dia = build.create();
                dia.show();
                GlobalClass.isnonetworknotifier =true;

            }
            else
            {
                getofflineDocs();

            }

            ale.dismiss();
        }


    }



    void getofflineDocs()
    {

        try {


            String offlinestr = SharedPrefManager.getInstance(getApplicationContext()).getData("offlinedata");
            JSONArray bresult = new JSONArray(offlinestr);


            for(int i=0; i<bresult.length(); i++)
            {

                JSONObject jb = bresult.getJSONObject(i);


                if(jb.has("mechanicals")) {

                    JSONArray jam = jb.getJSONArray("mechanicals");

                    for(int j=0;j<jam.length(); j++)
                    {

                        JSONObject mechjson = jam.getJSONObject(j);
                        String meid = mechjson.getString("id");

                        if(Constants.GMechanical_ID.equals(meid))
                        {

                            JSONArray equipmensArray = mechjson.getJSONArray("equipments");

                            for(int k = 0; k<equipmensArray.length(); k++)
                            {
                                String eid = equipmensArray.getJSONObject(k).getString("id");
                                if(eid.equals(equiID))
                                {


                                             equipData = equipmensArray.getJSONObject(k).getJSONArray("drawings");
                                              demodata = equipData;
                                           docsView.setText("(" + equipData.length() + ") Documents in Library");
                                    docCoutn = equipData.length();
                                           buildings = new ArrayList<String>();

                                           for (int l=0;l<equipData.length();l++)
                                           {
                                               JSONObject jobj = equipData.getJSONObject(l);
                                               String docName =  jobj.getString("file_name");
                                               buildings.add(docName);


                                           }





                                   // docsSpinner.setOnItemSelectedListener(EquipmentData.this);

                                    spinnerArrayAdapter = new ArrayAdapter<String>(EquipmentData.this,R.layout.spincleartext,buildings);
                                    doclist.setAdapter(spinnerArrayAdapter);


//                                    spinnerArrayAdapter.setDropDownViewResource(R.layout.spincleartext);
//                                    spinnerArrayAdapter.notifyDataSetChanged();
                                   // docsSpinner.setAdapter(spinnerArrayAdapter);




                                    k = equipmensArray.length();
                                    break;



                                }



                            }

                          //  equiID





                            i = bresult.length();
                            break;
                        }






                    }






                }


            }










        }catch (Exception e)
        {

            Log.i("msg", "got json exception "   +e);


        }


    }

    private void getData() {



            Log.i("msg", "test " + Constants.CUST_EQD+equiID+"/"+SharedPrefManager.getInstance(this).getUsertype());


        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Constants.CUST_EQD+equiID+"/"+SharedPrefManager.getInstance(this).getUsertype(),

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        GlobalClass.isnonetworknotifier =false;
                        Log.i("msg", "got response " + response);
                        try {
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);

                            //Storing the Array of JSON String to our JSON Array
                            equipData = j.getJSONArray("response");

                              docCoutn = equipData.length();
                            ale.dismiss();
                            if (docCoutn > 0) {

                                docsView.setText("(" + docCoutn + ") Documents in Library");
                                buildings = new ArrayList<String>();

                               demodata =  equipData ;

                                for(int i = 0; i<docCoutn; i++)
                                {

                                 JSONObject jobj = equipData.getJSONObject(i);
                                String docName =  jobj.getString("file_name");
                                          buildings.add(docName);

                                }

                               // docsSpinner.setOnItemSelectedListener(EquipmentData.this);

                                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(EquipmentData.this,R.layout.spincleartext,buildings);

                                doclist.setAdapter(spinnerArrayAdapter);
//                                spinnerArrayAdapter.setDropDownViewResource(R.layout.spincleartext);
//                                spinnerArrayAdapter.notifyDataSetChanged();
                               // docsSpinner.setAdapter(spinnerArrayAdapter);









                            }




                           // getEquipmentData(equipData);
                        } catch (JSONException e) {
                            ale.dismiss();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ale.dismiss();
Log.i("msg", "got error as " + error);
                    }
                });

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);

    }
    private void getEquipmentData(JSONArray j) {
        //Traversing through all the items in the json array
        Log.i("msg", j.toString());
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



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

Log.i("msg", "total count " + docCoutn);
        Log.i("msg", "total count " + position);

        if(position > 0 &&    docCoutn > position - 1 )
        {

            try {

               JSONObject jobj =  equipData.getJSONObject(position - 1);

               String fileName = jobj.getString("file_path");
                Intent intent = new Intent(this, DocumentLFile.class);


                intent.putExtra("path_file", fileName);
                intent.putExtra("fromdata", true);
                Log.i("msg", "filename is "+ fileName);

               startActivity(intent);
                overridePendingTransition(R.xml.enter, R.xml.exit);

               // docsSpinner.setSelection(0);
            }
            catch (Exception e)
            {

               // docsSpinner.setSelection(0);
            }



        }
        else
        {

           // docsSpinner.setSelection(0);
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        dropview.setVisibility(View.GONE);
    }

    void calldocLibrary(int positio) {
        try {
            hidekeyboardonclick(dropview);
            hidekeyboardonclick(null);

            JSONObject jobj = demodata.getJSONObject(positio);

            String fileName = jobj.getString("file_path");
            Intent intent = new Intent(this, DocumentLFile.class);


            intent.putExtra("path_file", fileName);
            intent.putExtra("fromdata", true);
            Log.i("msg", "filename is " + fileName);

            startActivity(intent);
            overridePendingTransition(R.xml.enter, R.xml.exit);



//

        } catch (Exception e) {
            Log.i("msg", "got exception here is " + e);

        }

    }


    void hidekeyboardonclick(View v)
    {
        try {


            if(v != null)
            {
                InputMethodManager inputManagers = (InputMethodManager)
                        getSystemService(MainActivity.INPUT_METHOD_SERVICE);

                inputManagers.hideSoftInputFromWindow(v.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
            else {
                InputMethodManager inputManagers = (InputMethodManager)
                        getSystemService(MainActivity.INPUT_METHOD_SERVICE);

                inputManagers.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }catch (Exception e)
        {

            Log.i("msg", "hide exception");
        }
    }
}
