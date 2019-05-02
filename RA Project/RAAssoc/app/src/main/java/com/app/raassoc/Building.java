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
 * Created by New android on 14-11-2018.
 */

public class Building extends AppCompatActivity  implements Spinner.OnItemSelectedListener{
    //Declaring an Spinner
    private Spinner spinner;

    //An ArrayList for Spinner Items
    private ArrayList<String> buildings;
    private ArrayList<String> buildingID;

    //JSON Array
    private JSONArray result;

    //TextViews to display details
    private TextView textViewName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }


        //Initializing the ArrayList
        buildings = new ArrayList<String>();
        buildingID = new ArrayList<String>();


        //Initializing Spinner
        spinner = (Spinner) findViewById(R.id.buildingS);

        //Adding an Item Selected Listener to our Spinner
        //As we have implemented the class Spinner.OnItemSelectedListener to this class iteself we are passing this to setOnItemSelectedListener
        spinner.setOnItemSelectedListener(this);

        //Initializing TextViews
        textViewName = (TextView) findViewById(R.id.textViewName);

        //This method will fetch the data from the URL
        getData();
    }

    private void getData() {
        //Creating a string request
        StringRequest stringRequest = new StringRequest( Request.Method.GET,
                Constants.DATA_URL + SharedPrefManager.getInstance(this).getUserId()+"/"+SharedPrefManager.getInstance(this).getUsertype(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);

                            //Storing the Array of JSON String to our JSON Array
                            result = j.getJSONArray(Constants.JSON_ARRAY);

                            //Calling method getBuildings to get the students from the JSON Array
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

                //Adding the name of the student to array list
                buildings.add(json.getString(Constants.TAG_USERNAME));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Setting adapter to show the items in the spinner
        spinner.setAdapter(new ArrayAdapter<String>(Building.this, android.R.layout.simple_spinner_dropdown_item, buildings));
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


    //this method will execute when we pic an item from the spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Setting the values to textviews for a selected item
        Button btnBuild = (Button) findViewById(R.id.btnBuild);
        btnBuild.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Building.this, MechRoom.class);
               // intent.putExtra( "id", textViewName.getText().toString() );
                intent.putExtra( "id", textViewName.getText().toString() );
                //   intent.putExtras(b);
                startActivity(intent);
            }

        });
        textViewName.setText(getId(position));

    }

    //When no item is selected this method would execute
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        textViewName.setText("");

    }
}