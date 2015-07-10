package com.apphunt.app.ui.views.collection;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.ui.collections.CollectionBannerSelectedEvent;
import com.apphunt.app.event_bus.events.ui.collections.EditCollectionEvent;
import com.apphunt.app.event_bus.events.ui.collections.SaveCollectionEvent;
import com.apphunt.app.ui.fragments.collections.ChooseCollectionBannerFragment;
import com.apphunt.app.ui.views.FavouriteCollectionButton;
import com.apphunt.app.ui.views.vote.CollectionVoteButton;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.apphunt.app.constants.Constants.*;

public class CollectionView extends RelativeLayout {
    private boolean areButtonsEnabled;

    private LayoutInflater inflater;
    private AppsCollection appsCollection;

    @InjectView(R.id.apps_left)
    TextView appsLeft;

    @InjectView(R.id.banner)
    ImageView banner;

    @InjectView(R.id.collection_name)
    TextView name;

    @InjectView(R.id.edit_collection_name)
    EditText editName;

    @InjectView(R.id.created_by_image)
    Target createdByAvatar;

    @InjectView(R.id.created_by)
    TextView createdBy;

    @InjectView(R.id.collection_status)
    ImageView status;

    @InjectView(R.id.separator)
    View separator;

    @InjectView(R.id.vote_btn)
    CollectionVoteButton voteButton;

    @InjectView(R.id.favourite_collection)
    FavouriteCollectionButton favouriteButton;

    @InjectView(R.id.edit_banner)
    ImageView editBanner;

    public CollectionView(Context context) {
        super(context);
        if(!isInEditMode()) {
            init(null);
        }
    }

    public CollectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode()) {
            init(attrs);
        }
    }

    public CollectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!isInEditMode()) {
            init(attrs);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CollectionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if(!isInEditMode()) {
            init(attrs);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        BusProvider.getInstance().unregister(this);
    }

    private void init(AttributeSet attrs) {
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_collection, this, true);
        ButterKnife.inject(this, view);

        TypedArray array = getContext().getTheme().obtainStyledAttributes(attrs,
                R.styleable.CollectionView, 0, 0);

        areButtonsEnabled= array.getBoolean(R.styleable.CollectionView_buttonsEnabled, true);
    }

    @Subscribe
    public void onEditCollection(EditCollectionEvent event) {
        if(!appsCollection.getId().equals(event.getCollectionId())) {
            return;
        }

        name.setVisibility(View.INVISIBLE);
        editBanner.setVisibility(View.VISIBLE);
        editName.setVisibility(View.VISIBLE);
        editName.setText(appsCollection.getName());
    }

    @Subscribe
    public void onSaveCollection(SaveCollectionEvent event) {
        if(!appsCollection.getId().equals(event.getCollectionId())) {
            return;
        }

        appsCollection.setName(editName.getText().toString());
        name.setText(appsCollection.getName());
        name.setVisibility(View.VISIBLE);
        editBanner.setVisibility(View.GONE);
        editName.setVisibility(View.GONE);
    }

    @Subscribe
    public void onCollectionBannerSelected(CollectionBannerSelectedEvent event) {
        String bannerUrl = event.getBannerUrl();
        Picasso.with(getContext()).load(bannerUrl).placeholder(R.drawable.collection_placeholder).into(banner);
        appsCollection.setPicture(bannerUrl);
    }

    @OnClick(R.id.edit_banner)
    public void editBanner() {
        ChooseCollectionBannerFragment fragment = new ChooseCollectionBannerFragment();
        ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                .add(R.id.container, fragment, Constants.TAG_CHOOSE_COLLECTION_BANNER_FRAGMENT)
                .addToBackStack(Constants.TAG_CHOOSE_COLLECTION_BANNER_FRAGMENT)
                .commit();
    }




    public void setCollection(AppsCollection collection, boolean areButtonsEnabled) {
        this.areButtonsEnabled = areButtonsEnabled;
        this.appsCollection = collection;
        if(collection.getStatus() == CollectionStatus.DRAFT) {
            int appsLeftCount = MIN_COLLECTION_APPS_SIZE - collection.getApps().size();
            String text = "You need " + getResources().getQuantityString(R.plurals.appsLeft, appsLeftCount, appsLeftCount);
            appsLeft.setText(text);
            setVisibilityWhenDraft();
        } else {
            if(collection.isOwnedByCurrentUser((Activity) getContext())) {
                favouriteButton.setVisibility(View.INVISIBLE);
            } else {
                favouriteButton.setVisibility(View.VISIBLE);
            }

            setVisibilityWhenPublic();
        }

        favouriteButton.setCollection(collection);
        voteButton.setCollection(collection);
        name.setText(collection.getName());
        createdBy.setText(collection.getCreatedBy().getUsername());
        Picasso.with(getContext()).load(collection.getCreatedBy().getProfilePicture()).into(createdByAvatar);
        Picasso.with(getContext()).load(collection.getPicture()).into(banner);
    }

    public AppsCollection getCollection() {
        return appsCollection;
    }

    private void setVisibilityWhenPublic() {
        appsLeft.setVisibility(View.INVISIBLE);
        status.setVisibility(View.INVISIBLE);
        separator.setVisibility(View.INVISIBLE);
        if(areButtonsEnabled) {
            voteButton.setVisibility(View.VISIBLE);
        }
    }

    private void setVisibilityWhenDraft() {
        appsLeft.setVisibility(View.VISIBLE);
        status.setVisibility(View.VISIBLE);
        separator.setVisibility(View.VISIBLE);
        voteButton.setVisibility(View.INVISIBLE);
    }
}
