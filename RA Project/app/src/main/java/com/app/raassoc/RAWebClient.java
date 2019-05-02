package com.app.raassoc;

import android.app.AlertDialog;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class RAWebClient extends WebViewClient {
    AlertDialog ale;
    RAWebClient(AlertDialog le, String ut)
    {

        ale = le;
         ale.show();

    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        Log.i("msg", "calling from should");
        return true;
    }



    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Log.i("msg", "shuld load url called");
        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        Log.i("msg", "loading ffinisheddd" +url);
      ale.dismiss();

    }


    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        Log.i("msg", "got client error" + error);
        ale.dismiss();
    }
}
