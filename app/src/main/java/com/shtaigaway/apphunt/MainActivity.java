package com.shtaigaway.apphunt;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ListView;

import com.shamanland.fab.FloatingActionButton;
import com.shtaigaway.apphunt.api.AppHuntApiClient;
import com.shtaigaway.apphunt.api.Callback;
import com.shtaigaway.apphunt.api.models.App;
import com.shtaigaway.apphunt.api.models.AppsList;
import com.shtaigaway.apphunt.app.AppItem;
import com.shtaigaway.apphunt.app.Item;
import com.shtaigaway.apphunt.app.MoreAppsItem;
import com.shtaigaway.apphunt.app.SeparatorItem;
import com.shtaigaway.apphunt.ui.adapters.TrendingAppsAdapter;
import com.shtaigaway.apphunt.utils.Constants;
import com.shtaigaway.apphunt.utils.SharedPreferencesHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit.client.Response;

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

        if (SharedPreferencesHelper.getBooleanPreference(this, Constants.IS_LOGGED_IN)) {
            inflater.inflate(R.menu.logged_in_menu, menu);
        } else {
            inflater.inflate(R.menu.menu, menu);
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
}
