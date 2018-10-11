package com.sportsclub.baseballqld;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.sportsclub.baseballqld.models.Event;
import com.sportsclub.baseballqld.models.Group;
import com.sportsclub.baseballqld.models.GroupResponse;
import com.sportsclub.baseballqld.models.MediaAlbum;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Vector;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class VideoGridVC extends BaseVC {

    public static MediaAlbum mediaAlbum;
    Group group;
    private GridView gridView;
    private ArrayAdapter<Event> gridAdapter;
    private SwipeRefreshLayout refreshLayout;
    private ImageView emptyIV;

    private List<Group> groups = new Vector<Group>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_grid);

        Toolbar toolbars = findViewById(R.id.toolbar_second);
        toolbars.setBackgroundColor(Color.BLACK);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadData();

        emptyIV = findViewById(R.id.empty);


        gridView = findViewById(R.id.list_video);


        gridAdapter = new ArrayAdapter<Event>(VideoGridVC.this, R.layout.group_cell) {


            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    try {
                        convertView = LayoutInflater.from(VideoGridVC.this).inflate(R.layout.group_cell, parent, false);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                final Group group = groups.get(position);


                final ImageView myImageView = convertView.findViewById(R.id.myImageView);

                final TextView tv = convertView.findViewById(R.id.textView);

                Picasso.Builder builder = new Picasso.Builder(VideoGridVC.this);
                builder.listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Log.d("hq", "uri: " + uri.getPath());
                        exception.printStackTrace();
                    }
                });
                try {
                    Picasso p = builder.build();
                    p.load(group.groupImage).placeholder(R.drawable.group_first).into(myImageView);//.networkPolicy(NetworkPolicy.NO_CACHE).
                    tv.setText(group.groupName);

                } catch (NullPointerException n) {
                    n.printStackTrace();
                }

                return convertView;
            }

            @Override
            public int getCount() {
                return groups.size();
            }
        };
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    Group g = groups.get(position);
                    GroupVC.group = g;
                    Intent i = new Intent(VideoGridVC.this, GroupVC.class);
                    startActivity(i);

                    Log.d("Group", "id :" + g.groupId);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }
        });

        refreshLayout = findViewById(R.id.swiperefresh);
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

        DM.getApi().getAllGrouping(auth, new Callback<GroupResponse>() {
            @Override
            public void success(GroupResponse gs, Response response) {


                groups = gs.getData();
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
        });
    }


}
