package com.app.raassoc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.app.raassoc.helper.GlobalClass;
import com.app.raassoc.helper.NetworkChangeReceiver;
import com.app.raassoc.helper.VendorUpdateORM;
import com.orm.SugarContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;

import static java.lang.Integer.getInteger;
import static java.lang.Integer.parseInt;

public class RepairsMain extends AppCompatActivity implements  SearchView.OnQueryTextListener {

    RecyclerView mRecycle;
    LinearLayoutManager mLayoutManager;

   // public static final String URL = "http://mybets360.com/getvendorinformation/2/44/1";
    private String venID;
    private List<MyBets> betsList = new ArrayList<>();
    private DividerDecoration divider;
    private HomeAdapter homeAdapter;
    private LinearLayout testLayout;
    public Button btnCancel,llBtn,btnClick;
    public static int selectdRow=0;
    public static MyBets holderSelectd;
    public  JSONArray mJArray;
    AlertDialog ale;
    private ImageView backBtn, docBtn;
    SearchView editSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reparis_main);
        SugarContext.init(this);
        backBtn = (ImageView)findViewById(R.id.btnback);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,0);
            }
        });

        editSearch = (SearchView) findViewById(R.id.svEquip);
        editSearch.setBackground(null);


        docBtn = findViewById(R.id.docLibr);
        docBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(RepairsMain.this, DocumentLibrary.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        venID = Constants.GMechanical_ID;
        Log.i("msg", venID);

        divider = new DividerDecoration(this);


        mRecycle = (RecyclerView) findViewById(R.id.recycler);
        mLayoutManager = new LinearLayoutManager(this);
        mRecycle.setLayoutManager(mLayoutManager);
        mRecycle.addItemDecoration(divider);

        testLayout =(LinearLayout) findViewById(R.id.testLayout);



        ale = new SpotsDialog(this,"Loading",R.style.Customdmax);
        ale.setCancelable(false);
        ale.show();
         btnCancel = (Button) findViewById(R.id.btnCancel);
        btnClick   = (Button) findViewById(R.id.btnClick);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if(homeAdapter != null) {
                    for (int i = 0; i < betsList.size(); i++) {
                        betsList.get(i).setVisible(false);
                    }
                    homeAdapter.notifyDataSetChanged();
                }
            }
        });

      btnClick.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              hideKeyboard();



                  Log.i("msg", "button clicked ");
                  saveVendorRepair();
    }

      });




        Boolean isnet =  new NetworkChangeReceiver().isConnectedToInternet(RepairsMain.this);
        if (isnet)
        {

            getList();


        }
        else
        {

            if(!GlobalClass.isnonetworknotifier)
            {
                AlertDialog.Builder build = new AlertDialog.Builder(RepairsMain.this);
                build.setTitle("Alert!");

                build.setCancelable(false);
                build.setMessage("No network connection would you like to use app in offline mode.");
                build.setNegativeButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getofflineVendiorData();
                    }
                });


                AlertDialog dia = build.create();
                dia.show();
                GlobalClass.isnonetworknotifier =true;

            }
            else
            {
                getofflineVendiorData();

            }


        }


    }



    void getofflineVendiorData()
    {
        editSearch.setOnQueryTextListener(RepairsMain.this);
        ale.hide();
        Log.i("mg", "got offline vendror data");

        try {

            betsList = new ArrayList<>();
            String offlinestr = SharedPrefManager.getInstance(getApplicationContext()).getData("offlinedata");
            JSONArray result = new JSONArray(offlinestr);

            MyBets offlinebets;
            for(int i=0; i<result.length(); i++)
            {

                JSONObject jb = result.getJSONObject(i);
                if(jb.has("mechanicals"))
                {

                    JSONArray jam = jb.getJSONArray("mechanicals");

                    for(int j = 0; j<jam.length(); j++)
                    {
                        JSONObject jad = jam.getJSONObject(j);

                        String mechid = jad.getString("id");

                        if(Constants.GMechanical_ID.equals(mechid))
                        {

                        if (jad.has("vendors")) {
                            JSONArray jadr = jad.getJSONArray("vendors");


                            for (int k = 0; k < jadr.length(); k++) {



                                offlinebets = new MyBets();
                                // Toast.makeText(getApplicationContext(),"TEST EXPANDlist"+i,Toast.LENGTH_LONG).show();
                                offlinebets.setId(jadr.getJSONObject(k).getString("id"));
                                offlinebets.setVname(jadr.getJSONObject(k).getString("vname"));
                                offlinebets.setStatus(jadr.getJSONObject(k).getString("status"));
                                offlinebets.setErepaired(jadr.getJSONObject(k).getString("erepaired"));
                                offlinebets.setDaterep(jadr.getJSONObject(k).getString("daterep"));
                                offlinebets.setNotes(jadr.getJSONObject(k).getString("notes"));
                                betsList.add(offlinebets);
                            }

                        }}}}



            }

            homeAdapter = new HomeAdapter(RepairsMain.this, betsList);
            mRecycle.setAdapter(homeAdapter);
            refreshList();


        }catch (Exception e)
        {

            Log.i("msg", "got json exception "   +e);


        }









    }





    public void onViewClick(View v) {


        homeAdapter.clikdedhere(v);

    }

    private void getList() {


        if (ConnectivityHelper.isConnectingToInternet(this)) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET,Constants.VEND_LIST+venID+"/"+
                    SharedPrefManager.getInstance(this).getUserId()+"/"+SharedPrefManager.getInstance(this).getUsertype(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.i("msg", "Response:" + response);

                            try {
                                JSONObject mJOBJResponse = new JSONObject(response);
                                 mJArray = mJOBJResponse.getJSONArray("response");
                                betsList = new ArrayList<>();
                                 Log.i("msg", "im after respone print");
                                if (mJArray.length() > 0) {
                                    editSearch.setOnQueryTextListener(RepairsMain.this);
                                    MyBets myBets;
                                    for (int i = 0; i < mJArray.length(); i++) {
                                        myBets = new MyBets();

                                        myBets.setId(mJArray.getJSONObject(i).getString("id"));
                                        myBets.setVname(mJArray.getJSONObject(i).getString("vname"));
                                        myBets.setStatus(mJArray.getJSONObject(i).getString("status"));
                                        myBets.setErepaired(mJArray.getJSONObject(i).getString("erepaired"));
                                        myBets.setDaterep(mJArray.getJSONObject(i).getString("daterep"));
                                        myBets.setNotes(mJArray.getJSONObject(i).getString("notes"));

                                        betsList.add(myBets);
                                    }

                                    Log.i("msg", "Bets List Size:" + betsList.size());

                                    if (!betsList.isEmpty()) {

                                        refreshList();

                                    }

                                }
                                else
                                {
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                                    mRecycle.setLayoutParams(lp);
                                    mRecycle.requestLayout();

                                }

                                ale.dismiss();




                            } catch (JSONException e) {
                                ale.dismiss();
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                                mRecycle.setLayoutParams(lp);
                                mRecycle.requestLayout();

                                Log.i("msg", "error in jason array " +e);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ale.dismiss();

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
//                    params.put("Name", userName);
                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {

            ale.dismiss();
        }
    }
    public void refreshList(){



        Log.i("msg", "refreshed list");
        homeAdapter = new HomeAdapter(RepairsMain.this, betsList);
        hideKeyboard();
        if(homeAdapter != null) {
            for (int i = 0; i < betsList.size(); i++) {
                betsList.get(i).setVisible(false);
            }
            mRecycle.setAdapter(homeAdapter);
            homeAdapter.notifyDataSetChanged();
            homeAdapter.getBtn(testLayout,this);

        }


    }


    public void updatevendordataofline()
    {
        Spinner drops = (Spinner) findViewById(selectdRow);
        final String vendor_id = betsList.get(selectdRow).getId();

        final String spinJob = drops.getSelectedItem().toString();
        final String editNote = ((TextView) findViewById( 35 + selectdRow  )).getText().toString();
        final String userType = SharedPrefManager.getInstance(this).getUsertype();


        Log.i("msg","selected edit note data " + editNote);
        Log.i("msg","selected spinJob data " + spinJob);

        VendorUpdateORM vuorm = new VendorUpdateORM(vendor_id,spinJob,editNote,userType);
        long savedstat = vuorm.save();
        if(savedstat > 0)
        {

            AlertDialog.Builder build = new AlertDialog.Builder(RepairsMain.this);
            build.setTitle("Success!");
            build.setCancelable(false);
            build.setMessage("Vendor report updated successfully in local database");
            build.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                 getofflineVendiorData();
                }
            });

            AlertDialog dia = build.create();
            dia.show();

        }








    }



    private void saveVendorRepair() {



        Spinner drops = (Spinner) findViewById(selectdRow);
        final String vendor_id = betsList.get(selectdRow).getId();

        final String spinJob = drops.getSelectedItem().toString();
        final String editNote = ((TextView) findViewById( 35 + selectdRow  )).getText().toString();
        final String userType = SharedPrefManager.getInstance(this).getUsertype();


        Log.i("msg","selected edit note data " + editNote);
        Log.i("msg","selected spinJob data " + spinJob);


        if (vendor_id.length() < 1 || drops.getSelectedItemPosition() < 1 || editNote.length() < 1) {
            AlertDialog.Builder build = new AlertDialog.Builder(RepairsMain.this);
            build.setTitle("Alert!");
            build.setCancelable(false);
            build.setMessage("Please fill all required fields");
            build.setPositiveButton("Ok", null);
            AlertDialog dia = build.create();
            dia.show();


        } else {



            Boolean isnet =  new NetworkChangeReceiver().isConnectedToInternet(RepairsMain.this);
            if (!isnet)
            {

                if(!GlobalClass.isnonetworknotifier)
                {
                    AlertDialog.Builder build = new AlertDialog.Builder(RepairsMain.this);
                    build.setTitle("Alert!");
                    build.setCancelable(false);
                    build.setMessage("No network connection would you like to use app in offline mode.");
                    build.setNegativeButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updatevendordataofline();
                        }
                    });
                    build.setPositiveButton("Cancel", null );

                    AlertDialog dia = build.create();
                    dia.show();
                    GlobalClass.isnonetworknotifier =true;

                }
                else
                {
                    updatevendordataofline();

                }
                return;

            }




        ale.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.UPDATE_VEN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            AlertDialog.Builder build = new AlertDialog.Builder(RepairsMain.this);
                            build.setTitle("Success!");
                            build.setCancelable(false);
                            build.setMessage("Vendor report updated successfully.");
                            build.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getList();
                                }
                            });
                            AlertDialog dia = build.create();
                            dia.show();

                        } catch (JSONException e) {
                            ale.dismiss();
                            //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }

                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        ale.dismiss();
                    }

                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("vendor_id", vendor_id);
                params.put("jobstatus", spinJob);
                params.put("notes", editNote);
                params.put("user_type", userType);
                return params;
            }
        };
        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        500000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        //connection.setChunkedStreamingMode(0);

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

    }
    }



    void hideKeyboard()
    {
        try {
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(this.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }catch (Exception e)
        {
            Log.i("msg", "got error " + e);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        Log.i("msg", "got message " + newText);
        homeAdapter.filter(newText);
        return false;
    }
}