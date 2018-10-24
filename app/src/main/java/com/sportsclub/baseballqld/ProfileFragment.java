package com.sportsclub.baseballqld;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.sportsclub.baseballqld.models.Profile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import de.hdodenhof.circleimageview.CircleImageView;
import info.hoang8f.android.segmented.SegmentedGroup;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class ProfileFragment extends Fragment {
    

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_PICK_IMAGE = 2;

    //VIEWS
    private CircleImageView profileIV;
    private Button chooseImageButton;

    private EditText emailET;
    private EditText firstNameET;
    private EditText surnameET;
    private SegmentedGroup genderSG;
    private RadioButton buttonSG1;
    private RadioButton buttonSG2;
    private EditText birthYearET;
    private EditText countryET;
    private EditText postCodeET;

    private TextView usernameTV;
    private Button logoutButton;
    private LinearLayout ll_country;
    private int MY_PERMISSIONS_REQUEST_CAMERA = 2000;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {


        inflater.inflate(R.menu.update_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //theres only create
        if(item.getItemId() == R.id.update)this.updateAction();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        isOnline();

        emailET = v.findViewById(R.id.email);
        
        firstNameET = v.findViewById(R.id.firstNameET);
        surnameET = v.findViewById(R.id.surnameET);

        genderSG = v.findViewById(R.id.genderSG);
        buttonSG1 = v.findViewById(R.id.buttonsg1);
        buttonSG2 = v.findViewById(R.id.buttonsg2);

        birthYearET = v.findViewById(R.id.birthYearET);

        birthYearET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                birthYearET.setFocusableInTouchMode(true);
                birthYearET.setFocusable(true);
                birthYearET.requestFocus();
                InputMethodManager bd = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                bd.showSoftInput(birthYearET, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        countryET = v.findViewById(R.id.countryET);
      
        ll_country = v.findViewById(R.id.ll_country);

        ll_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseCountryAction();
            }
        });

       

        postCodeET = v.findViewById(R.id.postCodeET);

        postCodeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postCodeET.setFocusable(true);
                postCodeET.requestFocus();
               
            }
        });

        usernameTV = v.findViewById(R.id.usernameTV);

        logoutButton = v.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutAction();
            }
        });


        DM.getApi().getMemberDetailing(DM.getAuthString(), new Callback<Profile>() {
            @Override
            public void success(Profile profilev2, Response response) {
                if (response.getStatus()==200){

                    //copy attributes over
                    DM.member.copyAttributesFromDetails(profilev2.getData());
                    modelToView();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(ProfileFragment.this.getContext(),
                        "Could not load member details:"+error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });


        return v;
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

        AlertDialog.Builder b = new AlertDialog.Builder(this.getContext());
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

    private void modelToView()
    {


        emailET.setText(DM.member.username);
        //emailET.setEnabled(false);
        firstNameET.setText(DM.member.firstName);
        surnameET.setText(DM.member.lastName);
        countryET.setEnabled(false);

        if(DM.member.gender != null) {
            Log.d("hq","gender: "+DM.member.gender);
            if (DM.member.gender.equalsIgnoreCase("M")) {
                //  buttonSG1.setSelected(true);
                genderSG.check(buttonSG1.getId());
            }
            if (DM.member.gender.equalsIgnoreCase("F")) {
                // buttonSG2.setSelected(true);
                genderSG.check(buttonSG2.getId());
            }
        }

        birthYearET.setText(DM.member.birthYear);
        //birthYearET.setEnabled(false);
        countryET.setText(DM.member.country);
        postCodeET.setText(DM.member.postCode);

        usernameTV.setText(DM.member.username);

        /*Picasso.with (ProfileFragment.this.getActivity())
                .load(DM.member.profileUrl)
                .placeholder (R.drawable.logo_167)
                .into(profileIV);*/
    }

    private void logoutAction()
    {
        unregisterForPush();
    }

    private boolean initialLoaded = false;

    public void loadIfUnloaded() {
        if (initialLoaded == false) loadData();
    }

    private void loadData() {
        final ProgressDialog pd = DM.getPD(this.getActivity(), "Loading Log out user...");
        pd.show();

        DM.getApi().logoutUser(DM.getAuthString(),new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(getActivity(), "Successfully Logout", Toast.LENGTH_SHORT).show();
                pd.dismiss();

                DM.member = null;

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ProfileFragment.this.getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("HQUsername");
                editor.remove("HQToken");
                //editor.remove("DeviceId");
                editor.apply();

                getActivity().finish();

                Intent i = new Intent(ProfileFragment.this.getActivity(), Login.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), "Successfully Logout" + error.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();

            }
        });
    }


    private void unregisterForPush()
    {

        loadData();

        /*DM.getApi().logoutUser(DM.getAuthString(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {


                Toast.makeText(getActivity(), "Successfully Remove Push notification", Toast.LENGTH_SHORT).show();
                Log.d("push", response2.toString());

                getActivity().finish();

                Intent i = new Intent(ProfileFragment.this.getActivity(), Login.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), "Something is wrong:" + error, Toast.LENGTH_SHORT).show();
                Log.d("push" , error.toString());
            }
        });*/

    }

    private void updateAction()
    {
        //view to model

        DM.member.firstName = firstNameET.getText().toString();
        DM.member.lastName = surnameET.getText().toString();

        DM.member.gender = "U"; //unknown


        int checkedID = genderSG.getCheckedRadioButtonId();
        if(buttonSG1.getId() == checkedID) DM.member.gender = "M";
        if(buttonSG2.getId() == checkedID) DM.member.gender = "F";
        // DM.member.birthYear = birthYearET.getText().toString();


        final ProgressDialog pd = DM.getPD(this.getActivity(), "Updating Profile...");
        pd.show();



        DM.member.birthYear = birthYearET.getText().toString();
        DM.member.country = countryET.getText().toString();
        DM.member.postCode = postCodeET.getText().toString();

        DM.getApi().putMember(DM.getAuthString(), DM.member, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(ProfileFragment.this.getActivity(), "User Update success!", Toast.LENGTH_LONG).show();
                DM.hideKeyboard(ProfileFragment.this.getActivity());
                pd.dismiss();

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(ProfileFragment.this.getActivity(), "could not update", Toast.LENGTH_LONG).show();
                DM.hideKeyboard(ProfileFragment.this.getActivity());
                pd.dismiss();

            }
        });



    }


    private Uri capturedImageUri;
    String mCurrentPhotoPath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();

        capturedImageUri = Uri.fromFile(image);
        return image;
    }


    private String imgDecodableString;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Bitmap b = null;

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            // Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");

            Log.d("hq", "request take photo");
            try {
                b= MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), capturedImageUri);
                Log.d("hipcook", "I now have a photo bitmap:" + b.getWidth());
                float scaleFactor = 640f/ b.getWidth();
                b = DM.createScaledBitmap(b,scaleFactor);
                Log.d("hipcook", "I now have a scaled photo bitmap:" + b.getWidth());


            } catch (IOException e) {
                e.printStackTrace();
                Log.d("hipcook", "bitmap exception");
            }
        }
        else if (requestCode == REQUEST_PICK_IMAGE &&
                resultCode == Activity.RESULT_OK &&
                null != data) {
            // Get the Image from data

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            // Get the cursor
            Cursor cursor = this.getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imgDecodableString = cursor.getString(columnIndex);
            cursor.close();

            b = DM.decodeSampledBitmapFromFile(imgDecodableString, 640,640);
            Log.d("hipcook", "I now have a bitmap:" + b.getWidth());



        } else {
            Toast.makeText(this.getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
        }

        //if found a bitmap, upload or save in registration model
        if(b != null)
        {
            final ProgressDialog pd = DM.getPD(this.getActivity(), "Updating Profile Image...");
            pd.show();
            profileIV.setImageBitmap(b);


            String fileName = "photo.png";
            File f = new File(this.getContext().getCacheDir(), fileName);
            try {
                f.createNewFile();
                //Convert bitmap to byte array

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

                //write the bytes in file
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();


                TypedFile typedImage = new TypedFile("image/png", f);
                DM.getApi().postProfileImage(DM.getAuthString(), typedImage, new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        Toast.makeText(ProfileFragment.this.getContext(),"Profile Image Updated",Toast.LENGTH_LONG).show();
                        pd.hide();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Toast.makeText(ProfileFragment.this.getContext(),"Could not update profile image: "+error.getMessage(),Toast.LENGTH_LONG).show();
                        pd.hide();
                    }
                });
            }
            catch (IOException e) {
                e.printStackTrace();
                Log.d("hq","file exception");
            }

        }
    }

    public boolean isOnline() {
        ConnectivityManager connec =
                (ConnectivityManager)getActivity().getSystemService(getActivity().getBaseContext().CONNECTIVITY_SERVICE);

        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {

            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {

            Toast.makeText(ProfileFragment.this.getActivity(), "Internet is not Connected! ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }



}
