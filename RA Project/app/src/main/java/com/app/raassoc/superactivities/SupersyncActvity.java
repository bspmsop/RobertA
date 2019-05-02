package com.app.raassoc.superactivities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.app.raassoc.Building;
import com.app.raassoc.MainActivity;
import com.app.raassoc.R;
import com.app.raassoc.SharedPrefManager;
import com.app.raassoc.helper.Completionhanlder;
import com.app.raassoc.helper.GlobalClass;
import com.orm.SugarContext;

import dmax.dialog.SpotsDialog;

public class SupersyncActvity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SugarContext.init(this);
        setContentView(R.layout.activity_supersync_actvity);
        ale = new SpotsDialog(this,"Fetching local database",R.style.Customdmax);
        ale.setCancelable(false);

        syncBttn = (Button)findViewById(R.id.syncBtn);
        syncBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncTapped();
            }
        });
        defaultVlaues();
    }

    ImageButton imback;
    AlertDialog ale;
    Button syncBttn;
    DrawerLayout drawer;





    public void defaultVlaues()
    {
        Toolbar tbar = findViewById(R.id.synctoolbarnav);
        setSupportActionBar(tbar);
        drawer  = findViewById(R.id.sync_drawer_layouts);
        ActionBarDrawerToggle tog = new ActionBarDrawerToggle(this,drawer,tbar,R.string.action_settings,R.string.action_settings);
        drawer.addDrawerListener(tog);

        tog.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.sync_nav_views);
        navigationView.setNavigationItemSelectedListener(this);

        View header =  navigationView.getHeaderView(0);
        TextView usertitle = (TextView)header.findViewById(R.id.usernametxt);
        TextView userEmail = (TextView) header.findViewById(R.id.useremailtxt);
        usertitle.setText(SharedPrefManager.getInstance(this).getUsername());
        userEmail.setText(SharedPrefManager.getInstance(this).getUseremail());


        this.getSupportActionBar().setTitle("SYNC");











    }


    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


        String title = menuItem.getTitle().toString();


        if (title.equals("Dashboard"))
        {
            drawer.closeDrawers();
            Intent inten = new Intent(this,Building.class);
             startActivity(inten);
            overridePendingTransition(0,0);
            finish();


            return false;


        }
        else if (title.equals("Sync"))
        {
            drawer.closeDrawers();
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
                    Intent inten = new Intent(SupersyncActvity.this,MainActivity.class);
                    SharedPrefManager.getInstance(SupersyncActvity.this).setislogged(false);
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




    public boolean isConnectedToInternet(Context context) {
        try {
            if (context != null) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
            return false;
        } catch (Exception e) {
            Log.i("msg", "got exception " + e);
            return false;
        }
    }



    public void syncTapped()
    {

     boolean isnet =   isConnectedToInternet(this);
     if(!isnet)
     {
         AlertDialog.Builder build = new AlertDialog.Builder(this);
         build.setTitle("Network Alert!");
         build.setMessage("Please check your network connection and try again.");
         build.setPositiveButton("ok", null);
         build.show();
         build.setCancelable(false);
         return;

     }



        ale.show();
        GlobalClass.isFromMainClass = true;
        checkIstransferred();





    }



    public  void checkIstransferred()
    {

        if(!GlobalClass.isBackgroundApiRunning)
        {
             ale.dismiss();
             this.runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     AlertDialog  nale = new SpotsDialog(SupersyncActvity.this,"Processing your files",R.style.Customdmax);
                     nale.show();

                     Log.i("msg", "background thread not running");
                     GlobalClass.startSync(SupersyncActvity.this, nale);
                 }
             });


        }
        else if(GlobalClass.istransfersync)
        {
            ale.dismiss();

            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog  nale = new SpotsDialog(SupersyncActvity.this,"Processing your files",R.style.Customdmax);
                    nale.show();

                    Log.i("msg", "background thread not running");
                    GlobalClass.startSync(SupersyncActvity.this, nale);
                }
            });
        }
        else
        {
            Thread checkingthread = new Thread(new Runnable() {
                @Override
                public void run() {
                    checkIstransferred();
                    Log.i("td", "checking transferring ");

                }
            });

            checkingthread.start();

        }


    }









}
