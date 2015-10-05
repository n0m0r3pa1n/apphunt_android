package com.apphunt.app.ui.fragments.navigation;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.clients.rest.ApiService;
import com.apphunt.app.api.apphunt.clients.sockets.HistoryConnectionManager;
import com.apphunt.app.api.apphunt.models.users.HistoryEvent;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.users.GetUserHistoryApiEvent;
import com.apphunt.app.event_bus.events.ui.auth.LoginEvent;
import com.apphunt.app.event_bus.events.ui.auth.LogoutEvent;
import com.apphunt.app.event_bus.events.ui.history.UnseenHistoryEvent;
import com.apphunt.app.ui.adapters.HistoryAdapter;
import com.apphunt.app.ui.interfaces.OnEndReachedListener;
import com.apphunt.app.ui.listeners.EndlessRecyclerScrollListener;
import com.apphunt.app.ui.models.history.HistoryRowBuilder;
import com.apphunt.app.ui.models.history.row.HeaderHistoryRow;
import com.apphunt.app.ui.models.history.row.base.HistoryRowComponent;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RightDrawerFragment extends Fragment implements HistoryConnectionManager.OnRefreshListener {
    public static final String TAG = RightDrawerFragment.class.getSimpleName();

    @InjectView(R.id.history_events_list)
    RecyclerView historyEventsList;

    @InjectView(R.id.no_history)
    RelativeLayout noHistoryView;

    DrawerLayout drawerLayout;

    private AppCompatActivity activity;
    private LinearLayoutManager layoutManager;
    private HistoryAdapter adapter;
    private List<HistoryRowComponent> previousRows;

    private int historyRequestsCount = 0;
    private boolean isFirstRequest = true;
    private int previousItemsCount;
    private String fromDate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
        if (LoginProviderFactory.get(activity).isUserLoggedIn()) {
            ApiClient.getClient(activity).getUserHistory(LoginProviderFactory.get(activity).getUser().getId(), new Date());
            HistoryConnectionManager.getInstance().addRefreshListener(this);
            HistoryConnectionManager.getInstance().emitAddUser(LoginProviderFactory.get(activity).getUser().getId());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_right_drawer, container, false);
        ButterKnife.inject(this, view);

        layoutManager = new LinearLayoutManager(getActivity());
        historyEventsList.setLayoutManager(layoutManager);
        historyEventsList.addOnScrollListener(new EndlessRecyclerScrollListener(new OnEndReachedListener() {
            @Override
            public void onEndReached() {
                if (isFirstRequest) {
                    isFirstRequest = false;
                    return;
                }
                ApiService.getInstance(activity).loadHistoryForPreviousDate();
            }
        }, layoutManager));

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
        if (LoginProviderFactory.get(activity).isUserLoggedIn()) {
            HistoryConnectionManager.getInstance().removeRefreshListener(this);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (AppCompatActivity) activity;
    }

    @Override
    public void onRefresh(final HistoryEvent event) {
        if (isDrawerClosed()) {
            BusProvider.getInstance().post(new UnseenHistoryEvent(1));
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                noHistoryView.setVisibility(View.GONE);
                historyRequestsCount = 0;
                HistoryRowComponent row = HistoryRowBuilder.build(activity, fromDate, event);
                row.setIsUnseen(true);
                if (adapter == null) {
                    adapter = new HistoryAdapter(activity, row);
                    historyEventsList.setAdapter(adapter);
                } else {
                    adapter.addRow(row);
                }
            }
        });

    }

    @Override
    public void onUnseenEvents(final List<String> eventIds) {
        BusProvider.getInstance().post(new UnseenHistoryEvent(eventIds.size()));
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.markUnseenEvents(eventIds);
            }
        });
    }

    @Subscribe
    public void onUserLogin(LoginEvent loginEvent) {
        HistoryConnectionManager.getInstance().addRefreshListener(this);
        HistoryConnectionManager.getInstance().emitAddUser(LoginProviderFactory.get(activity).getUser().getId());
        ApiService.getInstance(activity).loadHistoryForToday();
    }

    @Subscribe
    public void onUserLogout(LogoutEvent logoutEvent) {
        adapter.reset();
        noHistoryView.setVisibility(View.VISIBLE);
        HistoryConnectionManager.getInstance().removeRefreshListener(this);
    }


    @Subscribe
    public void onUserHistory(GetUserHistoryApiEvent event) {
        if (historyRequestsCount >= 6) {
            return;
        }

        if (event.getEventsList().getEvents() == null || event.getEventsList().getEvents().size() == 0) {
            historyRequestsCount++;
            ApiService.getInstance(activity).loadHistoryForPreviousDate();
            return;
        }

        List<HistoryRowComponent> rows = new ArrayList<>();
        List<HistoryEvent> events = event.getEventsList().getEvents();
        fromDate = event.getEventsList().getFromDate();

        rows.add(new HeaderHistoryRow(event.getEventsList().getFromDate()));
        for (HistoryEvent e : events) {
            rows.add(HistoryRowBuilder.build(activity, fromDate, e));
        }

        if (adapter == null) {
            adapter = new HistoryAdapter(activity, rows);
            historyEventsList.setAdapter(adapter);

            String lastSeenEventDate = SharedPreferencesHelper.getStringPreference(Constants.KEY_LAST_SEEN_EVENT_DATE);
            String lastSeenEventId = SharedPreferencesHelper.getStringPreference(Constants.KEY_LAST_SEEN_EVENT_ID);
            if (lastSeenEventDate != null && lastSeenEventId != null) {
                HistoryConnectionManager.getInstance().emitLastSeenId(LoginProviderFactory.get(activity).getUser().getId(), lastSeenEventId, lastSeenEventDate);
            }
        } else {
            adapter.addRows(rows);
        }

        noHistoryView.setVisibility(View.GONE);

        Log.d(TAG, "onUserHistory " + event.getEventsList().getEvents().size());
    }

    public void setup(DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.bg_primary));
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                if (drawerView.getId() == R.id.fragment_right_drawer) {
                    updateLastSeenEvent();
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

    }

    public boolean isDrawerOpen() {
        return drawerLayout.isDrawerOpen(Gravity.RIGHT);
    }

    public void openDrawer() {
        BusProvider.getInstance().post(new UnseenHistoryEvent(UnseenHistoryEvent.RESET_COUNTER));
        drawerLayout.openDrawer(Gravity.RIGHT);
    }

    public void closeDrawer() {
        updateLastSeenEvent();
        drawerLayout.closeDrawer(Gravity.RIGHT);
    }

    private void updateLastSeenEvent() {
        if(adapter == null || adapter.isEmpty()) {
            return;
        }
        adapter.markEventsAsSeen();
        HistoryRowComponent row = adapter.getRow(1);
        SharedPreferencesHelper.setPreference(Constants.KEY_LAST_SEEN_EVENT_ID, row.getId());
        SharedPreferencesHelper.setPreference(Constants.KEY_LAST_SEEN_EVENT_DATE, row.getDate());
    }

    public boolean isDrawerClosed() {
        return !drawerLayout.isDrawerOpen(Gravity.RIGHT);
    }

}
