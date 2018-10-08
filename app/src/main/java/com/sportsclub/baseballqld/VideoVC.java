package com.sportsclub.baseballqld;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
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
import com.sportsclub.baseballqld.models.MediaAlbum;
import com.sportsclub.baseballqld.models.MediaAlbumResponse;
import com.sportsclub.baseballqld.models.VideoAlbum;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.util.List;
import java.util.Vector;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class VideoVC extends Fragment {

    private static final String TAG = "QLD";
    private static final int REQUEST_PICK_VIDEO = 101;
    private static final int REQUEST_CODE = 101;

    boolean isSelected;

    public Group group;
    //private List<MediaAlbum> albums = new Vector<MediaAlbum>();
    private List<MediaAlbum> videoalbums = new Vector<MediaAlbum>();

    UploadTask uploadTask;
    private Uri videouri;
    private StorageReference videoref;
    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private ArrayAdapter listAdapter;
    private ImageView emptyIV;
    private ProgressDialog pd;
    private ProgressBar progressBar;



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

                final MediaAlbum album = videoalbums.get(position);

                final TextView tv_media = convertView.findViewById(R.id.tv_video);
                tv_media.setVisibility(View.VISIBLE);

                progressBar = convertView.findViewById(R.id.progressBar_video);
                progressBar.setVisibility(View.GONE);

                final ImageView showiv = convertView.findViewById(R.id.iv);


                if (showiv != null || album.mediaModels != null) {
                    Picasso.Builder builder = new Picasso.Builder(VideoVC.this.getActivity());
                    builder.listener(new Picasso.Listener() {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                            Log.d("hq", "uri: " + uri.getPath());
                            exception.printStackTrace();
                        }
                    });
                    Picasso p = builder.build();

                    if (showiv == null || album.mediaModels == null) {
                        showiv.setImageResource(R.drawable.icon);
                        showiv.setClickable(false);

                    } else {
                        p.load(album.coverImage)//.networkPolicy(NetworkPolicy.NO_CACHE)
                                .placeholder(R.drawable.icon).into(showiv);

                        p.load(album.coverImage).transform(new VideoVC.RoundedCornersTransform()).into(showiv, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                if (album.mediaModels == null) {
                                    progressBar.setVisibility(View.GONE);
                                    tv_media.setVisibility(View.GONE);
                                }

                                showiv.setScaleType(ImageView.ScaleType.FIT_XY);

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
                                /*if (showiv == null || album.videoModels == null || album.videoModels.size() == 0 || showiv.equals("0")) {
                                    Toast.makeText(VideoVC.this.getActivity(), "This album is empty! It will fill up only when you select album while upload any photos!", Toast.LENGTH_SHORT).show();
                                    return;
                                }*/

                                Toast.makeText(getActivity(),"id:" + album.mediaAlbumId,Toast.LENGTH_SHORT).show();

                                Log.d("hq","slider click!");
                                //VideoGridVC.mediaAlbum = album;
                                Intent i = new Intent(getActivity(), VideoGridVC.class);
                                startActivity(i);

                            }
                        });

                        showiv.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                if (album.mediaAlbumId == DM.member.memberId) {
                                    Log.d(TAG, "memberID: " + DM.member.memberId);
                                    Log.d(TAG, "mediaalbumID: " + album.mediaAlbumId);

                                    Toast.makeText(getActivity(), "Delete album", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "Cannot delete album", Toast.LENGTH_SHORT).show();
                                }

                                return true;
                            }
                        });
                    }
                }


                TextView firstTV = convertView.findViewById(R.id.firstTV);
                firstTV.setText(album.name + " \n" + album.mediaModels.size() + " videos");
                firstTV.setTextColor(getResources().getColor(R.color.white));
                if (album.mediaModels.size() == 0) {
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
                uploadButtonAction();
            }
        });


        return v;
    }

    private void uploadButtonAction() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("video/*");
        startActivityForResult(galleryIntent, REQUEST_PICK_VIDEO);
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
                        Log.d("video", "id:" + videoalbums.get(0).mediaAlbumId);
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
        startActivityForResult(intent, REQUEST_PICK_VIDEO);
    }


    private void uploadAction(final int albumID) {

        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        videoref = storageRef.child("/Videos/" + DM.member.memberId + "/" + ts + ".mp4");

        final ProgressDialog pd = DM.getPD(this.getActivity(), "Uploading Video...");
        pd.show();

        Log.d("video", "albumID=" + albumID);

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
                            Log.d("video", "url:" + videouri);

                            pd.dismiss();

                            DM.getApi().postVideoToAlbum(DM.getAuthString(), albumID, videouri, new Callback<Response>() {
                                @Override
                                public void success(Response response, Response response2) {
                                    Toast.makeText(getActivity(), "Successfully " + videouri, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    Toast.makeText(getActivity(), "Failed" + error, Toast.LENGTH_SHORT).show();
                                }
                            });
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

        if (requestCode == REQUEST_PICK_VIDEO) {
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

        final Bitmap b = null;

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
            Log.d("video", "album id:" + videoalbums.get(i).mediaAlbumId);
        }
        picker.setDisplayedValues(sa);

        d.setView(v);
        d.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int index = picker.getValue();
                uploadAction(videoalbums.get(index).mediaAlbumId);
                Log.d("video", "id:" + videoalbums.get(index).mediaAlbumId);
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





    private boolean initialLoaded = false;

    public void loadIfUnloaded() {
        if (initialLoaded == false) loadData();
    }

    private void loadData() {
        initialLoaded = true;

        final ProgressDialog pd = DM.getPD(this.getActivity(), "Loading Video Albums...");
        pd.show();


        if (group != null)
            DM.getApi().getGroupingVideoAlbums(DM.getAuthString(), group.groupId, new Callback<MediaAlbumResponse>() {
                @Override
                public void success(MediaAlbumResponse mediaAlbums, Response response) {
                    videoalbums = mediaAlbums.getData();
                    for (MediaAlbum a : videoalbums) {
                        a.sortMediaAlbumsByDate();
                    }

                    listAdapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                    pd.dismiss();

                    Log.d("video", "albums: " + videoalbums.get(0).mediaAlbumId);

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

    private class RoundedCornersTransform implements Transformation {
        public Bitmap getRoundedCornerBitmap(Bitmap bitmap, float r, float v, float r1, float v1) {
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
