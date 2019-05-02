package com.app.raassoc;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.app.raassoc.helper.GlobalClass;
import com.app.raassoc.helper.NetworkChangeReceiver;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextUsername,editTextPassword;
    private Button buttonLogin;
    private TextView forgetView;

    AlertDialog ale;
    AlertDialog dias;
    AlertDialog talt;
    ArrayList<String> doclibarays;
    View vwr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        ale = new SpotsDialog(this,"Loading",R.style.Customdmax);
        if (SharedPrefManager.getInstance(this).isLogged()) {
            finish();
            startActivity(new Intent(this, Building.class));
            return;
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 2005);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 23);

        }

        editTextUsername=(EditText) findViewById(R.id.editTextUsername);
        editTextPassword=(EditText) findViewById(R.id.editTextPassword);
        buttonLogin =(Button) findViewById(R.id.buttonLogin);
       forgetView = (TextView)findViewById(R.id.fgetBtn) ;
        forgetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editTextUsername.setText("");
                editTextPassword.setText("");


                  vwr = getLayoutInflater().inflate(R.layout.forgotpassword, null);




                AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this);

                enterTapped(true);

        final EditText emailtexter = (EditText)vwr.findViewById(R.id.recoveryemail);
        Button sendReqBtn = (Button)vwr.findViewById(R.id.sendRecoveryBtn);




        sendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterTapped(false);

                if (emailtexter.getText().length() != 0)
                {

                    Boolean isnet =  new NetworkChangeReceiver().isConnectedToInternet(MainActivity.this);
                    if (!isnet)
                    {

                        AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this);
                        build.setTitle("Alert!");
                        build.setCancelable(false);
                        build.setMessage("Please check your network connection and try again.");
                        build.setNegativeButton("ok", null);
                        AlertDialog dia = build.create();
                        dia.show();
                        return ;
                    }



                       talt = new SpotsDialog(MainActivity.this,"Loading",R.style.Customdmax);
                    talt.setTitle("Loading");
                    talt.show();
                    callingForgotPasswordApi(emailtexter.getText().toString());









                }
                else
                {
                    AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this);
                    build.setTitle("Alert!");
                    build.setCancelable(false);
                    build.setMessage("Please enter your email address to reset your password");
                    build.setPositiveButton("Ok", null);
                    build.show();

                }
            }
        });



        build.setView(vwr);
          dias = build.create();
        dias.show();

        dias.getWindow().setDimAmount(0.6f);
        int fht = Constants.convertDp(MainActivity.this, 290);
        dias.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,fht);









            }
        });


        buttonLogin.setOnClickListener(this);
    }



    private void userLogin() {
        enterTapped(true);
        final String username= editTextUsername.getText().toString().trim();
        final String password= editTextPassword.getText().toString().trim();
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setCancelable(false);
        build.setTitle("Alert!");
        if (username.length() <1 || password.length() <1)
        {

            build.setMessage("Please fill required fields");
            build.setNegativeButton("ok", null);
            AlertDialog dia = build.create();
            dia.show();


            return;
        }

        Boolean isnet =  new NetworkChangeReceiver().isConnectedToInternet(this);
       if (!isnet)
       {

           build.setMessage("Please check your network connection and try again.");
           build.setNegativeButton("ok", null);
           AlertDialog dia = build.create();
           dia.show();
           return ;
       }

       ale.setCancelable(false);

        ale.show();



        StringRequest stringRequest =new StringRequest(
                Request.Method.POST,
                Constants.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {




                        try{
                            JSONObject obj =new JSONObject(response);
                           // Toast.makeText(getApplicationContext(),obj.getString("error"),Toast.LENGTH_LONG).show();
                            //if error is false response
                            if(!obj.getBoolean("error")){
                                //getting the user from the response
                                JSONObject userJSON = obj.getJSONObject("user");

                              // Toast.makeText(getApplicationContext(),userJSON.getString("user_type"),Toast.LENGTH_LONG).show();
                              //  startActivity(new Intent(getApplicationContext(), Building.class));



                                callingofflineapi( userJSON.getString("id"), userJSON.getString("user_type"), userJSON.getString("username"), userJSON.getString("email") );


                            }else{
                                ale.dismiss();
                                AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this);
                                build.setTitle("Alert!");
                                build.setCancelable(false);
                                build.setMessage(obj.getString("message"));
                                build.setPositiveButton("Ok", null);
                                AlertDialog dia = build.create();
                                dia.show();



                            }
                        }catch (JSONException e){
                             ale.dismiss();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        ale.dismiss();
                        AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this);
                        build.setTitle("Alert!");
                        build.setCancelable(false);
                        build.setMessage("Invalid username or password.");
                        build.setPositiveButton("Ok", null);
                        AlertDialog dia = build.create();
                        dia.show();

                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
               //  params.put("user_type",usertype);
                params.put("username",username);
                params.put("password",password);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }



    public void callingofflineapi(final String id, final String typ, final String uname, final String email )
    {
       // ale.dismiss();
        ale = new SpotsDialog(this,"Downloading",R.style.Customdmax);
          ale.show();
        String offlineURL = Constants.URL_OFFLINE + id +"/"+typ;
        Log.i("msg", "offline url is " + offlineURL);
        StringRequest req = new StringRequest(
                Request.Method.GET,
                offlineURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try{
                            doclibarays = new ArrayList<String>();
                            JSONObject obj =new JSONObject(response);
                            if (obj.has("scode")) {

                                String scode = obj.getString("scode");
                                if (scode.equals("200")) {
                                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(
                                            id, uname,  email,typ  );

                                    String offlinestr = obj.getString("buildings");
                                    SharedPrefManager.getInstance(getApplicationContext()).setData(offlinestr,"offlinedata");

                                    JSONArray ja = new JSONArray(offlinestr);

                                    for (int i = 0;i<ja.length(); i++)
                                    {
                                            JSONObject jb = ja.getJSONObject(i);
                                            if(jb.has("mechanicals"))
                                            {

                                                JSONArray jam = jb.getJSONArray("mechanicals");

                                                   for(int j = 0; j<jam.length(); j++)
                                                   {
                                                       JSONObject jad = jam.getJSONObject(j);

                                                       if (jad.has("drawings"))
                                                       {
                                                           JSONArray jadr = jad.getJSONArray("drawings");

                                                           for(int k = 0; k<jadr.length();k++)
                                                           {

                                                               String apipath = jadr.getJSONObject(k).getString("file_path");

                                                               apipath =  Constants.URL_bBASIC + apipath;
                                                               apipath = apipath.replaceAll(" ","%20");

                                                               Log.i("msg", "got path as " + apipath);
                                                               doclibarays.add(apipath);

                                                           }

                                                       }

                                                   }

                                            }

                                    }







                                    callfiledownloader();


//                                      startActivity(new Intent(getApplicationContext(),Building.class));
//                                      finish();
                                    Log.i("msg", "offlinestr " + offlinestr);


                                } else {
                                    AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this);
                                    build.setTitle("Failed!");
                                    build.setCancelable(false);
                                    build.setMessage("Pleae enter valid email address");
                                    build.setPositiveButton("Ok", null);
                                    AlertDialog dia = build.create();
                                    dia.show();
                                }

                            }}
                        catch (JSONException e){
                            ale.dismiss();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        ale.dismiss();
                        AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this);
                        build.setTitle("Alert!");
                        build.setCancelable(false);
                        build.setMessage("Invalid username or password.");
                        build.setPositiveButton("Ok", null);
                        AlertDialog dia = build.create();
                        dia.show();

                    }
                }
        );


        RequestHandler.getInstance(this).addToRequestQueue(req);



    }









    public void enterTapped(Boolean isfrommain)
    {
        Log.i("msg", "im tapped here");
                 try {
                     if (isfrommain) {
                         InputMethodManager inputManagers = (InputMethodManager)
                                 getSystemService(MainActivity.INPUT_METHOD_SERVICE);

                         inputManagers.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                 InputMethodManager.HIDE_NOT_ALWAYS);
                     }
                     else
                     {
                         if (vwr != null) {
                             InputMethodManager inputManagers = (InputMethodManager)
                                     getSystemService(MainActivity.INPUT_METHOD_SERVICE);

                             inputManagers.hideSoftInputFromWindow(vwr.getWindowToken(),
                                     InputMethodManager.HIDE_NOT_ALWAYS);
                         }
                     }
            }
            catch (Exception e)
            {

                Log.i("msg", "handled error" + e);
            }

    }




    public  void callingForgotPasswordApi(final String emailStr)
    {
        Log.i("msg", "got email " + emailStr);


        StringRequest forgotmobile =new StringRequest(
                Request.Method.POST,
                Constants.URL_FORGETPASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        talt.hide();



                        try{
                            JSONObject obj =new JSONObject(response);
                            Log.i("msg", "json got respone here " + obj);

                            AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this);
                            build.setTitle("Success!");
                            build.setCancelable(false);
                            build.setMessage("An email with a verification link has been sent to your email. ");
                            build.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                    dias.hide();
                                }
                            });

                            AlertDialog dia = build.create();
                            dia.show();
                            // Toast.makeText(getApplicationContext(),obj.getString("error"),Toast.LENGTH_LONG).show();
                            //if error is false response

                        }catch (JSONException e){
                            ale.dismiss();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.i("msg", "error her " + error);
                        talt.hide();
                        AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this);
                        build.setTitle("Failed!");
                        build.setCancelable(false);
                        build.setMessage("Pleae enter valid email address");
                        build.setPositiveButton("Ok", null);
                        AlertDialog dia = build.create();
                        dia.show();

                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                //  params.put("user_type",usertype);
                params.put("email",emailStr);

                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(forgotmobile);









    }







    public void onClick(View view){
        if(view==buttonLogin){
            userLogin();
        }
    }




Integer downloadercount = 0;



    public void callfiledownloader()
    {


        if (downloadercount < doclibarays.size())
        {
            new downloadFiles().execute(doclibarays.get(downloadercount));


        }
        else
        {



           startActivity(new Intent(getApplicationContext(),Building.class));
            finish();
            Log.i("msg", "completed ");

        }









    }







    class downloadFiles extends AsyncTask<String,Void,InputStream>
    {

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream isp = null;


            String fileName =strings[0];

            String fileNameer = fileName.substring(fileName.lastIndexOf('/') + 1);
            File photo=new File(Environment.getExternalStorageDirectory(), fileNameer);

                Log.i("msg", "running background " + strings[0]);
                try {
                    URL pdfurl = new URL(strings[0]);
                    HttpURLConnection hcon = (HttpURLConnection) pdfurl.openConnection();
                    if (hcon.getResponseCode() == 200) {
                        isp = new BufferedInputStream(hcon.getInputStream());
                        Log.i("msg", "got streams heere");


                        Log.i("msg", "got file name " + photo.getAbsolutePath());


                        try {
                            FileOutputStream fos = new FileOutputStream(photo.getAbsolutePath());
                            IOUtils.copy(hcon.getInputStream(), fos);
                            Log.i("msg", "got saved here");
                        } catch (java.io.IOException e) {
                            Log.i("msg ", "Exception in photoCallback ", e);
                        }


                    } else {

                        Log.i("msg", "url problemo");
                        return isp;
                    }


                } catch (Exception e) {
                    return isp;
                }



            return isp;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {




            downloadercount = downloadercount + 1;
            Log.i("msg", "calling again.");
            callfiledownloader();




//            ale.dismiss();
//            if(inputStream == null)
//            {
//                showalert();
//            }
//            else {
//
//                Log.i("msg", "called pos texcej");
//
//                myviewr.fromStream(inputStream).onLoad(new OnLoadCompleteListener() {
//                    @Override
//                    public void loadComplete(int nbPages) {
//                        ale.dismiss();
//                    }
//                }).load();
//            }
        }
    }













}
