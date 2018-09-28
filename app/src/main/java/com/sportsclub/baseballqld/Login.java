package com.sportsclub.baseballqld;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sportsclub.baseballqld.models.Member;
import com.sportsclub.baseballqld.models.Token;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Login extends BaseVC {


    private static final String TAG = "RegisterToken";
    private TextView DeviceID;
    String unique_id;

    private EditText mEmailView,mPasswordView,et_clubcheck;
    private Button btn_register;
    private TextView tv_forgot1;

    public static String justRegisteredUsername = null;
    public static String justRegisteredPassword = null;
    public static String justMemberID = null;
    SharedPreferences pref;
    public static final String MYPref = "Pref";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_login);

        pref = getSharedPreferences(MYPref, MODE_PRIVATE);

        mEmailView = findViewById(R.id.et_username);
        mPasswordView = findViewById(R.id.et_password);
        btn_register = findViewById(R.id.register_button);
        tv_forgot1 = findViewById(R.id.tv_forgot1);

        tv_forgot1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgot();

            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        Button btn_login = findViewById(R.id.login_in_button);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mEmailView.getText().toString().isEmpty() && mPasswordView.getText().toString().isEmpty()){

                    Toast.makeText(Login.this, "Username and Password Missing!", Toast.LENGTH_SHORT).show();
                    return;
                }
                attemptLogin();
            }
        });

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE){
                    DM.hideKeyboard(Login.this);
                    return true;
                }

                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        //check for registered user
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = preferences.getString("HQUsername", null);
        String token = preferences.getString("HQToken", null);
        int memberID = preferences.getInt("HQMemberId", 0);
        if (username != null && token != null) {
            Token t = new Token();
            t.userName = username;
            t.access_token = token;
            t.memberId = memberID;
            setUserOnDevice(t);
            proceed();
        }

        DeviceID = findViewById(R.id.textView1);

        unique_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        DeviceID.setText("My ID is: " + unique_id);
        Log.i(TAG, "Registration Device: " + unique_id);
        //Log.d("LoginID" , "LoginID:" + memberID);
    }



    private void forgot() {

        final Dialog dialog = new Dialog(Login.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialogbox_forgot);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final EditText etEmail = dialog.findViewById(R.id.etName);

        Button dialogBtn_done = dialog.findViewById(R.id.btn_dialog);
        dialogBtn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = etEmail.getText().toString();
                if(email.isEmpty())
                {
                    Toast.makeText(Login.this, "You must provide an Email ID", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")){
                    Toast toast = Toast.makeText(Login.this, "Invalid Email ID", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    dialog.dismiss();
                    return;
                }

                final ProgressDialog pd = DM.getPD(Login.this, "Sending mail...");
                pd.show();

                DM.getApi().forgetPassword(DM.getAuthString(), email, new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {

                        Toast.makeText(Login.this, "Mail Send success!", Toast.LENGTH_LONG).show();
                        DM.hideKeyboard(Login.this);
                        pd.dismiss();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Toast.makeText(Login.this, "Failed Send mail!", Toast.LENGTH_LONG).show();
                        DM.hideKeyboard(Login.this);
                        pd.dismiss();
                    }
                });
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void register() {

        Intent i = new Intent(this, Registration.class);
        startActivity(i);
    }

    private void proceed() {

        //  if(true)return;
        Intent i = new Intent(this, MainTabbing.class);
        startActivity(i);
    }

    private void setUserOnDevice(Token tokenModel)
    {
        DM.member = new Member();
        DM.member.username = tokenModel.userName;
        DM.member.access_token = tokenModel.access_token;
        DM.member.memberId = tokenModel.memberId;

        Log.d("LoginMember: ","here is a memberID"+DM.member.memberId);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("HQUsername",tokenModel.userName);
        editor.putString("HQToken",tokenModel.access_token);
        editor.putInt("HQMemberId",tokenModel.memberId);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(justRegisteredUsername != null && justRegisteredPassword != null)
        {

            mEmailView.setText(justRegisteredUsername);
            mPasswordView.setText(justRegisteredPassword);
            attemptLogin();
            justRegisteredPassword = null;
            justRegisteredUsername = null;
        }

    }

    @SuppressLint("CommitPrefEdits")
    private void attemptLogin() {



        mEmailView.setError(null);
        mPasswordView.setError(null);


        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !DM.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!DM.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.error_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            DM.hideKeyboard(this);
            final ProgressDialog pd = DM.getPD(this,"Logging In...");
            pd.show();

            DM.getApi().login("password", email, password, new Callback<Token>() {
                @Override
                public void success(Token login, Response response) {
                    Log.d("hq","success");
                    pd.dismiss();
                    setUserOnDevice(login);

                    Intent service = new Intent(Login.this, RegistrationIntentService.class);
                    startService(service);

                    proceed();

                }

                @Override
                public void failure(RetrofitError error) {

                    pd.dismiss();
                    Log.d("hq","failed"+error.getMessage());
                    Toast.makeText(Login.this,"Could not login: "+error.getMessage(),Toast.LENGTH_LONG).show();

                }
            });


        }


    }

}