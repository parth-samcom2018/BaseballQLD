package com.sportsclub.baseballqld;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sportsclub.baseballqld.models.Group;
import com.sportsclub.baseballqld.models.MediaAlbum;
import com.sportsclub.baseballqld.models.MediaAlbumResponse;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class MediaVC extends Fragment implements CropActivity.CropProtocol {

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_PICK_IMAGE = 2;

    boolean isSelected;

    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    public static final String ALLOW_KEY = "ALLOWED";
    public static final String CAMERA_PREF = "camera_pref";
    public Group group;
    private List<MediaAlbum> albums = new Vector<MediaAlbum>();

    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private ArrayAdapter listAdapter;
    private ImageView emptyIV;
    private ProgressDialog pd;
    File photoFile = null;

    public MediaVC() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.fragment_media_vc, container, false);

        emptyIV = v.findViewById(R.id.empty);

        listView = v.findViewById(R.id.list);
        listView.setDivider(null);


        listAdapter = new ArrayAdapter(this.getActivity(), R.layout.media_cell) {


            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if(convertView == null)
                {
                    convertView = LayoutInflater.from(MediaVC.this.getActivity()).inflate(R.layout.media_cell, parent, false);

                }

                final MediaAlbum album = albums.get(position);

                final TextView tv_media = convertView.findViewById(R.id.tv_media);
                tv_media.setVisibility(View.VISIBLE);

                final ProgressBar progressBar = convertView.findViewById(R.id.progressBar_media);
                progressBar.setVisibility(View.VISIBLE);

                final ImageView showiv = convertView.findViewById(R.id.iv);

                if (showiv!=null || album.mediaModels!=null){
                    Picasso.Builder builder = new Picasso.Builder(MediaVC.this.getActivity());
                    builder.listener(new Picasso.Listener() {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                            Log.d("hq", "uri: " + uri.getPath());
                            exception.printStackTrace();
                        }
                    });
                    Picasso p = builder.build();

                    if (showiv==null || album.mediaModels==null){
                        showiv.setImageResource(R.drawable.icon);
                        showiv.setClickable(false);

                    }
                    else {
                        p.load(album.coverImage)//.networkPolicy(NetworkPolicy.NO_CACHE)
                                .placeholder(R.drawable.icon).into(showiv);

                        p.load(album.coverImage).transform(new RoundedCornersTransform()).into(showiv, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                if (album.mediaModels == null) {
                                    progressBar.setVisibility(View.GONE);
                                    tv_media.setVisibility(View.GONE);
                                }

                                progressBar.setVisibility(View.GONE);

                                tv_media.setVisibility(View.GONE);

                                if (album.mediaModels == null) {

                                    progressBar.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onError() {
                                tv_media.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);

                                showiv.setImageResource(R.drawable.splashlogo);
                                showiv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            }
                        });

                        showiv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (showiv==null || album.mediaModels==null || album.mediaModels.size()==0 || showiv.equals("0")){
                                    Toast.makeText(MediaVC.this.getActivity(), "This album is empty! It will fill up only when you select album while upload any photos!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Log.d("hq","slider click!");
                                MediaDetailVC.mediaAlbum = album;
                                Intent i = new Intent(getActivity(), MediaDetailVC.class);
                                startActivity(i);

                            }
                        });
                    }
                }

                TextView firstTV = convertView.findViewById(R.id.firstTV);
                firstTV.setText(album.name+" \n" +album.mediaModels.size()+" photos");
                firstTV.setTextColor(getResources().getColor(R.color.white));
                if (album.mediaModels.size()==0){
                    firstTV.setTextColor(getResources().getColor(R.color.black));
                    return convertView;
                }
                Button flagButton = convertView.findViewById(R.id.flagButton);
                flagButton.setOnClickListener(DM.getFlagOnClickListener(MediaVC.this.getActivity()));

                return convertView;
            }

            @Override
            public int getCount() {
                return albums.size();
            }
        };
        listView.setAdapter(listAdapter);

        refreshLayout = v.findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        View cameraButton = v.findViewById(R.id.cameraIV);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (checkpermission()==true){
                    cameraAction();
                }
                checkpermission();
            }
        });

        View uploadButton = v.findViewById(R.id.uploadIV);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadAction();
            }
        });


        return v;
    }


    private boolean checkpermission() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(MediaVC.this.getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        int locationPermission = ContextCompat.checkSelfPermission(MediaVC.this.getActivity(),
                Manifest.permission.CAMERA);
        int permissionExternal = ContextCompat.checkSelfPermission(MediaVC.this.getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionExternal != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }


        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(MediaVC.this.getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MY_PERMISSIONS_REQUEST_CAMERA);
            cameraAction();
            return false;
        }
        return true;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {


        inflater.inflate(R.menu.create_album_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(item.getItemId() == R.id.createAlbum) this.createAlbumAction();

        return super.onOptionsItemSelected(item);
    }

    private void createAlbumAction() {

        final AlertDialog.Builder alert = new AlertDialog.Builder(MediaVC.this.getActivity());

        LinearLayout lila1 = new LinearLayout(MediaVC.this.getActivity());
        lila1.setOrientation(LinearLayout.VERTICAL);
        final EditText nameET = new EditText(MediaVC.this.getActivity());
        nameET.setHint("Album Name");
        final EditText descET = new EditText(MediaVC.this.getActivity());
        descET.setVisibility(View.GONE);
        descET.setHint("Album Description");
        lila1.addView(nameET);
        int pad = (int)getResources().getDimension(R.dimen.small_pad);
        lila1.setPadding(pad,pad,pad,pad);
        alert.setView(lila1);

        alert.setTitle("Create Album");


        alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int whichButton) {
                String name = nameET.getText().toString();

                if(name.length() == 0 || name == null)
                {
                    Toast.makeText(MediaVC.this.getActivity(),"Enter a name",Toast.LENGTH_LONG).show();
                    DM.hideKeyboard(MediaVC.this.getActivity());
                    return;
                }


                pd = DM.getPD(MediaVC.this.getActivity(),"Loading Creating Album..");
                pd.show();

                DM.getApi().postMediaAlbum(DM.getAuthString(), name,  group.groupId, new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        Toast.makeText(MediaVC.this.getActivity(),"Album Created!",Toast.LENGTH_LONG).show();
                        pd.dismiss();
                        dialog.dismiss();
                        DM.hideKeyboard(MediaVC.this.getActivity());
                        loadData();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(MediaVC.this.getActivity(),"Could not create album: "+error.getMessage(),Toast.LENGTH_LONG).show();
                        pd.dismiss();
                        dialog.dismiss();
                        DM.hideKeyboard(MediaVC.this.getActivity());
                        loadData();
                    }
                });

            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DM.hideKeyboard(MediaVC.this.getActivity());
                dialog.dismiss();
            }
        });

        alert.show();
    }

    private void cameraAction() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
        saveToPreferences(MediaVC.this.getActivity(), ALLOW_KEY, true);
    }

    @Override
    public void onRequestPermissionsResult
            (int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                for (int i = 0, len = permissions.length; i < len; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        boolean showRationale =
                                ActivityCompat.shouldShowRequestPermissionRationale
                                        (MediaVC.this.getActivity(), permission);
                        if (showRationale) {
                            checkpermission();
                        } else if (!showRationale) {

                            saveToPreferences(MediaVC.this.getActivity(), ALLOW_KEY, true);

                        }
                    }
                }
            }

        }
    }



    public static void saveToPreferences(Context context, String key,
                                         Boolean allowed) {
        SharedPreferences myPrefs = context.getSharedPreferences
                (CAMERA_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putBoolean(key, allowed);
        prefsEditor.commit();
    }



    private void uploadAction()
    {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
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

            Log.d("hipcook", "request take photo");
            try {
                b= MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), capturedImageUri);
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
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToNext();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imgDecodableString = cursor.getString(columnIndex);
            cursor.close();

            b = DM.decodeSampledBitmapFromFile(imgDecodableString, 640,640);
            Log.d("hipcook", "I now have a bitmap:" + b.getWidth());



        } else {
            Toast.makeText(this.getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
        }

        //if found a bitmap do the crop fun
        if(b != null)
        {
            CropActivity.del = this;
            bitmapToCrop = b;
            Intent i = new Intent(this.getActivity(), CropActivity.class);
            startActivity(i);
        }
    }



    @Override
    public void didCropBitmap(final Bitmap b) {
        // imageView.setImageBitmap(b);

        if(albums.size() == 0)
        {
            Toast.makeText(this.getActivity(),"No albums loaded yet",Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder d = new AlertDialog.Builder(this.getActivity());
        d.setTitle("Upload Image To Folder?");

        View v = this.getActivity().getLayoutInflater().inflate(R.layout.uploadphoto_dialog,null);

        ImageView iv = v.findViewById(R.id.imageView);
        iv.setImageBitmap(b);

        final NumberPicker picker = v.findViewById(R.id.numberPicker);
        picker.setMinValue(0);
        picker.setMaxValue(albums.size() - 1);
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); //stops editing...
        String[] sa = new String[albums.size()];
        for(int i=0; i<albums.size(); i++)
        {
            sa[i] = albums.get(i).name;
            Log.d("hq","album id:"+albums.get(i).mediaAlbumId);
        }
        picker.setDisplayedValues(sa);

        d.setView(v);
        d.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int index = picker.getValue();
                uploadBitmap(b,albums.get(index).mediaAlbumId);
            }
        });
        d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        d.show();

    }

    private void uploadBitmap(final Bitmap bitmap, int albumID)
    {
        final ProgressDialog pd = DM.getPD(this.getActivity(), "Uploading Image...");
        pd.show();

        Log.d("hq","uploading bitmap to server, albumID="+albumID);
        String fileName = "photo.png";

        File f = new File(this.getContext().getCacheDir(), fileName);
        try {
            f.createNewFile();
            //Convert bitmap to byte array

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();


            TypedFile typedImage = new TypedFile("application/octet-stream", f);
            Log.d("HQ","Uploading image "+typedImage.file().length());

            DM.getApi().postImageToAlbum(DM.getAuthString(), albumID, typedImage, new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    Toast.makeText(getActivity(),"Imaged posted to album",Toast.LENGTH_LONG).show();
                    loadData();
                    pd.hide();

                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(getActivity(),"Imaged posting failed: "+error.getMessage(),Toast.LENGTH_LONG).show();
                    pd.hide();
                    loadData();
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
            Log.d("hq","file exception");
            pd.hide();
        }



    }

    private Bitmap bitmapToCrop;
    @Override
    public Bitmap getInputBitmap() {
        return bitmapToCrop;
    }


    private boolean initialLoaded = false;
    public void loadIfUnloaded(){
        if(initialLoaded == false) loadData();
    }
    private void loadData()
    {
        initialLoaded = true;

        final ProgressDialog pd = DM.getPD(this.getActivity(),"Loading Media Albums...");
        pd.show();


        if(group != null) DM.getApi().getGroupingMediaAlbums(DM.getAuthString(), group.groupId, new Callback<MediaAlbumResponse>() {
            @Override
            public void success(MediaAlbumResponse mediaAlbums, Response response) {
                albums = mediaAlbums.getData();
                for(MediaAlbum a : albums)
                {
                    a.sortMediaAlbumsByDate();
                }

                listAdapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
                pd.dismiss();

                if(mediaAlbums.getData().size()==0) emptyIV.setVisibility(View.VISIBLE);
                else emptyIV.setVisibility(View.GONE);

            }

            @Override
            public void failure(RetrofitError error) {
                pd.dismiss();
                refreshLayout.setRefreshing(false);

            }
        });

    }

    private class RoundedCornersTransform implements Transformation{
        public  Bitmap getRoundedCornerBitmap(Bitmap bitmap, float r, float v, float r1, float v1) {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);
            final float roundPx = 12;

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            return output;
        }

        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            float r = size / 4f;

            Bitmap roundedBitmap = getRoundedCornerBitmap(squaredBitmap, r, r, r, r);

            squaredBitmap.recycle();

            return roundedBitmap;
        }

        @Override
        public String key() {
            return "rounded_corners";
        }
    }
}
