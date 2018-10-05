package com.sportsclub.baseballqld;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sportsclub.baseballqld.models.Group;
import com.sportsclub.baseballqld.models.Profile;
import com.sportsclub.baseballqld.models.Token;
import com.squareup.picasso.Picasso;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;


public class MainTabbing extends BaseVC {

    private int REQUEST_TAKE_PHOTO = 1;
    private int REQUEST_PICK_IMAGE = 2;
    private String TAG = "Register";
    private Context context = this;

    Group g;
    Token token;

    public static Group group;

    private NoticeBoardVCN noticeBoardVCN;
    private NoticeboardFragment noticeBoardVC;
    private GroupFragment groupsVC;
    private EventsFragment eventsVC;
    private ProfileFragment profileVC;
    private FrameLayout frmL;
    private ImageButton ib_edit;
    private CircleImageView cp;

    private String[] titles = {"Noticeboard", "Groups", "Events", "Profile"};

    private TextView tv_title,tv_create,mTitle,tvend,tv_left;
    private LinearLayout ll_edit;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;


    private int[] tabIcons = {
            R.drawable.noticeboard_empty,
            R.drawable.groups_empty,
            R.drawable.events_empty,
            R.drawable.profile_empty
    };

    ProgressDialog pd;

    boolean isDoubleClick = false;
    private int MY_PERMISSIONS_REQUEST_CAMERA = 2000;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_main_tabbing);

        Toolbar toolbar = findViewById(R.id.toolbar_top);
        mTitle = toolbar.findViewById(R.id.toolbar_title);
        tvend = toolbar.findViewById(R.id.tv_end);
        tv_left = toolbar.findViewById(R.id.tv_left);

        setSupportActionBar(toolbar);
        mTitle.setText("Noticeboard");

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        cp = findViewById(R.id.profilePic1);
        ib_edit = findViewById(R.id.ib_edit);
        frmL = findViewById(R.id.frm);
        frmL.setVisibility(View.GONE);

        tv_create = findViewById(R.id.tv_create);
        ll_edit = findViewById(R.id.ll_edit);


        mSectionsPagerAdapter = new SectionsPagerAdapter(this.getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.viewpager);


        mViewPager.setAdapter(mSectionsPagerAdapter);


        ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                setTitle(titles[position]);


                switch (position)
                {
                    case 0:
                        tvend.setVisibility(View.GONE);
                        tv_left.setVisibility(View.GONE);
                        frmL.setVisibility(View.GONE);
                        mTitle.setText("Noticeboard");
                        noticeBoardVC.loadIfUnloaded();
                        break;
                    case 1:
                        try
                        {
                            tvend.setVisibility(View.GONE);
                            tv_left.setVisibility(View.GONE);
                            frmL.setVisibility(View.GONE);
                            mTitle.setText("Groups");
                            //ll_edit.setVisibility(View.GONE);
                            groupsVC.loadIfUnloaded();
                        }
                        catch (NullPointerException e){
                            e.printStackTrace();
                            Log.e(TAG, "onPageSelected: "+e.getMessage());
                        }

                        break;
                    case 2:

                        try{
                            tvend.setVisibility(View.VISIBLE);
                            tv_left.setVisibility(View.GONE);
                            frmL.setVisibility(View.GONE);
                            mTitle.setText("Events");
                            tvend.setText("CREATE");
                            tvend.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    newEventAction();
                                }
                            });
                            eventsVC.loadIfUnloaded();
                            break;
                        }
                        catch (NullPointerException e){
                            e.printStackTrace();
                            Log.e(TAG, "onPageSelected: "+e.getMessage());
                        }

                    case 3:
                        tvend.setVisibility(View.GONE);
                        tv_left.setVisibility(View.VISIBLE);
                        tv_left.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onChangePassword();
                            }
                        });
                        frmL.setVisibility(View.VISIBLE);
                        mTitle.setText("Profile");
                        ib_edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (checkpermission()==true){
                                    chooseImage();
                                }
                                checkpermission();
                            }
                        });
                        break;
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };

        mViewPager.addOnPageChangeListener(pageListener);



        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        //setUpIcon();
        setupTabIcons();

        this.noticeBoardVC = (NoticeboardFragment) NoticeboardFragment.instantiate(this, NoticeboardFragment.class.getName());
        this.noticeBoardVC.group = group;

        this.groupsVC = (GroupFragment) GroupFragment.instantiate(this, GroupFragment.class.getName());
        //this.groupsVC.group = group;

        this.eventsVC = (EventsFragment) EventsFragment.instantiate(this, EventsFragment.class.getName());
        //this.eventsVC.event = events;

        this.profileVC = (ProfileFragment) ProfileFragment.instantiate(this, ProfileFragment.class.getName());
        //this.profileVC.group = group;

        //this.setTitle(group.groupName);

        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        //timestamp
        //Toast.makeText(context, "" + ts, Toast.LENGTH_SHORT).show();

        DM.getApi().getMemberDetailing(DM.getAuthString(), new Callback<Profile>() {
            @Override
            public void success(Profile profilev2, Response response) {

                //copy attributes over
                DM.member.copyAttributesFromDetails(profilev2.getData());
                //modelToView();
                Picasso.with (MainTabbing.this)
                        .load(DM.member.profileUrl)
                        //.networkPolicy(NetworkPolicy.NO_CACHE)
                        .placeholder(R.drawable.splashlogo)
                        .into(cp);
            }

            @Override
            public void failure(RetrofitError error) {

                Toast.makeText(MainTabbing.this,
                        "Could not load member details:"+error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onChangePassword() {
        Intent i = new Intent(MainTabbing.this,ChangePassowrd.class);
        startActivity(i);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);

        Resources res1 = getResources();
        int primaryColor = res1.getColor(R.color.tab_background_unselected);
        int second = res1.getColor(R.color.tab_background_selected);


        tabLayout.getTabAt(0).getIcon().setColorFilter(second, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(primaryColor, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().setColorFilter(primaryColor, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(3).getIcon().setColorFilter(primaryColor, PorterDuff.Mode.SRC_IN);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Resources res = getResources();
                int primaryColor1 = res.getColor(R.color.tab_background_selected);
                tab.getIcon().setColorFilter(primaryColor1, PorterDuff.Mode.SRC_IN);
                /*if (tab.isSelected()){
                    tabLayout.getTabAt(0).setIcon(tabsIcons[0]);
                    return;
                }*/

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //for removing the color of first icon when switched to next tab
                tabLayout.getTabAt(0).getIcon().clearColorFilter();
                tab.getIcon().clearColorFilter();

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });}

    private void UpdateDetails() {
        //view to model
        pd = DM.getPD(this, "Updating Profile...");
        pd.show();


        DM.getApi().putMember(DM.getAuthString(), DM.member, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(MainTabbing.this, "User Update success!", Toast.LENGTH_LONG).show();
                DM.hideKeyboard(MainTabbing.this);
                pd.dismiss();

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(MainTabbing.this, "could not update", Toast.LENGTH_LONG).show();
                DM.hideKeyboard(MainTabbing.this);
                pd.dismiss();

            }
        });
    }




    @SuppressLint("ResourceAsColor")
    private void setUpIcon() {

        tabLayout.getTabAt(0).setIcon(R.drawable.noticeboard_hover);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);

        Resources res1 = getResources();
        int primaryColor = res1.getColor(R.color.tab_background_unselected);


        tabLayout.getTabAt(0).setIcon(R.drawable.noticeboard_hover);
        tabLayout.getTabAt(1).getIcon().setColorFilter(primaryColor, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().setColorFilter(primaryColor, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(3).getIcon().setColorFilter(primaryColor, PorterDuff.Mode.SRC_IN);

        tabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(mViewPager)
                {

                    @Override
                    public void onTabSelected(TabLayout.Tab tab)
                    {

                        Resources res = getResources();
                        int primaryColor1 = res.getColor(R.color.tab_background_selected);
                        tab.getIcon().setColorFilter(primaryColor1, PorterDuff.Mode.SRC_IN);

                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab)
                    {


                        if (tabLayout.getSelectedTabPosition()==0){
                            tabLayout.getTabAt(0).setIcon(R.drawable.noticeboard_empty);
                            return;
                        }

                        tabLayout.getTabAt(0).getIcon().clearColorFilter();
                        tab.getIcon().clearColorFilter();


                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab)
                    {
                        super.onTabReselected(tab);
                    }
                }
        );


    }



    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (position == 0){

                return noticeBoardVC;
            }
            if (position == 1){
                return groupsVC;
            }
            if (position == 2){
                return eventsVC;
            }
            if (position == 3){
                return profileVC;
            }

            else return noticeBoardVC;


        }


        @Override
        public int getCount() {
            // tab count

            return 4;
        }



        @Override
        public CharSequence getPageTitle(int position) {

            return titles[position];
        }

        @Override
        public int getItemPosition(Object object) {

            //return getItemPosition(object);
            return super.getItemPosition(object);
        }
    }


    @Override
    public void onBackPressed() {

        if (isDoubleClick) {

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            System.exit(0);
            return;
        }

        this.isDoubleClick = true;
        Toast.makeText(this, "Click again to Exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                isDoubleClick=false;
            }
        }, 2000);
    }


    private boolean checkpermission() {

        int permissionStorage = ContextCompat.checkSelfPermission(MainTabbing.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int permissionExternal = ContextCompat.checkSelfPermission(MainTabbing.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionExternal != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(MainTabbing.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MY_PERMISSIONS_REQUEST_CAMERA);
            return false;
        }
        return true;
    }

    private void chooseImage()
    {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE);
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
                b= MediaStore.Images.Media.getBitmap(MainTabbing.this.getContentResolver(), capturedImageUri);
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

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };


            Cursor cursor = MainTabbing.this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imgDecodableString = cursor.getString(columnIndex);
            cursor.close();

            try{
                b = DM.decodeSampledBitmapFromFile(imgDecodableString, 640,640);
                Log.d("hipcook", "I now have a bitmap:" + b.getWidth());


            }
            catch (Exception e){
                e.printStackTrace();
            }


        } else {
            Toast.makeText(MainTabbing.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }

        if(b != null)
        {
            pd = DM.getPD(MainTabbing.this, "Updating Profile Image...");
            pd.show();
            cp.setImageBitmap(b);


            String fileName = "photo.png";
            File f = new File(this.context.getCacheDir(), fileName);
            try {
                f.createNewFile();


                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.PNG, 0 , bos);
                byte[] bitmapdata = bos.toByteArray();


                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();


                TypedFile typedImage = new TypedFile("image/png", f);
                DM.getApi().postProfileImage(DM.getAuthString(), typedImage, new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        Toast.makeText(MainTabbing.this,"Profile Image Updated",Toast.LENGTH_LONG).show();
                        pd.hide();

                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Toast.makeText(MainTabbing.this,"Could not update profile image: "+error.getMessage(),Toast.LENGTH_LONG).show();
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

    private void newEventAction()
    {
        Log.d("hq","click!");
        Intent i = new Intent(MainTabbing.this,EventFormVC.class);
        startActivity(i);
    }

}