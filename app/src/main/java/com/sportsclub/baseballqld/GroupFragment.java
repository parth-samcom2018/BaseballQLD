package com.sportsclub.baseballqld;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.sportsclub.baseballqld.models.Event;
import com.sportsclub.baseballqld.models.Group;
import com.sportsclub.baseballqld.models.GroupResponse;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Vector;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GroupFragment extends Fragment {

    Group group;
    private GridView gridView;
    private ArrayAdapter<Event> gridAdapter;
    private SwipeRefreshLayout refreshLayout;
    private ImageView emptyIV;

    private List<Group> groups = new Vector<Group>();


    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.fragment_groups, container, false);

        isOnline();

        emptyIV = v.findViewById(R.id.empty);

        final ProgressDialog pd = DM.getPD(getActivity(),"Loading Groups...");
        if(this.isVisible())pd.show();

        gridView = v.findViewById(R.id.list);


        gridAdapter= new ArrayAdapter<Event>(GroupFragment.this.getActivity(), R.layout.group_cell){


            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    try
                    {
                        convertView = LayoutInflater.from(GroupFragment.this.getActivity()).inflate(R.layout.group_cell, parent, false);
                    }

                    catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }

                final Group group = groups.get(position);


                final ImageView myImageView = convertView.findViewById(R.id.myImageView);

                final TextView tv = convertView.findViewById(R.id.textView);

                Picasso.Builder builder = new Picasso.Builder(GroupFragment.this.getActivity());
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

                }
                catch (NullPointerException n){
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
                    Intent i = new Intent(GroupFragment.this.getActivity(), GroupVC.class);
                    startActivity(i);
                }
                catch (NullPointerException e){
                    e.printStackTrace();
                }

            }
        });

        refreshLayout = v.findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                loadData();
            }
        });



        return v;
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

            Toast.makeText(GroupFragment.this.getActivity(), "Internet is not Connected! ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }


    // NEEDED with configChange in manifest, stops view changer from recalling onCreateView
    private boolean initialLoaded = false;
    public void loadIfUnloaded()
    {
        if(initialLoaded == false) loadData();
    }



    private void loadData()
    {

        initialLoaded = true;
        //checkPermission();

        final ProgressDialog pd = DM.getPD(getActivity(),"Loading Groups...");
        if(this.isVisible())pd.show();


        String auth = DM.getAuthString();


        DM.getApi().getAllGrouping(auth, new Callback<GroupResponse>() {
            @Override
            public void success(GroupResponse gs, Response response) {


                groups = gs.getData();
                gridAdapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
                pd.dismiss();

                if(gs.getData().size()==0) emptyIV.setVisibility(View.VISIBLE);
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
