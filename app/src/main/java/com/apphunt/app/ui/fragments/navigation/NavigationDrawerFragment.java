package com.apphunt.app.ui.fragments.navigation;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.apphunt.app.R;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.ui.DrawerStatusEvent;
import com.apphunt.app.event_bus.events.ui.auth.LoginEvent;
import com.apphunt.app.event_bus.events.ui.auth.LogoutEvent;
import com.apphunt.app.ui.adapters.DrawerItemAdapter;
import com.apphunt.app.ui.interfaces.OnItemClickListener;
import com.apphunt.app.ui.models.drawer.DrawerItem;
import com.apphunt.app.ui.models.drawer.DrawerLabel;
import com.apphunt.app.ui.models.drawer.DrawerMenu;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static com.apphunt.app.constants.Constants.COLLECTIONS;
import static com.apphunt.app.constants.Constants.HELP_ADD_APP;
import static com.apphunt.app.constants.Constants.HELP_APPS_REQUIREMENTS;
import static com.apphunt.app.constants.Constants.HELP_TOP_HUNTERS_POINTS;
import static com.apphunt.app.constants.Constants.SETTINGS;
import static com.apphunt.app.constants.Constants.SUGGESTIONS;
import static com.apphunt.app.constants.Constants.TOP_APPS;
import static com.apphunt.app.constants.Constants.TOP_HUNTERS;
import static com.apphunt.app.constants.Constants.TRENDING_APPS;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment implements OnItemClickListener {
    public static final String TAG = NavigationDrawerFragment.class.getSimpleName();
    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private DrawerItemAdapter adapter;
    private RecyclerView mDrawerList;

    private View mFragmentContainerView;
    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private static NavigationDrawerFragment instance;
    private int selectedPosition = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        mDrawerList = (RecyclerView) view.findViewById(R.id.drawerList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDrawerList.setLayoutManager(layoutManager);
        mDrawerList.setHasFixedSize(true);

        adapter = new DrawerItemAdapter(getActivity(), getMenu());
        adapter.setOnItemClickListener(this);
        mDrawerList.setAdapter(adapter);

        return view;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public ActionBarDrawerToggle getActionBarDrawerToggle() {
        return mActionBarDrawerToggle;
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public List<DrawerItem> getMenu() {
        List<DrawerItem> items = new ArrayList<DrawerItem>();
        String[] menuItems = getResources().getStringArray(R.array.drawer_menu);
        items.add(new DrawerItem(DrawerItem.Type.HEADER));
        for (int i = 0; i < menuItems.length; i++) {
            if(i == menuItems.length - 4) {
                items.add(new DrawerItem(DrawerItem.Type.DIVIDER));
                items.add(new DrawerLabel(menuItems[i]));
                continue;
            }

            items.add(new DrawerMenu().setIconRes(getIcon(i)).setText(menuItems[i]));
            if(menuItems[i].equals(menuItems[3])) {
                items.add(new DrawerItem(DrawerItem.Type.DIVIDER));
            }
        }

        return items;
    }

    public int getIcon(int position) {
        switch(position + 1) {
            case TRENDING_APPS:
                return R.drawable.ic_home;
            case TOP_HUNTERS:
                return R.drawable.ic_tophunters;
            case TOP_APPS:
                return R.drawable.ic_topandroid;
            case COLLECTIONS:
                return R.drawable.ic_collecton_navigation;
            case SUGGESTIONS - 1:
                return R.drawable.ic_feedback;
            case SETTINGS - 1:
                return R.drawable.ic_settings;
            case HELP_ADD_APP - 2:
                return R.drawable.ic_help_item;
            case HELP_APPS_REQUIREMENTS - 2:
                return R.drawable.ic_help_item;
            case HELP_TOP_HUNTERS_POINTS - 2:
                return R.drawable.ic_help_item;
        }

        return R.drawable.ic_menu_check;
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setup(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = (View) getActivity().findViewById(fragmentId).getParent();
        mDrawerLayout = drawerLayout;

        mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.bg_primary));
        mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) return;
                getActivity().supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) return;
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu();
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mActionBarDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        ((DrawerItemAdapter) mDrawerList.getAdapter()).selectPosition(Constants.TRENDING_APPS);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
//        ((DrawerItemAdapter) mDrawerList.getAdapter()).selectPosition(position);
        this.selectedPosition = position;
    }

    public void markSelectedPosition(int position) {
        ((DrawerItemAdapter) mDrawerList.getAdapter()).selectPosition(position);
    }

    public int getSelectedItemIndex() {
        return selectedPosition;
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(mFragmentContainerView);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mFragmentContainerView);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View view, int position) {
        selectItem(position);
    }

    @Subscribe
    public void onDrawerStatusEvent(DrawerStatusEvent event) {
        if(event.shouldLockDrawer()) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    public static void setDrawerIndicatorEnabled(boolean isEnabled) {
        ValueAnimator anim;
        if (isEnabled) {
            anim = ValueAnimator.ofFloat(0, 1);
        } else {
            anim = ValueAnimator.ofFloat(1, 0);
        }
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float slideOffset = (Float) valueAnimator.getAnimatedValue();
                instance.getActionBarDrawerToggle().onDrawerSlide(instance.getDrawerLayout(), slideOffset);
            }
        });
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(500);
        anim.start();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onUserLogin(LoginEvent event) {
        adapter = new DrawerItemAdapter(getActivity(), getMenu());
        adapter.setOnItemClickListener(this);

        mDrawerList.setAdapter(adapter);
        markSelectedPosition(selectedPosition);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onUserLogout(LogoutEvent event) {
        adapter = new DrawerItemAdapter(getActivity(), getMenu());
        adapter.setOnItemClickListener(this);

        mDrawerList.setAdapter(adapter);
        markSelectedPosition(selectedPosition);
    }
}
