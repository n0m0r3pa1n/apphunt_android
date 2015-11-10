package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.GetTopAppsCollectionApiEvent;
import com.apphunt.app.ui.adapters.rankings.TopAppsAdapter;
import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.ui.views.MonthYearPicker;
import com.apphunt.app.utils.StringUtils;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.flurry.android.FlurryAgent;
import com.squareup.otto.Subscribe;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 5/26/15.
 * *
 * * NaughtySpirit 2015
 */
public class TopAppsFragment extends BaseFragment {

    private static final String TAG = TopAppsFragment.class.getSimpleName();

    private Activity activity;
    private View view;
    private LinearLayoutManager layoutManager;
    private String title;

    private Calendar calendar = Calendar.getInstance();
    private int selectedMonth = 0;
    private int selectedYear = 0;
    private int currentYear = 2015;
    private int nextYear = 2016;
    private int lastAvailableMonthWithTopApps = 4;


    @InjectView(R.id.collection_apps_list)
    RecyclerView collectionAppsList;

    @InjectView(R.id.vs_no_apps)
    RelativeLayout noAppsViewStub;

    private DialogInterface.OnClickListener datePickedListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            FlurryAgent.logEvent(TrackingEvents.UserViewedPreviousTopAppsRanking);
            if(selectedYear == currentYear || (selectedYear == nextYear && selectedMonth < lastAvailableMonthWithTopApps)) {
                ApiClient.getClient(activity).getTopAppsCollection(StringUtils.getMonthStringFromCalendar(selectedMonth, false),
                        LoginProviderFactory.get(getActivity()).getUser().getId());
            } else {
                ApiClient.getClient(activity).getTopAppsCollection(StringUtils.getMonthStringFromCalendar(selectedMonth, false) +
                                "%20" + StringUtils.getYearStringFromCalendar(selectedYear)
                        ,
                        LoginProviderFactory.get(getActivity()).getUser().getId());
            }
        }
    };

    public TopAppsFragment() {
        setFragmentTag(Constants.TAG_TOP_APPS_FRAGMENT);
        FlurryAgent.logEvent(TrackingEvents.UserViewedTopApps);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        selectedMonth = calendar.get(Calendar.MONTH) - 1;
        selectedYear = calendar.get(Calendar.YEAR);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_top_apps, container, false);
        ButterKnife.inject(this, view);

        layoutManager = new LinearLayoutManager(getActivity());
        collectionAppsList.setItemAnimator(new DefaultItemAnimator());
        collectionAppsList.setLayoutManager(layoutManager);
        collectionAppsList.setHasFixedSize(true);

        int previousMonth = Calendar.getInstance().get(Calendar.MONTH) - 1;
        ApiClient.getClient(activity).getTopAppsCollection(StringUtils.getMonthStringFromCalendar(previousMonth, false),
                LoginProviderFactory.get(getActivity()).getUser().getId());

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_date_picker).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                MonthYearPicker monthYearPicker = new MonthYearPicker(activity);
                monthYearPicker.build(selectedMonth, selectedYear, datePickedListener, null);
                monthYearPicker.setMonthValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        selectedMonth = newVal;
                    }
                });

                monthYearPicker.setYearValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        selectedYear = newVal;
                    }
                });

                monthYearPicker.show();
                return true;
            }
        });
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onCollectionReceived(GetTopAppsCollectionApiEvent event) {
        if(event.getAppsCollection() == null || event.getAppsCollection().getTotalCount() == 0) {
            collectionAppsList.setVisibility(View.INVISIBLE);
            noAppsViewStub.setVisibility(View.VISIBLE);
            ((TextView)noAppsViewStub.findViewById(R.id.score_text)).setText("There is no ranking available for " +
                StringUtils.getMonthStringFromCalendar(selectedMonth, false) + " " + selectedYear);
         return;
        } else {
            collectionAppsList.setVisibility(View.VISIBLE);
            noAppsViewStub.setVisibility(View.GONE);
        }
        TopAppsAdapter adapter = new TopAppsAdapter(getActivity(), event.getAppsCollection().getCollections().get(0).getApps());
        collectionAppsList.setAdapter(adapter);
        title = event.getAppsCollection().getCollections().get(0).getName();
        ActionBarUtils.getInstance().setTitle(title);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public String getStringTitle() {
        return title;
    }
}
