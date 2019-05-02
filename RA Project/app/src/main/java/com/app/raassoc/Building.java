package com.app.raassoc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.app.raassoc.superactivities.SupersyncActvity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

/**
 * Created by New android on 14-11-2018.
 */

public class Building extends AppCompatActivity  implements  NavigationView.OnNavigationItemSelectedListener{
    //Declaring an Spinner
    // private Spinner spinner;


    private ArrayList<String> buildings;
    private ArrayList<String> buildingID;
    private int bid=0;
    AlertDialog ale;
    private JSONArray result;
    private JSONArray mediatorresult;

    Button dropdownBtn;
    ListView dropdownlist;
    LinearLayout dropview;
    FrameLayout parentview;
    Integer selectedbuilding = -1;
    String buildid = "0";
    String selectedbuildtitle = "";
    EditText searchbox;

    //TextViews to display details
    private TextView textViewName,tvBuild;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);
        dropdownBtn = findViewById(R.id.popbtn);
        GlobalClass.registerNotifier(this);
        Toolbar tbar = findViewById(R.id.toolbarnav);
        setSupportActionBar(tbar);
        drawer  = findViewById(R.id.drawer_layouts);
        ActionBarDrawerToggle tog = new ActionBarDrawerToggle(this,drawer,tbar,R.string.action_settings,R.string.action_settings);
        drawer.addDrawerListener(tog);
        tog.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_views);
        navigationView.setNavigationItemSelectedListener(this);
        View header =  navigationView.getHeaderView(0);

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
        TextView usertitle = (TextView)header.findViewById(R.id.usernametxt);
        TextView userEmail = (TextView) header.findViewById(R.id.useremailtxt);
        usertitle.setText(SharedPrefManager.getInstance(this).getUsername());
        userEmail.setText(SharedPrefManager.getInstance(this).getUseremail());
        ale = new SpotsDialog(this,"Loading",R.style.Customdmax);
        ale.setCancelable(false);

        ale.show();

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        //Initializing the ArrayList
        buildings = new ArrayList<String>();
        buildingID = new ArrayList<String>();


        //  spinner = (Spinner) findViewById(R.id.buildingS);

        //Adding an Item Selected Listener to our Spinner
        //As we have implemented the class Spinner.OnItemSelectedListener to this class iteself we are passing this to setOnItemSelectedListener
        // spinner.setOnItemSelectedListener(this);

        //Initializing TextViews
        textViewName = (TextView) findViewById(R.id.textViewName);
        tvBuild = (TextView) findViewById(R.id.tvBuild);

        //This method will fetch the data from the URL
        Boolean isnet =  new NetworkChangeReceiver().isConnectedToInternet(Building.this);
        if (isnet)
        {

            getData();


        }
        else
        {

            if(!GlobalClass.isnonetworknotifier)
            {
                AlertDialog.Builder build = new AlertDialog.Builder(Building.this);
                build.setTitle("Alert!");
                build.setCancelable(false);
                build.setMessage("No network connection would you like to use app in offline mode.");
                build.setNegativeButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getofflineBuildings();
                    }
                });
                build.setPositiveButton("Cancel", null );

                AlertDialog dia = build.create();
                dia.show();
                GlobalClass.isnonetworknotifier =true;

            }
            else
            {
                getofflineBuildings();

            }

            ale.dismiss();
        }




        dropdownlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    hidekeyboardonclick(dropview);
                    hidekeyboardonclick(null);
                    dropdownBtn.setText(buildings.get(position));
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


        Button btnBuild = (Button) findViewById(R.id.btnBuild);
        btnBuild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(selectedbuilding < 0)
                {


                    Toast.makeText(Building.this, "Please select a building", Toast.LENGTH_SHORT).show();


                }
                else
                {
                    Intent intent = new Intent(Building.this, MechRoom.class);
                    try {




                        Bundle datas = new Bundle();
                        datas.putString("id", buildid);
                        datas.putString("title", selectedbuildtitle);

                        intent.putExtras(datas);
                        startActivity(intent);
                        overridePendingTransition(R.xml.enter, R.xml.exit);
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
    }


    public void getofflineBuildings()
    {

        try {


            String offlinestr = SharedPrefManager.getInstance(getApplicationContext()).getData("offlinedata");
            result = new JSONArray(offlinestr);
            mediatorresult =result;
            buildings = new ArrayList<String>();

            for(int i=0; i<result.length(); i++)
            {

                JSONObject jb = result.getJSONObject(i);
                if(jb.has("title"))
                {

                    buildings.add(jb.getString("title"));


                }



            }


            if(buildings.size()<1)
            {
                LinearLayout.LayoutParams livtht = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                dropdownlist.setLayoutParams(livtht);


            }
            else {
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdownlist, buildings);


                dropdownlist.setAdapter(arrayAdapter);

            }









        }catch (Exception e)
        {

            Log.i("msg", "got json exception "   +e);


        }


    }




    private void getData() {


        StringRequest stringRequest = new StringRequest( Request.Method.GET,
                Constants.DATA_URL + SharedPrefManager.getInstance(this).getUserId()+"/"+SharedPrefManager.getInstance(this).getUsertype(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        GlobalClass.isnonetworknotifier =false;
                        JSONObject j = null;
                        try {
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);

                            //Storing the Array of JSON String to our JSON Array
                            result = j.getJSONArray(Constants.JSON_ARRAY);

                            mediatorresult = result;
                            Log.i("msg", "got building response as " + response);
                            //Calling method getBuildings to get the students from the JSON Array
                            getBuildings(result);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            ale.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("msg","test msg " + error);
                        AlertDialog.Builder build = new AlertDialog.Builder(Building.this);
                        build.setMessage("Please check your network connection.");
                        build.setCancelable(false);
                        build.setTitle("Network Alert!");
                        build.setPositiveButton("Ok", null);
                        build.setNegativeButton("Try again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ale.show();
                                getData();
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
        //Traversing through all the items in the json array
        buildings = new ArrayList<String>();
        for (int i = 0; i < j.length(); i++) {
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                buildings.add(json.getString(Constants.TAG_USERNAME));
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
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdownlist, buildings);


            dropdownlist.setAdapter(arrayAdapter);
            Log.i("msg", "go tlenth " + j.length());
        }




        ale.dismiss();
    }

    //Method to get student name of a particular position
    private String getId(int position) {
        String name = "";
        try {
            //Getting object of given index
            JSONObject json = result.getJSONObject(position);

            //Fetching name from that object
            name = json.getString(Constants.TAG_NAME);
            //Toast.makeText(getApplicationContext(),name, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return name;
    }
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






    @Override
    protected void onResume() {
        super.onResume();
        this.getSupportActionBar().setTitle("Building");
        Log.i("msg", "i'm resume");

    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


        String title = menuItem.getTitle().toString();


        if (title.equals("Dashboard"))
        {
            drawer.closeDrawers();
            return false;
        }
        else if (title.equals("Sync"))
        {
            drawer.closeDrawers();
            Intent inten = new Intent(this,SupersyncActvity.class);

            startActivity(inten);
            overridePendingTransition(0,0);
            finish();


            return false;
        }
        else if (title.equals("Logout"))
        {
            AlertDialog.Builder build = new AlertDialog.Builder(this);
            build.setTitle("Alert!");
            build.setCancelable(false);
            build.setMessage("Are you sure want to logout from the app.");
            build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    drawer.closeDrawers();
                    Intent inten = new Intent(Building.this,MainActivity.class);
                    SharedPrefManager.getInstance(Building.this).setislogged(false);
                    startActivity(inten);
                    overridePendingTransition(0,0);
                    finish();
                }
            });
            build.setNegativeButton("No", null);
            AlertDialog dia = build.create();
            dia.show();

            return false;
        }
        else


        {
            return  true;
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