package com.apphunt.app.ui.fragments.collections;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.api.apphunt.models.collections.NewCollection;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.CreateCollectionEvent;
import com.apphunt.app.utils.ui.NotificationsUtils;
import com.squareup.otto.Subscribe;

import me.gujun.android.taggroup.TagGroup;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 6/26/15.
 * *
 * * NaughtySpirit 2015
 */
public class CreateCollectionFragment extends Fragment implements OnClickListener {

    private Activity activity;
    private View view;
    private TextInputLayout collectionNameLayout;
    private TextInputLayout collectionDescLayout;
    private EditText collectionName;
    private EditText collectionDesc;
    private TagGroup tagGroup;
    private Button saveCollection;
    private ImageButton chooseBanner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_collection, container, false);

        initUI();

        return view;
    }

    public void initUI() {
        collectionNameLayout = (TextInputLayout) view.findViewById(R.id.collection_name_layout);
        collectionName = (EditText) view.findViewById(R.id.collection_name);

        collectionDescLayout = (TextInputLayout) view.findViewById(R.id.collection_desc_layout);
        collectionDesc = (EditText) view.findViewById(R.id.collection_desc);

        collectionNameLayout.setErrorEnabled(true);
        collectionDescLayout.setErrorEnabled(true);

        saveCollection = (Button) view.findViewById(R.id.collection_save);
        chooseBanner = (ImageButton) view.findViewById(R.id.choose_banner);

        saveCollection.setOnClickListener(this);
        chooseBanner.setOnClickListener(this);

        tagGroup = (TagGroup) view.findViewById(R.id.tag_group);
        tagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String s) {

            }
        });
        tagGroup.setOnTagChangeListener(new TagGroup.OnTagChangeListener() {
            @Override
            public void onAppend(TagGroup tagGroup, String s) {

            }

            @Override
            public void onDelete(TagGroup tagGroup, String s) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.collection_save:
                NewCollection collection = new NewCollection();
                collection.setName(collectionName.getText().toString());
                collection.setDescription(collectionDesc.getText().toString());
                collection.setPicture("http://1.bp.blogspot.com/-Bi4mi7nN3Zg/T2HOV6mAHtI/AAAAAAAAAfA/YOh046D0Xa8/s1600/Beautiful_Nature_1280x1024_Wallpaper.jpg");
                collection.setUserId(LoginProviderFactory.get(activity).getUser().getId());

                ApiClient.getClient(activity).createCollection(collection);

                break;

            case R.id.choose_banner:

                break;

        }
    }

    @Subscribe
    public void onCollectionCreateSuccess(CreateCollectionEvent event) {
        ((FragmentActivity) activity).getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = activity;
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        BusProvider.getInstance().unregister(this);
    }


}
