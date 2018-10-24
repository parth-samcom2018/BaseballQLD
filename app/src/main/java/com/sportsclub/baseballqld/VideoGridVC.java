package com.sportsclub.baseballqld;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sportsclub.baseballqld.models.Media;
import com.sportsclub.baseballqld.models.MediaAlbum;
import com.sportsclub.baseballqld.models.MediaAlbumResponse;
import com.sportsclub.baseballqld.models.VideoAlbum;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Vector;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class VideoGridVC extends BaseVC {

    public static MediaAlbum mediaAlbum;
    //public static VideoAlbum mediaAlbum;
    public static int selectedMediaId;
    private Media selectedMedia;
    //public static VideoAlbum mediaAlbum;

    Media group;
    //Media media;
    private GridView gridView;
    private ArrayAdapter<Media> gridAdapter;
    private SwipeRefreshLayout refreshLayout;
    private ImageView emptyIV;

    private List<MediaAlbum> medias = new Vector<MediaAlbum>();
    private List<VideoAlbum> albums = new Vector<VideoAlbum>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_grid);

        Toolbar toolbars = findViewById(R.id.toolbar_second);
        toolbars.setBackgroundColor(Color.BLACK);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadData();

        emptyIV = findViewById(R.id.empty_grid);

        gridView = findViewById(R.id.list_video);


        gridAdapter = new ArrayAdapter<Media>(VideoGridVC.this, R.layout.video_album_cell) {


            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    try {
                        convertView = LayoutInflater.from(VideoGridVC.this).inflate(R.layout.video_album_cell, parent, false);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                final MediaAlbum group = medias.get(position);

                final ImageView myImageView = convertView.findViewById(R.id.myImageView_solo);

                final TextView tv = convertView.findViewById(R.id.textView_solo);

                Picasso.Builder builder = new Picasso.Builder(VideoGridVC.this);
                builder.listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Log.d("hq", "uri: " + uri.getPath());
                        exception.printStackTrace();
                    }
                });
                Picasso p = builder.build();

                mediaAlbum.sortMediaAlbumsByDate(); //sort by oldest last, since api is useless
                try {
                    selectedMedia = mediaAlbum.mediaModels.get(0); //DEFAULT TO FIRST
                    Log.d("video", "response:" + mediaAlbum.mediaModels.size());
                    Log.d("video", "response:" + selectedMedia.url);
                    Log.d("video", "response:" + selectedMedia.thumbnail);
                    p.load(group.thumbnail)//.networkPolicy(NetworkPolicy.NO_CACHE)
                            .placeholder(R.drawable.video).into(myImageView);
                    tv.setText(group.url);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Log.e("video", "I got an error", e);

                }

                return convertView;
            }

            @Override
            public int getCount() {
                return medias.size();
            }
        };
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(VideoGridVC.this, "" + parent, Toast.LENGTH_SHORT).show();
            }
        });

        refreshLayout = findViewById(R.id.swiperefresh_grid);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                loadData();
            }
        });
    }

    private void loadData() {

        final ProgressDialog pd = DM.getPD(this, "Loading Videos...");
        pd.show();

        String auth = DM.getAuthString();

        /*DM.getApi().getAllGrouping(auth, new Callback<GroupResponse>() {
            @Override
            public void success(GroupResponse gs, Response response) {


                medias = gs.getData();
                gridAdapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
                pd.dismiss();

                if (gs.getData().size() == 0) emptyIV.setVisibility(View.VISIBLE);
                else emptyIV.setVisibility(View.GONE);

            }

            @Override
            public void failure(RetrofitError error) {
                refreshLayout.setRefreshing(false);
                pd.dismiss();

            }
        });*/

        Log.d("res", "onresponse: " + mediaAlbum.mediaAlbumId);
        Log.d("res", "onresponse: " + mediaAlbum.mediaModels.size());

        DM.getApi().getMediaModelRes(auth, mediaAlbum.mediaAlbumId, new Callback<MediaAlbumResponse>() {
            @Override
            public void success(MediaAlbumResponse mediaModelReponse, Response response) {


                medias = mediaModelReponse.getData();
                gridAdapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
                pd.dismiss();

                if (mediaModelReponse.getData().size() == 0) emptyIV.setVisibility(View.VISIBLE);
                else emptyIV.setVisibility(View.GONE);


                Log.d("grid", "size: " + mediaModelReponse.getData().size());

            }

            @Override
            public void failure(RetrofitError error) {
                refreshLayout.setRefreshing(false);
                pd.dismiss();
                gridAdapter.notifyDataSetChanged();
            }
        });

    }


}