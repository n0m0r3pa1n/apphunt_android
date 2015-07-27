package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiService;
import com.apphunt.app.api.apphunt.models.apps.App;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.apps.LoadAppCommentsApiEvent;
import com.apphunt.app.event_bus.events.api.apps.LoadAppDetailsApiEvent;
import com.apphunt.app.event_bus.events.ui.votes.AppVoteEvent;
import com.apphunt.app.ui.adapters.VotersAdapter;
import com.apphunt.app.ui.views.CommentsBox;
import com.apphunt.app.ui.views.gallery.GalleryView;
import com.apphunt.app.ui.views.vote.AppVoteButton;
import com.apphunt.app.ui.views.widgets.DownloadButton;
import com.apphunt.app.ui.views.widgets.JHexedPhotoView;
import com.apphunt.app.utils.ImageUtils;
import com.apphunt.app.utils.LoginUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.apphunt.app.utils.ui.NavUtils;
import com.flurry.android.FlurryAgent;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class AppDetailsFragment extends BaseFragment implements CommentsBox.OnDisplayCommentBox {

    private static final String TAG = AppDetailsFragment.class.getName();
    private static final String TAG_LOAD_VOTERS_REQ = "LOAD_VOTERS_IMAGE";

    private String appId;
    private String userId;
    private int itemPosition;

    private Animation enterAnimation;
    private View view;
    private Activity activity;
    private VotersAdapter votersAdapter;

    private RelativeLayout.LayoutParams params;

    private App baseApp;
    private User user;

    //region InjectViews
    @InjectView(R.id.icon)
    ImageView icon;

    @InjectView(R.id.app_name)
    TextView appName;

    @InjectView(R.id.desc)
    TextView appDescription;

    @InjectView(R.id.creator_avatar)
    CircleImageView creator;

    @InjectView(R.id.vote_btn)
    AppVoteButton voteBtn;

    @InjectView(R.id.creator_name)
    TextView creatorName;

    @InjectView(R.id.rating)
    TextView rating;

    @InjectView(R.id.box_details)
    RelativeLayout boxDetails;

    @InjectView(R.id.box_desc)
    RelativeLayout boxDesc;

    @InjectView(R.id.comments_box)
    CommentsBox commentsBox;

    @InjectView(R.id.download)
    DownloadButton downloadBtn;

    @InjectView(R.id.gallery)
    GalleryView gallery;

    @InjectView(R.id.hexedView)
    LinearLayout hexedPhotoView;

    private Handler handler = new Handler();
    public static final int MIN_HEX_IMAGES_SIZE = 15;
    private boolean shouldStopLoading = false;
    //endregion

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appId = getArguments().getString(Constants.KEY_APP_ID);
        itemPosition = getArguments().getInt(Constants.KEY_ITEM_POSITION);
        Map<String, String> params = new HashMap<>();
        params.put("appId", appId);
        FlurryAgent.logEvent(TrackingEvents.UserViewedAppDetails, params);

        setFragmentTag(Constants.TAG_APP_DETAILS_FRAGMENT);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_app_details, container, false);
        ButterKnife.inject(this, view);

        initUI();

        return view;
    }

    private void initUI() {
        ActionBarUtils.getInstance().hideActionBarShadow();
        commentsBox.setBelowId(boxDetails.getId());
        commentsBox.setAppId(appId);
        enterAnimation = AnimationUtils.loadAnimation(activity, R.anim.slide_in_left);
        enterAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                loadData();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    public void loadData() {
        userId = SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID);
        ApiService.getInstance(activity).loadAppDetails(userId, appId);
        ApiService.getInstance(activity).loadAppComments(appId, userId, 1, 3);
    }


    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        commentsBox.addOnDisplayCommentsBoxListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
        commentsBox.removeOnDisplayCommentsBoxListener(this);
    }

    @Subscribe
    public void onVotersReceived(AppVoteEvent event) {
        user = new User();
        user.setId(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID));
        user.setProfilePicture(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_PROFILE_PICTURE));
        voteBtn.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        commentsBox.invalidateViews();
    }

    @Subscribe
    public void onAppDetailsLoaded(LoadAppDetailsApiEvent event) {
        baseApp = event.getBaseApp();
        if (!isAdded() || baseApp == null) {
            return;
        }

        baseApp.setPosition(itemPosition);
        voteBtn.setBaseApp(baseApp);

        Picasso.with(activity)
                .load(baseApp.getCreatedBy().getProfilePicture())
                .into(creator);
        creatorName.setText(String.format(getString(R.string.posted_by),
                baseApp.getCreatedBy().getUsername()));

        Picasso.with(activity)
                .load(baseApp.getIcon())
                .into(icon);
        appName.setText(baseApp.getName());
        appDescription.setText(baseApp.getDescription());
        rating.setText(String.format("%.1f", baseApp.getRating()));

        commentsBox.checkIfUserCanComment();
        downloadBtn.setAppPackage(baseApp.getPackageName());
        if(baseApp.getScreenshots() == null || baseApp.getScreenshots().size() == 0) {
            gallery.setVisibility(View.GONE);
        } else {
            gallery.setImages(baseApp.getScreenshots());
        }


        userId = SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID);

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                final List<Bitmap> icons = new ArrayList<Bitmap>();
                Random random = new Random();
                int size = 0;
                if(baseApp.getVotes().size() > MIN_HEX_IMAGES_SIZE) {
                    size = MIN_HEX_IMAGES_SIZE;
                } else {
                    size = baseApp.getVotes().size();
                }

                for (int i = 0; i < size; i++) {
                    try {
                        if(shouldStopLoading) {
                            break;
                        }
                        icons.add(Picasso.with(getActivity()).load(baseApp.getVotes().get(i).getUser().getProfilePicture())
                                .tag(TAG_LOAD_VOTERS_REQ).get());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(isAdded()) {
                    icons.add(BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo_a, null));
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            hexedPhotoView.findViewById(R.id.loading).setVisibility(View.GONE);
                            JHexedPhotoView hexedView = new JHexedPhotoView(getActivity(), icons, null);
                            hexedView.setBackgroundColor(getResources().getColor(R.color.bg_primary));
                            hexedView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                    getResources().getDimensionPixelSize(R.dimen.app_details_hexagon)));
                            hexedPhotoView.addView(hexedView);
                        }
                    });
                }

                return null;
            }
        }.execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        shouldStopLoading = true;
        Picasso.with(getActivity()).cancelTag(TAG_LOAD_VOTERS_REQ);
    }

    @Subscribe
    public void onAppCommentsLoaded(LoadAppCommentsApiEvent event) {
        if (event.shouldReload()) {
            commentsBox.resetComments(event.getComments());
        } else {
            commentsBox.setComments(event.getComments());
        }
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return enterAnimation;
        } else {
            return AnimationUtils.loadAnimation(activity, R.anim.slide_out_right);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;

    }

    @Override
    public int getTitle() {
        return R.string.title_app_details;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ActionBarUtils.getInstance().showActionBarShadow();
    }

    @Override
    public void onCommentsBoxDisplayed(boolean isBoxFullscreen) {
        if (isBoxFullscreen) {
            boxDetails.setVisibility(View.GONE);
        } else {
            boxDetails.setVisibility(View.VISIBLE);
        }
    }

    public void showDetails() {
        commentsBox.hideBox();
    }

    public boolean isCommentsBoxOpened() {
        return commentsBox.isCommentsBoxOpened();
    }

    @OnClick(R.id.share)
    void share() {
        if(baseApp != null) {
            shareWithLocalApps();
            FlurryAgent.logEvent(TrackingEvents.UserSharedAppHuntWithoutFacebook);
        }
    }

    @OnClick(R.id.add_to_collection)
    void addToCollection() {
        if(!isAdded()) {
            return;
        }

        if(!LoginProviderFactory.get(getActivity()).isUserLoggedIn()) {
            LoginUtils.showLoginFragment(getActivity(), false, R.string.login_info_add_to_collection);
            return;
        }

        if(baseApp != null) {
            NavUtils.getInstance((AppCompatActivity) activity).presentSelectCollectionFragment(baseApp);
            FlurryAgent.logEvent(TrackingEvents.UserAddedAppToCollection);
        }
    }

    private void shareWithLocalApps() {
        final String message = baseApp.getName() + ". " + baseApp.getDescription() + " " + baseApp.getShortUrl();
        Uri iconUri = ImageUtils.getLocalBitmapUri(icon);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("*/*");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, iconUri);
        activity.startActivity(Intent.createChooser(sharingIntent, "Share using"));
        Map<String, String> params = new HashMap<>();
        params.put("appId", baseApp.getId());
        FlurryAgent.logEvent(TrackingEvents.UserSharedApp, params);
    }
}
