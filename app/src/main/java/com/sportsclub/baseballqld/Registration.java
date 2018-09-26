package com.sportsclub.baseballqld;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.sportsclub.baseballqld.models.ClubNames;
import com.sportsclub.baseballqld.models.ClubResponse;
import com.sportsclub.baseballqld.models.Event;
import com.sportsclub.baseballqld.models.Register;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import info.hoang8f.android.segmented.SegmentedGroup;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class Registration extends BaseVC{

    //MODELS
    private List<ClubNames> clubNames = new Vector<ClubNames>(); //empty
    private ArrayAdapter<ClubNames> arrayAdapter;
    ArrayList<String> selectedItems;

    public static final String MYPref = "Pref";
    public static final String ALLOW_KEY = "ALLOWED";
    public static final String CAMERA_PREF = "camera_pref";

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 100;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE = 100;
    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 100;

    boolean isSelected;
    private CheckedTextView checkBox;
    private ListView listView;
    private Button btn_dialog;
    private ProgressBar pr;
    Dialog dialog;
    Event event;

    private Button registerButton;
    private EditText emailET;
    private EditText passwordET;
    private EditText passwordConfirmET;
    private EditText firstNameET;
    private EditText surnameET;
    private SegmentedGroup genderSG;
    private RadioButton buttonSG1;
    private RadioButton buttonSG2;
    private EditText birthYearET;
    private EditText countryET;
    private ImageButton ib_down;
    private EditText postCodeET;
    SharedPreferences sPref;
    private Switch termsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_register);

        sPref = getSharedPreferences(MYPref, MODE_PRIVATE);

        emailET = findViewById(R.id.email);
        emailET = findViewById(R.id.email);
        passwordET = findViewById(R.id.password);
        passwordConfirmET = findViewById(R.id.password_confirm);
        firstNameET = findViewById(R.id.firstNameET);
        surnameET = findViewById(R.id.surnameET);

        selectedItems=new ArrayList<String>();

        genderSG = findViewById(R.id.genderSG);
        buttonSG1 = findViewById(R.id.buttonsg1);
        buttonSG2 = findViewById(R.id.buttonsg2);

        buttonSG1.setChecked(true);
        genderSG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (buttonSG1.isChecked()){
                    buttonSG1.setChecked(true);
                    return;
                }
                if (buttonSG2.isChecked()){
                    buttonSG2.setChecked(true);
                    return;
                }
            }
        });

        birthYearET = findViewById(R.id.birthYearET);
        countryET = findViewById(R.id.countryET);

        ib_down = findViewById(R.id.ib_down);

        ib_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseCountryAction();
            }
        });
        //countryET.setEnabled(false);
        countryET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseCountryAction();
            }
        });

        postCodeET = findViewById(R.id.postCodeET);

        postCodeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length()>=2){
                    DM.hideKeyboard(Registration.this);
                }
            }
        });

        termsSwitch = findViewById(R.id.termsSwitch);

        passwordConfirmET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    registerAction1();
                    return true;
                }
                return false;
            }
        });

        registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert();

            }
        });

        Button termsButton = findViewById(R.id.termsButton);
        termsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                termsAction();
            }
        });

        Button backToLoginButton = findViewById(R.id.login_button);
        backToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAction();
            }
        });

        termsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                switchOn = isChecked;
            }
        });
    }


    private void chooseCountryAction()
    {
        String[] isoCountryCodes = Locale.getISOCountries();
        Vector<String> countries = new Vector<String>();
        for (String countryCode : isoCountryCodes) {
            Locale locale = new Locale("", countryCode);
            String countryName = locale.getDisplayCountry();
            countries.add(countryName);
        }

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Choose Country");
        final String[] a = countries.toArray(new String[countries.size()]);

        b.setItems(a, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                countryET.setText(a[which]);
            }
        });

        b.show();
    }


    private boolean switchOn = false;

    private void registerAction1(){

        isOnline();
        // Reset errors.
        emailET.setError(null);
        passwordET.setError(null);
        passwordConfirmET.setError(null);
        firstNameET.setError(null);
        surnameET.setError(null);
        birthYearET.setError(null);
        countryET.setError(null);

        // Store values at the time of the login attempt.
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String passwordConfirm = passwordConfirmET.getText().toString();
        String firstName = firstNameET.getText().toString();
        String lastName = surnameET.getText().toString();
        String birthday = birthYearET.getText().toString();
        String postCode = postCodeET.getText().toString();
        String countryCode = countryET.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email) || !DM.isEmailValid(email)){
            Toast.makeText(this,"Enter an Email ID", Toast.LENGTH_LONG).show();
            emailET.requestFocus();
            emailET.setFocusable(true);
            focusView = emailET;
            cancel = true;
            return;
        }

        if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {

            Toast.makeText(this, "Invalid Email ID", Toast.LENGTH_SHORT).show();
            emailET.requestFocus();
            emailET.setFocusable(true);
            focusView = emailET;
            cancel = true;
            return;
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Enter Password", Toast.LENGTH_LONG).show();
            passwordET.requestFocus();
            passwordET.setFocusable(true);
            focusView = passwordET;
            cancel = true;
            return;
        }


        if (password.length()<6){
            passwordET.requestFocus();
            passwordET.setFocusable(true);
            Toast.makeText(this, "Password is too Short! Atleast 6 character required", Toast.LENGTH_SHORT).show();
            return;
        }



        if (TextUtils.isEmpty(passwordConfirm)){
            Toast.makeText(this,"Enter Confirm Password", Toast.LENGTH_LONG).show();
            passwordConfirmET.requestFocus();
            passwordConfirmET.setFocusable(true);
            focusView = passwordConfirmET;
            cancel = true;
            return;
        }

        if (!passwordET.getText().toString().matches(passwordConfirmET.getText().toString())){
            Toast.makeText(this,"Password doesn't match", Toast.LENGTH_LONG).show();
            passwordConfirmET.requestFocus();
            passwordConfirmET.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(firstName)) {

            Toast.makeText(this,"Enter a first name", Toast.LENGTH_LONG).show();
            firstNameET.requestFocus();
            firstNameET.setFocusable(true);
            focusView = firstNameET;
            cancel = true;
            return;
        }

        if (TextUtils.isEmpty(lastName)){
            Toast.makeText(this, "Enter a last name", Toast.LENGTH_SHORT).show();
            surnameET.requestFocus();
            surnameET.setFocusable(true);
            focusView = surnameET;
            cancel = true;
            return;
        }

        if (TextUtils.isEmpty(birthday)){
            Toast.makeText(this,"Enter Birthday Year", Toast.LENGTH_LONG).show();
            birthYearET.requestFocus();
            birthYearET.setFocusable(true);
            focusView = birthYearET;
            cancel = true;
            return;
        }

        if (TextUtils.isEmpty(countryCode)){
            Toast.makeText(this, "Select Country", Toast.LENGTH_SHORT).show();
            countryET.requestFocus();
            countryET.setFocusable(true);
            return;
        }


        if (TextUtils.isEmpty(postCode)){
            Toast.makeText(this,"Enter PostCode without using + character", Toast.LENGTH_LONG).show();
            postCodeET.requestFocus();
            postCodeET.setFocusable(true);
            focusView = postCodeET;
            cancel = true;
            return;
        }

        if(!switchOn)
        {
            Toast.makeText(this,"You must accept the terms first", Toast.LENGTH_LONG).show();
            return;
        }

        if (genderSG.getCheckedRadioButtonId() == -1)
        {
            Toast.makeText(this, "Select Gender", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            int selectedId = genderSG.getCheckedRadioButtonId();
            buttonSG1 = findViewById(selectedId);
            //Toast.makeText(getApplicationContext(), buttonSG1.getText().toString()+" is selected", Toast.LENGTH_SHORT).show();
        }


        if (cancel) {

            focusView.requestFocus();
        } else {


            final Register registerModel = new Register();
            registerModel.email = email;
            registerModel.password = password;
            registerModel.confirmPassword = passwordConfirm;
            registerModel.firstname = firstNameET.getText().toString();
            registerModel.surname = surnameET.getText().toString();

            registerModel.gender = "U"; //unknown
            final int checkedID = genderSG.getCheckedRadioButtonId();


            if (buttonSG1.getId() == checkedID) registerModel.gender = "M";
            if (buttonSG2.getId() == checkedID) registerModel.gender = "F";

            registerModel.birthYear = birthYearET.getText().toString();
            registerModel.country = countryET.getText().toString();
            registerModel.postCode = postCodeET.getText().toString();


            DM.getApi().getInvitedGrouping(registerModel.email, new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    String r = response.getBody().toString();
                    Log.d("HQ", "group invite response: " + r);
                    if (r.equals("true")) {

                        makeRegistrationRequest(registerModel);
                    } else {

                        dialog = new Dialog(Registration.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(true);
                        dialog.setContentView(R.layout.custom_dialogbox_register);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        pr = dialog.findViewById(R.id.progressbar);
                        btn_dialog = dialog.findViewById(R.id.btn_dialog);

                        listView = dialog.findViewById(R.id.list);
                        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                        DM.getApi().getClubNames(new Callback<ClubResponse>() {
                            @Override
                            public void success(ClubResponse clubResponse, Response response) {
                                clubNames = clubResponse.getData();
                                arrayAdapter.notifyDataSetChanged();
                                pr.setVisibility(View.GONE);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                pr.setVisibility(View.GONE);
                            }
                        });

                        arrayAdapter = new ArrayAdapter<ClubNames>(Registration.this, R.layout.club_one) {

                            @Override
                            public View getView(final int position, View convertView, ViewGroup parent) {

                                if (convertView == null) {
                                    convertView = LayoutInflater.from(Registration.this).inflate(R.layout.club_one, parent, false);
                                }

                                final ClubNames e = clubNames.get(position);

                                checkBox = convertView.findViewById(R.id.txt_title);
                                checkBox.setText(e.groupName);

                                return convertView;
                            }

                            @Override
                            public int getCount() {
                                return clubNames.size();
                            }
                        };
                        listView.setAdapter(arrayAdapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                // selected item
                                /*String selectedItem = ((TextView) view).getText().toString();
                                Toast.makeText(Registration.this, "" + selectedItem, Toast.LENGTH_SHORT).show();*/
                            }

                        });

                        btn_dialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                int selectedItem = listView.getCheckedItemPosition();
                                //Toast.makeText(Registration.this, "" + selectedItem, Toast.LENGTH_SHORT).show();
                                ClubNames e = clubNames.get(selectedItem);
                                String name = "Baseball NSW";

                                registerModel.groupId = e.groupId;
                                registerModel.groupName = name;
                                makeRegistrationRequest(registerModel);
                            }
                        });
                        dialog.show();

                        /*String name = "Baseball NSW";

                        registerModel.GroupName = name;
                        makeRegistrationRequest(registerModel);*/

                        SharedPreferences preferences = getSharedPreferences(MYPref, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("autoSave", passwordConfirmET.getText().toString());
                        editor.apply();

                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(Registration.this, "could not check groups", Toast.LENGTH_LONG).show();
                }
            });
        }
    }



    private void makeRegistrationRequest(final Register registerModel)
    {
        final ProgressDialog pd = DM.getPD(this, "Registering...");
        pd.show();

        DM.getApi().register(DM.getAuthString(), registerModel, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

                Toast.makeText(Registration.this, "Registration success!", Toast.LENGTH_LONG).show();
                DM.hideKeyboard(Registration.this);
                pd.dismiss();


                //triggers auto login
                Login.justRegisteredUsername = registerModel.email;
                Login.justRegisteredPassword = registerModel.password;

                finish();
            }

            @Override
            public void failure(RetrofitError error) {

                pd.dismiss();

                String s =  new String(((TypedByteArray)error.getResponse().getBody()).getBytes());
                Log.d("HQ",s);

                if(s.contains("already in use"))
                {

                    Toast.makeText(Registration.this, "User already registered, please try a different email", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(Registration.this, "Registration failed: "+error.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });

    }


    private void showAlert() {
        if (isSelected ==true){
            AlertDialog alertDialog = new AlertDialog.Builder(Registration.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("App needs to access the Camera.");
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ALLOW",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            ActivityCompat.requestPermissions(Registration.this,
                                    new String[]{android.Manifest.permission.CAMERA},
                                    MY_PERMISSIONS_REQUEST_CAMERA);
                            ActivityCompat.requestPermissions(Registration.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                            ActivityCompat.requestPermissions(Registration.this,
                                    new String[]{android.Manifest.permission.READ_PHONE_STATE},
                                    MY_PERMISSIONS_REQUEST_READ_PHONE);
                            ActivityCompat.requestPermissions(Registration.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);

                            saveToPreferences(Registration.this, ALLOW_KEY, true);
                        }
                    });
            alertDialog.show();
        }

        else {
            registerAction1();
        }
    }

    public static void saveToPreferences(Context context, String key,
                                         Boolean allowed) {
        SharedPreferences myPrefs = context.getSharedPreferences
                (CAMERA_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putBoolean(key, allowed);
        prefsEditor.apply();
    }

    private void termsAction()
    {

        WebVC.url = "http://www.sportsclubhq.com/standard-terms-of-use.html";
        WebVC.title = "Terms";
        Intent i = new Intent(this, WebVC.class);
        startActivity(i);
    }

    private void loginAction() //goes back to login screen
    {
        this.finish();
    }

    public boolean isOnline() {

        ConnectivityManager connec =
                (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {

            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {

            Toast.makeText(Registration.this, "Internet is not Connected! ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }
}
