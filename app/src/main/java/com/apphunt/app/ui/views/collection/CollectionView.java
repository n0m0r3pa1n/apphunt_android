package com.apphunt.app.ui.views.collection;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.UpdateCollectionApiEvent;
import com.apphunt.app.event_bus.events.ui.collections.CollectionBannerSelectedEvent;
import com.apphunt.app.event_bus.events.ui.collections.EditCollectionEvent;
import com.apphunt.app.ui.fragments.collections.ChooseCollectionBannerFragment;
import com.apphunt.app.ui.views.vote.CollectionVoteButton;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.apphunt.app.constants.Constants.CollectionStatus;
import static com.apphunt.app.constants.Constants.MIN_COLLECTION_APPS_SIZE;

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
    public EditText editName;

    @InjectView(R.id.created_by_image)
    CircleImageView createdByAvatar;

    @InjectView(R.id.created_by)
    TextView createdBy;

    @InjectView(R.id.tags_container)
    TextView tags;

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
        try {
            BusProvider.getInstance().register(this);
        } catch(Exception e) {}
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
    public void onCollectionUpdate(UpdateCollectionApiEvent event) {
        AppsCollection newCollection = event.getAppsCollection();
        if (!newCollection.getId().equals(appsCollection.getId())) {
            return;
        }

        name.setVisibility(View.VISIBLE);
        editBanner.setVisibility(View.GONE);
        editName.setVisibility(View.GONE);

        setCollection(newCollection, areButtonsEnabled);
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
        this.appsCollection = collection;
        this.areButtonsEnabled = areButtonsEnabled;

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

        String tags = "";
        for (int i = 0; i < collection.getTags().size(); i++) {
            if (i > 0 && i < collection.getTags().size()) {
                tags += ", ";
            }

            tags += collection.getTags().get(i);
        }
        this.tags.setText(String.format(getContext().getString(R.string.tags), (!TextUtils.isEmpty(tags) ? tags : "none")));

        final ViewTreeObserver viewTree = banner.getViewTreeObserver();
        viewTree.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = banner.getViewTreeObserver();
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeGlobalOnLayoutListener(this);
                } else {
                    obs.removeOnGlobalLayoutListener(this);
                }
                Picasso.with(getContext()).load(appsCollection.getPicture()).resize(
                        banner.getWidth(),
                        getResources().getDimensionPixelSize(R.dimen.view_collection_height)
                ).into(banner);
            }
        });
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
