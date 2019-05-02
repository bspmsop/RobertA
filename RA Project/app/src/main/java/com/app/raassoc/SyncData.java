package com.app.raassoc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by New android on 04-01-2019.
 */

public class SyncData extends AppCompatActivity {

    private EditText mEdtName;
    private Button mBtnSubmit;
    private ListView mLstNames;

    private ProgressBar mProgress;

    public static final String URL_SAVE_NAME = "http://rreg.in/testAPI12/testAPI.php";
    public static final String URL_GET_NAME = "http://rreg.in/testAPI12/listUser.php";

    //1 means data is synced and 0 means data is not synced
    public static final int NAME_SYNCED_WITH_SERVER = 0;
    public static final int NAME_NOT_SYNCED_WITH_SERVER = 1;

    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "net.simplifiedcoding.datasaved";

    //database helper object
    private DatabaseHelper db;
    private List<SyncUser> userList = new ArrayList<>();
    private SyncUserAdapter nameAdapter;

    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sync_activity);

        registerReceiver(new NetworkStateChecker(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        db = new DatabaseHelper(this);

        mEdtName = (EditText) findViewById(R.id.et_edtName);
        mBtnSubmit = (Button) findViewById(R.id.bt_btnSubmit);
        mProgress = (ProgressBar) findViewById(R.id.pb_progress);
        mLstNames = (ListView) findViewById(R.id.listViewNames);

//        loadUsers();
        getUserList();
        //the broadcast receiver to update sync status
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //loading the names again
                Log.i("TAG", "Broadcast Received");
//                submit();
                updateUsers();
            }
        };


        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mEdtName.getText().toString())) {
                    Toast.makeText(SyncData.this, "Please enter name...", Toast.LENGTH_SHORT).show();
                } else {
                    submit(0, mEdtName.getText().toString());
                }
            }
        });
    }

    private void updateUsers() {
        //getting all the unsynced names
        Cursor cursor = db.getUnsyncedNames();

        if (cursor.moveToFirst()) {
            do {
                //calling the method to save the unsynced name to MySQL
                submit(
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME))
                );
            } while (cursor.moveToNext());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //registering the broadcast receiver to update sync status
        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private void getUserList() {
        mProgress.setVisibility(View.VISIBLE);

        if (ConnectivityHelper.isConnectingToInternet(this)) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GET_NAME,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mProgress.setVisibility(View.GONE);
                            Log.i("TAG", "Response:" + response);
                            try {
                                JSONArray mJObjUser = new JSONArray(response);
                                Log.i("TAG", "Length:" + mJObjUser.length());

                                if(mJObjUser.length() > 0)
                                {
                                    SyncUser mUser;
                                    for(int i=0;i<mJObjUser.length();i++)
                                    {
                                        mUser = new SyncUser();
                                        mUser.setName(mJObjUser.getJSONObject(i).getString("Name"));
                                        mUser.setStatus(0);

                                        db.addName(mJObjUser.getJSONObject(i).getString("Name"), NAME_SYNCED_WITH_SERVER);
                                        userList.add(mUser);
                                    }
                                }

                                nameAdapter = new SyncUserAdapter(SyncData.this, R.layout.names, userList);
                                mLstNames.setAdapter(nameAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mProgress.setVisibility(View.GONE);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            mProgress.setVisibility(View.GONE);
//            loadUsers();
        }
    }

    private void submit(final int id, final String userName) {
        mProgress.setVisibility(View.VISIBLE);

        if (ConnectivityHelper.isConnectingToInternet(this)) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_NAME,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mProgress.setVisibility(View.GONE);
                            Log.i("TAG", "Response:" + response);

                            if (id == 0) {
                                saveNameToLocalStorage(mEdtName.getText().toString(), NAME_SYNCED_WITH_SERVER);
                            } else {
                                db.updateNameStatus(id, SyncData.NAME_SYNCED_WITH_SERVER);

                                loadUsers();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mProgress.setVisibility(View.GONE);
                            saveNameToLocalStorage(mEdtName.getText().toString(), NAME_NOT_SYNCED_WITH_SERVER);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Name", userName);
                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            mProgress.setVisibility(View.GONE);
            saveNameToLocalStorage(mEdtName.getText().toString(), NAME_NOT_SYNCED_WITH_SERVER);
        }
    }

    //saving the name to local storage
    private void saveNameToLocalStorage(String name, int status) {
        mEdtName.setText("");
        db.addName(name, status);
        SyncUser mUser = new SyncUser(name, status);
        userList.add(mUser);
        refreshList();
    }

    /*
     * this method will simply refresh the list
     * */
    private void refreshList() {
        nameAdapter.notifyDataSetChanged();
    }

    /*
     * this method will
     * load the names from the database
     * with updated sync status
     * */
    private void loadUsers() {
        userList.clear();
        Cursor cursor = db.getNames();
        if (cursor.moveToFirst()) {
            do {
                SyncUser name = new SyncUser(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS))
                );
                userList.add(name);
            } while (cursor.moveToNext());
        }

        nameAdapter = new SyncUserAdapter(this, R.layout.names, userList);
        mLstNames.setAdapter(nameAdapter);
    }
}


