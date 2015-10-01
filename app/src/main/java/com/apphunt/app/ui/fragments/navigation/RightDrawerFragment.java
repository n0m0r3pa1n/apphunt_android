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

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.clients.rest.ApiService;
import com.apphunt.app.api.apphunt.clients.sockets.HistoryConnectionManager;
import com.apphunt.app.api.apphunt.models.users.HistoryEvent;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.users.GetUserHistoryApiEvent;
import com.apphunt.app.ui.adapters.HistoryAdapter;
import com.apphunt.app.ui.models.history.HistoryRowBuilder;
import com.apphunt.app.ui.models.history.row.base.HistoryRow;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nmp on 15-9-20.
 */
public class RightDrawerFragment extends Fragment implements HistoryConnectionManager.OnRefreshListener {
    public static final String TAG = RightDrawerFragment.class.getSimpleName();

    @InjectView(R.id.history_events_list)
    RecyclerView historyEventsList;

    DrawerLayout drawerLayout;
    
    private AppCompatActivity activity;
    private HistoryAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
        if(LoginProviderFactory.get(activity).isUserLoggedIn()) {
            HistoryConnectionManager.getInstance().addRefreshListener(this);
            HistoryConnectionManager.getInstance().emitAddUser(LoginProviderFactory.get(activity).getUser().getId());
            ApiClient.getClient(activity).getUserHistory(LoginProviderFactory.get(activity).getUser().getId(), new Date());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_right_drawer, container, false);
        ButterKnife.inject(this, view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        historyEventsList.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
        if(LoginProviderFactory.get(activity).isUserLoggedIn()) {
            HistoryConnectionManager.getInstance().removeRefreshListener(this);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (AppCompatActivity) activity;
    }

    @Override
    public void onRefresh() {
        ApiService.getInstance(activity).loadHistoryForToday();
    }
    
    
    @Subscribe
    public void onUserHistory(GetUserHistoryApiEvent event) {
        List<HistoryRow> rows = new ArrayList<>();
        List<HistoryEvent> events = event.getEventsList().getEvents();
        for(HistoryEvent e : events) {
            rows.add(HistoryRowBuilder.build(activity, e));
        }

        if(adapter == null) {
            adapter = new HistoryAdapter(activity, rows);
        } else {
            adapter.addRows(rows);
        }

        historyEventsList.setAdapter(adapter);

        Log.d(TAG, "onUserHistory " + event.getEventsList().getEvents().size());
    }
    

    public void setup(DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.bg_primary));

    }

    public boolean isDrawerOpen() {
        return drawerLayout.isDrawerOpen(Gravity.RIGHT);
    }

    public void openDrawer() {
        drawerLayout.openDrawer(Gravity.RIGHT);
    }

    public void closeDrawer() {
        drawerLayout.closeDrawer(Gravity.RIGHT);
    }

    
}
