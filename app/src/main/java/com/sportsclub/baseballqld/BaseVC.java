package com.sportsclub.baseballqld;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class BaseVC extends AppCompatActivity {

    @Override
    protected void onResume() {
        //  overridePendingTransition(android.R.anim.slide_out_right,android.R.anim.slide_in_left);
        //  overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
        super.onResume();
    }



    @Override
    protected void onPause() {
        // overridePendingTransition(R.anim.push_left_in, R.anim.push_left_in);

        //   overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
        super.onPause();

    }

    //FOR BACK
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                // overridePendingTransition(android.R.anim.slide_out_right,android.R.anim.slide_in_left);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}

