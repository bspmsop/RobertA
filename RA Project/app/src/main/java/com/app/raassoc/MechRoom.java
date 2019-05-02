package com.app.raassoc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.app.raassoc.helper.NetworkChangeReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

/**
 * Created by New android on 15-11-2018.
 */

public class MechRoom extends AppCompatActivity  {


    private int id;
    private TextView  tvTitle,tvMechH;

    //An ArrayList for Spinner Items
    private ArrayList<String> mechRooms;
    private   String name,mechIDB;
    private JSONArray result;
    private ImageView backBtn;
    AlertDialog ale;
    private JSONArray mediatorresult;

    Button dropdownBtn;
    ListView dropdownlist;
    LinearLayout dropview;
    FrameLayout parentview;
    Integer selectedbuilding = -1;
    String buildid = "0";
    String selectedbuildtitle = "";
    EditText searchbox;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mech);
        dropdownBtn = findViewById(R.id.popbtn);
        GlobalClass.registerNotifier(this);
        backBtn = (ImageView) findViewById(R.id.btnback);
        backBtn.setOnClickListener(new View.OnClickListener() {
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


        Bundle datas = getIntent().getExtras();
        name =datas.getString("id");
        String mechTitle = datas.getString("title");
        Bundle dataB = getIntent().getExtras();
        mechIDB  = dataB.getString("mechIDB");
        mechRooms = new ArrayList<String>();



        // spinnerMech.setOnItemSelectedListener(this);
        ale = new SpotsDialog(this,"Loading",R.style.Customdmax);

        ale.setCancelable(false);




        //Initializing TextView

        tvTitle = (TextView) findViewById (R.id.tvTitle);
        tvMechH=(TextView) findViewById(R.id.tvMechH);
        tvMechH.setText(mechTitle);
        dropview = new LinearLayout(this);


        LinearLayout.LayoutParams dvl = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        // dvl.setMargins(Constants.convertDp(this,10),Constants.convertDp(this,145),Constants.convertDp(this,10),0);
        dropview.setLayoutParams(dvl);
        // dropview.setBackgroundColor(getResources().getColor(R.color.nav_font));
        parentview = findViewById(R.id.framer);
        parentview.addView(dropview);
        dropview.setVisibility(View.GONE);
        dropview.setClickable(true);

        View child = getLayoutInflater().inflate(R.layout.dropdownsearchlayout, null);
        dropview.addView(child);


        LinearLayout.LayoutParams d2l = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        // dvl.setMargins(Constants.convertDp(this,10),Constants.convertDp(this,145),Constants.convertDp(this,10),0);
        child.setLayoutParams(d2l);

        dropdownlist = child.findViewById(R.id.listdrop);

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


        dropdownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hidekeyboardonclick(null);


                if(dropview.getVisibility() == View.GONE)
                {

                    dropview.setVisibility(View.VISIBLE);
                }
                else{
                    dropview.setVisibility(View.GONE);


                }





            }
        });



        Boolean isnet =  new NetworkChangeReceiver().isConnectedToInternet(MechRoom.this);
        if (isnet)
        {

            getList();


        }
        else
        {

            if(!GlobalClass.isnonetworknotifier)
            {
                AlertDialog.Builder build = new AlertDialog.Builder(MechRoom.this);
                build.setTitle("Alert!");
                build.setCancelable(false);
                build.setMessage("No network connection would you like to use app in offline mode.");
                build.setNegativeButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getOfflineMechanicals();
                    }
                });
                build.setPositiveButton("Cancel", null );

                AlertDialog dia = build.create();
                dia.show();
                GlobalClass.isnonetworknotifier =true;

            }
            else
            {
                getOfflineMechanicals();

            }


        }


        dropdownlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    hidekeyboardonclick(dropview);
                    hidekeyboardonclick(null);
                    dropdownBtn.setText(mechRooms.get(position));
                    selectedbuilding = position;
                    JSONObject json = mediatorresult.getJSONObject(selectedbuilding);
                    buildid = json.getString("id");
                    selectedbuildtitle = json.getString("title");
                    dropview.setVisibility(View.GONE);
                    Log.i("msg","clcikd" + position);
                    Log.i("msg","clcikd id " + buildid);
                    Log.i("msg","clcikd name " + selectedbuildtitle);

                }
                catch (Exception e)
                {
                    Log.i("msg", "got exception here is " + e);

                }

            }
        });


        Button btnBuild = (Button) findViewById(R.id.btnMechsign);
        btnBuild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(selectedbuilding < 0)
                {


                    Toast.makeText(MechRoom.this, "Please select a Mechanical Room", Toast.LENGTH_SHORT).show();


                }
                else
                {
                    Intent intent = new Intent(MechRoom.this, MechRoom.class);
                    try {

                        AlertDialog.Builder build = new AlertDialog.Builder(MechRoom.this);
                        build.setTitle("Success!");
                        build.setCancelable(false);
                        build.setMessage("User Signin Mechanical Room");
                        build.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent(MechRoom.this, BoilerRoom.class);


                                Bundle datas = new Bundle();
                                datas.putString("id", buildid );
                                datas.putString("title", selectedbuildtitle);



                                intent.putExtras(datas);



                                startActivity(intent);
                                overridePendingTransition(0,0);
                            }
                        });
                        AlertDialog dia = build.create();
                        dia.show();




                    }
                    catch (Exception e)
                    {
                        Log.i("msg", " goty json exception");
                    }

                }



            }

        });




        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length() > 0 && result.length() > 0)
                {
                    try {


                        mediatorresult = new JSONArray();
                        for (int i = 0; i < result.length(); i++) {
                            String title = result.getJSONObject(i).getString("title");
                            if(title.contains(s))
                            {
                                mediatorresult.put(result.getJSONObject(i));
                            }

                        }
                        getBuildings(mediatorresult);


                    }
                    catch(Exception e)
                    {
                        Log.i("msg", "got json exception");
                    }


                }
                else
                {
                    mediatorresult = result;
                    getBuildings(mediatorresult);
                    Log.i("msg", "got hre no text ");
                }

                Log.i("msg", "text get change d " + s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        searchbox.addTextChangedListener(textWatcher);






        //This method will fetch the data from the URL

    }



    public void getOfflineMechanicals()
    {

        try {


            String offlinestr = SharedPrefManager.getInstance(getApplicationContext()).getData("offlinedata");
            JSONArray bresult = new JSONArray(offlinestr);


            for(int i=0; i<bresult.length(); i++)
            {

                JSONObject jb = bresult.getJSONObject(i);

                String buildid = jb.getString("id");

                if(name.equals(buildid))
                {
                    mechRooms = new ArrayList<String>();

                    result = jb.getJSONArray("mechanicals");
                    mediatorresult =result;
                    mechRooms = new ArrayList<String>();

                    for(int j= 0;j<result.length();j++)
                    {
                        mechRooms.add(result.getJSONObject(j).getString("title"));

                    }



                    break;

                }



            }




            if(mechRooms.size()<1)
            {
                LinearLayout.LayoutParams livtht = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                dropdownlist.setLayoutParams(livtht);


            }
            else {
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdownlist, mechRooms);


                dropdownlist.setAdapter(arrayAdapter);

            }





        }catch (Exception e)
        {

            Log.i("msg", "got json exception "   +e);


        }


    }







    private void getList() {


        ale.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Constants.MECH_URL+name+"/"+SharedPrefManager.getInstance(this).getUsertype(),

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        GlobalClass.isnonetworknotifier =false;
                        JSONObject j = null;
                        try {
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);

                            //Storing the Array of JSON String to our JSON Array

                            // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                            result = j.getJSONArray(Constants.MECH_ARRAY);
                            mediatorresult = result;
                            //Calling method getStudents to get the buildings from the JSON Array
                            getBuildings(result);
                            ale.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        AlertDialog.Builder build = new AlertDialog.Builder(MechRoom.this);
                        build.setTitle("Alert!");
                        build.setCancelable(false);
                        build.setMessage("Please check your network connection and try again.");
                        build.setPositiveButton("ok",null);
                        build.setNegativeButton("Try Again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getList();
                            }
                        });
                        AlertDialog dia = build.create();
                        dia.show();
                        ale.dismiss();
                    }
                });

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void getBuildings(JSONArray j) {

        mechRooms = new ArrayList<String>();
        for (int i = 0; i < j.length(); i++) {
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the buildings to array list
                mechRooms.add(json.getString(Constants.MECH_TITL));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(j.length()<1)
        {
            LinearLayout.LayoutParams livtht = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
            dropdownlist.setLayoutParams(livtht);


        }
        else {
            LinearLayout.LayoutParams livtht = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            dropdownlist.setLayoutParams(livtht);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdownlist, mechRooms);


            dropdownlist.setAdapter(arrayAdapter);
            Log.i("msg", "go tlenth " + j.length());
        }




        ale.dismiss();
    }

    //Method to get student name of a particular position
    private String getId(int position) {
        String name1 = "";
        try {
            //Getting object of given index
            JSONObject json = result.getJSONObject(position);

            //Fetching name from that object
            name1 = json.getString(Constants.TAG_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return name1;
    }
    //Method to get student name of a particular position
    private String getTitle(int position) {
        String title = "";
        try {
            //Getting object of given index
            JSONObject json = result.getJSONObject(position);

            //Fetching name from that object
            title = json.getString(Constants.TAG_USERNAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return title;
    }

    //this method will execute when we pic an item from the spinner
//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        //Setting the values to textviews for a selected item
//        Button btnMechSign = (Button) findViewById(R.id.btnMechsign);
//        btnMechSign.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
////                String mechId;
////                mechId = tvID.getText().toString();
////                if (mechId != "-1") {
////                    if (spinnerMech.getSelectedItem().toString().trim().equals("Select Mechanical Room")) {
////                        Toast.makeText(MechRoom.this, "Please select a mechanical room", Toast.LENGTH_SHORT).show();
////                    }
////                    else {
////
////
////
////                        AlertDialog.Builder build = new AlertDialog.Builder(MechRoom.this);
////                        build.setTitle("Success!");
////                        build.setCancelable(false);
////                        build.setMessage("User Signin Mechanical Room");
////                        build.setPositiveButton("ok", new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialog, int which) {
////
////                                Intent intent = new Intent(MechRoom.this, BoilerRoom.class);
////
////                                Bundle datas = new Bundle();
////                                datas.putString("id", tvID.getText().toString());
////                                datas.putString("title", tvTitle.getText().toString());
////
////                               Log.i("msg", "selected mech is " + tvID.getText().toString());
////
////                                intent.putExtras(datas);
////
////
////
////                                startActivity(intent);
////                                overridePendingTransition(0,0);
////                            }
////                        });
////                        AlertDialog dia = build.create();
////                        dia.show();
////
////
////
////
////
////                    }
////                }
//            }
//        });
//
//
//        tvTitle.setText(getTitle(position-1));
//        Log.i("msg", "selected id is " + getId(position-1));
//
//
//
//
//    }

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