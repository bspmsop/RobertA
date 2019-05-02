package com.app.raassoc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by New android on 15-11-2018.
 */

public class MechRoom extends AppCompatActivity implements Spinner.OnItemSelectedListener {

    private Spinner spinnerMech;
    private int id;
    private TextView tvID;
    //An ArrayList for Spinner Items
    private ArrayList<String> mechRooms;
    private   String name;
    private JSONArray result;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mech);
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        Bundle b = this.getIntent().getExtras();
        name = b.getString("id");

        mechRooms = new ArrayList<String>();

        spinnerMech = (Spinner) findViewById(R.id.mechList);
        spinnerMech.setOnItemSelectedListener(this);

        //Initializing TextView
        tvID = (TextView) findViewById (R.id.tvID);

        //This method will fetch the data from the URL
        getList();
    }
    private void getList() {
        //Creating a string request
        // ((TextView)findViewById(R.id.textViewSpin)).setText(name);
        //  Toast.makeText(getApplicationContext(),activekey,Toast.LENGTH_LONG).show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Constants.MECH_URL+name+"/"+SharedPrefManager.getInstance(this).getUsertype(),

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);

                            //Storing the Array of JSON String to our JSON Array


                             result = j.getJSONArray(Constants.MECH_ARRAY);

                            //Calling method getStudents to get the buildings from the JSON Array
                            getBuildings(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void getBuildings(JSONArray j) {
        //Traversing through all the items in the json array
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
        //Setting adapter to show the items in the spinner
        spinnerMech.setAdapter(new ArrayAdapter<String>(MechRoom.this, android.R.layout.simple_spinner_dropdown_item, mechRooms));
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

    //this method will execute when we pic an item from the spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Setting the values to textviews for a selected item
        Button btnMechSign = (Button) findViewById(R.id.btnMechsign);
        btnMechSign.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MechRoom.this, BoilerRoom.class);
                intent.putExtra( "id", tvID.getText().toString() );
                //   intent.putExtras(b);
                startActivity(intent);
            }
        });
        tvID.setText(getId(position));
    }

    //When no item is selected this method would execute
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

        tvID.setText("");
    }
}