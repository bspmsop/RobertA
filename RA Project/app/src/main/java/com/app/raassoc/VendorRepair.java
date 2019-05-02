package com.app.raassoc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.app.raassoc.helper.GlobalClass;
import com.app.raassoc.helper.NetworkChangeReceiver;
import com.app.raassoc.helper.VendorORM;
import com.orm.SugarContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;

import static com.app.raassoc.R.id.btnback;

/**
 * Created by New android on 03-11-2018.
 */

public class VendorRepair extends AppCompatActivity {

    private Spinner spinnerVendor;
    private int mStatusCode;
    private String equiID;
    private ArrayList<String> vendorList;
    private JSONArray result;
    private EditText tvVenName, etRepair;
    private Button btnSave;
    private String TIME = "TIME OUT Error";
    private String TIME1 = "AuthFailure Error";
    private String TIME2 = "Connection Error";
    private String result1;
    private SpotsDialog ale;
    private Button cancelBtn;
    //private RequestQueue getRequestQueue;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_repair);

        SugarContext.init(this);

        cancelBtn = (Button) findViewById(R.id.btnLogout);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.xml.benter, R.xml.bexit);
            }
        });

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
        ImageView back = (ImageView) findViewById(btnback);
        spinnerVendor = (Spinner) findViewById(R.id.spinJobStatus);
        Bundle b = this.getIntent().getExtras();
        equiID = b.getString("equiID");
        tvVenName = (EditText) findViewById(R.id.tvVenName);
        etRepair = (EditText) findViewById(R.id.etRepair);



        ale = new SpotsDialog(this,"Loading",R.style.Customdmax);
        ale.setCancelable(false);



        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (view == btnSave) {

                    saveVendor();




                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.xml.benter, R.xml.bexit);
//                //Start your activity here
//                Intent intent = new Intent(VendorRepair.this, EquipmentData.class);
//                startActivity(intent);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();


    }

    void saveOffline()
    {


        final String vendorName = tvVenName.getText().toString().trim();
        final String jobStatus = spinnerVendor.getSelectedItem().toString();
        final String repairNotes = etRepair.getText().toString().trim();
        final String userID = SharedPrefManager.getInstance(this).getUserId();
        final String userType = SharedPrefManager.getInstance(this).getUsertype();
        VendorORM vorm = new VendorORM(equiID,vendorName,jobStatus, repairNotes,userID,userType);
        Long savestatu = vorm.save();
         Log.i("msg", "saved data " + savestatu);
       if(savestatu > 0)
       {

           AlertDialog.Builder build =  new AlertDialog.Builder(VendorRepair.this);

           build.setTitle("Success!");
           build.setCancelable(false);
           build.setMessage("Vendor Report saved successfully in local database");
           build.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   finish();
                   overridePendingTransition(R.xml.benter, R.xml.bexit);
               }
           });
           AlertDialog dia = build.create();
           dia.show();


       }







    }




    private void saveVendor() {

        final String vendorName = tvVenName.getText().toString().trim();
        final String jobStatus = spinnerVendor.getSelectedItem().toString();
        final String repairNotes = etRepair.getText().toString().trim();
        final String userID = SharedPrefManager.getInstance(this).getUserId();
        final String userType = SharedPrefManager.getInstance(this).getUsertype();

        if (vendorName.length() < 1 || spinnerVendor.getSelectedItemPosition() < 1 || repairNotes.length() < 1) {

            AlertDialog.Builder build = new AlertDialog.Builder(VendorRepair.this);
            build.setTitle("Alert!");
            build.setCancelable(false);
            build.setMessage("Please fill all required fields");
            build.setPositiveButton("Ok", null);
            AlertDialog dia = build.create();
            dia.show();

        } else {

            Boolean isnet =  new NetworkChangeReceiver().isConnectedToInternet(VendorRepair.this);
            if (!isnet)
            {
               if(!GlobalClass.isnonetworknotifier)
                {
                    AlertDialog.Builder build = new AlertDialog.Builder(VendorRepair.this);
                    build.setTitle("Alert!");
                    build.setCancelable(false);
                    build.setMessage("No network connection would you like to use app in offline mode.");
                    build.setNegativeButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveOffline();
                        }
                    });
                    build.setPositiveButton("Cancel", null );

                    AlertDialog dia = build.create();
                    dia.show();
                    GlobalClass.isnonetworknotifier =true;

                }
                else
                {
                    saveOffline();

                }

                return;
            }









        ale.show();;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.SAVE_VENDOR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       ale.dismiss();
                        try {

                            spinnerVendor.setSelection(0);
                            tvVenName.setText("");
                            etRepair.setText("");


                            JSONObject jsonObject = new JSONObject(response);
                            Log.i("msg", "got respone vendor " + jsonObject.toString());
                            AlertDialog.Builder build =  new AlertDialog.Builder(VendorRepair.this);
                            build.setTitle("Success!");
                            build.setCancelable(false);
                            build.setMessage("Vendor Report Created Successfully");
                            build.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                    overridePendingTransition(R.xml.benter, R.xml.bexit);
                                }
                            });
                            AlertDialog dia = build.create();
                            dia.show();



                        } catch (JSONException e) {
                            ale.dismiss();

                            AlertDialog.Builder build =  new AlertDialog.Builder(VendorRepair.this);
                            build.setTitle("Failed!");
                            build.setCancelable(false);
                            build.setMessage("An internal error occured please try again.");
                            build.setPositiveButton("Ok", null);
                            AlertDialog dia = build.create();
                            dia.show();
                            e.printStackTrace();
                        }

                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       ale.dismiss();

                       AlertDialog.Builder build =  new AlertDialog.Builder(VendorRepair.this);
                       build.setTitle("Failed!");
                        build.setCancelable(false);
                       build.setMessage("Please check your network connection and try again.");
                       build.setPositiveButton("Ok", null);
                       AlertDialog dia = build.create();
                       dia.show();


                    }

                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("equipment_id", equiID);
                params.put("vendor_name", vendorName);
                params.put("jobstatus", jobStatus);
                params.put("notes", repairNotes);
                params.put("user_id", userID);
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


        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

}
}
