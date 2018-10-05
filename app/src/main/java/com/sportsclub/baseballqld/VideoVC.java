package com.sportsclub.baseballqld;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sportsclub.baseballqld.models.Group;
import com.sportsclub.baseballqld.models.VideoAlbum;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import java.util.Vector;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.media.ThumbnailUtils.createVideoThumbnail;

public class VideoVC extends Fragment {

    private static final String TAG = "QLD";
    private static final int REQUEST_CODE = 101;
    static final int REQUEST_PICK_IMAGE = 2;

    boolean isSelected;

    public Group group;
    //private List<MediaAlbum> albums = new Vector<MediaAlbum>();
    private List<VideoAlbum> videoalbums = new Vector<VideoAlbum>();

    UploadTask uploadTask;
    private Uri videouri;
    private StorageReference videoref;
    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private ArrayAdapter listAdapter;
    private ImageView emptyIV;
    private ProgressDialog pd;
    private ProgressBar progressBar;
    VideoView showvideo;
    File photoFile = null;


    public VideoVC() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.fragment_video_vc, container, false);

        emptyIV = v.findViewById(R.id.empty);

        listView = v.findViewById(R.id.list);
        listView.setDivider(null);


        loadData();

        listAdapter = new ArrayAdapter(this.getActivity(), R.layout.video_cell) {


            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    convertView = LayoutInflater.from(VideoVC.this.getActivity()).inflate(R.layout.video_cell, parent, false);

                }

                final VideoAlbum album = videoalbums.get(position);

                final TextView tv_media = convertView.findViewById(R.id.tv_video);
                tv_media.setVisibility(View.VISIBLE);

                progressBar = convertView.findViewById(R.id.progressBar_video);
                progressBar.setVisibility(View.VISIBLE);

                showvideo = convertView.findViewById(R.id.videoview_main);

                if (showvideo != null || album.videoModels != null) {
                    Picasso.Builder builder = new Picasso.Builder(VideoVC.this.getActivity());
                    builder.listener(new Picasso.Listener() {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                            Log.d("hq", "uri: " + uri.getPath());
                            exception.printStackTrace();
                        }
                    });
                    Picasso p = builder.build();

                    if (showvideo == null || album.videoModels == null) {
                        //showiv.setImageResource(R.drawable.icon);
                        //showiv.setClickable(false);
                        showvideo.setBackgroundResource(R.drawable.icon);
                        showvideo.setClickable(false);
                    } else {

                       showvideo.setVideoURI(videouri);


                        /*p.load(album.coverImage)//.networkPolicy(NetworkPolicy.NO_CACHE)
                                .addRequestHandler(showvideo)
                                .build();*/


                       /* Picasso.with(getActivity())
                                .load(Uri.parse(String.valueOf(videouri)))
                                .placeholder(R.drawable.icon)
                                .into((Target) showvideo);*/



                        /*p.load(album.coverImage).transform(new RoundedCornersTransform()).into(showvideo, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                if (album.videoModels == null) {
                                    progressBar.setVisibility(View.GONE);
                                    tv_media.setVisibility(View.GONE);
                                }

                                //showvideo.setScaleType(ImageView.ScaleType.FIT_XY);

                                progressBar.setVisibility(View.GONE);

                                tv_media.setVisibility(View.GONE);

                                if (album.videoModels == null) {

                                    progressBar.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onError() {
                                tv_media.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);

                                //showiv.setImageResource(R.drawable.splashlogo);
                                //showiv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                showvideo.setBackgroundResource(R.drawable.splashlogo);
                            }
                        });*/

                        showvideo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (showvideo == null || album.videoModels == null || album.videoModels.size() == 0) {
                                    Toast.makeText(VideoVC.this.getActivity(), "This album is empty! It will fill up only when you select album while upload any photos!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Log.d("hq", "slider click!");
                                /*MediaDetailVC.mediaAlbum = album;
                                Intent i = new Intent(getActivity(), MediaDetailVC.class);
                                startActivity(i);*/

                                VideoDetailsVC.mediaAlbum = album;
                                Intent i = new Intent(getActivity(), VideoDetailsVC.class);
                                startActivity(i);
                            }
                        });
                    }
                }

                TextView firstTV = convertView.findViewById(R.id.firstTV);
                firstTV.setText(album.name + " \n" + album.videoModels.size() + " videos");
                firstTV.setTextColor(getResources().getColor(R.color.white));
                if (album.videoModels.size() == 0) {
                    firstTV.setTextColor(getResources().getColor(R.color.black));
                    return convertView;
                }
                Button flagButton = convertView.findViewById(R.id.flagButton);
                flagButton.setOnClickListener(DM.getFlagOnClickListener(VideoVC.this.getActivity()));


                return convertView;
            }

            @Override
            public int getCount() {
                return videoalbums.size();
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

                cameraAction();
            }
        });

        View uploadButton = v.findViewById(R.id.uploadIV);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


        return v;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {


        inflater.inflate(R.menu.create_album_video_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == R.id.createAlbum) this.createVideoAlbumAction();

        return super.onOptionsItemSelected(item);
    }

    private void createVideoAlbumAction() {

        final AlertDialog.Builder alert = new AlertDialog.Builder(VideoVC.this.getActivity());

        LinearLayout lila1 = new LinearLayout(VideoVC.this.getActivity());
        lila1.setOrientation(LinearLayout.VERTICAL);
        final EditText nameET = new EditText(VideoVC.this.getActivity());
        nameET.setHint("Video Album Name");
        final EditText descET = new EditText(VideoVC.this.getActivity());
        descET.setVisibility(View.GONE);
        descET.setHint("Video Album Description");
        lila1.addView(nameET);
        int pad = (int) getResources().getDimension(R.dimen.small_pad);
        lila1.setPadding(pad, pad, pad, pad);
        alert.setView(lila1);

        alert.setTitle("Create Video Album");


        alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int whichButton) {
                String name = nameET.getText().toString();

                if (name.length() == 0 || name == null) {
                    Toast.makeText(VideoVC.this.getActivity(), "Enter a name", Toast.LENGTH_LONG).show();
                    DM.hideKeyboard(VideoVC.this.getActivity());
                    return;
                }


                pd = DM.getPD(VideoVC.this.getActivity(), "Loading Creating Video Album..");
                pd.show();

                DM.getApi().postVideoAlbum(DM.getAuthString(), name, group.groupId, "video", new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        Toast.makeText(VideoVC.this.getActivity(), "Video Album Created!", Toast.LENGTH_LONG).show();
                        pd.dismiss();
                        dialog.dismiss();
                        DM.hideKeyboard(VideoVC.this.getActivity());
                        loadData();
                        Log.d("onSuccess", "response" + response);
                        Log.d("onSuccess", "response" + response2);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(VideoVC.this.getActivity(), "Could not create video album: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        pd.dismiss();
                        dialog.dismiss();
                        DM.hideKeyboard(VideoVC.this.getActivity());
                        loadData();

                        Log.d("onFailed", "response" + error);
                    }
                });

            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DM.hideKeyboard(VideoVC.this.getActivity());
                dialog.dismiss();
            }
        });

        alert.show();
    }

    private void cameraAction() {

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, REQUEST_CODE);
    }


    private void uploadAction(int albumID) {

        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        videoref = storageRef.child("/Videos/" + DM.member.memberId + "/" + ts + ".mp4");

        final ProgressDialog pd = DM.getPD(this.getActivity(), "Uploading Video...");
        pd.show();

        Log.d("hq", "uploading bitmap to server, albumID=" + albumID);

        if (videouri != null) {
            uploadTask = videoref.putFile(videouri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(),
                            "Upload failed: " + e.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();

                }
            }).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(getActivity(), "Upload complete",
                                    Toast.LENGTH_LONG).show();
                            Log.d(TAG, "onsuccess" + videouri);

                            pd.dismiss();
                            Toast.makeText(getActivity(), "Successfully add in firebase db", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(
                    new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            updateProgress(taskSnapshot);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "Nothing to upload",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void updateProgress(UploadTask.TaskSnapshot taskSnapshot) {

        @SuppressWarnings("VisibleForTests") long fileSize =
                taskSnapshot.getTotalByteCount();

        @SuppressWarnings("VisibleForTests")
        long uploadBytes = taskSnapshot.getBytesTransferred();

        long progress = (100 * uploadBytes) / fileSize;

        ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.pbar);
        progressBar.setProgress((int) progress);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        videouri = data.getData();
        if (requestCode == REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                Toast.makeText(getActivity(), "Video saved to:\n" +
                        videouri, Toast.LENGTH_LONG).show();
                groupSelection();
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Video recording cancelled.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Failed to record video",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void groupSelection() {

        if (videoalbums.size() == 0) {
            Toast.makeText(this.getActivity(), "No albums loaded yet!!", Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder d = new AlertDialog.Builder(this.getActivity());
        d.setTitle("Upload Video To Folder?");

        View v = this.getActivity().getLayoutInflater().inflate(R.layout.uploadvideo_dialog, null);

        VideoView iv = v.findViewById(R.id.VideoView);
        iv.setVideoURI(Uri.parse(String.valueOf(videouri)));
        iv.requestFocus();
        iv.start();

        final NumberPicker picker = v.findViewById(R.id.numberPicker);
        picker.setMinValue(0);
        picker.setMaxValue(videoalbums.size() - 1);
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); //stops editing...
        String[] sa = new String[videoalbums.size()];
        for (int i = 0; i < videoalbums.size(); i++) {
            sa[i] = videoalbums.get(i).name;
            Log.d("video", "album id:" + videoalbums.get(i).videoAlbumId);
        }
        picker.setDisplayedValues(sa);

        d.setView(v);
        d.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int index = picker.getValue();
                //uploadBitmap(videoalbums.get(index).videoAlbumId);
                uploadAction(videoalbums.get(index).videoAlbumId);
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


    private void uploadBitmap(int albumID) {
        final ProgressDialog pd = DM.getPD(this.getActivity(), "Uploading Video...");
        pd.show();

        Log.d("hq", "uploading bitmap to server, albumID=" + albumID);

        if (uploadTask.isSuccessful() || uploadTask.isComplete()) {
            pd.dismiss();
            Toast.makeText(getActivity(), "Successfully add in firebase db", Toast.LENGTH_SHORT).show();
        } else {
            pd.dismiss();
            Toast.makeText(getActivity(), "Something is wrong", Toast.LENGTH_SHORT).show();
        }


        /*String fileName = "photo.png";

        File f = new File(this.getContext().getCacheDir(), fileName);
        try {
            f.createNewFile();
            //Convert bitmap to byte array

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 *//*ignored for PNG*//*, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();


            TypedFile typedImage = new TypedFile("application/octet-stream", f);
            Log.d("HQ", "Uploading image " + typedImage.file().length());

            DM.getApi().postVideoToAlbum(DM.getAuthString(), albumID, typedImage, new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    Toast.makeText(getActivity(), "Imaged posted to album", Toast.LENGTH_LONG).show();
                    loadData();
                    pd.hide();

                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(getActivity(), "Imaged posting failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    pd.hide();
                    loadData();
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
            Log.d("hq", "file exception");
            pd.hide();
        }*/


    }


    private boolean initialLoaded = false;

    public void loadIfUnloaded() {
        if (initialLoaded == false) loadData();
    }

    private void loadData() {
        initialLoaded = true;

        final ProgressDialog pd = DM.getPD(this.getActivity(), "Loading Video Albums...");
        pd.show();


        if (group != null)
            DM.getApi().getGroupingVideoAlbums(DM.getAuthString(), group.groupId, new Callback<VideoAlbumResponse>() {
                @Override
                public void success(VideoAlbumResponse mediaAlbums, Response response) {
                    videoalbums = mediaAlbums.getData();
                    for (VideoAlbum a : videoalbums) {
                        a.sortVideoAlbumsByDate();
                    }

                    listAdapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                    pd.dismiss();

                    if (mediaAlbums.getData().size() == 0) emptyIV.setVisibility(View.VISIBLE);
                    else emptyIV.setVisibility(View.GONE);

                }

                @Override
                public void failure(RetrofitError error) {
                    pd.dismiss();
                    refreshLayout.setRefreshing(false);

                }
            });

    }

}
