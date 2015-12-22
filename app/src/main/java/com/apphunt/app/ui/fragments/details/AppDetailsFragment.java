package com.apphunt.app.ui.fragments.details;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.clients.rest.ApiService;
import com.apphunt.app.api.apphunt.models.apps.App;
import com.apphunt.app.api.apphunt.models.comments.Comment;
import com.apphunt.app.api.apphunt.models.comments.NewComment;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.auth.LoginProvider;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.events.api.apps.LoadAppDetailsApiEvent;
import com.apphunt.app.event_bus.events.ui.ReplyToCommentEvent;
import com.apphunt.app.ui.adapters.details.AppDetailsTabsPagerAdapter;
import com.apphunt.app.ui.fragments.base.BackStackFragment;
import com.apphunt.app.ui.views.CreatorView;
import com.apphunt.app.ui.views.app.DownloadButton;
import com.apphunt.app.ui.views.app.FavouriteAppButton;
import com.apphunt.app.ui.views.vote.AppVoteButton;
import com.apphunt.app.utils.FlurryWrapper;
import com.apphunt.app.utils.LoginUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.apphunt.app.utils.ui.NavUtils;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;

public class AppDetailsFragment extends BackStackFragment {

    private static final String TAG = AppDetailsFragment.class.getName();
    public static final int PAGE_COMMENTS = 0;

    private String appId;
    private String userId;
    private int itemPosition;

    private Animation enterAnimation;
    private View view;
    private AppCompatActivity activity;

    private RelativeLayout.LayoutParams params;

    private App baseApp;

    @InjectView(R.id.icon)
    ImageView icon;

    @InjectView(R.id.app_name)
    TextView appName;

    @InjectView(R.id.box_name)
    CreatorView creator;


//    @InjectView(R.id.comments_action)
//    Button commentsAction;

    @InjectView(R.id.desc)
    TextView appDescription;

    @InjectView(R.id.vote_btn)
    AppVoteButton voteBtn;

    @InjectView(R.id.rating)
    TextView rating;

    @InjectView(R.id.box_details)
    RelativeLayout boxDetails;

    @InjectView(R.id.avatar)
    CircleImageView avatar;

    @InjectView(R.id.comment_box)
    RelativeLayout commentBox;

    @InjectView(R.id.comment_entry)
    EditText commentEntry;

    @InjectView(R.id.download)
    DownloadButton downloadBtn;

    @InjectView(R.id.tabs)
    TabLayout tabLayout;

    @InjectView(R.id.details_tabs)
    ViewPager detailsTabsPager;

    @InjectView(R.id.send)
    Button send;

    @InjectView(R.id.paid_info_container)
    RelativeLayout paidInfoContainer;

    FavouriteAppButton favouriteAppButton;

    private AppDetailsTabsPagerAdapter pagerAdapter;
    private Comment replyToComment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appId = getArguments().getString(Constants.KEY_APP_ID);
        itemPosition = getArguments().getInt(Constants.KEY_ITEM_POSITION);
        Map<String, String> params = new HashMap<>();
        params.put("appId", appId);
        FlurryWrapper.logEvent(TrackingEvents.UserViewedAppDetails, params);

        setFragmentTag(Constants.TAG_APP_DETAILS_FRAGMENT);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_app_details, container, false);
        ButterKnife.inject(this, view);

        initUI();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment();
            }
        });
        pagerAdapter = new AppDetailsTabsPagerAdapter(getChildFragmentManager());
        detailsTabsPager.setAdapter(pagerAdapter);
        detailsTabsPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == PAGE_COMMENTS) {
                    FlurryWrapper.logEvent(TrackingEvents.UserSwitchedToComments, new HashMap<String, String>() {{
                        put("appId", appId);
                    }});
                    commentBox.setVisibility(View.VISIBLE);
                } else {
                    FlurryWrapper.logEvent(TrackingEvents.UserSwitchedToGallery, new HashMap<String, String>() {{
                        put("appId", appId);
                    }});
                    commentBox.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d(TAG, "onPageScrollStateChanged: " + state);
            }
        });

        Picasso.with(activity)
                .load(LoginProviderFactory.get(activity).getUser().getProfilePicture())
                .placeholder(R.drawable.avatar_placeholder)
                .into(avatar);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout.setupWithViewPager(detailsTabsPager);
    }

    private void initUI() {
        ActionBarUtils.getInstance().hideActionBarShadow();
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
        this.activity = (AppCompatActivity) activity;

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem favouriteAppAction = menu.findItem(R.id.action_favourite_app);
        if(activity.getSupportFragmentManager().findFragmentById(R.id.container) instanceof AppDetailsFragment) {
            menu.findItem(R.id.action_random).setVisible(true);
            menu.findItem(R.id.action_random).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    LoginProvider loginProvider = LoginProviderFactory.get(activity);
                    String userId = loginProvider.isUserLoggedIn() ? loginProvider.getUser().getId() : "";
                    FlurryWrapper.logEvent(TrackingEvents.UserOpenedRandomApp, new HashMap<String, String>() {{
                        put("screen", TAG);
                    }});
                    ApiClient.getClient(activity).getRandomApp(userId);
                    return true;
                }
            });
            favouriteAppAction.setVisible(true);
        } else {
            favouriteAppAction.setVisible(false);
        }

        favouriteAppButton = (FavouriteAppButton) MenuItemCompat.getActionView(favouriteAppAction).findViewById(R.id.favourite_app_button);
        favouriteAppButton.setActivity(activity);
    }

    @Subscribe
    public void onAppDetailsLoaded(LoadAppDetailsApiEvent event) {
        baseApp = event.getBaseApp();
        populateAppDetails();
    }

    @Subscribe
    public void onReplyCommentSelected(ReplyToCommentEvent event) {
        this.replyToComment = event.getComment();
        showKeyboard();
        if (commentEntry == null) {
            return;
        }

        String replyName = String.format(getResources().getString(R.string.reply_to), event.getComment().getUser().getUsername()) + " ";

        commentEntry.getText().clear();
        commentEntry.setText(replyName);
        commentEntry.setSelection(replyName.length());
    }

    private void populateAppDetails() {
        if (!isAdded() || baseApp == null) {
            return;
        }

        favouriteAppButton.setApp(baseApp);
        baseApp.setPosition(itemPosition);

        User createdBy = baseApp.getCreatedBy();
        creator.setContext(activity);
        creator.setUser(createdBy.getId(), createdBy.getProfilePicture(), createdBy.getName());

        Picasso.with(activity)
                .load(baseApp.getIcon())
                .into(icon);
        appName.setText(baseApp.getName());
        downloadBtn.setAppPackage(baseApp.getPackageName());
        downloadBtn.setTrackingScreen(AppDetailsFragment.TAG);

        if (!baseApp.isFree()) {
            paidInfoContainer.setVisibility(View.VISIBLE);
        }

        userId = SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID);
        voteBtn.setApp(baseApp);
        appDescription.setText(baseApp.getDescription());
        rating.setText(String.format("%.1f", baseApp.getRating()));
        rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlurryWrapper.logEvent(TrackingEvents.UserClickedRating);
            }
        });
    }

    @OnClick(R.id.share)
    void share() {
        if(baseApp != null) {
            shareWithLocalApps();
        }
    }

    @OnClick(R.id.add_to_collection)
    void addToCollection() {
        if(!isAdded()) {
            return;
        }

        if(!LoginProviderFactory.get(activity).isUserLoggedIn()) {
            LoginUtils.showLoginFragment(false, R.string.login_info_add_to_collection);
            return;
        }

        if(baseApp != null) {
            NavUtils.getInstance(activity).presentSelectCollectionFragment(baseApp);
        }
    }


    private void shareWithLocalApps() {
        BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                .setCanonicalIdentifier("app/" + baseApp.getId())
                .setTitle(baseApp.getName())
                .setContentDescription("Download " + baseApp.getName() + " for free from AppHunt NOW!\n" +
                    "AppHunt is your daily source of amazing apps! Find the best new useful apps, verified by our team, available in the Play Store!")
                .setContentImageUrl(baseApp.getIcon())
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .addContentMetadata(Constants.KEY_BRANCH_APP_ID, baseApp.getId());

        LinkProperties linkProperties = new LinkProperties()
                .setChannel("android")
                .setFeature("sharing")
                .addControlParameter("$desktop_url", "http://theapphunt.com/app/" + baseApp.getPackageName())
                .addControlParameter("$ios_url", "http://theapphunt.com/app/" + baseApp.getPackageName());

        ShareSheetStyle shareSheetStyle = new ShareSheetStyle(activity, baseApp.getName(), "Download " + baseApp.getName() + " for free from AppHunt NOW!\n" +
                "AppHunt is your daily source of amazing apps! Find the best new useful apps, verified by our team, available in the Play Store!")
                .setCopyUrlStyle(getResources().getDrawable(android.R.drawable.ic_menu_send), "Copy", "Added to clipboard")
                .setMoreOptionStyle(getResources().getDrawable(android.R.drawable.ic_menu_more), "Show more")
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.TWITTER)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.WHATS_APP)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.MESSAGE)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FLICKR)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL);

        branchUniversalObject.showShareSheet(activity,
                linkProperties,
                shareSheetStyle,
                new Branch.BranchLinkShareListener() {
                    @Override
                    public void onShareLinkDialogLaunched() {
                    }

                    @Override
                    public void onShareLinkDialogDismissed() {
                    }

                    @Override
                    public void onLinkShareResponse(String sharedLink, String sharedChannel, BranchError error) {
                        Map<String, String> params = new HashMap<>();
                        params.put("appId", baseApp.getId());
                        params.put("appPackage", baseApp.getPackageName());
                        params.put("channel", sharedChannel);
                        FlurryWrapper.logEvent(TrackingEvents.UserSharedApp, params);
                    }

                    @Override
                    public void onChannelSelected(String channelName) {
                    }
                });

        Map<String, String> params = new HashMap<>();
        params.put("appId", baseApp.getId());
        params.put("appPackage", baseApp.getPackageName());
        FlurryWrapper.logEvent(TrackingEvents.UserClickedShareApp, params);

    }

    public void sendComment() {
        Log.d(TAG, "sendComment: ");
        send.setClickable(false);
        send.setEnabled(false);
        NewComment comment = new NewComment();
        if (commentEntry.getText().length() > 0) {
            if (replyToComment != null) {
                String replyToName = String.format(getResources().getString(R.string.reply_to), replyToComment.getUser().getUsername());
                int replyToNameLength = replyToName.length();

                if (commentEntry.getText().length() > replyToNameLength &&
                        replyToName.toLowerCase().equals(commentEntry.getText().toString().substring(0, replyToNameLength).toLowerCase())) {
                    if (replyToComment.getParent() == null) {
                        comment.setParentId(replyToComment.getId());
                    } else {
                        comment.setParentId(replyToComment.getParent());
                    }
                    FlurryWrapper.logEvent(TrackingEvents.UserSentReplyComment);
                }
            } else {
                commentEntry.setHint(R.string.comment_entry_hint);
                FlurryWrapper.logEvent(TrackingEvents.UserSentComment);
            }

            comment.setText(commentEntry.getText().toString());
            comment.setUserId(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID));
            comment.setAppId(appId);
            ApiClient.getClient(getActivity()).sendComment(comment);
        }

        closeKeyboard(send);
        commentEntry.getText().clear();
        send.setEnabled(true);
        send.setClickable(true);
    }

    private void closeKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void showKeyboard() {
        commentEntry.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(commentEntry, 0);
    }
}
