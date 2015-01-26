package com.shtaigaway.apphunt;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ListView;

import com.facebook.Session;
import com.shamanland.fab.FloatingActionButton;
import com.shtaigaway.apphunt.ui.LoginFragment;
import com.shtaigaway.apphunt.ui.adapters.TrendingAppsAdapter;
import com.shtaigaway.apphunt.utils.Constants;
import com.shtaigaway.apphunt.utils.SharedPreferencesHelper;

public class MainActivity extends ActionBarActivity implements AbsListView.OnScrollListener {

    private ListView trendingAppsList;
    private FloatingActionButton addAppButton;

    private TrendingAppsAdapter trendingAppsAdapter;
    private boolean endOfList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
    }

    private void initUI() {
        addAppButton = (FloatingActionButton) findViewById(R.id.add);

        trendingAppsList = (ListView) findViewById(R.id.trending_list);

        trendingAppsAdapter = new TrendingAppsAdapter(MainActivity.this, trendingAppsList);
        trendingAppsList.setAdapter(trendingAppsAdapter);
        trendingAppsList.setOnScrollListener(MainActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        Session session = Session.getActiveSession();

        if (session != null && session.isOpened()) {
            inflater.inflate(R.menu.logged_in_menu, menu);
        } else {
            inflater.inflate(R.menu.menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_login:
                Fragment loginFragment = new LoginFragment();
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.bounce, R.anim.slide_out_top)
                        .replace(R.id.container, loginFragment)
                        .addToBackStack(null)
                        .commit();
                break;

            case R.id.action_logout:
                Session session = Session.getActiveSession();

                if (session != null && session.isOpened()) {
                    session.close();
                }
                break;
        }

        return true;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
            endOfList = true;
        } else {
            endOfList = false;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            Animation slideInBottom = AnimationUtils.loadAnimation(MainActivity.this, R.anim.abc_slide_in_bottom);
            addAppButton.startAnimation(slideInBottom);
            addAppButton.setVisibility(View.VISIBLE);

            if (endOfList) {
                trendingAppsAdapter.getAppsForNextDate();
            }
        } else {
            Animation slideOutBottom = AnimationUtils.loadAnimation(MainActivity.this, R.anim.abc_slide_out_bottom);
            addAppButton.startAnimation(slideOutBottom);
            addAppButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

//        Fragment fragment = getSupportFragmentManager().findFragmentByTag("login_fragment");

    }
}
