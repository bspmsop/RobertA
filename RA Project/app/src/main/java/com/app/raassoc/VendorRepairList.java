package com.app.raassoc;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by New android on 09-11-2018.
 */

public class VendorRepairList extends AppCompatActivity {

    RecyclerView mRecycle;
    LinearLayoutManager mLayoutManager;

    ProgressBar mProgress;
    private List<MyBets> betsList = new ArrayList<>();
    private DividerDecoration divider;
    private HomeAdapter homeAdapter;
    private ImageView iv_imgProfile;
    private SearchView svEquip;
    private  ImageView backBtn;
    //public RelativeLayout llBtn;

    private Button btnCancel,btnSave;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reparis_main);
        backBtn = (ImageView)findViewById(R.id.btnback);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                overridePendingTransition(0, 0);
            }
        });
       // divider = new DividerDecoration(this);

      /*  mProgress = (ProgressBar) findViewById(R.id.pb_progress);
        mRecycle = (RecyclerView) findViewById(R.id.recycler);
        mLayoutManager = new LinearLayoutManager(this);
        mRecycle.setLayoutManager(mLayoutManager);
        svEquip  = (SearchView) findViewById(R.id.svEquip);
        btnSave  =(Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TAG","Click Save");
                RelativeLayout llBtn    = (RelativeLayout) findViewById(R.id.testLayout);
                llBtn.getLayoutParams().height=0;
                llBtn.requestLayout();
                // llBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button1));
                llBtn.setBackgroundColor(Color.GREEN);
            }
        });

        // btnCancel= (Button) findViewById(R.id.btnCancel);
        mRecycle.addItemDecoration(divider);

       // llBtn.setVisibility(View.VISIBLE);
        iv_imgProfile = (ImageView) findViewById(R.id.iv_imgProfile);
        Bundle data = getIntent().getExtras();
        userID =data.getString("equiID");
      //  llBtn.setVisibility(View.INVISIBLE);
        //llBtn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // Toast.makeText(getApplicationContext(),userID,Toast.LENGTH_LONG).show();
        //setupSearchView();*/
       // getList();
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
                                    homeAdapter = new HomeAdapter(VendorRepairList.this, betsList);
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