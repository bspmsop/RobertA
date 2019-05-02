package com.app.raassoc;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONObject;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

/**
 * Created by New android on 22-11-2018.
 */

public class ScanQR extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private String mechID;
    JSONArray eja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        Bundle b = this.getIntent().getExtras();
        mechID = b.getString("idm");
        String ja = b.getString("overalljson");
        try {
            eja = new JSONArray(ja);
        }
        catch (Exception e)
        {
            Log.i("msg", "got json excepiton here "+e);
        }
        int currentApiVersion = Build.VERSION.SDK_INT;

        if(currentApiVersion >=  Build.VERSION_CODES.M)
        {
            if(checkPermission())
            {
                Toast.makeText(getApplicationContext(), "Permission already granted!", Toast.LENGTH_LONG).show();
            }
            else
            {
                requestPermission();
            }
        }
    }

    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if(scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(ScanQR.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result result) {
        final String myResult = result.getText();

        Log.i("msg", "got scann result " + myResult);
        //Toast.makeText(this, myResult, Toast.LENGTH_LONG).show();
        String[] arrayList = myResult.split(",");


        if (arrayList.length > 6) {

            //ArrayList list = new ArrayList<String>();
            final String[] mechArray = arrayList[0].split(":");
            final String[] equiIDArr = arrayList[1].split(":");
            final String[] equiNamArr = arrayList[2].split(":");
            final String[] modelArr = arrayList[3].split(":");
            final String[] serialArr = arrayList[4].split(":");
            final String[] addrArr = arrayList[5].split(":");
            String[] mechtitlArr = arrayList[6].split(":");
            //final String[] mechTitle = mechtitlArr[1].split("}");

            // Log.d("QRCodeScanner", mechtitlArr[1] +mechArray[1]+equiIDArr[1]);
            //Log.d("QRCodeScanner", result.getBarcodeFormat().toString());
            Log.d("QRCodeScanner", mechtitlArr[1] + mechArray[1] + equiIDArr[1]);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            if (mechArray[1].equals(mechID)) {
                Bundle datas = new Bundle();

//            Bundle data = new Bundle();
//            data.putString("id",ids[position]);
//            data.putString("title",heroes[position]);
//            data.putString("model",models[position]);
//            data.putString("serial",serial[position]);
//            //data.putString("effname",effiName[position]);
//
//            intent.putExtras(data);


                datas.putString("id", equiIDArr[1]);
                datas.putString("model", modelArr[1]);

                datas.putString("title", equiNamArr[1]);
                datas.putString("equiAddr1", addrArr[1]);
                datas.putString("serial", serialArr[1]);


                try {
                    String effnamer = "";
                    String effid = "0";

                    for (int i =0; i<eja.length(); i++)
                    {

                        JSONObject jobj = eja.getJSONObject(i);
                       String ids = jobj.getString("id");
                       if(ids.equals(equiIDArr[1]))
                       {
                           effnamer = jobj.getString("effname");
                           effid = jobj.getString("effid");
                           Log.i("msg", "matched id");
                           break;
                       }


                    }

                    datas.putString("effname", effnamer);
                    datas.putString("effid",effid);


                    //datas.putString("mechTitle",mechTitle[0]);

                    Intent intent1 = new Intent(ScanQR.this, EquipmentData.class);
                    intent1.putExtras(datas);
                    startActivity(intent1);
                }
                catch(Exception e)
                {
                    Log.i("msg", "got excepiton " + e);

                }




            } else {
                builder.setMessage("Please select valid mechanical room");
                AlertDialog alert1 = builder.create();
                alert1.show();
                scannerView.resumeCameraPreview(ScanQR.this);

            }


        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage("Please select valid QR code and try again.");
            AlertDialog alert1 = builder.create();
            alert1.show();
            scannerView.resumeCameraPreview(ScanQR.this);


        }
    }

}
