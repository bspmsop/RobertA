package com.app.raassoc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

/**
 * Created by New android on 03-12-2018.
 */

public class Registration extends AppCompatActivity implements View.OnClickListener{
        private EditText editTextUsername,editTextEmail,editTextPassword;
        private Button buttonRegister;
        private ProgressDialog progressDialog;
        private TextView textViewLogin;
        private String equiID;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);
            if(SharedPrefManager.getInstance(this).isLoggedIn()){
                finish();
                startActivity(new Intent(this,Registration.class));
                return;
            }
            Bundle b = this.getIntent().getExtras();
            equiID = b.getString("equiID");

            editTextEmail=(EditText) findViewById(R.id.editTextEmail);
            editTextUsername=(EditText) findViewById(R.id.editTextUsername);
            editTextPassword=(EditText) findViewById(R.id.editTextPassword);
            textViewLogin=(TextView) findViewById(R.id.textViewLogin);
            buttonRegister =(Button) findViewById(R.id.buttonRegister);
            progressDialog =new ProgressDialog(this);
            buttonRegister.setOnClickListener(this);
            textViewLogin.setOnClickListener(this);
        }
    private  void registerUser(){
        final String email= editTextEmail.getText().toString().trim();
        final String username= editTextUsername.getText().toString().trim();
        final String password= editTextPassword.getText().toString().trim();
        progressDialog.setMessage("Registering User...");
        progressDialog.show();
        StringRequest stringRequest =new StringRequest(Request.Method.POST,
                Constants.URL_REGISTER1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                        try{
                            JSONObject jsonObject =new JSONObject(response);
                            Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params =new HashMap<>();
                params.put("username",username);
                params.put("email",email);
                params.put("password",password);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

    }
    @Override
    public void onClick(View view){
        if(view==buttonRegister)
            registerUser();
    }
}
