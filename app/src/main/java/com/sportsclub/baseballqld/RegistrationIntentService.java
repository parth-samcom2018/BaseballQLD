package com.sportsclub.baseballqld;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RegistrationIntentService extends IntentService {

    public RegistrationIntentService()
    {
        super("My Intent service");
    }

    public RegistrationIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("hq","My service on handle intent");

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);


            SharedPreferences prefs = getSharedPreferences("HQGCM",MODE_PRIVATE);
            String existingRegistered = prefs.getString("GCM_TOKEN","");

            if(token != existingRegistered) registerPushWithServer(token);
            else Log.d("hq","No need to update "+token);

        } catch (Exception e) {
            Log.d("hq","gcm registration exception");
            e.printStackTrace();
        }

    }

    private void registerPushWithServer(final String token)
    {
        Log.d("hq","Registering GCM token: "+token);

        if(DM.member != null)
        {
            final int DEVICE_TYPE =2; //for android
            final int MEMBER_ID = DM.member.memberId;
            if(MEMBER_ID == 0)return;


            DM.getApi().registerDeviceForPushs(DM.getAuthString(), DEVICE_TYPE, token, MEMBER_ID, new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {

                    Log.d("hq","Updated GCM on server");

                    SharedPreferences prefs = getSharedPreferences("HQGCM",MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("GCM_TOKEN",token);
                    editor.commit();
                    Log.d("deviceID", token);
                }

                @Override
                public void failure(RetrofitError error) {

                    Log.d("hq","could not register for push "+error.getMessage());
                }
            });
        }
    }
}


