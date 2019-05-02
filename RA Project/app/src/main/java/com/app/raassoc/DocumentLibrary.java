package com.app.raassoc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
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
import java.util.List;

import dmax.dialog.SpotsDialog;


/**
 * Created by New android on 09-11-2018.
 */

public class DocumentLibrary extends Activity implements SearchView.OnQueryTextListener {


    private ListView lvDocments;
    private JSONArray documents;
    List<ListDoc> docmList;
    ListDocAdapter adapter;
    private String docID, mechDID;
    private TextView tvHeadNam;
    private ImageView backBtn;
    ImageView vendorBtn;
    AlertDialog ale;
    SearchView editSearch;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);


        backBtn = (ImageView) findViewById(R.id.btnback);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,0);
            }
        });

        vendorBtn = (ImageView) findViewById(R.id.vendRepaire);
        vendorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DocumentLibrary.this, RepairsMain.class);

                startActivity(intent);
                overridePendingTransition(0, 0);


            }
        });

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
        Bundle datas = getIntent().getExtras();

        mechDID = Constants.GMechanical_ID;


        ale = new SpotsDialog(this,"Loading",R.style.Customdmax);
        ale.setCancelable(false);

        ale.show();

        lvDocments = (ListView) findViewById(R.id.lvDocments);


        TextView emptyText = (TextView) findViewById(android.R.id.empty);
        lvDocments.setEmptyView(emptyText);


        ImageView vendRepr = (ImageView) findViewById(R.id.vendRepr);
        editSearch = (SearchView) findViewById(R.id.searchView);
        editSearch.setBackground(null);
        tvHeadNam = (TextView) findViewById(R.id.tvHeadNam);


        tvHeadNam.setText(Constants.GMechanical_Title);
       /* vendRepr.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DocumentLibrary.this, RepairsMain.class);
                startActivity(intent);
            }
        });*/
        editSearch.setOnSuggestionListener(new SearchView.OnSuggestionListener() {

            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Log.i("msg", "caling every time");
                Cursor cursor = editSearch.getSuggestionsAdapter().getCursor();
                cursor.moveToPosition(position);
                String suggestion = cursor.getString(2);//2 is the index of col containing suggestion name.
                editSearch.setQuery(suggestion, true);//setting suggestion
                return true;
            }
        });



        Boolean isnet =  new NetworkChangeReceiver().isConnectedToInternet(DocumentLibrary.this);
        if (isnet)
        {

            getDocuments();


        }
        else
        {

            if(!GlobalClass.isnonetworknotifier)
            {
                AlertDialog.Builder build = new AlertDialog.Builder(DocumentLibrary.this);
                build.setTitle("Alert!");
                build.setCancelable(false);
                build.setMessage("No network connection would you like to use app in offline mode.");
                build.setNegativeButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getofflineDocData();
                    }
                });
                build.setPositiveButton("Cancel", null );

                AlertDialog dia = build.create();
                dia.show();
                GlobalClass.isnonetworknotifier =true;

            }
            else
            {
                getofflineDocData();

            }


        }

    }





    void getofflineDocData()
    {
        editSearch.setOnQueryTextListener(DocumentLibrary.this);
        ale.hide();
        Log.i("mg", "got offline vendror data");

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

                            if (jad.has("drawings")) {
                                documents = jad.getJSONArray("drawings");



                                if (documents.length() <1)
                                {
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                                    lvDocments.setLayoutParams(lp);
                                    lvDocments.requestLayout();

                                }
                                else
                                {
                                    getList(documents);
                                    editSearch.setOnQueryTextListener(DocumentLibrary.this);

                                }


                                //Calling method getList to get the documents from the JSON Array

                            }}}}



            }





        }catch (Exception e)
        {

            Log.i("msg", "got json exception "   +e);


        }









    }




    public void getDocuments() {
Log.i("msg","url is " + Constants.DOC_LIST + mechDID + "/" + SharedPrefManager.getInstance(this).getUsertype());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Constants.DOC_LIST + mechDID + "/" + SharedPrefManager.getInstance(this).getUsertype(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject j = null;
                        try {

                            Log.i("msg", "got docus " + response);
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);
                            //Storing the Array of JSON String to our JSON Array
                            if(j.has(Constants.JSON_ARRAY))
                            {
                                try {
                                    documents = j.getJSONArray(Constants.JSON_ARRAY);
                                    if (documents.length() <1)
                                    {
                                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                                        lvDocments.setLayoutParams(lp);
                                        lvDocments.requestLayout();

                                    }
                                    else
                                    {
                                        getList(documents);
                                        editSearch.setOnQueryTextListener(DocumentLibrary.this);

                                    }
                                    Log.i("msg", "" + j);
                                    Log.i("msg", "myresponse " + response);
                                    //Calling method getList to get the documents from the JSON Array

                                }
                                catch (Exception e)
                                {
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                                    lvDocments.setLayoutParams(lp);
                                    lvDocments.requestLayout();






                                }




                            }

                            ale.dismiss();

                            //Calling method getBuildings to get the students from the JSON Array

                        } catch (JSONException e) {
                            e.printStackTrace();
                            ale.dismiss();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ale.dismiss();
                    }
                });

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);

    }

    private void getList(JSONArray j) throws JSONException {
        Log.i("msg","get the list");
        final String[] listDoc = new String[j.length()];
        final String[] listDPath = new String[j.length()];
        final String[] listDate = new String[j.length()];
        final String[] listFPath = new String[j.length()];


        docmList = new ArrayList<>();

        for (int i = 0; i < j.length(); i++) {
            JSONObject obj = j.getJSONObject(i);
            listDoc[i] = obj.getString("file_name");
            listDPath[i] = obj.getString("file_short_name");
            listDate[i] = obj.getString("draw_date");
            listFPath[i] = obj.getString("file_path");
            //Toast.makeText(getApplicationContext(),listDPath[i],Toast.LENGTH_LONG).show();
            //getting the username from the json object and putting it inside string array
            docmList.add(new ListDoc(R.drawable.document, listDoc[i], listDPath[i], listDate[i], listFPath[i]));
        }
           /* ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.custom_list, listDoc);
            lvDocments.setAdapter(arrayAdapter);*/

        adapter = new ListDocAdapter(this, R.layout.list_documents, docmList);


        //attaching adapter to the listview
        lvDocments.setAdapter(adapter);
        lvDocments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Toast.makeText(getBaseContext(),"http://mybets360.com/"+listFPath[position], Toast.LENGTH_LONG).show();
                enterTapped();
                adapter.onclickedRow(position);


            }
        });

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.e("Enter", "Text ->" + query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;

        Log.i("msg", "go tmessgjjkj " + newText);
        Log.e("Enter", "newText ->" + newText);
        adapter.filter(text);
        return false;
    }
    public void enterTapped()
    {
        Log.i("msg", "im tapped here");
        try {

                InputMethodManager inputManagers = (InputMethodManager)
                        getSystemService(MainActivity.INPUT_METHOD_SERVICE);

                inputManagers.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

        }
        catch (Exception e)
        {

            Log.i("msg", "handled error" + e);
        }

    }


}
