package com.app.raassoc;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.raassoc.helper.GlobalClass;
import com.app.raassoc.helper.InspectionDataORM;
import com.app.raassoc.helper.NetworkChangeReceiver;
import com.orm.SugarContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import dmax.dialog.SpotsDialog;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import static com.app.raassoc.Constants.CUST_SAVEEFF;
import static com.app.raassoc.R.id.btnback;
import static com.app.raassoc.R.id.time;

/**
 * Created by New android on 03-11-2018.
 */

public class EfficiencyTest extends AppCompatActivity implements View.OnClickListener {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.efficiency_test);
        SugarContext.init(this);
        defaultFunc();
        settingReferences();


    }


    LinearLayout lmLayout;


    private ArrayList<CustomSections> listSection;
    Integer btnId = 123;
     String equipMentId;
    ArrayList<Integer> ids = new ArrayList<>();
    HashMap<Integer, String> hasmap = new HashMap<>();
    HashMap<Integer, String> imageMaps = new HashMap<>();
    HashMap<Integer, String> imageuploadStaus = new HashMap<>();
    HashMap<Integer, String> imageStoredPath = new HashMap<>();
    private int RESULT_LOAD_GALLERY_IMAGE = 1;
    Integer imageresourceid;
    Bitmap thumbnail;
    ByteArrayOutputStream bytes;
    private String filePath=null;
    private boolean flag = false;
    private String insID;
    private ImageView backBtn;
    AlertDialog ale;
    Button selectedImage;
    public Uri cameraFile;
    Boolean isnetworkavailable =  new NetworkChangeReceiver().isConnectedToInternet(EfficiencyTest.this);

    final Calendar myCalendar = Calendar.getInstance();

    EditText EfficiencyName;
    EditText EfficiencyTime;
    String tempFile="";





    public void defaultFunc()
    {
        backBtn = (ImageView) findViewById(R.id.inspectionbtnback);
        backBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              finish();
              overridePendingTransition(R.xml.benter, R.xml.bexit);
          }
      });

    }

    @Override
    protected void onStart() {
        super.onStart();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, 2005);

                return;
            }
            else
            {
                ActivityCompat.requestPermissions(EfficiencyTest.this,
                        new String[]{Manifest.permission.CAMERA},
                        2005);
                Log.i("msg", "permission   granted..");
            }
        }




    }

    public void checkTextBoxData()
    {
        ale.show();


        Boolean isvalidinputs = true;
        Boolean isProcessRunning = false;

        for (Integer resourceId : ids) {

            for (CustomSections section : listSection) {

                for (CustomFields fields : section.getListFields()) {

                    if (resourceId.equals(fields.getId())) {




                        if (EfficiencyName.getText().toString().length() <1 || EfficiencyTime.getText().toString().length() <1 )
                        {
                            isvalidinputs = false;


                        }
                        else if (fields.getiType().equals("1")) {
                            EditText et1 = (EditText) findViewById(resourceId);
                            if (et1.getText().toString().length() < 1)
                            {

                                isvalidinputs = false;
                            }

                        }

                        else if (fields.getiType().equals("9")) {
                            Button cbutton = (Button) findViewById(resourceId);
                            String FilePath = imageMaps.get(resourceId);

                            String imagep = imageuploadStaus.get(resourceId);
                            if (imagep == null)
                            {
                                Log.i("msg", "image is null");
                                isvalidinputs = false;

                            }

                        }

                        break;
                    }

                }
                if (!isvalidinputs)
                {
                    break;
                }
            }

            if (!isvalidinputs)
            {
                break;
            }

        }


        if ( !isvalidinputs )
        {
            ale.dismiss();
            AlertDialog.Builder build = new AlertDialog.Builder(EfficiencyTest.this);
            build.setTitle("Alert!");
            build.setCancelable(false);
            build.setMessage("Please fill required fields");
            build.setPositiveButton("Ok", null);
            AlertDialog dia = build.create();
            dia.show();

        }
        else if ( isvalidinputs )
        {
            checkImagesExists(true);

        }

    }



    public void checkImagesExists(Boolean uploadstatus) {

        if (uploadstatus) {


        hasmap = new HashMap<>();


        Boolean isvalidinputs = true;
        Boolean isProcessRunning = false;

        for (Integer resourceId : ids) {

            for (CustomSections section : listSection) {

                for (CustomFields fields : section.getListFields()) {

                    if (resourceId.equals(fields.getId())) {


                        if (fields.getiType().equals("1")) {
                            EditText et1 = (EditText) findViewById(resourceId);
                            if (et1.getText().toString().length() < 1) {
                                Log.i("msg", "no data entered in checking");
                                isProcessRunning = false;
                                isvalidinputs = false;
                            }


                            hasmap.put(resourceId, et1.getText().toString());


                        }

                        if (fields.getiType().equals("9")) {
                            Button cbutton = (Button) findViewById(resourceId);
                            String FilePath = imageMaps.get(resourceId);

                            String imagep = imageuploadStaus.get(resourceId);
                            if (imagep != null) {
                                if (imagep.equals("0")) {


                                    if (!isnetworkavailable) {

                                        String filepath = imageMaps.get(resourceId);
                                        imageStoredPath.put(resourceId, filepath);


                                    } else {
                                        String localPath = imageMaps.get(resourceId);
                                        try {
                                            File looc = new File(localPath);
                                            Uri filedata = Uri.fromFile(looc);

                                            imageresourceid = resourceId;
                                            thumbnail = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), filedata);
                                            bytes = new ByteArrayOutputStream();
                                            thumbnail.compress(Bitmap.CompressFormat.PNG, 50, bytes);
                                            new EfficiencyTest.UploadImage().execute();

                                        } catch (Exception e) {
                                            Log.i("msg", "got excepiton " + e);
                                        }
                                        isProcessRunning = true;
                                        isvalidinputs = false;

                                    }

                                }

                            } else {
                                Log.i("msg", "image is null");
                                isvalidinputs = false;
                            }


                        }


                        break;
                    }

                }
                if (!isvalidinputs) {
                    break;
                }
            }

            if (!isvalidinputs) {
                break;
            }

        }


        if (!isProcessRunning && !isvalidinputs) {
            ale.dismiss();
            AlertDialog.Builder build = new AlertDialog.Builder(EfficiencyTest.this);
            build.setTitle("Alert!");
            build.setCancelable(false);
            build.setMessage("Please fill required fields");
            build.setPositiveButton("Ok", null);
            AlertDialog dia = build.create();
            dia.show();

        } else if (!isProcessRunning && isvalidinputs) {
            createData();
            String jsonstr = null;
            Log.i("msg", "got hashmap " + hasmap);
            try {
                jsonstr = createJSON().toString();
                final String finalJsonstr = jsonstr;

                if (isnetworkavailable) {
                    Log.i("msg", "inspection saved");

                    //------------------------------ inspection saved -----------------
                     new EfficiencyTest.UploadData().execute(jsonstr);
                    //------------------------------ inspection saved -----------------

                } else {


                    saveoffline(finalJsonstr);


                }


                Log.i("msg", jsonstr);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {

        }
    }
    else
    {
        ale.dismiss();
        AlertDialog.Builder build = new AlertDialog.Builder(EfficiencyTest.this);
        build.setTitle("Alert!");
        build.setCancelable(false);
        build.setMessage("Network error occured, please try again.");
        build.setPositiveButton("Ok", null);
        AlertDialog dia = build.create();
        dia.show();






    }



    }







    public void saveoffline(String instr)
    {
        String useridd = SharedPrefManager.getInstance(this).getUserId();
        String userTypee = SharedPrefManager.getInstance(this).getUsertype();





        InspectionDataORM iorm = new InspectionDataORM(useridd,userTypee,"0",instr);
        long insStatus = iorm.save();

        if(insStatus >0)
        {
            Log.i("msg","test" + insStatus);

            AlertDialog.Builder build = new AlertDialog.Builder(EfficiencyTest.this);
            build.setTitle("Success!");
            build.setCancelable(false);
            build.setMessage("Inspection sheet successfully saved to local database");
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



        Log.i("msg", instr);



    }


    private void updateLabel() {
        String myFormat = "MM-dd-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);


        EfficiencyName.setText(sdf.format(myCalendar.getTime()));
    }


    private void customForm() {


        Log.i("msg", "equipmetn test api " +Constants.CUST_EFF+insID+"/"+SharedPrefManager.getInstance(this).getUsertype() );
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Constants.CUST_EFF + insID+"/"+SharedPrefManager.getInstance(this).getUsertype(),

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            Log.i("msg","respone is  "+response);
                            listSection = CustomParseJSON.parseSections(response);

                            // Do stuff from layout
                            createLayout();
                            ale.dismiss();


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
                        Log.i("msg ", "got eror "+ error);
                    }
                });

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    /**
     * This method creates JSON for all the inspection data collected.
     *
     * @return JSONObject
     * @throws JSONException
     */
    private JSONObject createJSON() throws JSONException {

        // parent json
        JSONObject parent = new JSONObject();

        // id json
        JSONObject atfId = new JSONObject();

        // value json
        JSONObject atfValue = new JSONObject();

        // type json
        JSONObject atfType = new JSONObject();

        // required json
        JSONObject atfRequired = new JSONObject();

        // title json
        JSONObject atfTitle = new JSONObject();


        for (Map.Entry<Integer, String> entry : hasmap.entrySet()) {

            String key = String.valueOf(entry.getKey());
            String value = entry.getValue();

            atfId.put(key, key);
            if (value.contains(",")) {
                String[] split = value.split(",");

                JSONObject obj = new JSONObject();

                for (int i = 0; i < split.length; i++) {
                    obj.put(i + "", split[i]);
                }
                atfValue.put(key, obj);

            } else {
                atfValue.put(key, value);
            }

            for (CustomSections sec : listSection) {

                for (CustomFields fie : sec.getListFields()) {

                    if (entry.getKey() == fie.getId()) {
                        atfType.put(key, fie.getiType());
                        atfRequired.put(key, fie.getRequired());
                        atfTitle.put(key, fie.getLabel());
                        break;
                    }
                }
            }
        }





        String useridd = SharedPrefManager.getInstance(this).getUserId();
        String userTypee = SharedPrefManager.getInstance(this).getUsertype();


        parent.put("overall_efficiency", EfficiencyTime.getText().toString());
        parent.put("edate", EfficiencyName.getText().toString());
        parent.put("eqp_id",  equipMentId);
        parent.put("asset_id", insID);
        parent.put("user_type", userTypee);
        parent.put("user_id", useridd);
        parent.put("atfid", atfId);
        parent.put("atfvalue", atfValue);
        parent.put("attitle", atfTitle);
        parent.put("atrequired", atfRequired);
        parent.put("atftype", atfType);

        return parent;
    }

    /**
     * This method uploads image on the server
     *
     * @return json which contains filePath
     */
    private String uploadImage() {

        String res;

        MultipartBody.Part body;
        RequestBody requestFile;

        requestFile = RequestBody.create(MediaType.parse("image/jpeg"), bytes.toByteArray());
        body = MultipartBody.Part.createFormData("file", "image.jpg", requestFile);

        RequestBody req = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addPart(body)
                .addFormDataPart("firstName", "image.jpg")
                .build();

        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.readTimeout(60, TimeUnit.SECONDS);

            OkHttpClient client = builder.build();




            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(Constants.IMAGE_UPLOAD_URL)
                    .post(req)
                    .build();

            okhttp3.Response response = client.newCall(request).execute();

            res = response.body().string();

        } catch (Exception e) {
            e.printStackTrace();
            res = null;
        }
        return res;
    }

    /**
     * This method sends the inspection data to the server
     *
     * @param param inspection data json
     * @return json output in form of string
     */
    private String sendData(final String param) {

        String res;
        String urleff =  Constants.CUST_SAVEEFF ;

        Log.i("msg", "got url eff " + urleff);

        try {

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.readTimeout(60, TimeUnit.SECONDS);

            OkHttpClient client = builder.build();

            FormBody.Builder fb = new FormBody.Builder();

            fb.add("eqipmentsData", param);

            RequestBody req = fb.build();



            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(urleff)
                    .post(req)
                    .build();

            okhttp3.Response response = client.newCall(request).execute();

            res = response.body().string();

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("msg", "got exception send data " + e);
            res = null;
        }
        return res;
    }


    /**
     * {@link Inspection.UploadData} class to upload inspection data to the server
     */
    private class UploadData extends AsyncTask<String, Void, String> {



        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {

            return sendData(params[0]);
        }

        @Override
        protected void onPostExecute(String response) {

            {
                ale.dismiss();

                Log.i("msg", "got respone here "+ response);

                try {
                    String ouput=CustomParseJSON.parseUploadDataResponse(response);

                    AlertDialog.Builder build = new AlertDialog.Builder(EfficiencyTest.this);
                    build.setTitle("Success!");
                    build.setCancelable(false);
                    build.setMessage(ouput);
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
                    Log.i("msg", "got exception for upload data " +e);

                    AlertDialog.Builder build = new AlertDialog.Builder(EfficiencyTest.this);
                    build.setTitle("Alert!");
                    build.setCancelable(false);
                    build.setMessage("Internal error occurred please try again.");
                    build.setPositiveButton("Ok",  null);
                    AlertDialog dia = build.create();
                    dia.show();

                    e.printStackTrace();
                }


            }

        }
    }

    /**
     * {@link Inspection.UploadData} class to upload image to the server
     */
    private class UploadImage extends AsyncTask<Void, Void, String> {

        ProgressDialog pd = new ProgressDialog(EfficiencyTest.this);

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(Void... params) {

            return uploadImage();
        }

        @Override
        protected void onPostExecute(String apr) {
            try {

                if(apr != null) {
                    filePath = CustomParseJSON.parseUploadImageResponse(apr);
                    imageuploadStaus.put(imageresourceid,"1");
                    imageStoredPath.put(imageresourceid,filePath);

                    checkImagesExists(true);
                }
                else {
                    checkImagesExists(false);


                }



            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }


    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Generate a value suitable for use in setId.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    /**
     * This method creates linear layout for checkbox
     *
     * @param weightsum number of checkboxes in single line
     * @return LinearLayout
     */
    private LinearLayout createHeadLinearLayout(int weightsum, int ori) {

        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final LinearLayout ll = new LinearLayout(EfficiencyTest.this);


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            ll.setId(generateViewId());
        } else {
            ll.setId(View.generateViewId());
        }


        ll.setLayoutParams(lparams);
        ll.setWeightSum(weightsum);
        ll.setOrientation(ori);
        ll.setPadding((int) getResources().getDimension(R.dimen.padding), (int) getResources().getDimension(R.dimen.padding), (int) getResources().getDimension(R.dimen.padding), (int) getResources().getDimension(R.dimen.padding));
        ll.setGravity(Gravity.CENTER_VERTICAL);

        return ll;
    }


    /**
     * This method is there to handle {@link RadioButton}, selecting one of them, makes other unselected.
     *
     * @param v selected {@link RadioButton} id
     */
    @Override
    public void onClick(View v) {

        Integer vid = v.getId();
        String svid = String.valueOf(vid);
        String id = svid.substring(0, svid.length() - 1);



        for (CustomSections sec : listSection) {

            for (CustomFields fe : sec.getListFields()) {

                if (id.equals(String.valueOf(fe.getId()))) {

                    if (fe.getiType().equals("4")) {
                        String[] split = fe.getiOptions().split("\r\n");

                        for (int i = 1; i <= split.length; i++) {

                            String rid = fe.getId() + String.valueOf(i);
                            Integer aid = Integer.parseInt(rid);


                            if (aid.equals(vid)) {

                            } else {

                                RadioButton rb = (RadioButton) findViewById(aid);
                                rb.setChecked(false);
                            }
                        }
                        break;
                    }

                }
            }
        }
    }


    /**
     * This method creates layout based on input from customForm method.
     */
    private void createLayout() {



        LinearLayout timeNdatelayut = new LinearLayout(this);
        timeNdatelayut.setBackgroundColor(getResources().getColor(R.color.nav_font));
        timeNdatelayut.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams timelp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,Constants.convertDp(EfficiencyTest.this, 95));
        timelp.setMargins(0,  Constants.convertDp(EfficiencyTest.this, 5),0,Constants.convertDp(EfficiencyTest.this, 10));

          EfficiencyName = new EditText(this);
        LinearLayout.LayoutParams enamelp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,Constants.convertDp(EfficiencyTest.this, 50));
        enamelp.setMargins(Constants.convertDp(EfficiencyTest.this, 25),  0,Constants.convertDp(EfficiencyTest.this, 25),0);

        EfficiencyName.setHint("Date");
        EfficiencyName.setPadding(Constants.convertDp(EfficiencyTest.this, 20),0,0,0);

        EfficiencyName.setLayoutParams(enamelp);
        timeNdatelayut.addView(EfficiencyName);

          EfficiencyTime = new EditText(this);
        EfficiencyTime.setPadding(Constants.convertDp(EfficiencyTest.this, 20),0,0,0);
        LinearLayout.LayoutParams etimelp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,Constants.convertDp(EfficiencyTest.this, 50));
        etimelp.setMargins(Constants.convertDp(EfficiencyTest.this, 25),  0,Constants.convertDp(EfficiencyTest.this, 25),0);
        EfficiencyTime.setLayoutParams(etimelp);
        EfficiencyTime.setHint("Overall Efficiency");
        try {
            EfficiencyTime.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }
        catch (Exception e)
        {
            Log.i("msg", "got exception here");
        }
        EfficiencyTime.setBackgroundColor(Color.TRANSPARENT);


      final   DatePickerDialog.OnDateSetListener dae  = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        EfficiencyName.setFocusable(false);

        EfficiencyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("msg", "got clicked ");
                new DatePickerDialog(EfficiencyTest.this, dae, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        timeNdatelayut.addView(EfficiencyTime);

        timeNdatelayut.setLayoutParams(timelp);


        lmLayout.addView(timeNdatelayut);

        for (CustomSections section : listSection) {



            LinearLayout sectionlay = new LinearLayout(this);
            sectionlay.setBackgroundColor(getResources().getColor(R.color.nav_font));
            sectionlay.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0,  5,0,10);
            sectionlay.setLayoutParams(lp);
            lmLayout.addView(sectionlay);


            String myhead = section.getHead();
            TextView sectionTitleField = new TextView(this);
            sectionTitleField.setText(myhead);
            sectionTitleField.setLines(1);
            sectionTitleField.setTextSize(18);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            lp2.setMargins(Constants.convertDp(EfficiencyTest.this, 15),  Constants.convertDp(EfficiencyTest.this, 15),Constants.convertDp(EfficiencyTest.this, 15),0);
            sectionTitleField.setLayoutParams(lp2);









            sectionTitleField.setTextColor(getResources().getColor(R.color.headercolor));
            sectionlay.addView(sectionTitleField);


            for (CustomFields fields : section.getListFields()) {

                if (fields.getiType().equals("1")) {
                    ids.add(fields.getId());
                    EditText lEditText = new EditText(this);
                    lEditText.setPadding(Constants.convertDp(EfficiencyTest.this, 20),0,0,0);
                    lEditText.setText(fields.getiDefault());
                    lEditText.setId(fields.getId());
                    LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,Constants.convertDp(EfficiencyTest.this, 40));
                    lp3.setMargins(Constants.convertDp(EfficiencyTest.this, 25),  Constants.convertDp(EfficiencyTest.this, 5),Constants.convertDp(EfficiencyTest.this, 25),10);
                    lEditText.setLayoutParams(lp3);
                    lEditText.setHint(fields.getLabel());

                    sectionlay.addView(lEditText);




                } else if (fields.getiType().equals("3")) {

                    //On\r\nOff\r\nLeaking\r\nNot Leaking

                    TextView tv = new TextView(this);
                    tv.setLines(1);

                    tv.setText(fields.getLabel());
                    LinearLayout.LayoutParams paramss = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramss.setMargins(Constants.convertDp(EfficiencyTest.this, 25), 0, 0, 0);
                    tv.setLayoutParams(paramss);
                    tv.setTextSize(18);
                    tv.setTextColor(ContextCompat.getColor(EfficiencyTest.this, R.color.colorAccent));
                    //tv.setTypeface(Typeface.DEFAULT_BOLD);

                    sectionlay.addView(tv);

                    ids.add(fields.getId());
                    String[] split = fields.getiOptions().split("\r\n");
                    int size = split.length;

                    int n = (size / 2) + ((size % 2 == 0) ? 0 : 1);

                    int count = 0;
                    for (int j = 0; j < n; j++) {
                        LinearLayout headLinear = createHeadLinearLayout(2, LinearLayout.HORIZONTAL);
                        for (int i = 0; i < 2; i++) {

                            if (!((count) == split.length)) {
                                final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                lparams.setMargins(Constants.convertDp(EfficiencyTest.this, 25),Constants.convertDp(EfficiencyTest.this, 5),0,0);
                                lparams.weight = 1;
                                CheckBox cb = new CheckBox(this);
                                cb.setLayoutParams(lparams);



                                String ide = String.valueOf(fields.getId()) + (count + 1);
                                cb.setId(Integer.parseInt(ide));
                                cb.setText(split[count]);
                                count++;
                                headLinear.addView(cb);
                            }
                        }
                        sectionlay.addView(headLinear);
                    }
                } else if (fields.getiType().equals("4")) {

                    //On\r\nOff\r\nLeaking\r\nNot Leaking

                    TextView tv = new TextView(this);
                    tv.setLines(1);
                    tv.setText(fields.getLabel());
                    tv.setBackgroundColor(getResources().getColor(R.color.nav_font));

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(Constants.convertDp(EfficiencyTest.this, 25), Constants.convertDp(EfficiencyTest.this, 20), 0, 0);
                    tv.setLayoutParams(params);
                    tv.setTextSize(18);
                    tv.setTextColor(ContextCompat.getColor(EfficiencyTest.this, R.color.colorAccent));
                   // tv.setTypeface(Typeface.DEFAULT_BOLD);
                    sectionlay.addView(tv);

                    String[] split = fields.getiOptions().split("\r\n");

                    LinearLayout headLinear = createHeadLinearLayout(split.length, LinearLayout.VERTICAL);
                    headLinear.setBackgroundColor(getResources().getColor(R.color.nav_font));

                    ids.add(fields.getId());

                    for (int i = 0; i < split.length; i++) {



                        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        if(i==0)
                        {
                            lparams.setMargins(Constants.convertDp(EfficiencyTest.this, 25),Constants.convertDp(EfficiencyTest.this, 5),0,0);

                        }
                        else {
                            lparams.setMargins(Constants.convertDp(EfficiencyTest.this, 25),Constants.convertDp(EfficiencyTest.this, 10),0,0);

                        }
                          lparams.weight = 1;
                        RadioButton rb = new RadioButton(this);
                        rb.setButtonDrawable(getResources().getDrawable(R.drawable.customgreenradio));
                        rb.setLayoutParams(lparams);

                        rb.setHighlightColor(getResources().getColor(R.color.colorGr));

                        // rb.setBackgroundColor(getResources().getColor(R.color.colorGr));

                        String ide = String.valueOf(fields.getId()) + (i + 1);

                        rb.setId(Integer.parseInt(ide));
                        try{
                            String defaultvalue = fields.getiDefault();

                            Log.i("msg", "got default try blick " + defaultvalue);
                            if(split[i].equals(defaultvalue))
                            {
                                Log.i("msg", "got idefault ");

                                rb.setChecked(true);
                            }

                        }catch (Exception e)
                        {

                        }
                        rb.setText(split[i]);
                        rb.setOnClickListener(this);
                        headLinear.addView(rb);
                    }

                    sectionlay.addView(headLinear);

                } else if (fields.getiType().equals("2")) {

                    TextView tv = new TextView(this);
                    tv.setLines(1);
                    tv.setText(fields.getLabel());

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(Constants.convertDp(EfficiencyTest.this, 25), 0, 0, 0);
                    tv.setLayoutParams(params);
                    tv.setTextSize(18);
                    tv.setTextColor(ContextCompat.getColor(EfficiencyTest.this, R.color.colorAccent));
                   // tv.setTypeface(Typeface.DEFAULT_BOLD);


                    sectionlay.addView(tv);

                    //Yes\r\nNo\r\nDefault
                    String[] split = fields.getiOptions().split("\r\n");
                    ArrayList<String> listItems = new ArrayList<>();

                    ids.add(fields.getId());

                    for (int i = 0; i < split.length; i++) {
                        listItems.add(split[i]);
                    }

                    Spinner spinner = new Spinner(this);
                    spinner.setId(fields.getId());
                    LinearLayout.LayoutParams sparmas = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Constants.convertDp(EfficiencyTest.this, 35));
                    sparmas.setMargins(Constants.convertDp(EfficiencyTest.this, 25), 0, 0, 0);
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listItems);
                    spinner.setAdapter(spinnerArrayAdapter);
                    spinner.setLayoutParams(sparmas);
                    sectionlay.addView(spinner);

                } else if (fields.getiType().equals("9")) {




                    TextView imageTitle = new TextView(this);
                    imageTitle.setLines(1);
                    imageTitle.setText(fields.getLabel());
                    imageTitle.setTextSize(18);
                    LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp4.setMargins(Constants.convertDp(EfficiencyTest.this, 25),  Constants.convertDp(EfficiencyTest.this, 25),0,0);
                    imageTitle.setLayoutParams(lp4);
                    imageTitle.setTextColor(ContextCompat.getColor(EfficiencyTest.this, R.color.colorAccent));
                    sectionlay.addView(imageTitle);
                    //imageTitle.setTypeface(Typeface.DEFAULT_BOLD);



                    ids.add(fields.getId());

                   final Button btn = new Button(this);
                    btn.setId(fields.getId());
                    btn.setText("Choose File");
                    btn.setLines(1);
                    btn.setAllCaps(false);
                    btn.setTextSize(15);
                    btn.setTextColor(getResources().getColor(R.color.nav_font));
                    btn.setPadding( Constants.convertDp(EfficiencyTest.this, 10),0, Constants.convertDp(EfficiencyTest.this, 10),0);


                    btn.setBackgroundColor(getResources().getColor(R.color.colorBl));

                    LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Constants.convertDp(EfficiencyTest.this, 30) );


                    ll.setMargins(Constants.convertDp(this,35),Constants.convertDp(this,5),Constants.convertDp(this,35),Constants.convertDp(EfficiencyTest.this, 10));
                    ll.gravity = Gravity.CENTER_HORIZONTAL;

                    btn.setLayoutParams(ll);



                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedImage = btn;


                            AlertDialog.Builder build = new AlertDialog.Builder(EfficiencyTest.this);
                            build.setTitle("Alert!");
                            build.setCancelable(false);
                            build.setMessage("Please choose below.");
                            build.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= 23) {

                                        if (isReadStorageAllowed()) {
                                            //If permission is already having then showing the toast

                                            openGallery();
                                        } else {

                                            //If the app has not the permission then asking for the permission
                                            requestStoragePermission();
                                        }

                                    } else {
                                        openGallery();
                                    }

                                }
                            });
                            build.setNegativeButton("Take a photo", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    openCamera();

                                }
                            });

                            AlertDialog dia = build.create();
                            dia.show();





                        }
                    });

                    sectionlay.addView(btn);
                }
            }

            LinearLayout botline = new LinearLayout(this);
            LinearLayout.LayoutParams lpe = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Constants.convertDp(EfficiencyTest.this, 2));
            botline.setBackgroundColor(getResources().getColor(R.color.colorGy));
            botline.setLayoutParams(lpe);
            sectionlay.addView(botline);
        }


        Button btn = new Button(this);
        btn.setId(btnId);
        btn.setText("Save and Close");
        btn.setAllCaps(false);
        btn.setPadding(0,0,0,0);
        btn.setTextSize(18);
        btn.setTextColor(getResources().getColor(R.color.nav_font));


        btn.setBackgroundColor(getResources().getColor(R.color.colorGr));
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Constants.convertDp(EfficiencyTest.this, 35));
        ll.setMargins(Constants.convertDp(EfficiencyTest.this, 25),Constants.convertDp(EfficiencyTest.this, 50),Constants.convertDp(EfficiencyTest.this, 25),0);
        ll.gravity = Gravity.CENTER_HORIZONTAL;

        btn.setLayoutParams(ll);
        btn.setBackground(getResources().getDrawable(R.drawable.button));
        lmLayout.addView(btn);

        Button cbtn = new Button(this);
        cbtn.setId(btnId);
        cbtn.setText("Cancel");
        cbtn.setTextSize(18);

        cbtn.setTextColor(getResources().getColor(R.color.nav_font));
        cbtn.setPadding(0,0,0,0);
        cbtn.setBackground(getResources().getDrawable(R.drawable.scan));
        cbtn.setAllCaps(false);
        LinearLayout.LayoutParams lll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Constants.convertDp(EfficiencyTest.this, 35));
        lll.setMargins(Constants.convertDp(EfficiencyTest.this, 25),Constants.convertDp(EfficiencyTest.this, 30),Constants.convertDp(EfficiencyTest.this, 25),Constants.convertDp(EfficiencyTest.this, 50));
        lll.gravity = Gravity.CENTER_HORIZONTAL;


        cbtn.setLayoutParams(lll);
        lmLayout.addView(cbtn);






        cbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.xml.benter, R.xml.bexit);
            }
        });



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    InputMethodManager inputManagers = (InputMethodManager)
                            getSystemService(MainActivity.INPUT_METHOD_SERVICE);

                    inputManagers.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
                catch (Exception e)
                {
                    Log.i("msg", "got error " +e);
                }


                isnetworkavailable =  new NetworkChangeReceiver().isConnectedToInternet(EfficiencyTest.this);





                if (isnetworkavailable)
                {

                    checkTextBoxData();


                }
                else
                {

                    if(!GlobalClass.isnonetworknotifier)
                    {
                        AlertDialog.Builder build = new AlertDialog.Builder(EfficiencyTest.this);
                        build.setTitle("Alert!");
                        build.setCancelable(false);
                        build.setMessage("No network connection would you like to use app in offline mode.");

                        build.setNegativeButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkTextBoxData();
                            }
                        });
                        build.setPositiveButton("Cancel", null );

                        AlertDialog dia = build.create();
                        dia.show();
                        GlobalClass.isnonetworknotifier =true;

                    }
                    else
                    {

                        checkTextBoxData();

                    }


                }




                //checkTextBoxData();
//                if (flag )
//                    Log.i("msg", "test");
//                //69666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666
//                  //  new Inspection.UploadImage().execute();
//                else {
//                    createData();
//
//                    String json = null;
//                    try {
//                        json = createJSON().toString();
//                        Log.i("msg", json);
//
//                        // new UploadData().execute(json);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }

            }
        });

    }

    /**
     * This method creates inspection json, which is to be send to server.
     */
    private void createData() {

        for (Integer resourceId : ids) {

            for (CustomSections section : listSection) {

                for (CustomFields fields : section.getListFields()) {

                    if (resourceId.equals(fields.getId())) {



                        if (fields.getiType().equals("1")) {
                            EditText et1 = (EditText) findViewById(resourceId);
                            hasmap.put(resourceId, et1.getText().toString());



                        } else if (fields.getiType().equals("3")) {
                            String[] split = fields.getiOptions().split("\r\n");
                            String s = "";
                            for (int i = 0; i < split.length; i++) {
                                String ide = String.valueOf(fields.getId()) + (i + 1);
                                Integer cbIde = Integer.parseInt(ide);

                                CheckBox cb = (CheckBox) findViewById(cbIde);

                                if (cb.isChecked() && s.isEmpty()) {
                                    s = cb.getText().toString() + "";
                                } else if (cb.isChecked()) {
                                    s += "," + cb.getText().toString();
                                }
                            }
                            hasmap.put(resourceId, s);



                        } else if (fields.getiType().equals("4")) {

                            String[] split = fields.getiOptions().split("\r\n");
                            String s = "";
                            for (int i = 0; i < split.length; i++) {
                                String ide = String.valueOf(fields.getId()) + (i + 1);
                                Integer rbIde = Integer.parseInt(ide);

                                RadioButton rb = (RadioButton) findViewById(rbIde);

                                if (rb.isChecked()) {
                                    hasmap.put(resourceId, rb.getText().toString());

                                    break;
                                }

                            }

                        } else if (fields.getiType().equals("2")) {

                            Spinner spinner = (Spinner) findViewById(resourceId);
                            String selected = (String) spinner.getSelectedItem();

                            hasmap.put(resourceId, selected);
                        } else if (fields.getiType().equals("9")) {
                            if(filePath!=null)
                                hasmap.put(resourceId, filePath);
                            else
                                hasmap.put(resourceId,"");
                        }

                        break;
                    }

                }
            }
        }

    }


    //This method checks the read external storage permission status
    private boolean isReadStorageAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }





    public void openCamera(){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
        Date now = new Date();
        String fileName = "Inspection_" + formatter.format(now) ;
        tempFile = fileName;

        Log.d("msg","path"+fileName);

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {


            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            try {

                File image = File.createTempFile(fileName, ".png", storageDir);


                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


                Uri photoUri = Uri.fromFile(image);

                cameraFile = photoUri;
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, 2005);








//
//                                            File image = new File(storageDir, fileName);
//                                               image.mkdir();
//
//                                            String er  = image.getAbsolutePath();
//                                            Log.d("msg", "s" + er);
//                                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                            Uri photoUri = FileProvider.getUriForFile(Inspection.this,"test",image);
//                                            cameraFile = photoUri;
//                                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//                                            startActivityForResult(cameraIntent, 2005);
            }
            catch (Exception e)
            {
                Log.i("msge","got error" + e);


            }

        }
        else
        {

            ActivityCompat.requestPermissions(EfficiencyTest.this,
                    new String[]{Manifest.permission.CAMERA},
                    2005);


        }




    }
    //This method requests read external storage permission
    private void requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 23);
    }


    //This method will be called when the user will tap on allow or deny of permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == 23) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // open gallery
                openGallery();
            } else {
                //Displaying toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Method to open gallery
    private void openGallery() {
        try {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_GALLERY_IMAGE);
        } catch (Exception e) {

        }
    }

    /**
     * This method checks Image selected from Gallery
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == RESULT_LOAD_GALLERY_IMAGE && resultCode == RESULT_OK && null != data) {
                flag = true;
                thumbnail = null;
                try {
                    thumbnail = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            }

        } catch (Exception e) {

        }
    }


    // public void setParams(int width, int height)





    void getofflineVendiorData()
    {

        ale.hide();


        try {


            String offlinestr = SharedPrefManager.getInstance(getApplicationContext()).getData("offlinedata");
            JSONArray result = new JSONArray(offlinestr);


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



                                JSONArray equipdata = jad.getJSONArray("equipments");

                                for (int l=0;l<equipdata.length(); l++)
                                {

                                    String equipidr = equipdata.getJSONObject(l).getString("id");
                                    Log.i("msg", "equipment id " + equipidr);

                                    Log.i("msg", " insid id " + equipMentId);
                                    if(equipMentId.equals(equipidr))

                                    {

                                           JSONObject equipjson = equipdata.getJSONObject(l);


                                        Log.i("msg", "matched id");


                                         JSONObject  jobj = new JSONObject();



                                        JSONArray insjson = equipjson.getJSONArray("esections");

                                        jobj.put("sections", insjson);
                                        Log.i("msg","got esection are " + insjson);
                                        listSection = CustomParseJSON.parseSections(jobj.toString());
                                        createLayout();





                                        j=jam.length();
                                        i = result.length();
                                        break;
                                    }




                                }










                        }
                    }
                }



            }




        }catch (Exception e)
        {

            Log.i("msg", "got json exception "   +e);


        }









    }








    //----------------Default Oncreate Methods --------------------------------

    public void settingReferences()
    {



        ale = new SpotsDialog(this,"Loading",R.style.Customdmax);
        ale.show();
        lmLayout = (LinearLayout) findViewById(R.id.linearLayout);
        lmLayout.setOrientation(LinearLayout.VERTICAL);
        Bundle b = this.getIntent().getExtras();
        insID = b.getString("idIns");
        equipMentId = b.getString("equiID");
        backBtn = (ImageView)findViewById(R.id.btnback);

        Boolean isnet =  new NetworkChangeReceiver().isConnectedToInternet(EfficiencyTest.this);
        if (isnet)
        {

            customForm();


        }
        else
        {

            if(!GlobalClass.isnonetworknotifier)
            {
                AlertDialog.Builder build = new AlertDialog.Builder(EfficiencyTest.this);
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




}





