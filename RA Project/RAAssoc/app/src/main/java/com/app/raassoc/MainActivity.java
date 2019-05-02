package com.app.raassoc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextUsername,editTextPassword;
    private Button buttonLogin;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, Building.class));
            return;
        }
        editTextUsername=(EditText) findViewById(R.id.editTextUsername);
        editTextPassword=(EditText) findViewById(R.id.editTextPassword);
        buttonLogin =(Button) findViewById(R.id.buttonLogin);
        progressDialog =new ProgressDialog(this);
        progressDialog.setMessage("Getting Data...");
        buttonLogin.setOnClickListener(this);
    }
    private void userLogin() {
        final String username= editTextUsername.getText().toString().trim();
        final String password= editTextPassword.getText().toString().trim();
        progressDialog.show();
        //validating inputs

        StringRequest stringRequest =new StringRequest(
                Request.Method.POST,
                Constants.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try{
                            JSONObject obj =new JSONObject(response);
                            Toast.makeText(getApplicationContext(),obj.getString("error"),Toast.LENGTH_LONG).show();
                            //if error is false response
                            if(!obj.getBoolean("error")){
                                //getting the user from the response
                                JSONObject userJSON = obj.getJSONObject("user");

                               Toast.makeText(getApplicationContext(),userJSON.getString("user_type"),Toast.LENGTH_LONG).show();
                              //  startActivity(new Intent(getApplicationContext(), Building.class));

                                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(
                                            userJSON.getString("id"),
                                            userJSON.getString("username"),
                                            userJSON.getString("email"),
                                            userJSON.getString("user_type")

                                    );
                                startActivity(new Intent(getApplicationContext(),Building.class));
                                finish();
                              //  Toast.makeText(getApplicationContext(),userJSON.getString("id"),Toast.LENGTH_LONG).show();

                            }else{
                                Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_LONG).show();

                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
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
    public void onClick(View view){
        if(view==buttonLogin){
            userLogin();
        }
    }
}
