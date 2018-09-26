package com.sportsclub.baseballqld;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sportsclub.baseballqld.models.Article;
import com.sportsclub.baseballqld.models.Group;


public class ArticleVC extends BaseVC {

    //MODELS
    public static Article article;
    public static Group group;


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private TextView tv_name, tv_event_title;
    private LinearLayout ll_edit, ll_back;

    private ArticleDetailsVC articleDetailsVC;
    private ArticleCommentsVC articleCommentsVC;

    private String[] titles = {"Details", "Comments"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_event_vc); //Use the event one why not

        //BACK, rest defined in base class
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSectionsPagerAdapter = new SectionsPagerAdapter(this.getSupportFragmentManager());

        tv_name = findViewById(R.id.tv_name);
        tv_name.setText("Details");
        tv_event_title = findViewById(R.id.tv_event_title);
        tv_name.setText("Articles");

        ll_edit = findViewById(R.id.ll_edit);
        ll_back = findViewById(R.id.ll_back);

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ll_edit.setVisibility(View.GONE);

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.viewPager_event);
        //mViewPager.setPagingEnabled(true);

        ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                setTitle(titles[position]);


                switch (position) {
                    case 0:
                        tv_event_title.setText("Details");
                        break;
                    case 1:
                        tv_event_title.setText("Comments");
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };

        mViewPager.addOnPageChangeListener(pageListener);


        mViewPager.setAdapter(mSectionsPagerAdapter);


        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        this.articleDetailsVC = (ArticleDetailsVC) ArticleDetailsVC.instantiate(this, ArticleDetailsVC.class.getName());
        this.articleDetailsVC.article = article;

        this.articleCommentsVC = (ArticleCommentsVC) ArticleCommentsVC.instantiate(this, ArticleCommentsVC.class.getName());
        this.articleCommentsVC.article = article;
        this.articleCommentsVC.group = group;


        this.setTitle(article.shortDescription);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (position == 0) return articleDetailsVC;
            else return articleCommentsVC;
        }


        @Override
        public int getCount() {
            // tab count

            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return titles[position];
        }


    }
}
