package com.sportsclub.baseballqld;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sportsclub.baseballqld.models.Notification;
import com.sportsclub.baseballqld.models.NotificationComment;
import com.sportsclub.baseballqld.models.NotificationResponse;
import com.sportsclub.baseballqld.views.TextPoster;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NotificationVC extends BaseVC {

    Context context = NotificationVC.this;
    private static final String TAG = NotificationVC.class.getSimpleName();

    Dialog dialog;

    public static Notification notification;
    JSONObject jsonObject;
    //VIEWS
    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private ArrayAdapter listAdapter;
    private TextPoster textPoster;
    private LinearLayout ll_back;

    private void postComment(final String text) {
        if (text.length() == 0) {
            Toast.makeText(this, "You must enter text", Toast.LENGTH_LONG).show();
            return;
        }

        DM.hideKeyboard(this);

        final ProgressDialog pd = DM.getPD(this, "Posting Comment...");
        pd.show();



            /*DM.getApi().postNotificationComment(DM.getAuthString(), notification.notificationId,text,  new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {

                    Toast.makeText(getApplicationContext(),"Notification Posted!",Toast.LENGTH_LONG).show();
                    textPoster.clearText();
                    refreshNotification();
                    pd.dismiss();

                    Log.d(TAG, "data: " );

                }

                @Override
                public void failure(RetrofitError error) {

                    Toast.makeText(getApplicationContext(),"Post failed "+error.getMessage(),Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
            });*/

        DM.getApi().postNotificationComments(DM.getAuthString(), notification.notificationId, text, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(getApplicationContext(), "Notification Posted!", Toast.LENGTH_LONG).show();
                textPoster.clearText();
                refreshNotification();
                pd.dismiss();
                refreshLayout.setRefreshing(true);

                Log.d(TAG, "data: ");

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), "Post failed " + error.getMessage(), Toast.LENGTH_LONG).show();
                pd.dismiss();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_notification_vc);

        //BACK, rest defined in base class
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setTitle("Notification");

        TextView firstTV = findViewById(R.id.firstTV);
        firstTV.setText(notification.memberName);

        TextView secondTV = findViewById(R.id.secondTV);
        secondTV.setText(notification.getTimeAgo() + "");

        final TextView mainTV = findViewById(R.id.mainTV);
        mainTV.setMovementMethod(LinkMovementMethod.getInstance());
        mainTV.setText(Html.fromHtml(notification.text));

        ll_back = findViewById(R.id.ll_back);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ImageView userIV = findViewById(R.id.imageView);
        Picasso p = Picasso.with(this);
        p.setIndicatorsEnabled(true);
        p.load(notification.memberAvatar)
                .placeholder(R.drawable.splashlogo)
                //.fetch();
                .into(userIV);

        Linkify.addLinks(mainTV, Linkify.WEB_URLS);

        /*mainTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                WebVC.url ="https://" +mainTV.getText().toString();
                WebVC.title = "Terms";
                Intent i = new Intent(NotificationVC.this, WebVC.class);
                startActivity(i);
            }
        });*/
        textPoster = findViewById(R.id.textposter);
        textPoster.setOnSendListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment(textPoster.getText());
            }
        });

        listView = findViewById(R.id.list);
        listAdapter = new ArrayAdapter(this, R.layout.main_cell) {


            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    Log.d("hq", "convertView is null");
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.main_cell, parent, false);
                }

                Log.d("hq", "notification cell height: " + convertView.getHeight());

                final NotificationComment c = notification.comments.get(position);

                TextView firstTV = convertView.findViewById(R.id.firstTV);
                String topString = "<font color='e2441f'>" + c.memberName + "</font> group";
                firstTV.setText(Html.fromHtml(topString));

                TextView secondTV = convertView.findViewById(R.id.secondTV);
                secondTV.setText(c.comment);

                TextView thirdTV = convertView.findViewById(R.id.thirdTV);
                thirdTV.setText(c.getTimeAgo());

                TextView usernameTV = convertView.findViewById(R.id.usernameTV);
                usernameTV.setText(c.memberName);

                ImageView userIV = convertView.findViewById(R.id.imageView);
                Picasso p = Picasso.with(this.getContext());
                p.setIndicatorsEnabled(true);
                p.load(c.memberAvatar)
                        .placeholder(R.drawable.splashlogo)
                        //.fetch();
                        .into(userIV);

                Button flagButton = convertView.findViewById(R.id.flagButton);
                flagButton.setOnClickListener(DM.getFlagOnClickListener(NotificationVC.this));

                convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        dialog = new Dialog(NotificationVC.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(true);
                        dialog.setContentView(R.layout.my_notifications_comments);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                        TextView tvdata = dialog.findViewById(R.id.tvData);

                        tvdata.setText("" + c.comment);

                        Log.d(TAG, "memberID: " + DM.member.memberId);
                        Log.d(TAG, "NotifyId: " + c.memberId);

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
                                if (DM.member.memberId == c.memberId) {
                                    DM.getApi().CommentsDelete(DM.getAuthString(), c.notificationCommentId, new Callback<Response>() {
                                        @Override
                                        public void success(Response response, Response response2) {
                                            Toast.makeText(NotificationVC.this, "Delete Comments", Toast.LENGTH_SHORT).show();
                                            refreshNotification();
                                            refreshLayout.setRefreshing(true);
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            Toast.makeText(NotificationVC.this, "Cannot delete this comment!!", Toast.LENGTH_SHORT).show();
                                            refreshNotification();
                                            refreshLayout.setRefreshing(true);
                                        }
                                    });

                                }
                                else {
                                    Toast.makeText(NotificationVC.this, "You are authorized to delete this notification!!", Toast.LENGTH_SHORT).show();
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
                return notification.comments.size();
            }
        };
        listView.setAdapter(listAdapter);


        refreshLayout = findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshNotification();
            }
        });
    }


    private void refreshNotification() {
        //NO Refresh node??? get all instead and filter
        final ProgressDialog pd = DM.getPD(this, "Refreshing Notification...");
        pd.show();
        DM.getApi().getGroupNotificationsnew(DM.getAuthString(), notification.familyId, new Callback<NotificationResponse>() {
            @Override
            public void success(NotificationResponse notifications, Response response) {
                for (Notification n : notifications.getData()) {
                    if (n.notificationId == NotificationVC.notification.notificationId) {

                        //found match!
                        NotificationVC.notification = n;
                        listAdapter.notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
                        break;
                    }
                }
                pd.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                pd.dismiss();
                refreshLayout.setRefreshing(false);
            }
        });

    }
}

