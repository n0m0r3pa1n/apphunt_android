package com.shtaigaway.apphunt;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ListView;

import com.shamanland.fab.FloatingActionButton;
import com.shtaigaway.apphunt.services.DailyNotificationService;
import com.shtaigaway.apphunt.ui.adapters.TrendingAppsAdapter;
import com.shtaigaway.apphunt.ui.fragments.SaveAppFragment;
import com.shtaigaway.apphunt.ui.fragments.SelectAppFragment;
import com.shtaigaway.apphunt.ui.fragments.SettingsFragment;
import com.shtaigaway.apphunt.ui.interfaces.OnAppSelectedListener;
import com.shtaigaway.apphunt.utils.ActionBarUtils;
import com.shtaigaway.apphunt.utils.Constants;
import com.shtaigaway.apphunt.utils.FacebookUtils;
import com.shtaigaway.apphunt.utils.SharedPreferencesHelper;

import java.util.Calendar;

public class MainActivity extends ActionBarActivity implements AbsListView.OnScrollListener, OnClickListener, OnAppSelectedListener {

    private FloatingActionButton addAppButton;
    private TrendingAppsAdapter trendingAppsAdapter;
    private boolean endOfList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        if(!SharedPreferencesHelper.getBooleanPreference(this, Constants.IS_DAILY_NOTIFICATION_SETUP_KEY)) {
            SharedPreferencesHelper.setPreference(this, Constants.IS_DAILY_NOTIFICATION_SETUP_KEY, true);
            setupDailyNotificationService();
        }

    }

    private void initUI() {
        addAppButton = (FloatingActionButton) findViewById(R.id.add_app);
        addAppButton.setOnClickListener(this);

        ListView trendingAppsList = (ListView) findViewById(R.id.trending_list);

        trendingAppsAdapter = new TrendingAppsAdapter(this, trendingAppsList);
        trendingAppsList.setAdapter(trendingAppsAdapter);
        trendingAppsList.setOnScrollListener(this);

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                ActionBarUtils.getInstance().configActionBar(MainActivity.this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_app:
                if (FacebookUtils.isSessionOpen()) {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.bounce, R.anim.slide_out_top)
                            .add(R.id.container, new SelectAppFragment(), Constants.TAG_ADD_APP_FRAGMENT)
                            .addToBackStack(Constants.TAG_ADD_APP_FRAGMENT)
                            .commit();

                    getSupportFragmentManager().executePendingTransactions();
                } else {
                    FacebookUtils.getInstance().showLoginFragment(this);
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

       if (FacebookUtils.isSessionOpen()) {
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
                FacebookUtils.getInstance().showLoginFragment(this);
                break;

            case R.id.action_logout:
                if (FacebookUtils.isSessionOpen()) {
                    FacebookUtils.closeSession();

                    supportInvalidateOptionsMenu();
                    FacebookUtils.getInstance().hideLoginFragment(this);
                }
                break;

            case R.id.action_settings:
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.abc_slide_in_top, R.anim.slide_out_top)
                        .add(R.id.container, new SettingsFragment(), Constants.TAG_SETTINGS_FRAGMENT)
                        .addToBackStack(Constants.TAG_SETTINGS_FRAGMENT)
                        .commit();
                break;

            case android.R.id.home:
                getSupportFragmentManager().popBackStack();
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
            Animation slideInBottom = AnimationUtils.loadAnimation(this, R.anim.abc_slide_in_bottom);
            addAppButton.startAnimation(slideInBottom);
            addAppButton.setVisibility(View.VISIBLE);

            if (endOfList) {
                trendingAppsAdapter.getAppsForNextDate();
            }
        } else {
            Animation slideOutBottom = AnimationUtils.loadAnimation(this, R.anim.abc_slide_out_bottom);
            addAppButton.startAnimation(slideOutBottom);
            addAppButton.setVisibility(View.INVISIBLE);
        }
    }

    public void setupDailyNotificationService() {
        Intent intent = new Intent(this, DailyNotificationService.class);
        PendingIntent alarmIntent = PendingIntent.getService(this, 123, intent, 0);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);

        if(alarmMgr == null || alarmIntent == null) {
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 19);
        calendar.set(Calendar.MINUTE, 0);

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    @Override
    public void onAppSelected(ApplicationInfo data) {
        Bundle extras = new Bundle();
        extras.putParcelable(Constants.KEY_DATA, data);

        SaveAppFragment saveAppFragment = new SaveAppFragment();
        saveAppFragment.setArguments(extras);

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                .add(R.id.container, saveAppFragment, Constants.TAG_SAVE_APP_FRAGMENT)
                .addToBackStack(Constants.TAG_SAVE_APP_FRAGMENT)
                .commit();
    }
}
