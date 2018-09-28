package com.sportsclub.baseballqld;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sportsclub.baseballqld.models.Event;
import com.sportsclub.baseballqld.models.EventResponse;
import com.sportsclub.baseballqld.models.Group;

import java.util.List;
import java.util.Vector;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FixturesVC extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    Group group;
    private ListView listView;
    private ArrayAdapter<Event> listAdapter;
    private SwipeRefreshLayout refreshLayout;
    private ImageView emptyIV;

    public static boolean oneShotRefresh = false;


    //MODELS
    private List<Event> events = new Vector<Event>(); //empty

    public FixturesVC(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.fragment_fixtures, container, false);


        emptyIV = v.findViewById(R.id.empty);

        listView = v.findViewById(R.id.list);

        listAdapter= new ArrayAdapter<Event>(this.getActivity(), R.layout.event_cell){


            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    convertView = LayoutInflater.from(FixturesVC.this.getContext()).inflate(R.layout.event_cell, parent, false);
                }

                Event e = events.get(position);

                TextView titleTV = convertView.findViewById(R.id.titleTV);
                titleTV.setText(e.eventName);

                TextView tv_group_title = convertView.findViewById(R.id.tv_group_title);
                tv_group_title.setText(e.groupName);

                TextView locationTV = convertView.findViewById(R.id.locationTV);
                locationTV.setText("At "+e.location);

                String topString = " <font color='#d7d7d7'> At </font>" +e.location ;
                locationTV.setText(Html.fromHtml(topString));

                TextView timeTV = convertView.findViewById(R.id.timeTV);
                timeTV.setText(e.getDateString()+"\n"+e.getTimeString());

                return convertView;
            }

            @Override
            public int getCount() {
                return events.size();
            }
        };
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Event e = events.get(position);
                EventVC.event = e;

                Intent j = new Intent(FixturesVC.this.getActivity(), EventVC.class);
                j.putExtra("Key",e.groupName);
                startActivity(j);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                Event e = events.get(position);
                EventVC.event = e;

                //Toast.makeText(getActivity(), "" + e.eventId, Toast.LENGTH_SHORT).show();

                deleteEvent();

                return true;
            }
        });

        refreshLayout = v.findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(this);

        return v;
    }

    private void deleteEvent() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Event");
        builder.setMessage("Are you sure want to delete this event?");
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        Event e = events.get(0);
                        EventVC.event = e;

                        String auth = DM.getAuthString();

                        DM.getApi().delete(auth,e.eventId, new Callback<Response>() {
                            @Override
                            public void success(Response response, Response response2) {

                                Toast.makeText(getActivity(), "Successfully Deleted Event", Toast.LENGTH_SHORT).show();
                                loadData();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Toast.makeText(getActivity(), "Event cannot be delete!!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        //Toast.makeText(getActivity(), "Dismiss", Toast.LENGTH_SHORT).show();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean initialLoaded = false;
    public void loadIfUnloaded(){

        if(initialLoaded == false) loadData();
    }

    private void loadData()
    {
        initialLoaded = true;

        final ProgressDialog pd = DM.getPD(this.getActivity(),"Loading Fixtures...");
        if(true)pd.show();

        String auth = DM.getAuthString();

        DM.getApi().getFixtureGroup(auth, group.groupId, new Callback<EventResponse>() {
            @Override
            public void success(EventResponse events, Response response) {
                FixturesVC.this.events = events.getData();
                Log.d("hq", "events: "+(events.getData()).size()+"");
                listAdapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
                pd.dismiss();

                if(events.getData().size()==0) emptyIV.setVisibility(View.VISIBLE);
                else emptyIV.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("hq", "failed: "+error.getMessage());
                refreshLayout.setRefreshing(false);
                pd.dismiss();
            }
        });

    }

    @Override
    public void onRefresh() {
        loadData();

    }

    @Override
    public void onResume() {

        if(oneShotRefresh)
        {
            loadData();
            oneShotRefresh = false;
        }

        super.onResume();


    }
}
