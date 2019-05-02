package com.app.raassoc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.app.raassoc.helper.GlobalClass;
import com.app.raassoc.helper.NetworkChangeReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VendorList extends AppCompatActivity {
    RecyclerView mRecycle;
    LinearLayoutManager mLayoutManager;

    ProgressBar mProgress;
    private List<MyBets> betsList = new ArrayList<>();
    private DividerDecoration divider;
    private HomeAdapter homeAdapter;
    private ImageView iv_imgProfile;
    private SearchView svEquip;
    public RelativeLayout llBtn;

    private Button btnCancel,btnSave;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_list);

        divider = new DividerDecoration(this);

        mProgress = (ProgressBar) findViewById(R.id.pb_progress);
        mRecycle = (RecyclerView) findViewById(R.id.recycler);
        mLayoutManager = new LinearLayoutManager(this);
        mRecycle.setLayoutManager(mLayoutManager);
        svEquip  = (SearchView) findViewById(R.id.svEquip);
        btnSave  =(Button) findViewById(R.id.btnSave);
        RelativeLayout llBtn    = (RelativeLayout) findViewById(R.id.testLayout);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TAG","Click Save");
            }
        });

        // btnCancel= (Button) findViewById(R.id.btnCancel);
        mRecycle.addItemDecoration(divider);

        // llBtn.setVisibility(View.VISIBLE);
        iv_imgProfile = (ImageView) findViewById(R.id.iv_imgProfile);
        Bundle data = getIntent().getExtras();
        userID =data.getString("equiID");

        Boolean isnet =  new NetworkChangeReceiver().isConnectedToInternet(VendorList.this);
        if (isnet)
        {

            getList();


        }
        else
        {

            if(!GlobalClass.isnonetworknotifier)
            {
                AlertDialog.Builder build = new AlertDialog.Builder(VendorList.this);
                build.setTitle("Alert!");
                build.setCancelable(false);
                build.setMessage("No network connection would you like to use app in offline mode.");
                build.setNegativeButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getofflineVendiorData();
                    }
                });
                build.setPositiveButton("Cancel", null );

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

                        if (jad.has("vendors")) {
                            JSONArray jadr = jad.getJSONArray("vendors");
                            Log.i("msg", "got vendor listoo");

                            for (int k = 0; k < jadr.length(); k++) {

                                Log.i("msg", "got vendor list");

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

                        }}}



            }

            homeAdapter = new HomeAdapter(VendorList.this, betsList);
             mRecycle.setAdapter(homeAdapter);



        }catch (Exception e)
        {

            Log.i("msg", "got json exception "   +e);


        }









    }






    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.rl_parent:
                int position = Integer.parseInt(v.getTag().toString());

                for (int i = 0; i < betsList.size(); i++) {
                    if (i != position) {
                        betsList.get(i).setVisible(false);
                    }
                }

                if (betsList.get(position).isVisible()) {
                    betsList.get(position).setVisible(false);

                } else {
                    betsList.get(position).setVisible(true);
                }
                homeAdapter.notifyDataSetChanged();

                break;
        }
    }

    private void getList() {
        mProgress.setVisibility(View.VISIBLE);
// if (ConnectivityHelper.isConnectingToInternet(this)) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.VEND_LIST+userID+"/"+
                SharedPrefManager.getInstance(this).getUserId()+"/"+SharedPrefManager.getInstance(this).getUsertype(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mProgress.setVisibility(View.GONE);
                        Log.i("TAG", "Response:" + response);

                        try {
                            JSONObject mJOBJResponse = new JSONObject(response);
                            JSONArray mJArray = mJOBJResponse.getJSONArray("response");

                            if (mJArray.length() > 0) {
                                MyBets myBets;
                                for (int i = 0; i < mJArray.length(); i++) {
                                    myBets = new MyBets();
                                    // Toast.makeText(getApplicationContext(),"TEST EXPANDlist"+i,Toast.LENGTH_LONG).show();
                                    myBets.setId(mJArray.getJSONObject(i).getString("id"));
                                    myBets.setVname(mJArray.getJSONObject(i).getString("vname"));
                                    myBets.setStatus(mJArray.getJSONObject(i).getString("status"));
                                    myBets.setErepaired(mJArray.getJSONObject(i).getString("erepaired"));
                                    myBets.setDaterep(mJArray.getJSONObject(i).getString("daterep"));
                                    myBets.setNotes(mJArray.getJSONObject(i).getString("notes"));
                                    betsList.add(myBets);
                                }

                                Log.i("TAG", "Bets List Size:" + betsList.size());

                                if (!betsList.isEmpty()) {
                                    homeAdapter = new HomeAdapter(VendorList.this, betsList);
                                    // homeAdapter.getBtn(llBtn);

                                    mRecycle.setAdapter(homeAdapter);


                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgress.setVisibility(View.GONE);
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
        /*} else {
            mProgress.setVisibility(View.GONE);
        }*/


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Tag","LinearLayoutInvisible");
        //llBtn.setVisibility(View.INVISIBLE);
    }
}
