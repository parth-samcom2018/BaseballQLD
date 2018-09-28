package com.sportsclub.baseballqld;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sportsclub.baseballqld.models.Group;
import com.sportsclub.baseballqld.models.Ladders;
import com.sportsclub.baseballqld.models.LaddersResponse;

import java.util.List;
import java.util.Vector;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LaddersVC extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public Group group;
    Ladders ladder;
    private ListView listView;
    private ArrayAdapter<Ladders> listadapter;
    private SwipeRefreshLayout refreshLayout;
    private TextView emptyIV;
    private LinearLayout ll_first, ll_second;


    private List<Ladders> ladders = new Vector<Ladders>();
    private String TAG = "Ladders";

    public LaddersVC() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.fragment_ladders, container, false);


        emptyIV = v.findViewById(R.id.empty);

        final ProgressDialog pd = DM.getPD(getActivity(), "Loading Ladders...");
        if (this.isVisible()) pd.show();

        listView = v.findViewById(R.id.list);
        ll_first = v.findViewById(R.id.ll_first);
        ll_second = v.findViewById(R.id.ll_second);


        ll_first.setVisibility(View.VISIBLE);
        ll_second.setVisibility(View.VISIBLE);

        listadapter = new ArrayAdapter<Ladders>(this.getActivity(), R.layout.ladder_cell) {


            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    convertView = LayoutInflater.from(LaddersVC.this.getActivity()).inflate(R.layout.ladder_cell, parent, false);
                }


                final Ladders ladder = ladders.get(position);

                TextView textteam = convertView.findViewById(R.id.tvteam);
                textteam.setText("" + ladder.teamName);

                TextView textplayed = convertView.findViewById(R.id.tvplayed);
                textplayed.setText(Integer.toString(ladder.gamesPlayed));

                TextView textwin = convertView.findViewById(R.id.tvwin);
                textwin.setText(Integer.toString(ladder.gamesWon));

                TextView textlost = convertView.findViewById(R.id.tvlost);
                textlost.setText(Integer.toString(ladder.gamesLost));

                TextView textdrawn = convertView.findViewById(R.id.tvdrawn);
                textdrawn.setText(Integer.toString(ladder.gamesDrawn));

                TextView textpoints = convertView.findViewById(R.id.tvpoints);
                textpoints.setText(Double.toString(ladder.totalPoints));


                return convertView;
            }

            @Override
            public int getCount() {
                return ladders.size();
            }
        };
        listView.setAdapter(listadapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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

    private boolean initialLoaded = false;

    public void loadIfUnloaded() {

        if (initialLoaded == false) loadData();
    }

    private void loadData() {
        initialLoaded = true;
        Log.d("hq", "groupID: " + group.groupId);
        final ProgressDialog pd = DM.getPD(getActivity(), "Loading Ladders...");
        if (this.isVisible()) pd.show();


        String auth = DM.getAuthString();


        DM.getApi().getLadders(auth, group.groupId, new Callback<LaddersResponse>() {
            @Override
            public void success(LaddersResponse gs, Response response) {

                ladders = gs.getData();
                listadapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
                pd.dismiss();

                ll_second.setVisibility(View.VISIBLE);
                emptyIV.setVisibility(View.GONE);

                Log.d(TAG, "success:" + response.getStatus());
            }

            @Override
            public void failure(RetrofitError error) {
                refreshLayout.setRefreshing(false);
                pd.dismiss();

                ll_second.setVisibility(View.GONE);
                emptyIV.setVisibility(View.VISIBLE);

                Log.d(TAG, "failure: " + error.getMessage());
                Log.d(TAG, "fail: " + error.getResponse());
            }
        });


    }

    @Override
    public void onRefresh() {

    }
}
