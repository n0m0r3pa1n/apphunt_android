package com.apphunt.app.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.apphunt.app.constants.Constants.*;

public class CollectionView extends RelativeLayout {
    LayoutInflater inflater;

    @InjectView(R.id.apps_left)
    TextView appsLeft;

    @InjectView(R.id.banner)
    ImageView banner;

    @InjectView(R.id.collection_name)
    TextView name;

    @InjectView(R.id.created_by_image)
    Target createdByAvatar;

    @InjectView(R.id.created_by)
    TextView createdBy;

    @InjectView(R.id.votes_count)
    TextView votesCount;

    @InjectView(R.id.collection_status)
    ImageView status;

    public CollectionView(Context context) {
        super(context);
        if(!isInEditMode()) {
            init();
        }
    }

    public CollectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode()) {
            init();
        }
    }

    public CollectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!isInEditMode()) {
            init();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CollectionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if(!isInEditMode()) {
            init();
        }
    }

    private void init() {
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_collection, this, true);
        ButterKnife.inject(this, view);
    }

    public void setCollection(AppsCollection collection) {
        if(collection.getStatus() == CollectionStatus.DRAFT) {
            appsLeft.setVisibility(View.VISIBLE);
            int appsLeftCount = MIN_COLLECTION_APPS_SIZE - collection.getApps().size();
            String text = "You need " + getResources().getQuantityString(R.plurals.appsLeft, appsLeftCount, appsLeftCount);
            appsLeft.setText(text);
            status.setVisibility(View.VISIBLE);
            votesCount.setVisibility(View.INVISIBLE);
        } else {
            appsLeft.setVisibility(View.INVISIBLE);
            status.setVisibility(View.INVISIBLE);
        }

        name.setText(collection.getName());
        createdBy.setText(collection.getCreatedBy().getUsername());
        votesCount.setText(collection.getVotesCount() + "");
        Picasso.with(getContext()).load(collection.getCreatedBy().getProfilePicture()).into(createdByAvatar);
        Picasso.with(getContext()).load(collection.getPicture()).into(banner);
    }
}
