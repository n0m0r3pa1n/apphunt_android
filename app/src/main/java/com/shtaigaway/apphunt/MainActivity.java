package com.shtaigaway.apphunt;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ListView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.shamanland.fab.FloatingActionButton;
import com.shtaigaway.apphunt.api.AppHuntApiClient;
import com.shtaigaway.apphunt.api.Callback;
import com.shtaigaway.apphunt.api.models.User;
import com.shtaigaway.apphunt.services.DailyNotificationService;
import com.shtaigaway.apphunt.ui.adapters.TrendingAppsAdapter;
import com.shtaigaway.apphunt.ui.fragments.NotificationFragment;
import com.shtaigaway.apphunt.ui.fragments.SaveAppFragment;
import com.shtaigaway.apphunt.ui.fragments.SelectAppFragment;
import com.shtaigaway.apphunt.ui.fragments.SettingsFragment;
import com.shtaigaway.apphunt.ui.interfaces.OnAppSelectedListener;
import com.shtaigaway.apphunt.ui.interfaces.OnNetworkStateChange;
import com.shtaigaway.apphunt.ui.interfaces.OnUserAuthListener;
import com.shtaigaway.apphunt.utils.ActionBarUtils;
import com.shtaigaway.apphunt.utils.ConnectivityUtils;
import com.shtaigaway.apphunt.utils.Constants;
import com.shtaigaway.apphunt.utils.FacebookUtils;
import com.shtaigaway.apphunt.utils.SharedPreferencesHelper;

import org.json.JSONObject;

import java.util.Calendar;

public class MainActivity extends ActionBarActivity implements AbsListView.OnScrollListener, OnClickListener,
        OnAppSelectedListener, OnUserAuthListener, OnNetworkStateChange {

    private ListView trendingAppsList;
    private FloatingActionButton addAppButton;
    private TrendingAppsAdapter trendingAppsAdapter;
    private boolean endOfList = false;

    private UiLifecycleHelper uiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        if (!ConnectivityUtils.isNetworkAvailable(this)) {
            Bundle extras = new Bundle();
            extras.putString(Constants.KEY_NOTIFICATION, getString(R.string.notification_no_internet));
            NotificationFragment notificationFragment = new NotificationFragment();
            notificationFragment.setArguments(extras);

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.alpha_in, R.anim.slide_out_top)
                    .add(R.id.container, notificationFragment, Constants.TAG_NOTIFICATION_FRAGMENT)
                    .addToBackStack(Constants.TAG_NOTIFICATION_FRAGMENT)
                    .commit();
        }

        FacebookUtils.onStart(this);

        initUI();

        if(!SharedPreferencesHelper.getBooleanPreference(this, Constants.IS_DAILY_NOTIFICATION_SETUP_KEY)) {
            SharedPreferencesHelper.setPreference(this, Constants.IS_DAILY_NOTIFICATION_SETUP_KEY, true);
            setupDailyNotificationService();
        }


    }

    private void initUI() {
        addAppButton = (FloatingActionButton) findViewById(R.id.add_app);
        addAppButton.setOnClickListener(this);

        trendingAppsList = (ListView) findViewById(R.id.trending_list);

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

    private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(Constants.TAG_NOTIFICATION_FRAGMENT);

            if (!ConnectivityUtils.isNetworkAvailable(context)) {
                if (fragment == null)  {
                    Bundle extras = new Bundle();
                    extras.putString(Constants.KEY_NOTIFICATION, getString(R.string.notification_no_internet));
                    NotificationFragment notificationFragment = new NotificationFragment();
                    extras.putBoolean(Constants.KEY_SHOW_SETTINGS, true);
                    notificationFragment.setArguments(extras);

                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.alpha_in, R.anim.slide_out_top)
                            .add(R.id.container, notificationFragment, Constants.TAG_NOTIFICATION_FRAGMENT)
                            .addToBackStack(Constants.TAG_NOTIFICATION_FRAGMENT)
                            .commit();
                }
            } else {
                if (fragment != null) {
                    getSupportFragmentManager().popBackStack(Constants.TAG_NOTIFICATION_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }

                onNetworkAvailable();
            }
        }
    };

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
                    FacebookUtils.showLoginFragment(this);
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
                FacebookUtils.showLoginFragment(this);
                break;

            case R.id.action_logout:
                if (FacebookUtils.isSessionOpen()) {
                    FacebookUtils.closeSession();

                    supportInvalidateOptionsMenu();
                    FacebookUtils.hideLoginFragment(this);
                    FacebookUtils.onLogout(this);

                    for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                        getSupportFragmentManager().popBackStack();
                    }
                }
                break;

            case R.id.action_settings:
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.abc_fade_in, R.anim.alpha_out)
                        .add(R.id.container, new SettingsFragment(), Constants.TAG_SETTINGS_FRAGMENT)
                        .addToBackStack(Constants.TAG_SETTINGS_FRAGMENT)
                        .commit();
                break;

            case R.id.action_share:
                if (FacebookDialog.canPresentShareDialog (getApplicationContext(),
                        FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
                    FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
                            .setName("AppHunt")
                            .setPicture("https://launchrock-assets.s3.amazonaws.com/logo-files/LWPRHM35_1421410706452.png?_=4")
                            .setLink("http://theapphunt.com").build();
                    uiHelper.trackPendingDialogCall(shareDialog.present());
                } else {
                }
                break;

            case android.R.id.home:
                getSupportFragmentManager().popBackStack();
                break;
        }

        return true;
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isClosed()) {
            FacebookUtils.onLogout(MainActivity.this);
            onUserLogout();
        } else if (state.isOpened()) {
            onUserLogin();
        }

        this.supportInvalidateOptionsMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (uiHelper == null) {
            return;
        }

        uiHelper.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                Log.e("Activity", String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                Log.i("Activity", "Success!");
            }
        });
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

    @Override
    public void onUserLogin() {
        trendingAppsAdapter.resetAdapter();
    }

    @Override
    public void onUserLogout() {
        trendingAppsAdapter.resetAdapter();
    }

    @Override
    public void onNetworkAvailable() {
        trendingAppsAdapter.resetAdapter();

        while (getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (uiHelper != null)
            uiHelper.onResume();

        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (uiHelper != null) {
            uiHelper.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (uiHelper != null)
            uiHelper.onPause();

        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (uiHelper != null)
            uiHelper.onDestroy();
    }
}
