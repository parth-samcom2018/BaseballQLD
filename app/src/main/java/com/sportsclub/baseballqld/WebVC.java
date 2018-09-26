package com.sportsclub.baseballqld;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class WebVC extends BaseVC {

    public static String url;
    public static String title ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_vc);

        //BACK, rest defined in base class
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.setTitle(title);

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);



        final ProgressDialog pd = DM.getPD(this,"Downloading Content...");
        pd.show();
        webView.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                pd.setMessage("Downloading "+newProgress+" %");

                if(newProgress > 99)
                {
                    pd.dismiss();
                }


            }
        });

        Log.d("hq","Webview opening: "+url);
        webView.loadUrl(url);

    }
}

