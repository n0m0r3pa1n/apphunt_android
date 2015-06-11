package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiService;
import com.apphunt.app.api.apphunt.models.apps.App;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.apps.LoadAppCommentsApiEvent;
import com.apphunt.app.event_bus.events.api.apps.LoadAppDetailsApiEvent;
import com.apphunt.app.event_bus.events.ui.votes.AppVoteEvent;
import com.apphunt.app.ui.adapters.VotersAdapter;
import com.apphunt.app.ui.views.CommentsBox;
import com.apphunt.app.ui.views.vote.AppVoteButton;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.TrackingEvents;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.crashlytics.android.Crashlytics;
import com.facebook.widget.FacebookDialog;
import com.flurry.android.FlurryAgent;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AppDetailsFragment extends BaseFragment implements OnClickListener, CommentsBox.OnDisplayCommentBox {

    private static final String TAG = AppDetailsFragment.class.getName();

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
    Target creator;

    @InjectView(R.id.vote_btn)
    AppVoteButton voteBtn;

    @InjectView(R.id.creator_name)
    TextView creatorName;

    @InjectView(R.id.header_voters)
    TextView headerVoters;

    @InjectView(R.id.voters)
    GridView votersAvatars;

    @InjectView(R.id.box_details)
    RelativeLayout boxDetails;

    @InjectView(R.id.box_desc)
    RelativeLayout boxDesc;

    @InjectView(R.id.comments_box)
    CommentsBox commentsBox;

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
        boxDesc.setOnClickListener(this);
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

        if(event.isVote()) {
            votersAdapter.addCreatorIfNotVoter(user);

        } else {
            votersAdapter.removeCreator(user);
        }
        voteBtn.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        commentsBox.invalidateViews();
        headerVoters.setText(activity.getResources().getQuantityString(R.plurals.header_voters, votersAdapter.getTotalVoters(), votersAdapter.getTotalVoters()));
    }

    @Subscribe
    public void onAppDetailsLoaded(LoadAppDetailsApiEvent event) {
        baseApp = event.getBaseApp();
        if (!isAdded()) {
            return;
        }
        if (baseApp != null) {

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

            headerVoters.setText(activity.getResources().getQuantityString(R.plurals.header_voters, Integer.valueOf(baseApp.getVotesCount()),
                    Integer.valueOf(baseApp.getVotesCount())));
            votersAdapter = new VotersAdapter(activity, baseApp.getVotes());
            votersAvatars.setAdapter(votersAdapter);

            commentsBox.checkIfUserCanComment();

            userId = SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID);
        }
    }

    @Subscribe
    public void onAppCommentsLoaded(LoadAppCommentsApiEvent event) {
        if(event.shouldReload()) {
            commentsBox.resetComments(event.getComments());
        } else {
            commentsBox.setComments(event.getComments());
        }
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.box_desc:
                Map<String, String> params = new HashMap<>();
                params.put("appId", appId);
                FlurryAgent.logEvent(TrackingEvents.UserOpenedAppInMarket, params);
                openAppOnGooglePlay();
                break;
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

        ActionBarUtils.getInstance().setTitle(R.string.title_app_details);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ActionBarUtils.getInstance().showActionBarShadow();
        ActionBarUtils.getInstance().setTitle(R.string.title_home);
        ActionBarUtils.getInstance().invalidateOptionsMenu();

    }

    private void openAppOnGooglePlay() {
        if (baseApp == null) {
            Crashlytics.log("App is null!");
            return;
        }

        String appPackageName = baseApp.getPackageName();
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(baseApp.getPackageName()));
        marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            marketIntent.setData(Uri.parse("market://details?id=" + appPackageName));
            startActivity(marketIntent);
        } catch (android.content.ActivityNotFoundException anfe) {
            marketIntent.setData(Uri.parse(baseApp.getUrl()));
            startActivity(marketIntent);
        }
    }

    private boolean userHasPermissions() {
        return LoginProviderFactory.get(activity).isUserLoggedIn();
    }



    @Override
    public void onCommentsBoxDisplayed(boolean isBoxFullscreen) {
        if(isBoxFullscreen) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                if (FacebookDialog.canPresentShareDialog(activity.getApplicationContext(),
                        FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
                    shareWithFacebook();
                    FlurryAgent.logEvent(TrackingEvents.UserSharedAppHuntWithFacebook);
                } else {
                    shareWithLocalApps();
                    FlurryAgent.logEvent(TrackingEvents.UserSharedAppHuntWithoutFacebook);
                }
                return true;

            default:
                return false;
        }
    }

    private void shareWithLocalApps() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/html");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(getString(R.string.share_text)));
        startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }

    private void shareWithFacebook() {
        FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(activity)
                .setName("AppHunt")
                .setPicture("https://launchrock-assets.s3.amazonaws.com/logo-files/LWPRHM35_1421410706452.png?_=4")
                .setLink(Constants.GOOGLE_PLAY_APP_URL).build();
        shareDialog.present();
    }
}
