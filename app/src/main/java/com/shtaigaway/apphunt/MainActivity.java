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

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Calendar calendar = Calendar.getInstance();
    private Calendar today = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

//        AppHuntApiClient.getClient().createUser(new User("test"), new EmptyCallback<Response>());
    }

    private void initUI() {
        addAppButton = (FloatingActionButton) findViewById(R.id.add);

        trendingAppsList = (ListView) findViewById(R.id.trending_list);

        trendingAppsAdapter = new TrendingAppsAdapter(MainActivity.this, trendingAppsList);

        getAppsForTodayAndYesterday();
        trendingAppsList.setOnScrollListener(MainActivity.this);
    }

    private void getAppsForTodayAndYesterday() {
        AppHuntApiClient.getClient().getApps(dateFormat.format(today.getTime()), 1, 5, new Callback<AppsList>() {
            @Override
            public void success(AppsList appsList, Response response) {
                final ArrayList<Item> items = new ArrayList<>();

                if (appsList.getTotalCount() > 0) {
                    items.add(new SeparatorItem("Today"));

                    for (App app : appsList.getApps()) {
                        items.add(new AppItem(app));
                    }

                    calendar.add(Calendar.DATE, -1);

                    AppHuntApiClient.getClient().getApps(dateFormat.format(calendar.getTime()), 1, 5, new Callback<AppsList>() {
                        @Override
                        public void success(AppsList appsList, Response response) {
                            items.add(new SeparatorItem("Yesterday"));

                            for (App app : appsList.getApps()) {
                                items.add(new AppItem(app));
                            }

                            trendingAppsAdapter.addItems(items);
                            trendingAppsList.setAdapter(trendingAppsAdapter);
                        }
                    });
                }
            }
        });
    }

    private void getAppsForDate(final String date, int page, int pageSize) {
        AppHuntApiClient.getClient().getApps(date, page, pageSize, new Callback<AppsList>() {
            @Override
            public void success(AppsList appsList, Response response) {
                ArrayList<Item> items = new ArrayList<>();

                if (appsList.getTotalCount() > 0) {
                    try {
                        if (dateFormat.format(today.getTime()).equals(date)) {
                            items.add(new SeparatorItem("Today"));
                        } else {
                            items.add(new SeparatorItem(date));
                        }
                    } catch (Exception e) {
                        Log.e("Exception", e.getMessage());
                    }

                    for (App app : appsList.getApps()) {
                        items.add(new AppItem(app));
                    }

                    trendingAppsAdapter.addItems(items);
                }
            }
        });
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
                calendar.add(Calendar.DATE, -1);
                getAppsForDate(dateFormat.format(calendar.getTime()), 1, 5);
            }
        } else {
            Animation slideOutBottom = AnimationUtils.loadAnimation(MainActivity.this, R.anim.abc_slide_out_bottom);
            addAppButton.startAnimation(slideOutBottom);
            addAppButton.setVisibility(View.INVISIBLE);
        }
    }
}
