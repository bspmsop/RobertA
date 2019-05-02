package com.app.raassoc;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.ClientCertRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.app.raassoc.helper.NetworkChangeReceiver;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import dmax.dialog.SpotsDialog;

/**
 * Created by New android on 31-01-2019.
 */

public class DocumentLFile extends AppCompatActivity {

    private WebView mWebview;
    private String docPath;
    ImageView btnback;
    String googleDocs = "https://docs.google.com/viewer?url=";
    String pdf_url;
    PDFView myviewr;
      AlertDialog ale;
      VideoView vviwer;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documentlfile);

        mWebview = (WebView)findViewById(R.id.wvDispl);
        vviwer = (VideoView)findViewById(R.id.vid);
        myviewr = (PDFView)findViewById(R.id.pdfview);
        mWebview.getSettings().setBuiltInZoomControls(true);

        ale = new SpotsDialog(this,"Loading",R.style.Customdmax);

        ale.setCancelable(false);

        final Bundle datas = getIntent().getExtras();

        btnback = (ImageView) findViewById(R.id.btnback);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              goback();
            }
        });



        docPath =datas.getString("path_file");

        docPath = docPath.replaceAll(" ","%20");

        pdf_url =   Constants.URL_bBASIC + docPath;

       Log.i("msg", "path is " + pdf_url);


        String someFilepath = pdf_url;
        Boolean isnet =  new NetworkChangeReceiver().isConnectedToInternet(DocumentLFile.this);

        try {
            String extension = someFilepath.substring(pdf_url.lastIndexOf("."));
            extension = extension.toLowerCase();
            Log.i("msg", "extendion "+extension);


            if (extension.equals(".jpg") || extension.equals(".jpeg") || extension.equals(".png") )
            {

                if(isnet)
                {
                    mWebview.setWebViewClient(new RAWebClient(ale, pdf_url));

                    mWebview.loadUrl(pdf_url);


                    mWebview.getSettings().setJavaScriptEnabled(true);
                    mWebview.getSettings().setLoadWithOverviewMode(true);
                    mWebview.getSettings().setUseWideViewPort(true);

                }
                else
                {
                    String fileName = pdf_url;

                    String fileNameer = fileName.substring(fileName.lastIndexOf('/') + 1);

                    Log.i("msg", "file name is " + fileNameer);

                    File photso= new File(Environment.getExternalStorageDirectory(), fileNameer);
                    if ( photso.exists()) {


                        String imagePath = "file://" + photso.getAbsolutePath();
                        Log.i("msg", "image path " + imagePath);
                        String html = "<html><head></head><body> <img src=\""+ imagePath + "\"> </body></html>";
                        mWebview.loadDataWithBaseURL("", html, "text/html","utf-8", "");

                        // mWebview.loadDataWithBaseURL("file:///android_asset/",data , "text/html", "utf-8",null);
                        mWebview.getSettings().setJavaScriptEnabled(true);
                        mWebview.getSettings().setLoadWithOverviewMode(true);
                        mWebview.getSettings().setUseWideViewPort(true);


                    }
                    else
                    {
                        Log.i("msg", "file doesnot exists");
                        showalert("File does not exist in local database");


                    }





                }





            }
            else if(extension.equals(".pdf")){


                if (isnet)
                {

                    ale.show();


                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0);
                    mWebview.setLayoutParams(lp);
                    new filedviewr().execute(pdf_url);

                    Log.i("msg", extension);


                }
                else
                {

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0);
                    mWebview.setLayoutParams(lp);
                    String fileName = pdf_url;

                    String fileNameer = fileName.substring(fileName.lastIndexOf('/') + 1);

                    Log.i("msg", "file name is " + fileNameer);

                    File photso= new File(Environment.getExternalStorageDirectory(), fileNameer);
                    if ( photso.exists()) {

                        Log.i("msg", "path is " +photso.getAbsolutePath());
                        Log.i("msg", "file exists");
                        myviewr.fromFile(photso).load();

                    }
                    else
                    {
                        Log.i("msg", "file doesnot exists");
                        showalert("File does not exist in local database");


                    }









                }

                //pdf_url = googleDocs + pdf_url;

            }
            else if(extension.equals(".mov") ||extension.equals(".mp4") || extension.equals(".3gp") )
            {

                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0);
                mWebview.setLayoutParams(lp1);
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0);
                myviewr.setLayoutParams(lp2);


                if (isnet)
                {


                    ale.show();


                    Uri videouri =   Uri.parse(pdf_url);
                    try {
                        vviwer.setVideoURI(videouri);
                        MediaController mediaController = new
                                MediaController(this);
                        mediaController.setAnchorView(vviwer);
                        vviwer.setMediaController(mediaController);
                        vviwer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                ale.dismiss();
                            }
                        });
                        vviwer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                            @Override
                            public boolean onError(MediaPlayer mp, int what, int extra) {
                                ale.dismiss();
                                return false;
                            }
                        });

                        vviwer.start();
                    } catch (Exception e) {
                        ale.dismiss();
                        e.printStackTrace();
                        Log.i("msg", "got error videod eurl " +e);
                    }

                    Log.i("msg", extension);


                }
                else
                {
                    ale.show();

                    String fileName = pdf_url;

                    String fileNameer = fileName.substring(fileName.lastIndexOf('/') + 1);

                    Log.i("msg", "file name is " + fileNameer);

                    File photso= new File(Environment.getExternalStorageDirectory(), fileNameer);
                    if ( photso.exists()) {
                        Uri videouri = Uri.parse(photso.getAbsolutePath());

                        try {
                            vviwer.setVideoURI(videouri);
                            MediaController mediaController = new
                                    MediaController(this);
                            mediaController.setAnchorView(vviwer);
                            vviwer.setMediaController(mediaController);
                            vviwer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    ale.dismiss();
                                }
                            });
                            vviwer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                                @Override
                                public boolean onError(MediaPlayer mp, int what, int extra) {
                                    ale.dismiss();
                                    return false;
                                }
                            });

                            vviwer.start();
                        } catch (Exception e) {
                            ale.dismiss();
                            e.printStackTrace();
                            Log.i("msg", "got error videod eurl " +e);
                        }




                    }
                    else
                    {
                        Log.i("msg", "file doesnot exists");
                        showalert("File does not exist in local database");


                    }









                }

                //pdf_url = googleDocs + pdf_url;


                Log.i("msg", "im different formate");
//                AlertDialog.Builder build = new AlertDialog.Builder(this);
//                build.setTitle("Alert!");
//                build.setMessage("Unsupported formate, please try again.");
//                build.setPositiveButton("ok", null);
//                build.show();




            }
            else if(extension.equals(".mp3"))
            {

                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0);
                mWebview.setLayoutParams(lp1);
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0);
                myviewr.setLayoutParams(lp2);


                if (isnet)
                {


                    ale.show();



                    try {
                        MediaPlayer mp = new MediaPlayer();
                        mp.setDataSource(pdf_url);
                        mp.prepareAsync();
                        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                ale.dismiss();
                            }
                        });
                        mp.start();

                    } catch (Exception e) {
                        ale.dismiss();
                        e.printStackTrace();
                        Log.i("msg", "got error videod eurl " +e);
                    }

                    Log.i("msg", extension);


                }
                else
                {
                    ale.show();

                    String fileName = pdf_url;

                    String fileNameer = fileName.substring(fileName.lastIndexOf('/') + 1);

                    Log.i("msg", "file name is " + fileNameer);

                    File photso= new File(Environment.getExternalStorageDirectory(), fileNameer);
                    if ( photso.exists()) {
                        Uri videouri = Uri.parse(photso.getAbsolutePath());

                        try {
                            MediaPlayer mp = new MediaPlayer();
                            mp.setDataSource(photso.getAbsolutePath());
                            mp.prepareAsync();
                            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    ale.dismiss();
                                }
                            });
                            mp.start();

                        } catch (Exception e) {
                            ale.dismiss();
                            e.printStackTrace();
                            Log.i("msg", "got error videod eurl " +e);
                        }




                    }
                    else
                    {
                        Log.i("msg", "file doesnot exists");
                        showalert("File does not exist in local database");


                    }









                }

                //pdf_url = googleDocs + pdf_url;


                Log.i("msg", "im different formate");
//                AlertDialog.Builder build = new AlertDialog.Builder(this);
//                build.setTitle("Alert!");
//                build.setMessage("Unsupported formate, please try again.");
//                build.setPositiveButton("ok", null);
//                build.show();




            }
            else
            {
                Log.i("msg", "im different formate");
                AlertDialog.Builder build = new AlertDialog.Builder(this);
                build.setTitle("Alert!");
                build.setMessage("Unsupported formate, please try again.");
                build.setPositiveButton("ok", null);
                build.show();

            }
            Log.i("msg", "got pdf as " + pdf_url);






        }

        catch (Exception e)
        {
            Log.i("msg", "test" + e);
        }


    }


public void showalert(String msg)
{
    ale.dismiss();
    AlertDialog.Builder build = new AlertDialog.Builder(DocumentLFile.this);
    build.setTitle("Alert!");
    build.setCancelable(false);
    build.setMessage(msg);
    build.setPositiveButton("ok", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            goback();
        }
    });
    AlertDialog dia = build.create();
    dia.show();
    Log.i("msg","got excepiujlkjlk hee");



}

    class filedviewr extends AsyncTask<String,Void,InputStream>
    {





        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream isp = null;

            Log.i("msg", "running background " + strings[0]);
      try
      {
          URL pdfurl = new URL(strings[0]);
          HttpURLConnection hcon = (HttpURLConnection) pdfurl.openConnection();
                  if (hcon.getResponseCode() == 200)
                  {
                      isp = new BufferedInputStream(hcon.getInputStream());
                     Log.i("msg", "got streams heere");



                  }
                  else
                  {

                      Log.i("msg","got excepiujlkjlk hee");
                      return isp;
                  }


      }
      catch (Exception e)
      {
          return isp;
      }




            return isp;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {

        if(inputStream == null)
        {
            ale.dismiss();
            showalert("An internal error occured, please try again.");
        }
        else {

            Log.i("msg", "called pos texcej");

            myviewr.fromStream(inputStream).onLoad(new OnLoadCompleteListener() {
                @Override
                public void loadComplete(int nbPages) {
                    ale.dismiss();
                }
            }).load();
        }
        }
    }

public void goback()
{
    final Bundle datas = getIntent().getExtras();
    boolean isformequipmedata = datas.getBoolean("fromdata");
    if(isformequipmedata)
    {
        finish();
        overridePendingTransition(R.xml.benter, R.xml.bexit);
    }

    else {
        Intent inte = new Intent(DocumentLFile.this, DocumentLibrary.class);
        startActivity(inte);

        overridePendingTransition(R.xml.benter, R.xml.bexit);
    }
}




}