package com.sportsclub.baseballqld;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
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


    private static final String TAG = "QLD";
    private ListView listView;
    private ArrayAdapter<Event> listAdapter;
    private SwipeRefreshLayout refreshLayout;
    private TextPoster textPoster;
    Dialog dialog;

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


                convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        dialog = new Dialog(getActivity());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(true);
                        dialog.setContentView(R.layout.my_details_event);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                        TextView tvdata = dialog.findViewById(R.id.tvData);

                        tvdata.setText("" + ec.comment);

                        Log.d(TAG, "memberID: " + DM.member.memberId);
                        Log.d(TAG, "eventCommentID: " + ec.eventCommentId);


                        Button btn_no = dialog.findViewById(R.id.btn_no);
                        btn_no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        Button btnYes = dialog.findViewById(R.id.btn_yes);

                        btnYes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (DM.member.memberId == ec.eventCommentId) {
                                    String auth = DM.getAuthString();

                                    DM.getApi().eventCommentdelete(auth, ec.eventCommentId, new Callback<Response>() {
                                        @Override
                                        public void success(Response response, Response response2) {
                                            Toast.makeText(getActivity(), "Delete Notification", Toast.LENGTH_SHORT).show();
                                            refreshEvent();
                                            refreshLayout.setRefreshing(true);
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            Toast.makeText(getActivity(), "Cannot", Toast.LENGTH_SHORT).show();
                                            refreshEvent();
                                            refreshLayout.setRefreshing(true);
                                        }
                                    });
                                }
                                else {
                                    Toast.makeText(getActivity(), "You are authorized to delete this notification!!", Toast.LENGTH_SHORT).show();
                                }


                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        return true;
                    }
                });

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

