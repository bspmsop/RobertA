package com.app.raassoc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class defaultInspectionSheet extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_inspection_sheet);
        ibtn = (ImageView)findViewById(R.id.btnback);
        cacelBtn = (Button)findViewById(R.id.cancelBtn);
        cacelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.xml.benter, R.xml.bexit);
            }
        });
        ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.xml.benter, R.xml.bexit);
            }
        });

        outdoortemp = (EditText)findViewById(R.id.outdoorTemp);
        BoilerSupplytemp = (EditText)findViewById(R.id.supplytemp);
        targetTEmp = (EditText)findViewById(R.id.targetTemp);
        pump12 = (EditText)findViewById(R.id.pump12);
        pumpoff = (EditText)findViewById(R.id.editPumpoff);
        watergazes = (EditText)findViewById(R.id.watergazer);
        supplytemp = (EditText)findViewById(R.id.supplytempeditbox);
        returnTemp = (EditText)findViewById(R.id.returntempbox);
        dwhTemp = (EditText)findViewById(R.id.editTextDHW);
        checkfiletr = (EditText)findViewById(R.id.checkfilter);
        checkWaterlevel = (EditText)findViewById(R.id.checkwaterlevel);
        checkleaks = (EditText)findViewById(R.id.editTextLeaks);
        annualefficiency = (EditText)findViewById(R.id.editTextEfficiency);

        arrayboxes = new EditText[]{outdoortemp, BoilerSupplytemp, targetTEmp, pump12, pumpoff,watergazes,supplytemp,returnTemp,dwhTemp,checkfiletr,checkWaterlevel,checkleaks,annualefficiency};
        RadioButton[] arrayradio = new RadioButton[]{flashLightYes, flashligtsno, clearlightflashno, clearlightflashyes,pressedclearyes,pressedclearno,pumpoperationno,pumpoperationgyes,dhwfailurelightsyes,dhwfailutrelightno,dhwclearedyes,dhwcleardno,dhwpressedno, dhwpressedyes,boiler1,boiler2,boiler3,boiler4};
        saveBtn = (Button)findViewById(R.id.btnSave);
        cancelBtn = (Button)findViewById(R.id.cancelBtn);
        Bundle b = this.getIntent().getExtras();
        mid = b.getString("mechDID");
        Log.i("msg", "mechanical id in default is " +mid);
        ale = new SpotsDialog(this,"Loading",R.style.Customdmax);
        ale.setCancelable(false);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkData();

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.xml.benter, R.xml.bexit);


            }
        });





    }


    Button saveBtn, cancelBtn;
    ImageView ibtn;
    Button cacelBtn;
    EditText outdoortemp, BoilerSupplytemp, targetTEmp, pump12, pumpoff,watergazes,supplytemp,returnTemp,dwhTemp,checkfiletr,checkWaterlevel,checkleaks,annualefficiency;
    RadioButton  flashLightYes, flashligtsno, clearlightflashno, clearlightflashyes,pressedclearyes,pressedclearno,pumpoperationno,pumpoperationgyes,dhwfailurelightsyes,dhwfailutrelightno,dhwclearedyes,dhwcleardno,dhwpressedno, dhwpressedyes,boiler1,boiler2,boiler3,boiler4;
    AlertDialog ale;
    EditText[] arrayboxes;
    String[] arraystrings = {"outdoor_temperature", "boiler_supply_temperature","target_temperature", "pump_operating", "pump_off", "water_gauges","supply_temp", "return_temp", "dhw","check_filters", "check_water_level",  "check_for_leaks","annual_efficeiency_test"};
    String mid;
    JSONObject jb;

    @Override
    public void onClick(View v) {


    }




    public void checkData()
    {
        ale.show();

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




          jb = new JSONObject();
        Log.i("msg", "strings lenth  is " +arraystrings.length);
        Log.i("msg", "boxes lenth  is " +arrayboxes.length);

        for (int i=0; i<arrayboxes.length; i++)
        {

            EditText et = arrayboxes[i];
            if(et.getText().toString().length() < 1)
            {
                ale.dismiss();
                AlertDialog.Builder build = new AlertDialog.Builder(this);
                build.setTitle("Alert!");
                build.setCancelable(false);
                build.setMessage("Please fill required fields");
                build.setPositiveButton("Ok", null);
                AlertDialog dia = build.create();
                dia.show();
                break;
            }
            try {





                jb.put(arraystrings[i], et.getText().toString());

                if (i == arrayboxes.length - 1)
                {

                    SimpleDateFormat formatters = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.US);
                    Date now = new Date();
                    String timestring =  formatters.format(now);
                    Log.i("msg", "time is " + timestring);
                    String uid = SharedPrefManager.getInstance(this).getUserId();
                    String utype = SharedPrefManager.getInstance(this).getUsertype();




                    jb.put("mech_id", mid);
                    jb.put("inspection_date", timestring);
                    jb.put("condensation", "");
                    jb.put("user_type", utype);
                    jb.put("user_id", uid);

                    jb.put("user_id", uid);





                    jb.put("dhw_pump_operating", dpca);

                    jb.put("dhw_failure_lights", dfl);
                    jb.put("dhw_cleared_flights", ds);

                    jb.put("failure_lights", ff );
                    jb.put("dhw_press_calarm", dpca);
                    jb.put("press_calarm", pressed);

                    jb.put("cleared_flights", cfl);


                    jb.put("boiler1", b1);
                    jb.put("boiler3", b3);
                    jb.put("boiler2", b2);
                    jb.put("boiler4", b4);

                    new defaultInspectionSheet.UploadData().execute();


                }




            }
            catch (Exception e)
            {
                ale.dismiss();
                Log.i("msg", "unable to add msg");
            }


        }

        Log.i("msg","got message "+jb);

    }





Boolean ff = false, clf = false, pressed = false, ds = false, dfl = false,b4= false, b3=false,b2=false,b1=false,dpca=false,cfl=false;




    public void  onRadioButtonClicked(View v)
    {
switch (v.getId())
{
    case R.id.ffss:
        Log.i("msg", "fls tapped");
        ff = true;
         break;
    case R.id.ffno:
        ff = false;
        Log.i("msg", "fls tapped");
        break;
    case R.id.clfyes:
        clf = true;
        Log.i("msg", "fls tapped");
        break;
    case R.id.clfno:
        clf = false;
        Log.i("msg", "fls tapped");
        break;
    case R.id.pressedyes:
        pressed = true;
        Log.i("msg", "fls tapped");
        break;
    case R.id.pressedno:
        pressed = false;
        Log.i("msg", "fls tapped");
        break;
    case R.id.dsyes:
        ds = true;
        Log.i("msg", "fls tapped");
        break;
    case R.id.dsno:
        ds = false;
        Log.i("msg", "fls tapped");
        break;
        case R.id.dflfyes:
            dfl = true;
    Log.i("msg", "fls tapped");
            break;
    case R.id.dflfno:
        dfl = false;
    Log.i("msg", "fls tapped");
        break;
    case R.id.cflyes:
        cfl = true;
        Log.i("msg", "fls tapped");
        break;
    case R.id.cflno:
        cfl = false;
        Log.i("msg", "fls tapped");
        break;

    case R.id.dpcayes:
        dpca = true;
        Log.i("msg", "fls tapped");
        break;

    case R.id.dpcano:
        dpca = false;
        Log.i("msg", "fls tapped");
        break;

    case R.id.b1yes:
        b1=true;
        Log.i("msg", "fls tapped");
        break;

    case R.id.b1no:
        b1=false;
        Log.i("msg", "fls tapped");
        break;

    case R.id.b2yes:
        b2=true;
        Log.i("msg", "fls tapped");
        break;
    case R.id.b2no:
        b2=false;
        Log.i("msg", "fls tapped");
        break;
    case R.id.b3yes:
        b3=true;
        Log.i("msg", "fls tapped");
        break;
    case R.id.b3no:
        b3=false;
        Log.i("msg", "fls tapped");
        break;
    case R.id.b4yes:
        b4=true;
        Log.i("msg", "fls tapped");
        break;
    case R.id.b4no:
        b4=false;
        Log.i("msg", "fls tapped");
        break;


default:
    Log.i("msg", "dont do any thing");


}



    }




    private String sendData(final String param) {

        String res;
        String url = Constants.INS_SAVE;

        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.readTimeout(60, TimeUnit.SECONDS);

            OkHttpClient client = builder.build();

            FormBody.Builder fb = new FormBody.Builder();

            fb.add("insepectionData", param);

            RequestBody req = fb.build();



            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
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

    private class UploadData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
              String jstr = jb.toString();
            return   sendData(jstr);

        }

        @Override
        protected void onPostExecute(String response) {
            ale.dismiss();

            Log.i("msg", "got respone here "+ response);

            try {
                String ouput=CustomParseJSON.parseUploadDataResponse(response);

                AlertDialog.Builder build = new AlertDialog.Builder(defaultInspectionSheet.this);
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
                Log.i("msg", "got exception here " +e);

                AlertDialog.Builder build = new AlertDialog.Builder(defaultInspectionSheet.this);
                build.setTitle("Alert!");
                build.setMessage("Internal error occurred please try again.");
                build.setPositiveButton("Ok",  null);
                AlertDialog dia = build.create();
                dia.show();

                e.printStackTrace();
            }


        }
    }




}
