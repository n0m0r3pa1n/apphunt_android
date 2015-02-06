package com.apphunt.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ListView;

import com.apphunt.app.api.AppHuntApiClient;
import com.apphunt.app.smart_rate.SmartRate;
import com.apphunt.app.ui.adapters.TrendingAppsAdapter;
import com.apphunt.app.ui.fragments.SaveAppFragment;
import com.apphunt.app.ui.fragments.SelectAppFragment;
import com.apphunt.app.ui.fragments.SettingsFragment;
import com.apphunt.app.ui.interfaces.OnAppSelectedListener;
import com.apphunt.app.ui.interfaces.OnNetworkStateChange;
import com.apphunt.app.ui.interfaces.OnUserAuthListener;
import com.apphunt.app.utils.ActionBarUtils;
import com.apphunt.app.utils.ConnectivityUtils;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.FacebookUtils;
import com.apphunt.app.utils.NotificationsUtils;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.shamanland.fab.FloatingActionButton;

public class MainActivity extends ActionBarActivity implements AbsListView.OnScrollListener, OnClickListener,
        OnAppSelectedListener, OnUserAuthListener, OnNetworkStateChange {

    private static final String TAG = MainActivity.class.getName();

    private ListView trendingAppsList;
    private FloatingActionButton addAppButton;
    private TrendingAppsAdapter trendingAppsAdapter;
    private boolean endOfList = false;

    private UiLifecycleHelper uiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);

        FacebookUtils.onStart(this);

        initUI();

        sendBroadcast(new Intent(Constants.ACTION_ENABLE_NOTIFICATIONS));

        SmartRate.init(this, "ENTER TOKEN HERE", Constants.APP_SPICE_APP_ID);
    }

    private void initUI() {
        ActionBarUtils.getInstance().init(this);

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
                if (fragment == null) {
                    NotificationsUtils.showNotificationFragment(((ActionBarActivity) context), getString(R.string.notification_no_internet), true, false);
                }
                addAppButton.setVisibility(View.INVISIBLE);
            } else {
                if (fragment != null) {
                    getSupportFragmentManager().popBackStack(Constants.TAG_NOTIFICATION_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }

                addAppButton.setVisibility(View.VISIBLE);
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
                            .add(R.id.container, new SelectAppFragment(), Constants.TAG_SELECT_APP_FRAGMENT)
                            .addToBackStack(Constants.TAG_SELECT_APP_FRAGMENT)
                            .commit();

                    getSupportFragmentManager().executePendingTransactions();
                } else {
                    FacebookUtils.showLoginFragment(this);
                }

                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        if (FacebookUtils.isSessionOpen()) {
            menu.findItem(R.id.action_login).setVisible(false);
            menu.findItem(R.id.action_logout).setVisible(true);
        } else {
            menu.findItem(R.id.action_login).setVisible(true);
            menu.findItem(R.id.action_logout).setVisible(false);
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
                FacebookUtils.showLoginFragment(this);
                break;

            case R.id.action_settings:
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.abc_fade_in, R.anim.alpha_out)
                        .add(R.id.container, new SettingsFragment(), Constants.TAG_SETTINGS_FRAGMENT)
                        .addToBackStack(Constants.TAG_SETTINGS_FRAGMENT)
                        .commit();
                break;

            case R.id.action_share:
                if (FacebookDialog.canPresentShareDialog(getApplicationContext(),
                        FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
                    FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
                            .setName("AppHunt")
                            .setPicture("https://launchrock-assets.s3.amazonaws.com/logo-files/LWPRHM35_1421410706452.png?_=4")
                            .setLink(Constants.GOOGLE_PLAY_APP_URL).build();
                    uiHelper.trackPendingDialogCall(shareDialog.present());
                }
                break;

            case android.R.id.home:
                getSupportFragmentManager().popBackStack();
                break;
        }

        return true;
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
                Log.e(TAG, String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                Log.i(TAG, "Success!");
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
        if (ConnectivityUtils.isNetworkAvailable(this)) {
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

        while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (AppHuntApiClient.getExecutorService().isShutdown()) {
            AppHuntApiClient.getExecutorService().shutdown();
        }
    }
}
