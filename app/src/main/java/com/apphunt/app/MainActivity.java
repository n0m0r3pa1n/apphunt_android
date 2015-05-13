package com.apphunt.app;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.api.apphunt.models.User;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.users.UserUpdatedApiEvent;
import com.apphunt.app.event_bus.events.ui.ClearSearchEvent;
import com.apphunt.app.event_bus.events.ui.HideFragmentEvent;
import com.apphunt.app.event_bus.events.ui.ShowNotificationEvent;
import com.apphunt.app.event_bus.events.ui.auth.LoginEvent;
import com.apphunt.app.event_bus.events.ui.votes.AppVoteEvent;
import com.apphunt.app.smart_rate.SmartRate;
import com.apphunt.app.smart_rate.variables.RateDialogVariable;
import com.apphunt.app.ui.fragments.AppDetailsFragment;
import com.apphunt.app.ui.fragments.AppsListFragment;
import com.apphunt.app.ui.fragments.SaveAppFragment;
import com.apphunt.app.ui.fragments.SettingsFragment;
import com.apphunt.app.ui.fragments.SuggestFragment;
import com.apphunt.app.ui.fragments.navigation.NavigationDrawerCallbacks;
import com.apphunt.app.ui.fragments.navigation.NavigationDrawerFragment;
import com.apphunt.app.ui.interfaces.OnAppSelectedListener;
import com.apphunt.app.ui.interfaces.OnUserAuthListener;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.LoginUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.TrackingEvents;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.apphunt.app.utils.ui.LoadersUtils;
import com.apphunt.app.utils.ui.NavUtils;
import com.apphunt.app.utils.ui.NotificationsUtils;
import com.facebook.widget.FacebookDialog;
import com.flurry.android.FlurryAgent;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

import it.appspice.android.AppSpice;
import it.appspice.android.api.errors.AppSpiceError;
import kr.nectarine.android.fruitygcm.FruityGcmClient;
import kr.nectarine.android.fruitygcm.interfaces.FruityGcmListener;

public class MainActivity extends ActionBarActivity implements
        OnAppSelectedListener, OnUserAuthListener, NavigationDrawerCallbacks {
    public static final String TAG = MainActivity.class.getSimpleName();

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;

    private String registrationId;
    private boolean isBlocked = false;
    private boolean isFirstLaunch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.fragment_drawer);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    if (!mNavigationDrawerFragment.isDrawerOpen()) {
                        mNavigationDrawerFragment.openDrawer();
                    } else {
                        mNavigationDrawerFragment.closeDrawer();
                    }

                    return;
                }

                onBackPressed();
            }
        });
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer));

        SmartRate.init(this, Constants.APP_SPICE_APP_ID);

        String notificationType = getIntent().getStringExtra(Constants.KEY_NOTIFICATION_TYPE);
        if (!TextUtils.isEmpty(notificationType)) {
            Map<String, String> params = new HashMap<>();
            params.put("type", notificationType);
            FlurryAgent.logEvent(TrackingEvents.UserStartedAppFromNotification, params);
        }

        updateNotificationIdIfNeeded();

        initUI();

        sendBroadcast(new Intent(Constants.ACTION_ENABLE_NOTIFICATIONS));
        showStartFragments(getIntent());
        ActionBarUtils.getInstance().configActionBar(this);
    }

    public void setDrawerIndicatorEnabled(boolean isEnabled) {
        if(isFirstLaunch) {
            isFirstLaunch = false;
            return;
        }
        ValueAnimator anim;
        if(isEnabled) {
            anim = ValueAnimator.ofFloat(1, 0);
        } else {
            anim = ValueAnimator.ofFloat(0, 1);
        }
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float slideOffset = (Float) valueAnimator.getAnimatedValue();
                mNavigationDrawerFragment.getActionBarDrawerToggle().onDrawerSlide(mNavigationDrawerFragment.getDrawerLayout(), slideOffset);
            }
        });
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(500);
        anim.start();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showStartFragments(intent);
    }

    private boolean isStartedFromShareIntent(Intent intent) {
        return Intent.ACTION_SEND.equals(intent.getAction());
    }

    private void initUI() {
        ActionBarUtils.getInstance().init(this);
        getSupportFragmentManager().beginTransaction().add(R.id.container, new AppsListFragment(), Constants.TAG_APPS_LIST_FRAGMENT).commit();

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                ActionBarUtils.getInstance().configActionBar(MainActivity.this);
            }
        });
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

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            menu.findItem(R.id.action_search).setVisible(false);
        } else {
            menu.findItem(R.id.action_search).setVisible(true);
        }

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Map<String, String> params = new HashMap<>();
                params.put("query", s);
                FlurryAgent.logEvent(TrackingEvents.UserSearchedForApp, params);
                ApiClient.getClient(getApplicationContext()).searchApps(s, SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID), 1, Constants.SEARCH_RESULT_COUNT,
                        Constants.PLATFORM);

                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (item.getItemId() == R.id.action_search) {
                    BusProvider.getInstance().post(new ClearSearchEvent());
                }
                return true;
            }
        });

        searchView.setIconifiedByDefault(true);
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_login:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0 &&
                        getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals(Constants.TAG_LOGIN_FRAGMENT))
                    break;

                LoginUtils.showLoginFragment(this);
                break;

            case R.id.action_logout:
                LoginProviderFactory.get(this).logout();
                FlurryAgent.logEvent(TrackingEvents.UserLoggedOut);
                supportInvalidateOptionsMenu();
                break;

            case R.id.action_settings:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0 &&
                        getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals(Constants.TAG_SETTINGS_FRAGMENT))
                    break;
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.abc_fade_in, R.anim.alpha_out)
                        .add(R.id.container, new SettingsFragment(), Constants.TAG_SETTINGS_FRAGMENT)
                        .addToBackStack(Constants.TAG_SETTINGS_FRAGMENT)
                        .commit();
                break;

            case R.id.action_share:
                if (FacebookDialog.canPresentShareDialog(getApplicationContext(),
                        FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
                    shareWithFacebook();
                    FlurryAgent.logEvent(TrackingEvents.UserSharedAppHuntWithFacebook);
                } else {
                    shareWithLocalApps();
                    FlurryAgent.logEvent(TrackingEvents.UserSharedAppHuntWithoutFacebook);
                }
                break;

            case R.id.action_suggest:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0 &&
                    getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals(Constants.TAG_SUGGEST_FRAGMENT))
                    break;
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.abc_fade_in, R.anim.alpha_out)
                        .add(R.id.container, new SuggestFragment(), Constants.TAG_SUGGEST_FRAGMENT)
                        .addToBackStack(Constants.TAG_SUGGEST_FRAGMENT)
                        .commit();
                break;

            case android.R.id.home:
                AppDetailsFragment fragment = (AppDetailsFragment) getSupportFragmentManager().findFragmentByTag(Constants.TAG_APP_DETAILS_FRAGMENT);
                if (fragment != null && fragment.isVisible() && fragment.isCommentsBoxOpened()) {
                    fragment.showDetails();
                } else {
                    getSupportFragmentManager().popBackStack();
                }
                break;
        }

        return true;
    }

    private void shareWithLocalApps() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/html");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(getString(R.string.share_text)));
        startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }

    private void shareWithFacebook() {
        FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
                .setName("AppHunt")
                .setPicture("https://launchrock-assets.s3.amazonaws.com/logo-files/LWPRHM35_1421410706452.png?_=4")
                .setLink(Constants.GOOGLE_PLAY_APP_URL).build();
        shareDialog.present();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(Constants.TAG_LOGIN_FRAGMENT);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }



    @Override
    public void onAppSelected(ApplicationInfo data) {
        String curFragmentTag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();

        if (!curFragmentTag.equals(Constants.TAG_SAVE_APP_FRAGMENT)) {
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


        AppDetailsFragment fragment = (AppDetailsFragment) getSupportFragmentManager().findFragmentByTag(Constants.TAG_APP_DETAILS_FRAGMENT);
        if (fragment != null && fragment.isVisible()) {
            fragment.loadData();
        }
    }

    @Override
    public void onUserLogout() {
        setOnBackBlocked(false);
        LoadersUtils.hideBottomLoader(this);

        AppDetailsFragment fragment = (AppDetailsFragment) getSupportFragmentManager().findFragmentByTag(Constants.TAG_APP_DETAILS_FRAGMENT);
        if (fragment != null && fragment.isVisible()) {
            fragment.loadData();
        }
    }


    @Subscribe
    public void userVotedForAppEvent(AppVoteEvent event) {
        if(event.isVote()) {
            SmartRate.show(Constants.SMART_RATE_LOCATION_APP_VOTED);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        AppSpice.onResume(this);
    }

    private void showStartFragments(Intent intent) {
        if (isStartedFromShareIntent(intent)) {
            NavUtils.startSelectAppFragment(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
        AppSpice.onPause(this);

    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
            return;
        }

        if (!isBlocked) {
            AppDetailsFragment fragment = (AppDetailsFragment) getSupportFragmentManager().findFragmentByTag(Constants.TAG_APP_DETAILS_FRAGMENT);
            if (fragment != null && fragment.isVisible() && fragment.isCommentsBoxOpened()) {
                fragment.showDetails();
            } else {
                super.onBackPressed();
            }
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

    public void updateNotificationIdIfNeeded() {
        final String userId = SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID);
        String notificationId = SharedPreferencesHelper.getStringPreference(Constants.KEY_NOTIFICATION_ID);
        if (!TextUtils.isEmpty(userId) && TextUtils.isEmpty(notificationId)) {
            FruityGcmClient.start(this, Constants.GCM_SENDER_ID, new FruityGcmListener() {

                @Override
                public void onPlayServiceNotAvailable(boolean b) {
                }

                @Override
                public void onDeliverRegistrationId(final String regId, boolean b) {
                    registrationId = regId;
                    User user = new User();
                    user.setNotificationId(regId);
                    ApiClient.getClient(getApplicationContext()).updateUser(userId, user);

                }

                @Override
                public void onRegisterFailed() {
                }
            });
        }
    }

    @Subscribe
    public void onUserUpdated(UserUpdatedApiEvent event) {
        SharedPreferencesHelper.setPreference(Constants.KEY_NOTIFICATION_ID, registrationId);
    }


    @Subscribe
    @SuppressWarnings("unused")
    public void onRateDialogVariableReady(RateDialogVariable rateDialogVariable) {
        SmartRate.setRateDialogVariable(rateDialogVariable);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onAppSpiceError(AppSpiceError error) {
        SmartRate.onError();
    }

    @Subscribe
    public void onuserLogin(LoginEvent event) {
        onUserLogin();
        supportInvalidateOptionsMenu();
        updateNotificationIdIfNeeded();
    }

    @Subscribe
    public void onHideFragmentEvent(HideFragmentEvent event) {
        getSupportFragmentManager().popBackStack(event.getTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Subscribe
    public void showNotificationFragment(ShowNotificationEvent event) {
        NotificationsUtils.showNotificationFragment(this, event.getMessage(), false, true);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Toast.makeText(this, "Menu item selected -> " + position, Toast.LENGTH_SHORT).show();
    }
}
