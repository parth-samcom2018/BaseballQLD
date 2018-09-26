package com.sportsclub.baseballqld;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sportsclub.baseballqld.models.Event;
import com.sportsclub.baseballqld.models.Group;

public class EventVC extends BaseVC implements SwipeRefreshLayout.OnRefreshListener {

    //MODEL
    public static Group group;
    public static Event event;
    private TextView tv_event_title;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    private EventDetailVC eventDetailVC;
    private LocationVC locationVC;
    private EventCommentsVC commentsVC;
    private LinearLayout ll_back, ll_edit;
    String newString;
    private String[] titles = {"Details", "Location", "Comments"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_event_vc);


        //this.setTitle(event.eventName);

        tv_event_title = findViewById(R.id.tv_event_title);
        //tv_event_title.setText(event.eventName);

        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if (bd != null) {
            String getName = (String) bd.get("Key");
            tv_event_title.setSingleLine(true);
            tv_event_title.setText(getName);
        }


        ll_back = findViewById(R.id.ll_back);

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ll_edit = findViewById(R.id.ll_edit);

        ll_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editAction();
            }
        });

        //BACK, rest defined in base class
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSectionsPagerAdapter = new SectionsPagerAdapter(this.getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.viewPager_event);
        //mViewPager.setPagingEnabled(true);


        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        this.eventDetailVC = (EventDetailVC) EventDetailVC.instantiate(this, EventDetailVC.class.getName());
        this.eventDetailVC.event = event;

        this.locationVC = (LocationVC) LocationVC.instantiate(this, LocationVC.class.getName());
        this.locationVC.lattitude = event.latitude;
        this.locationVC.longitude = event.longitude;

        this.commentsVC = (EventCommentsVC) EventCommentsVC.instantiate(this, EventCommentsVC.class.getName());
        this.commentsVC.event = event;
    }


    @Override
    public void onRefresh() {

    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (position == 0)
                return eventDetailVC;
            else if (position == 1) return locationVC;
            else return commentsVC;


        }

        @Override
        public int getCount() {
            // tab count

            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return titles[position];
        }

    }

    private void editAction() {
        EventFormVC.event = EventVC.event;
        Intent i = new Intent(this, EventFormVC.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == R.id.edit) this.editAction();

        return super.onOptionsItemSelected(item);
    }
}
