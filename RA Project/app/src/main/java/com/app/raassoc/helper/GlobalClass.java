package com.app.raassoc.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.app.raassoc.Building;
import com.app.raassoc.Constants;
import com.app.raassoc.CustomFields;
import com.app.raassoc.CustomParseJSON;
import com.app.raassoc.CustomSections;
import com.app.raassoc.Inspection;
import com.app.raassoc.MainActivity;
import com.app.raassoc.R;
import com.app.raassoc.RepairsMain;
import com.app.raassoc.RequestHandler;
import com.app.raassoc.SharedPrefManager;
import com.app.raassoc.VendorRepair;
import com.orm.SugarContext;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.LoginException;

import dmax.dialog.SpotsDialog;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class GlobalClass {


    public static boolean isBackgroundApiRunning = false;

    public static Boolean isFromMainClass = false;
    public static Boolean isnonetworknotifier = false;

    public static Boolean istransfersync = true;

    public Context conns;
    private static List<VendorORM> vendorlist;
    private static List<VendorUpdateORM> vendorupdateorm;
    private static List<InspectionDataORM> inspectiondataorm;
    private static List<InspectionDataORM> efficiencydataorm;

    private static Integer vendorcount = 0;
    private static Integer vendroupdatecount = 0;
    private static Integer inspectioncount = 0;
    private static Integer efficiencycount = 0;
    private static AlertDialog.Builder build ;
   private static List<String> doclibarays = new ArrayList<String>();

    public static void registerNotifier(Context conn) {
        SugarContext.init(conn);
        GlobalClass.isFromMainClass = false;

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        NetworkChangeReceiver myReceiver = new NetworkChangeReceiver();
        conn.registerReceiver(myReceiver, filter);
        Log.i("back", "register notifier");


    }


    public static void CallbackgroundSyncAPI(Context coos) {

        try {
            Log.i("back", "called background api");
            GlobalClass.istransfersync = false;

            if (GlobalClass.isFromMainClass) {
                GlobalClass.istransfersync = true;
                return;
            }

            if (GlobalClass.isBackgroundApiRunning) {
                return;
            }
            isBackgroundApiRunning = true;
            startSync(coos, null);
        } catch (Exception e) {

            Log.i("back", "got exception " + e);
        }


    }


    public static void startSync(Context conns, AlertDialog ale) {


        final Context activitycon = conns;
        final AlertDialog fale = ale;

        Log.i("back", "from start sync");
        if (GlobalClass.isFromMainClass) {
            if (!GlobalClass.istransfersync) {
                Log.i("back", "not transferred here");
                GlobalClass.istransfersync = true;
                return;

            }

        }
        Log.i("back", "from start sync thread");
        GlobalClass.vendorcount = 0;
        Thread bapithread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (GlobalClass.isFromMainClass) {
                    if (!GlobalClass.istransfersync) {
                        Log.i("back", "not transferred here");
                        GlobalClass.istransfersync = true;
                        return;

                    }

                }

                Log.i("back", "background task running");


                GlobalClass.vendorlist = VendorORM.listAll(VendorORM.class);
                GlobalClass.vendorcount = 0;
                GlobalClass.vendorcount = vendorlist.size() - 1;
                Log.i("back", "venor count is " + GlobalClass.vendorcount);
                GlobalClass.callvendordata(activitycon, fale);


            }
        });
        bapithread.start();

    }


    //------------------------- calling vendor data --------------------------------

    public static void callvendordata(Context conn, AlertDialog ale) {

        if (GlobalClass.vendorcount < 0) {
            if (GlobalClass.isFromMainClass) {
                if (!GlobalClass.istransfersync) {
                    Log.i("back", "not transferred here");
                    GlobalClass.istransfersync = true;
                    return;

                }

            }
            GlobalClass.vendorupdateorm = VendorUpdateORM.listAll(VendorUpdateORM.class);
            GlobalClass.vendroupdatecount = 0;
            GlobalClass.vendroupdatecount = GlobalClass.vendorupdateorm.size() - 1;
            Log.i("back", "venor updagte count is " + GlobalClass.vendroupdatecount);
            Log.i("back", "calling update vendor");
            GlobalClass.callvendorupdatedata(conn, ale);

        } else {
            sendthevendorreportdata(conn, ale);

        }

    }


    //------------------------- calling vendor update data --------------------------------

    public static void callvendorupdatedata(Context conn, AlertDialog ale) {
        if (GlobalClass.vendroupdatecount < 0) {
            if (GlobalClass.isFromMainClass) {
                if (!GlobalClass.istransfersync) {
                    Log.i("back", "not transferred here");
                    GlobalClass.istransfersync = true;
                    return;

                }

            }
            GlobalClass.inspectiondataorm = InspectionDataORM.listAll(InspectionDataORM.class);
            List<InspectionDataORM> onlyinspection = new ArrayList<InspectionDataORM>();
            for(int i = 0; i<GlobalClass.inspectiondataorm.size(); i++)
            {
                InspectionDataORM iorms = GlobalClass.inspectiondataorm.get(i);

                String isinspection =  iorms.isins;
                if(isinspection.equals("1"))
                {
                    onlyinspection.add(iorms);
                }


            }

            GlobalClass.inspectiondataorm = onlyinspection;




            GlobalClass.inspectioncount = 0;
            GlobalClass.inspectioncount = GlobalClass.inspectiondataorm.size() - 1;
            Log.i("back", "inspection count is " + GlobalClass.inspectioncount);
            GlobalClass.callinspectiondata(conn, ale);



        } else {

            sendvendorupdatetoapi(conn, ale);


        }


    }


    //------------------------- calling inspection data --------------------------------

    public static void callinspectiondata(Context conn, AlertDialog ale) {


        if (GlobalClass.inspectioncount < 0) {

            if (GlobalClass.isFromMainClass) {
                if (!GlobalClass.istransfersync) {
                    Log.i("back", "not transferred here");
                    GlobalClass.istransfersync = true;
                    return;

                }

            }






            GlobalClass.efficiencydataorm = InspectionDataORM.listAll(InspectionDataORM.class);
            List<InspectionDataORM> onlyinspection = new ArrayList<InspectionDataORM>();
            for(int i = 0; i<GlobalClass.efficiencydataorm.size(); i++)
            {
                InspectionDataORM iorms = GlobalClass.efficiencydataorm.get(i);

                String isinspection =  iorms.isins;
                if(isinspection.equals("0"))
                {
                    onlyinspection.add(iorms);
                }


            }

            GlobalClass.efficiencydataorm = onlyinspection;


            GlobalClass.efficiencycount = 0;
            GlobalClass.efficiencycount = GlobalClass.efficiencydataorm.size() - 1;
            Log.i("back", "efficiency data count is " + GlobalClass.efficiencycount);

            GlobalClass.calledefficienydata(conn, ale);






        } else {


            final InspectionDataORM insorm = GlobalClass.inspectiondataorm.get(GlobalClass.inspectioncount);
            String myjsonstr = insorm.getMyinsdata();
            Log.i("back", "got updated data is " + insorm.getMyinsdata());
            try {

                JSONObject myjson = new JSONObject(myjsonstr);
                Long myid = insorm.getId();
                sendinspectiondatatoapi(conn, ale, myjson,myid);
            } catch (Exception e) {

                Log.i("back", "got exception is " + e);

            }


        }


    }


    //------------------------- calling efficiency data orm --------------------

    private static void calledefficienydata(final Context conn, AlertDialog ale) {

        if (GlobalClass.efficiencycount < 0) {

            String useridd = SharedPrefManager.getInstance(conn).getUserId();
            String usertype = SharedPrefManager.getInstance(conn).getUsertype();

            GlobalClass.callofflinewholeapi(useridd,usertype,conn,ale);





        } else {



            final InspectionDataORM insorm = GlobalClass.efficiencydataorm.get(GlobalClass.efficiencycount);
            String myjsonstr = insorm.getMyinsdata();
            Log.i("back", "got updated data is " + insorm.getMyinsdata());
            try {

                JSONObject myjson = new JSONObject(myjsonstr);
                Long myid = insorm.getId();
                sendefficiencydatatoapi(conn,ale,myjson,myid);

            } catch (Exception e) {

                Log.i("back", "got exception is " + e);

            }





        }


    }





    private static void callofflinewholeapi(final String id, final String typ, final Context conn, final AlertDialog ale )
    {

        String offlineURL = Constants.URL_OFFLINE + id +"/"+typ;
        StringRequest req = new StringRequest(
                Request.Method.GET,
                offlineURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try{
                             doclibarays = new ArrayList<String>();
                             GlobalClass.downloadercount = 0;

                            JSONObject obj =new JSONObject(response);
                            if (obj.has("scode")) {

                                String scode = obj.getString("scode");
                                if (scode.equals("200")) {


                                    String offlinestr = obj.getString("buildings");
                                    SharedPrefManager.getInstance(conn).setData(offlinestr,"offlinedata");

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
                                                        apipath = apipath.replaceAll(" ","");

                                                        doclibarays.add(apipath);

                                                    }

                                                }

                                            }

                                        }

                                    }






                                    Thread myt = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            GlobalClass.callfiledownloader(conn,ale);
                                        }
                                    });
                                        myt.start();



                                    Log.i("back", "offlinestr " + offlinestr);


                                } else {

                                      Log.i("back", "got exception " );
                                      showalert(conn,ale);


                                }

                            }}
                        catch (JSONException e){

                            Log.i("back", "got exception " );
                            showalert(conn,ale);

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {



                    }
                }
        );


        RequestHandler.getInstance(conn).addToRequestQueue(req);



    }





   private static Integer downloadercount = 0;



    private static void callfiledownloader(final Context con, AlertDialog ale)
    {


        if (downloadercount < doclibarays.size())
        {

            InputStream isp = null;
            String fileName = doclibarays.get(downloadercount);

            String fileNameer = fileName.substring(fileName.lastIndexOf('/') + 1);
            File photo=new File(Environment.getExternalStorageDirectory(), fileNameer);

            Log.i("back", "running background " + doclibarays.get(downloadercount));
            try {
                URL pdfurl = new URL(doclibarays.get(downloadercount));
                HttpURLConnection hcon = (HttpURLConnection) pdfurl.openConnection();
                if (hcon.getResponseCode() == 200) {
                    isp = new BufferedInputStream(hcon.getInputStream());
                    Log.i("back", "got streams heere");


                    Log.i("back", "got file name " + photo.getAbsolutePath());


                    try {
                        FileOutputStream fos = new FileOutputStream(photo.getAbsolutePath());
                        IOUtils.copy(hcon.getInputStream(), fos);
                        downloadercount = downloadercount + 1;
                        Log.i("back", "calling again.");
                        callfiledownloader(con,ale);
                        Log.i("back", "got saved here");
                    } catch (java.io.IOException e) {
                        Log.i("back ", "Exception in photoCallback ", e);
                    }


                } else {
                    downloadercount = downloadercount + 1;
                    Log.i("back", "calling again.");
                    callfiledownloader(con,ale);
                    Log.i("back", "url problemo");

                }


            } catch (Exception e) {

                downloadercount = downloadercount + 1;
                Log.i("back", "calling again got exception hre " + e.toString());
                callfiledownloader(con,ale);
            }





        }
        else
        {



            Log.i("back", "completed sync");
            isBackgroundApiRunning = false;
            GlobalClass.istransfersync = true;
            GlobalClass.isFromMainClass = false;

            if(con != null && ale != null )
            {

                try {



                    ale.dismiss();
                    Activity act = (Activity) con;

                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder build = new AlertDialog.Builder(con);
                            build.setTitle("Success!");
                            build.setCancelable(false);
                            build.setMessage("Successfully completed sync proccess");
                            build.setPositiveButton("ok", null);
                            build.show();
                        }
                    });



                }
                catch (Exception e)
                {
                    Log.i("back", "got exception here");

                }


            }









        }

    }






    //----------------------- vendor dta api calling ------------------------------------------------------
    private static void sendthevendorreportdata(final Context cons, AlertDialog ale) {
        final AlertDialog aler = ale;


        final VendorORM vorms = GlobalClass.vendorlist.get(GlobalClass.vendorcount);

        Log.i("back", "got user type as " + vorms.getUsertype());
        Log.i("back", "orm data is " + vorms.getVendorname() + "  " + vorms.getJobstatus() + "  " + vorms.getNots() + "   " + vorms.getUserid() + "  " + vorms.getUsertype());
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.SAVE_VENDOR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            Log.i("back", "got respone vendor " + jsonObject.toString());

                            Log.i("back", "vendor data executed");
                            Long myid = vorms.getId();
                            Log.i("back", "vendor id is " + myid);
                            VendorORM vro = VendorORM.findById(VendorORM.class, myid);
                            vro.delete();


                            GlobalClass.vendorcount = GlobalClass.vendorcount - 1;


                            callvendordata(cons, aler);


                        } catch (JSONException e) {


                            Log.i("back", "api parse errot");
                            showalert(cons, aler);


                        }

                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("back", "api volley errot " + error.toString());
                        showalert(cons, aler);


                    }

                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("equipment_id", vorms.equipid);
                params.put("vendor_name", vorms.vendorname);
                params.put("jobstatus", vorms.jobstatus);
                params.put("notes", vorms.nots);
                params.put("user_id", vorms.userid);
                params.put("user_type", vorms.usertype);
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

        if (cons != null) {
            RequestHandler.getInstance(cons).addToRequestQueue(stringRequest);

        } else {
            Log.i("back", "got cons problem");
            GlobalClass.showalert(cons, ale);

        }
    }


    private static void showalert(final Context con, AlertDialog ale) {
        if (GlobalClass.isFromMainClass && GlobalClass.istransfersync) {

            if (ale != null && con != null) {

                try {
                    Log.i("back", "completed sync");


                    ale.dismiss();
                    Activity act = (Activity) con;

                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder build = new AlertDialog.Builder(con);
                            build.setTitle("Failed!");
                            build.setCancelable(false);
                            build.setMessage("An internal network error occured please try again.");
                            build.setPositiveButton("ok", null);
                            build.show();
                        }
                    });



                }
                catch (Exception e)
                {
                    Log.i("back", "got exception here");

                }




            }

        }

        GlobalClass.istransfersync = true;
        GlobalClass.isBackgroundApiRunning = false;
        GlobalClass.isFromMainClass = false;


    }


    //------------------------Calling inspecion api ----------------------------------------------


    private static void sendvendorupdatetoapi(final Context cons, final AlertDialog ale) {

        final AlertDialog aler = ale;
        final VendorUpdateORM vporms = GlobalClass.vendorupdateorm.get(GlobalClass.vendroupdatecount);

        Log.i("back", "got updated data is " + vporms.getVendorid() + "  " + vporms.getJobstatus() + "   " + vporms.getNots() + "   " + vporms.getUsertype());
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.UPDATE_VEN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            Log.i("back", "got vendor update response " + jsonObject);
                            GlobalClass.vendroupdatecount = GlobalClass.vendroupdatecount - 1;
                            Long myid = vporms.getId();
                            Log.i("back", "vendor id is " + myid);
                            VendorUpdateORM vro = VendorUpdateORM.findById(VendorUpdateORM.class, myid);
                            vro.delete();

                            callvendorupdatedata(cons, ale);


                        } catch (JSONException e) {

                            Log.i("back", "api volley errot ");
                            showalert(cons, aler);
                        }

                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("back", "api volley errot " + error.toString());
                        showalert(cons, aler);

                    }

                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("vendor_id", vporms.getVendorid());
                params.put("jobstatus", vporms.getJobstatus());
                params.put("notes", vporms.getNots());
                params.put("user_type", vporms.getUsertype());
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


        if (cons != null) {
            RequestHandler.getInstance(cons).addToRequestQueue(stringRequest);

        } else {
            Log.i("back", "got cons problem");
            GlobalClass.showalert(cons, ale);
        }


    }


    //---------------------------Calling inspection api --------------------
    private static void sendinspectiondatatoapi(final Context cons, final AlertDialog ale, final JSONObject jdata, final Long ormid) {

        try {


            Boolean isfullyloaded = true;
            Log.i("back", "got inspection jdata is " + jdata);
            final AlertDialog aler = ale;

            if (jdata.has("atftype")) {
                JSONObject typejson = jdata.getJSONObject("atftype");

                Iterator<String> iter = typejson.keys();


                while (iter.hasNext()) {
                    String key = iter.next();
                    String typoo = typejson.getString(key);
                    if (typoo.equals("9")) {

                        Log.i("back", "got 9 here");
                        JSONObject atfvalues = jdata.getJSONObject("atfvalue");
                        String myimagepath = atfvalues.getString(key);
                        if (jdata.has("imgstatus")) {
                            JSONObject myimageuploadstaus = jdata.getJSONObject("imgstatus");


                            if (myimageuploadstaus.has(key)) {



                                String staus = myimageuploadstaus.getString(key);
                                if (staus.equals("0")) {

                                    isfullyloaded = false;
                                    uploadinspectionimagestatus(cons,ale,jdata,key,myimagepath,ormid);
                                    break;
                                } else {


                                }


                            } else {

                                isfullyloaded = false;
                                myimageuploadstaus.put(key, "0");

                                jdata.put("imgstatus", myimageuploadstaus);
                                uploadinspectionimagestatus(cons,ale,jdata,key,myimagepath,ormid);
                                break;
                            }


                        } else {
                            isfullyloaded = false;
                            JSONObject imgtypejson = new JSONObject();
                            imgtypejson.put(key, "0");
                            jdata.put("imgstatus", imgtypejson);
                            uploadinspectionimagestatus(cons,ale,jdata,key,myimagepath,ormid);

                            break;


                        }


                    }


                }


            }
            if(isfullyloaded)
            {

                Thread insthread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {


                            String res;
                            String url = Constants.INS_SAVE;


                            OkHttpClient.Builder builder = new OkHttpClient.Builder();
                            builder.readTimeout(60, TimeUnit.SECONDS);

                            OkHttpClient client = builder.build();

                            FormBody.Builder fb = new FormBody.Builder();


                            fb.add("insepectionData", jdata.toString());
                            Log.i("back", "got final inspection data is " + jdata.toString());


                            RequestBody req = fb.build();


                            okhttp3.Request request = new okhttp3.Request.Builder()
                                    .url(url)
                                    .post(req)
                                    .build();

                            okhttp3.Response response = client.newCall(request).execute();

                            res = response.body().string();


                            if (res != null) {

                                Log.i("back", "response of inspection is " + res);
                                InspectionDataORM idatorm = InspectionDataORM.findById(InspectionDataORM.class, ormid);
                                idatorm.delete();


                                Log.i("back", "inspection executed ");
                                GlobalClass.inspectioncount = GlobalClass.inspectioncount - 1;
                                GlobalClass.callinspectiondata(cons, ale);

                            } else {
                                GlobalClass.showalert(cons, ale);


                            }


                        } catch (Exception e) {

                            Log.i("back", "got exception hres");
                            showalert(cons,ale);
                        }
                    }
                });
                insthread.start();










            }







        } catch (Exception e) {
            Log.i("back", "got jsonparse exception  here is " + e);
        }


    }

//------------------------Inspectionimage existance ----------------


    private static void  uploadinspectionimagestatus(final Context con, final AlertDialog ale, final JSONObject jdta, final String key,final String pathe,final Long ids)
    {

Thread datath = new Thread(new Runnable() {
    @Override
    public void run() {

        try {



            File looc = new File(pathe);
            Uri filedata = Uri.fromFile(looc);


            Bitmap thumbnail = MediaStore.Images.Media.getBitmap(con.getContentResolver(), filedata);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.PNG, 50, bytes);

            String res;

            MultipartBody.Part body;
            RequestBody requestFile;

            requestFile = RequestBody.create(MediaType.parse("image/jpeg"), bytes.toByteArray());
            body = MultipartBody.Part.createFormData("file", "image.jpg", requestFile);

            RequestBody req = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addPart(body)
                    .addFormDataPart("firstName", "image.jpg")
                    .build();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.readTimeout(60, TimeUnit.SECONDS);

            OkHttpClient client = builder.build();



            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(Constants.IMAGE_UPLOAD_URL)
                    .post(req)
                    .build();

            okhttp3.Response response = client.newCall(request).execute();

            res = response.body().string();

            if(res != null) {

                Log.i("back", "got image response as " + res);
                try {

                    String filePath = CustomParseJSON.parseUploadImageResponse(res);
                    Log.i("back", "uploaded image respone is " + res);
                    JSONObject imgstaus =  jdta.getJSONObject("imgstatus");
                    imgstaus.put(key,"1");
                    JSONObject valuobj = jdta.getJSONObject("atfvalue");
                    valuobj.put(key, filePath);
                    jdta.put("atfvalue",valuobj);
                    jdta.put("imgstatus",imgstaus);


                    sendinspectiondatatoapi(con,ale,jdta,ids);



                }
                catch (Exception e)
                {

                    Log.i("back", "got cons problem");
                    GlobalClass.showalert(con, ale);


                }




            }
            else
            {

                Log.i("back", "got cons problem");
                GlobalClass.showalert(con, ale);


            }

        }
        catch (Exception e)
        {


            showalert(con, ale);


        }
    }
});

        datath.start();


    }




















    //---------------------------Calling inspection api --------------------
    private  static  void  sendefficiencydatatoapi(final Context cons, final AlertDialog ale, final JSONObject jdata, final Long ormid)
    {

        try {


            Boolean isfullyloaded = true;
            Log.i("back", "got inspection jdata is " + jdata);
            final AlertDialog aler = ale;

            if (jdata.has("atftype")) {
                JSONObject typejson = jdata.getJSONObject("atftype");

                Iterator<String> iter = typejson.keys();


                while (iter.hasNext()) {
                    String key = iter.next();
                    String typoo = typejson.getString(key);
                    if (typoo.equals("9")) {

                        Log.i("back", "got 9 here");
                        JSONObject atfvalues = jdata.getJSONObject("atfvalue");
                        String myimagepath = atfvalues.getString(key);
                        if (jdata.has("imgstatus")) {
                            JSONObject myimageuploadstaus = jdata.getJSONObject("imgstatus");


                            if (myimageuploadstaus.has(key)) {



                                String staus = myimageuploadstaus.getString(key);
                                if (staus.equals("0")) {

                                    isfullyloaded = false;
                                    uploadefficiencyimagestatus(cons,ale,jdata,key,myimagepath,ormid);
                                    break;
                                } else {


                                }


                            } else {

                                isfullyloaded = false;
                                myimageuploadstaus.put(key, "0");

                                jdata.put("imgstatus", myimageuploadstaus);
                                uploadefficiencyimagestatus(cons,ale,jdata,key,myimagepath,ormid);
                                break;
                            }


                        } else {
                            isfullyloaded = false;
                            JSONObject imgtypejson = new JSONObject();
                            imgtypejson.put(key, "0");
                            jdata.put("imgstatus", imgtypejson);
                            uploadefficiencyimagestatus(cons,ale,jdata,key,myimagepath,ormid);

                            break;


                        }


                    }


                }


            }
            if(isfullyloaded)
            {

                Thread effeidatath = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            String res;
                            String url =  Constants.CUST_SAVEEFF ;


                            OkHttpClient.Builder builder = new OkHttpClient.Builder();
                            builder.readTimeout(60, TimeUnit.SECONDS);

                            OkHttpClient client = builder.build();

                            FormBody.Builder fb = new FormBody.Builder();

                            fb.add("eqipmentsData", jdata.toString());
                            Log.i("back","got final effectd data is " + jdata.toString());


                            RequestBody req = fb.build();



                            okhttp3.Request request = new okhttp3.Request.Builder()
                                    .url(url)
                                    .post(req)
                                    .build();

                            okhttp3.Response response = client.newCall(request).execute();

                            res = response.body().string();


                            if(res != null)
                            {

                                Log.i("back", "response of efficiency  is " + res);
                                InspectionDataORM idatorm = InspectionDataORM.findById(InspectionDataORM.class,ormid);
                                idatorm.delete();


                                Log.i("back", "inspection executed ");
                                GlobalClass.efficiencycount =   GlobalClass.efficiencycount - 1;
                                GlobalClass.calledefficienydata(cons,ale);

                            }
                            else
                            {
                                GlobalClass.showalert(cons, ale);


                            }


                        }catch (Exception e)
                        {
                            Log.i("back", "got exception here");
                            showalert(cons,ale);
                        }
                    }
                });


               effeidatath.start();







            }







        } catch (Exception e) {
            Log.i("back", "got jsonparse exception  here is " + e);
        }


    }



    private static void  uploadefficiencyimagestatus(final Context con, final AlertDialog ale, final JSONObject jdta, final String key, final String pathe, final Long ids)
    {

        Thread efficencyimagethred = new Thread(new Runnable() {
            @Override
            public void run() {
                try {


                    File looc = new File(pathe);
                    Uri filedata = Uri.fromFile(looc);


                    Bitmap thumbnail = MediaStore.Images.Media.getBitmap(con.getContentResolver(), filedata);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.PNG, 50, bytes);

                    String res;

                    MultipartBody.Part body;
                    RequestBody requestFile;

                    requestFile = RequestBody.create(MediaType.parse("image/jpeg"), bytes.toByteArray());
                    body = MultipartBody.Part.createFormData("file", "image.jpg", requestFile);

                    RequestBody req = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addPart(body)
                            .addFormDataPart("firstName", "image.jpg")
                            .build();


                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    builder.readTimeout(60, TimeUnit.SECONDS);

                    OkHttpClient client = builder.build();




                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url(Constants.IMAGE_UPLOAD_URL)
                            .post(req)
                            .build();

                    okhttp3.Response response = client.newCall(request).execute();

                    res = response.body().string();

                    if(res != null) {

                        Log.i("back", "got image response as " + res);
                        try {

                            String filePath = CustomParseJSON.parseUploadImageResponse(res);
                            Log.i("back", "uploaded image respone is " + res);
                            JSONObject imgstaus =  jdta.getJSONObject("imgstatus");
                            imgstaus.put(key,"1");
                            JSONObject valuobj = jdta.getJSONObject("atfvalue");
                            valuobj.put(key, filePath);
                            jdta.put("atfvalue",valuobj);
                            jdta.put("imgstatus",imgstaus);


                            sendefficiencydatatoapi(con,ale,jdta,ids);



                        }
                        catch (Exception e)
                        {

                            Log.i("back", "got cons problem");
                            GlobalClass.showalert(con, ale);


                        }




                    }
                    else
                    {

                        Log.i("back", "got cons problem");
                        GlobalClass.showalert(con, ale);


                    }

                }
                catch (Exception e)
                {


                    showalert(con, ale);


                }
            }
        });

        efficencyimagethred.start();



    }












}
