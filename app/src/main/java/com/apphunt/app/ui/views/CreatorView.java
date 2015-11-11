package com.apphunt.app.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.utils.FlurryWrapper;
import com.apphunt.app.utils.ui.NavUtils;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class CreatorView extends LinearLayout {
    @InjectView(R.id.creator_avatar)
    CircleImageView creator;

    @InjectView(R.id.creator_name)
    TextView creatorName;

    View view;

    public CreatorView(Context context) {
        super(context);
        if(!isInEditMode()) {
            init(context, null);
        }
    }

    public CreatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode()) {
            init(context, attrs);
        }
    }

    public CreatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!isInEditMode()) {
            init(context, attrs);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CreatorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if(!isInEditMode()) {
            init(context, attrs);
        }
    }

    private void init(Context context, AttributeSet attrs) {
        view = LayoutInflater.from(context).inflate(R.layout.view_creator, this, true);
        ButterKnife.inject(this, view);
        TypedArray array = getContext().getTheme().obtainStyledAttributes(attrs,
                R.styleable.CreatorView, 0, 0);

        int pictureHeight = array.getDimensionPixelSize(R.styleable.CreatorView_pictureHeight, getResources().getDimensionPixelSize(R.dimen.details_avatar_creator));
        int pictureWidth = array.getDimensionPixelSize(R.styleable.CreatorView_pictureWidth, getResources().getDimensionPixelSize(R.dimen.details_avatar_creator));
        float textSize = array.getFloat(R.styleable.CreatorView_textSize, getResources().getInteger(R.integer.details_box_name_creator_name_text));
        ViewGroup.LayoutParams params = creator.getLayoutParams();
        params.height = pictureHeight;
        params.width = pictureWidth;
        creator.setLayoutParams(params);
        creatorName.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    public void setUser(final String userId, String pictureUrl, final String name) {
        creatorName.setText(String.format(getContext().getString(R.string.posted_by), name));
        Picasso.with(getContext())
                .load(pictureUrl)
                .into(creator);

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FlurryWrapper.logEvent(TrackingEvents.UserOpenedProfileFromAppDetails);
                NavUtils.getInstance((AppCompatActivity) getContext()).presentUserProfileFragment(userId, name);
            }
        });
    }

    public void setUserWithText(final String userId, String pictureUrl, String postByString, final String name) {
        creatorName.setText(postByString + " " + name);
        Picasso.with(getContext())
                .load(pictureUrl)
                .into(creator);

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FlurryWrapper.logEvent(TrackingEvents.UserOpenedProfileFromTrendingOrSearchApps);
                NavUtils.getInstance((AppCompatActivity) getContext()).presentUserProfileFragment(userId, name);
            }
        });
    }
}
