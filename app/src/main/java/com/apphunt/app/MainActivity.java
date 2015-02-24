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
import android.widget.Button;
import android.widget.ListView;

import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.smart_rate.SmartRate;
import com.apphunt.app.smart_rate.variables.RateDialogVariable;
import com.apphunt.app.ui.adapters.TrendingAppsAdapter;
import com.apphunt.app.ui.fragments.InviteFragment;
import com.apphunt.app.ui.fragments.SaveAppFragment;
import com.apphunt.app.ui.fragments.SelectAppFragment;
import com.apphunt.app.ui.fragments.SettingsFragment;
import com.apphunt.app.ui.fragments.SuggestFragment;
import com.apphunt.app.ui.interfaces.OnAppSelectedListener;
import com.apphunt.app.ui.interfaces.OnNetworkStateChange;
import com.apphunt.app.ui.interfaces.OnUserAuthListener;
import com.apphunt.app.utils.ActionBarUtils;
import com.apphunt.app.utils.ConnectivityUtils;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.FacebookUtils;
import com.apphunt.app.utils.LoadersUtils;
import com.apphunt.app.utils.NotificationsUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.TrackingEvents;
import com.crashlytics.android.Crashlytics;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.shamanland.fab.FloatingActionButton;
import com.squareup.otto.Subscribe;

import java.util.Random;

import it.appspice.android.AppSpice;
import it.appspice.android.api.errors.AppSpiceError;

public class MainActivity extends ActionBarActivity implements AbsListView.OnScrollListener, OnClickListener,
        OnAppSelectedListener, OnUserAuthListener, OnNetworkStateChange {

    private static final String TAG = MainActivity.class.getName();

    private ListView trendingAppsList;
    private FloatingActionButton addAppButton;
    private Button reloadButton;
    private TrendingAppsAdapter trendingAppsAdapter;
    private boolean endOfList = false;
    private boolean isBlocked = false;

    private UiLifecycleHelper uiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_main);

        SmartRate.init(this, "ENTER TOKEN HERE", Constants.APP_SPICE_APP_ID);

        boolean isStartedFromNotification = getIntent().getBooleanExtra(Constants.KEY_DAILY_REMINDER_NOTIFICATION, false);
        if (isStartedFromNotification) {
            AppSpice.createEvent(TrackingEvents.UserStartedAppFromDailyTrendingAppsNotification).track();
        }

        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);

        LoginProviderFactory.get(this).onCreate(this, savedInstanceState);
        initUI();

        sendBroadcast(new Intent(Constants.ACTION_ENABLE_NOTIFICATIONS));
//        showStartFragments(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showStartFragments(intent);
    }

    private boolean isStartedFromShareIntent(Intent intent) {
        return Intent.ACTION_SEND.equals(intent.getAction());
    }

    private void showInviteFragment() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.abc_fade_in, R.anim.alpha_out)
                .add(R.id.container, new InviteFragment(), Constants.TAG_INVITE_FRAGMENT)
                .commit();
    }

    private void initUI() {
        ActionBarUtils.getInstance().init(this);

        addAppButton = (FloatingActionButton) findViewById(R.id.add_app);
        addAppButton.setOnClickListener(this);

        trendingAppsList = (ListView) findViewById(R.id.trending_list);

        trendingAppsAdapter = new TrendingAppsAdapter(this, trendingAppsList);
        trendingAppsList.setAdapter(trendingAppsAdapter);
        trendingAppsList.setOnScrollListener(this);

        reloadButton = (Button) findViewById(R.id.reload);
        reloadButton.setOnClickListener(this);

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
                trendingAppsList.setVisibility(View.GONE);
                trendingAppsAdapter.clearAdapter();
                LoadersUtils.showCenterLoader(MainActivity.this);
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
                startSelectAppFragment();

                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                break;

            case R.id.reload:
                v.setVisibility(View.GONE);
                trendingAppsAdapter.resetAdapter();
                addAppButton.setVisibility(View.VISIBLE);
                trendingAppsList.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void startSelectAppFragment() {
        if (LoginProviderFactory.get(this).isUserLoggedIn()) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.bounce, R.anim.slide_out_top)
                    .add(R.id.container, new SelectAppFragment(), Constants.TAG_SELECT_APP_FRAGMENT)
                    .addToBackStack(Constants.TAG_SELECT_APP_FRAGMENT)
                    .commit();

            getSupportFragmentManager().executePendingTransactions();
        } else {
            FacebookUtils.showLoginFragment(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        if (LoginProviderFactory.get(this).isUserLoggedIn()) {
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
                    shareDialog.present();
                    AppSpice.createEvent(TrackingEvents.UserSharedAppHunt).track();
                }
                break;

            case R.id.action_suggest:
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.abc_fade_in, R.anim.alpha_out)
                        .add(R.id.container, new SuggestFragment(), Constants.TAG_SUGGEST_FRAGMENT)
                        .addToBackStack(Constants.TAG_SUGGEST_FRAGMENT)
                        .commit();
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
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        endOfList = (firstVisibleItem + visibleItemCount) == totalItemCount;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (ConnectivityUtils.isNetworkAvailable(this)) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                Animation slideInBottom = AnimationUtils.loadAnimation(this, R.anim.abc_slide_in_bottom);

                addAppButton.startAnimation(slideInBottom);
                addAppButton.setVisibility(View.VISIBLE);

                if (endOfList) {
                    AppSpice.createEvent(TrackingEvents.UserScrolledDownAppList).track();
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

    public void setOnBackBlocked(boolean isBlocked) {
        if (isBlocked) {
            ActionBarUtils.getInstance().hideActionBar(this);
        } else {
            ActionBarUtils.getInstance().showActionBar(this);
        }
        this.isBlocked = isBlocked;
    }

    @Override
    public void onUserLogin() {
        setOnBackBlocked(false);
        LoadersUtils.hideBottomLoader(this);
        trendingAppsAdapter.resetAdapter();
    }

    @Override
    public void onUserLogout() {
        setOnBackBlocked(false);
        LoadersUtils.hideBottomLoader(this);
        trendingAppsAdapter.resetAdapter();
    }

    @Override
    public void onNetworkAvailable() {
        if (trendingAppsAdapter.getCount() == 0) {
            LoadersUtils.hideCenterLoader(this);
            reloadButton.setVisibility(View.VISIBLE);
        }
    }
    
    public void resetAdapter(int itemPosition) {
        trendingAppsAdapter.resetAdapter();
        trendingAppsList.smoothScrollToPosition(itemPosition);
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();
        AppSpice.onResume(this);

        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void showStartFragments(Intent intent) {
        if (isStartedFromShareIntent(intent)) {
            startSelectAppFragment();
        }

        if (SharedPreferencesHelper.getIntPreference(this, Constants.KEY_INVITE_SHARE, Constants.INVITE_SHARES_COUNT) > 0) {

            Random random = new Random();
            int randInt = random.nextInt(100);
            if (randInt > Constants.USER_SKIP_INVITE_PERCENTAGE) {
                Intent splashIntent = new Intent(this, SplashActivity.class);
                startActivity(splashIntent);
                AppSpice.createEvent(TrackingEvents.AppShowedInviteScreen).track();
                showInviteFragment();
            } else {
                SharedPreferencesHelper.setPreference(this, Constants.KEY_INVITE_SHARE, 0);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        AppSpice.onPause(this);
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!isBlocked) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppSpice.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppSpice.onStop(this);
    }

    @Subscribe
    public void onRateDialogVariableReady(RateDialogVariable rateDialogVariable) {
        SmartRate.setRateDialogVariable(rateDialogVariable);
    }

    @Subscribe
    public void onAppSpiceError(AppSpiceError error) {
        SmartRate.onError();
    }
}
