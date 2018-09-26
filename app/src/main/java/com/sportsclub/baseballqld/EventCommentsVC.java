package com.sportsclub.baseballqld;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sportsclub.baseballqld.models.Event;
import com.sportsclub.baseballqld.models.EventComment;
import com.sportsclub.baseballqld.views.TextPoster;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EventCommentsVC extends Fragment {



    private ListView listView;
    private ArrayAdapter<Event> listAdapter;
    private SwipeRefreshLayout refreshLayout;
    private TextPoster textPoster;


    //MODELS
    public Event event;

    public EventCommentsVC() {
        // Required empty public constructor
    }

    private void postComment(String text)
    {
        if(text.length() == 0)
        {
            Toast.makeText(this.getActivity(),"You must enter text",Toast.LENGTH_LONG).show();
            return;
        }

        DM.hideKeyboard(this.getActivity());


        final ProgressDialog pd = DM.getPD(this.getActivity(),"Posting Comment...");
        pd.show();

        /*DM.getApi().postEventComment(DM.getAuthString(), event.eventId, text, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

                Toast.makeText(EventCommentsVC.this.getActivity(),"Comment Posted!",Toast.LENGTH_LONG).show();
                textPoster.clearText();
                refreshEvent();
                pd.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {

                Toast.makeText(EventCommentsVC.this.getActivity(),"Comment failed "+error.getMessage(),Toast.LENGTH_LONG).show();
                pd.dismiss();
            }
        });*/

        DM.getApi().postEventComments(DM.getAuthString(), event.eventId, text, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(EventCommentsVC.this.getActivity(), "Comment Posted!", Toast.LENGTH_SHORT).show();
                textPoster.clearText();
                refreshEvent();
                pd.dismiss();

                refreshLayout.setRefreshing(true);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(EventCommentsVC.this.getActivity(), "Comment Failed " + error.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.fragment_event_comments_vc, container, false);

        textPoster = v.findViewById(R.id.textposter);
        textPoster.setOnSendListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment(textPoster.getText());
            }
        });

        listView = v.findViewById(R.id.list);
        listAdapter= new ArrayAdapter<Event>(this.getActivity(), R.layout.main_cell){


            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    convertView = LayoutInflater.from(EventCommentsVC.this.getActivity()).inflate(R.layout.main_cell, parent, false);
                }

                final EventComment ec = event.comments.get(position);


                TextView firstTV = convertView.findViewById(R.id.firstTV);
                String topString = "<font color='#e2441f'>"+ec.memberName+"</font>";
                firstTV.setText(Html.fromHtml(topString));

                TextView secondTV = convertView.findViewById(R.id.secondTV);
                secondTV.setText(ec.comment);

                TextView thirdTV = convertView.findViewById(R.id.thirdTV);
                thirdTV.setText(ec.getTimeAgo()+"");

                TextView usernameTV = convertView.findViewById(R.id.usernameTV);
                usernameTV.setText(ec.memberName);

                ImageView userIV = convertView.findViewById(R.id.imageView);
                Picasso p = Picasso.with(EventCommentsVC.this.getActivity());
                p.setIndicatorsEnabled(true);
                p.load(ec.memberAvatar)
                        .placeholder(R.drawable.splashlogo)
                        //.fetch();
                        .into(userIV);



                return convertView;
            }

            @Override
            public int getCount() {
                return event.comments.size();
            }
        };
        listView.setAdapter(listAdapter);



        refreshLayout = v.findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshEvent();
            }
        });



        return v;
    }

    private void refreshEvent()
    {
        final ProgressDialog pd = DM.getPD(this.getActivity(),"Refreshing Event...");
        pd.show();
        DM.getApi().getEvent(DM.getAuthString(), event.eventId, new Callback<Event>() {
            @Override
            public void success(Event e, Response response) {
                event = e;
                listAdapter.notifyDataSetChanged();
                pd.dismiss();
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(RetrofitError error) {
                pd.dismiss();
                refreshLayout.setRefreshing(false);
            }
        });

    }

}

