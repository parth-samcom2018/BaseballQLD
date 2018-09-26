package com.sportsclub.baseballqld;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sportsclub.baseballqld.api.API;
import com.sportsclub.baseballqld.models.Member;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class DM {

    public  static String APIROOT ="http://baseballqldapi.azurewebsites.net";

    public static Member member;

    public static String fromHTTPStoHTTP(String httpsString)
    {
        String returnString = httpsString;
        if(httpsString.contains("https"))
        {
            return httpsString.replace("https","http");
        }
        return returnString;
    }

    public static boolean isEmailValid(String email) {

        return (email.contains("@") && email.contains("."));
    }

    public static boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    public static String getDateOnlyString(Date date)
    {
        return new SimpleDateFormat("MMM dd,yyyy").format(date);
    }


    public static String getTimeOnlyString(Date date)
    {
        return new SimpleDateFormat("h:mm a").format(date);
    }

    public static String getTimeAgo(Date date)
    {
        Long difference= (System.currentTimeMillis()/1000) -date.getTime()/1000;

        int days = (int) (difference/(60*60*24));
        if(days == 1 ) return "1 day ago";
        else return days+" days ago";
    }

    public static void hideKeyboard(Activity activity)
    {
        //hide keyboard
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static View.OnClickListener getFlagOnClickListener(final Activity context)
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("hq","flag  click");
                final String inappropriateURL = "http://www.sportsclubhq.com/contact---content-moderation.html";
                WebVC.url = inappropriateURL;


                AlertDialog.Builder b = new AlertDialog.Builder(context);
                b.setTitle("Inappropriate Content");
                b.setMessage("Are you sure you want to report this content as inappropriate?");
                b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(context, WebVC.class);
                        context.startActivity(i);
                    }
                });
                b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                b.show();



            }
        };
    }

    public static ProgressDialog getPD(Context context, String message)
    {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        return progressDialog;
    }

    public static String getAuthString()
    {
        if(member !=null) {
            String s ="Bearer "+DM.member.access_token;
            Log.d("hq", "auth key: "+s);
            return s;
        }
        else return "";
    }

    private static RestAdapter getRestAdapter()
    {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        String api = APIROOT;


        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(api)
                .setConverter(new GsonConverter(gson))
                .build();
        return restAdapter;
    }

    public static API getApi()
    {
        RestAdapter restAdapter = DM.getRestAdapter();
        API api = restAdapter.create(API.class);
        return api;
    }


    public static Bitmap createScaledBitmap(Bitmap original, float scaleFactor)
    {
        int newWidth = (int) (original.getWidth() * scaleFactor);
        int newHeight = (int) (original.getHeight() *scaleFactor);
        return Bitmap.createScaledBitmap(original,newWidth,newHeight,true);
    }

    public static Bitmap decodeSampledBitmapFromFile(String imageDecodableString, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageDecodableString, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imageDecodableString,options);
    }
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static void downloadFile(String fileURL, File directory) {
        try {

            FileOutputStream f = new FileOutputStream(directory);
            URL u = new URL(fileURL);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();

            InputStream in = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
            }
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
