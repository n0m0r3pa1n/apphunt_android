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
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.GetTopHuntersCollectionApiEvent;
import com.apphunt.app.ui.adapters.rankings.TopHuntersAdapter;
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
public class TopHuntersFragment extends BaseFragment {

    private static final String TAG = TopHuntersFragment.class.getSimpleName();
    private Activity activity;
    private String title;

    private Calendar calendar = Calendar.getInstance();
    private int selectedMonth = 0;
    private int selectedYear = 0;
    private int currentYear = 2015;
    private int nextYear = 2016;
    private int lastAvailableMonthWithTopApps = 4;

    private DialogInterface.OnClickListener datePickedListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            FlurryAgent.logEvent(TrackingEvents.UserViewedPreviousTopHuntersRanking);
            if(selectedYear == currentYear || (selectedYear == nextYear && selectedMonth < lastAvailableMonthWithTopApps)) {
                ApiClient.getClient(activity).getTopHuntersCollection(StringUtils.getMonthStringFromCalendar(selectedMonth, false));
            } else {
                ApiClient.getClient(activity).getTopHuntersCollection(StringUtils.getMonthStringFromCalendar(selectedMonth, false) +
                        "%20" + StringUtils.getYearStringFromCalendar(selectedYear));
            }
        }
    };

    @InjectView(R.id.collection_hunters_list)
    RecyclerView collectionHuntersList;

    @InjectView(R.id.vs_no_hunters)
    RelativeLayout noHuntersViewStub;

    public TopHuntersFragment() {
        setFragmentTag(Constants.TAG_TOP_HUNTERS_FRAGMENT);
        FlurryAgent.logEvent(TrackingEvents.UserViewedTopHunters);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        selectedMonth = calendar.get(Calendar.MONTH) - 1;
        selectedYear = calendar.get(Calendar.YEAR);

        int previousMonth = Calendar.getInstance().get(Calendar.MONTH) - 1;
        ApiClient.getClient(activity).getTopHuntersCollection(StringUtils.getMonthStringFromCalendar(previousMonth, false));
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_hunters, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        BusProvider.getInstance().register(this);
        this.activity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onCollectionReceived(GetTopHuntersCollectionApiEvent event) {
        if(event.getHuntersCollections() == null
                || event.getHuntersCollections().getCollections() == null
                || event.getHuntersCollections().getCollections().size() == 0) {
            collectionHuntersList.setVisibility(View.INVISIBLE);
            noHuntersViewStub.setVisibility(View.VISIBLE);
            ((TextView)noHuntersViewStub.findViewById(R.id.score_text)).setText("There is no ranking available for " +
                    StringUtils.getMonthStringFromCalendar(selectedMonth, false) + " " + selectedYear);
            return;
        } else {
            collectionHuntersList.setVisibility(View.VISIBLE);
            noHuntersViewStub.setVisibility(View.INVISIBLE);
        }
        collectionHuntersList.setItemAnimator(new DefaultItemAnimator());
        collectionHuntersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        collectionHuntersList.setHasFixedSize(true);
        collectionHuntersList.setAdapter(new TopHuntersAdapter(activity, event.getHuntersCollections().getCollections().get(0)));
        title = event.getHuntersCollections().getCollections().get(0).getName();
        ActionBarUtils.getInstance().setTitle(title);
    }

    @Override
    public String getStringTitle() {
        return title;
    }
}
