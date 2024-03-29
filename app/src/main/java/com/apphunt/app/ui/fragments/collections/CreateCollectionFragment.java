package com.apphunt.app.ui.fragments.collections;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.models.collections.NewCollection;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.events.api.collections.CreateCollectionApiEvent;
import com.apphunt.app.event_bus.events.api.tags.TagsSuggestionApiEvent;
import com.apphunt.app.event_bus.events.ui.collections.CollectionBannerSelectedEvent;
import com.apphunt.app.ui.fragments.base.BackStackFragment;
import com.apphunt.app.ui.views.widgets.TagGroup;
import com.apphunt.app.utils.FlurryWrapper;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.apphunt.app.utils.ui.NotificationsUtils;
import com.apptentive.android.sdk.Apptentive;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 6/26/15.
 * *
 * * NaughtySpirit 2015
 */
public class CreateCollectionFragment extends BackStackFragment implements ChooseCollectionBannerFragment.OnBannerChosenListener {

    private static final String TAG = CreateCollectionFragment.class.getSimpleName();

    private AppCompatActivity activity;
    private View view;
    private AutoCompleteTextView tagView;
    private String bannerUrl;

    @InjectView(R.id.collection_name_layout)
    TextInputLayout collectionNameLayout;

    @InjectView(R.id.collection_desc_layout)
    TextInputLayout collectionDescLayout;

    @InjectView(R.id.collection_name)
    EditText collectionName;

    @InjectView(R.id.collection_desc)
    EditText collectionDesc;

    @InjectView(R.id.collection_save)
    Button saveCollection;

    @InjectView(R.id.choose_banner)
    ImageButton chooseBanner;

    @InjectView(R.id.tags_container)
    TagGroup tagGroup;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_collection, container, false);

        initUI();

        return view;
    }

    public void initUI() {
        ButterKnife.inject(this, view);
        ActionBarUtils.getInstance().setTitle(R.string.title_create_collection);

        collectionNameLayout.setErrorEnabled(true);
        collectionDescLayout.setErrorEnabled(true);

        tagGroup.setOnTagTextEntryListener(new TagGroup.OnTagTextEntryListener() {
            @Override
            public void onTextEntry(AutoCompleteTextView view, String text) {
                tagView = view;
                if (!TextUtils.isEmpty(text)) ApiClient.getClient(activity).getTagsSuggestion(text);
            }
        });
    }

    @Override
    public int getTitle() {
        return R.string.title_create_collection;
    }

    @OnClick(R.id.choose_banner)
    public void onChooseBannerClick() {
        ChooseCollectionBannerFragment fragment = new ChooseCollectionBannerFragment();
        fragment.setOnBannerChosenListener(this);
        activity.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                .add(R.id.container, fragment, Constants.TAG_CHOOSE_COLLECTION_BANNER_FRAGMENT)
                .addToBackStack(Constants.TAG_CHOOSE_COLLECTION_BANNER_FRAGMENT)
                .commit();
    }

    @OnClick(R.id.collection_save)
    public void onSaveCollectionClick() {
        if(TextUtils.isEmpty(collectionName.getText().toString())) {
            collectionNameLayout.setError("Name can not be empty!");
            return;
        } else {
            collectionNameLayout.setError(null);
        }

        if(TextUtils.isEmpty(collectionDesc.getText().toString())) {
            FlurryWrapper.logEvent(TrackingEvents.UserTriedToCreateCollectionWithEmptyDesc);
            collectionDescLayout.setError("Description can not be empty!");
            return;
        } else {
            collectionDescLayout.setError(null);
        }

        FlurryWrapper.logEvent(TrackingEvents.UserCreatedCollection);

        NewCollection collection = new NewCollection();
        collection.setName(collectionName.getText().toString());
        collection.setDescription(collectionDesc.getText().toString());
        collection.setPicture(bannerUrl);
        collection.setUserId(LoginProviderFactory.get(activity).getUser().getId());

        if (tagGroup.getTags().length > 0) {
            collection.setTags(tagGroup.getTags());
        }

        ApiClient.getClient(activity).createCollection(collection);
    }

    @Subscribe
    public void onCollectionCreateSuccess(CreateCollectionApiEvent event) {
        activity.getSupportFragmentManager().popBackStack();
        NotificationsUtils.showNotificationFragment(activity, getString(R.string.notification_delete_confirmation), false, false);
        Apptentive.engage(activity, "user.created.collection");
    }

    @Subscribe
    public void onCollectionBannerSelected(CollectionBannerSelectedEvent event) {
        bannerUrl = event.getBannerUrl();
        Picasso.with(activity).load(bannerUrl).placeholder(R.drawable.collection_placeholder).into(chooseBanner);
    }

    @Subscribe
    public void onTagsSuggestionReceive(TagsSuggestionApiEvent event) {
        tagView.setAdapter(new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, event.getTags().getTags()));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = (AppCompatActivity) activity;
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        ActionBarUtils.getInstance().hideActionBarShadow();
    }

    @Override
    public void onBannerChosen(String url) {
        bannerUrl = url;
        Picasso.with(activity).load(bannerUrl).placeholder(R.drawable.collection_placeholder).into(chooseBanner);
    }
}