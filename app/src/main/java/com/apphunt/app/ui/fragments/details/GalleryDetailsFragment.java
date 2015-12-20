package com.apphunt.app.ui.fragments.details;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.apps.App;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.apps.LoadAppDetailsApiEvent;
import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.ui.fragments.search.SearchAppsFragment;
import com.apphunt.app.ui.views.gallery.GalleryView;
import com.apphunt.app.utils.FlurryWrapper;
import com.squareup.otto.Subscribe;
import com.wefika.flowlayout.FlowLayout;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nmp on 15-12-15.
 */
public class    GalleryDetailsFragment extends BaseFragment {

    @InjectView(R.id.gallery)
    GalleryView gallery;

    @InjectView(R.id.tags_container)
    FlowLayout tagsContainer;


    public GalleryDetailsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_additional_details, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void onAppDetailsLoaded(LoadAppDetailsApiEvent event) {
        populateAppDetails(event.getBaseApp());
    }

    private void populateAppDetails(App app) {
        if(app == null) {
            return;
        }

        if(app.getScreenshots() == null || app.getScreenshots().size() == 0) {
            gallery.setVisibility(View.GONE);
        } else {
            gallery.setImages(app.getScreenshots());
        }

        populateTags(app);
    }

    private void populateTags(App app) {
        for(final String tag : app.getTags()) {
            View tagButtonView = getLayoutInflater(null).inflate(R.layout.view_tag_button, tagsContainer, false);
            TextView textView =  ((TextView)tagButtonView.findViewById(R.id.tag));
            textView.setText(tag);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlurryWrapper.logEvent(TrackingEvents.UserSearchedWithTagFromAppDetails, new HashMap<String, String>() {{
                        put("Tag", tag);
                    }});
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.container, SearchAppsFragment.newInstance(tag), Constants.TAG_SEARCH_APPS_FRAGMENT)
                            .addToBackStack(Constants.TAG_SEARCH_APPS_FRAGMENT)
                            .commit();
                }
            });

            Resources resources = getResources();
            FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, resources.getDimensionPixelSize(R.dimen.details_box_tag_height));
            params.setMargins(resources.getDimensionPixelSize(R.dimen.details_box_tag_margin_left), resources.getDimensionPixelSize(R.dimen.details_box_desc_padding_top), 0, 0);
            textView.setPadding(15, 0, 15, 0);
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                textView.setElevation(0);
            }

            textView.setLayoutParams(params);
            tagsContainer.addView(tagButtonView);
        }
    }
}
