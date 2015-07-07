package com.apphunt.app.ui.fragments.collections;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.collections.apps.CollectionBanner;
import com.apphunt.app.api.apphunt.models.collections.apps.CollectionBannersList;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.ui.collections.CollectionBannerSelectedEvent;
import com.apphunt.app.ui.adapters.collections.CollectionBannersAdapter;
import com.apphunt.app.ui.fragments.BaseFragment;
import com.apphunt.app.utils.ui.ActionBarUtils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 7/7/15.
 * *
 * * NaughtySpirit 2015
 */
public class ChooseCollectionBannerFragment extends BaseFragment {

    private static final String TAG = ChooseCollectionBannerFragment.class.getSimpleName();

    private AppCompatActivity activity;
    private View view;
    private CollectionBannersList list;

    @InjectView(R.id.banners_list)
    ListView bannersList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_choose_collection_banner, container, false);

        initUI();

        return view;
    }

    private void initUI() {
        ButterKnife.inject(this, view);
        ActionBarUtils.getInstance().setTitle(R.string.title_choose_collection_banner);

        // TODO: Test until server logic is created
        list = new CollectionBannersList();
        ArrayList<CollectionBanner> banners = new ArrayList<>();
        banners.add(new CollectionBanner("http://orble.com/images/nature91.jpg"));
        banners.add(new CollectionBanner("http://www.clubalfa.it/wp-content/uploads/2014/10/Alfa-Romeo-Giulia-giugno-2015-uscita.jpg"));
        banners.add(new CollectionBanner("http://2015carmodels.com/wp-content/uploads/2013/06/2015_Alfa_Romeo_Giulia_b2.jpg"));
        banners.add(new CollectionBanner("http://www.designboom.com/wp-content/uploads/2013/07/audi-RS-7-sportback-neckarsulm-designboom01.jpg"));

        list.setBanners(banners);

        bannersList.setAdapter(new CollectionBannersAdapter(activity, list));
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (!enter) {
            return AnimationUtils.loadAnimation(activity, R.anim.slide_out_right);
        }

        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @OnItemClick(R.id.banners_list)
    public void onBannersListItemSelected(int position) {
        BusProvider.getInstance().post(new CollectionBannerSelectedEvent(list.getBanners().get(position)));
        activity.getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = (AppCompatActivity) activity;
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        BusProvider.getInstance().unregister(this);
        ActionBarUtils.getInstance().setPreviousTitle();
    }
}
