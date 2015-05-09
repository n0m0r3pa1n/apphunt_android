package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.api.apphunt.callback.Callback;
import com.apphunt.app.api.apphunt.models.App;
import com.apphunt.app.api.apphunt.models.Comment;
import com.apphunt.app.api.apphunt.models.Comments;
import com.apphunt.app.api.apphunt.models.NewComment;
import com.apphunt.app.api.apphunt.models.User;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.votes.AppVoteEvent;
import com.apphunt.app.ui.adapters.CommentsAdapter;
import com.apphunt.app.ui.adapters.VotersAdapter;
import com.apphunt.app.ui.interfaces.OnAppVoteListener;
import com.apphunt.app.ui.views.vote.AppVoteButton;
import com.apphunt.app.utils.ConnectivityUtils;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.LoginUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.TrackingEvents;
import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.client.Response;

public class AppDetailsFragment extends BaseFragment implements OnClickListener, AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    private static final String TAG = AppDetailsFragment.class.getName();

    private String appId;
    private String userId;
    private int itemPosition;
    private int commentBoxHeight;
    private boolean isCommentsBoxOpened = false;
    private boolean endOfList;

    private Animation enterAnimation;
    private View view;
    private Activity activity;
    private OnAppVoteListener callback;
    private VotersAdapter votersAdapter;
    private CommentsAdapter commentsAdapter;

    private RelativeLayout.LayoutParams params;

    private App app;
    private User user;
    private Comment replyToComment;

    // TODO: DEV
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

    @InjectView(R.id.comments_count)
    ListView commentsList;

    @InjectView(R.id.header_comments)
    TextView headerComments;

    @InjectView(R.id.box_details)
    RelativeLayout boxDetails;

    @InjectView(R.id.box_comments)
    RelativeLayout boxComments;

    @InjectView(R.id.send_comment)
    TextView send;

    @InjectView(R.id.comment_entry)
    EditText commentBox;

    @InjectView(R.id.box_desc)
    RelativeLayout boxDesc;

    @InjectView(R.id.label_comment)
    TextView labelComment;

    @InjectView(R.id.show_comments)
    TextView showAllComments;

    @InjectView(R.id.hide_comments)
    TextView hideAllComments;
    //endregion

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appId = getArguments().getString(Constants.KEY_APP_ID);
        itemPosition = getArguments().getInt(Constants.KEY_ITEM_POSITION);

        Map<String, String> params = new HashMap<>();
        params.put("appId", appId);
        FlurryAgent.logEvent(TrackingEvents.UserViewedAppDetails, params);

        setTitle(R.string.title_app_details);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_app_details, container, false);
        ButterKnife.inject(this, view);

        initUI();

        return view;
    }

    private void initUI() {
        boxDesc.setOnClickListener(this);

        commentsList.setOnItemClickListener(this);
        commentsList.setOnScrollListener(this);

        showAllComments.setOnClickListener(this);
        hideAllComments.setOnClickListener(this);

        commentBox.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT > 16) {
                    commentBox.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    commentBox.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                commentBoxHeight = commentBox.getHeight();
            }
        });

        commentBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideDetails();
                return false;
            }
        });

        if (userHasPermissions()) {
            labelComment.setVisibility(View.GONE);
            commentBox.setVisibility(View.VISIBLE);
        }

        labelComment.setOnClickListener(this);

        send = (TextView) view.findViewById(R.id.send_comment);
        send.setOnClickListener(this);

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
        ApiClient.getClient(getActivity()).getDetailedApp(userId, appId, new Callback<App>() {
            @Override
            public void success(App app, Response response) {
                if (!isAdded()) {
                    return;
                }
                if (app != null) {
                    AppDetailsFragment.this.app = app;
                    app.setPosition(itemPosition);
                    voteBtn.setApp(app);

                    Picasso.with(activity)
                            .load(app.getCreatedBy().getProfilePicture())
                            .into(creator);
                    creatorName.setText(String.format(getString(R.string.posted_by),
                            app.getCreatedBy().getUsername()));

                    Picasso.with(activity)
                            .load(app.getIcon())
                            .into(icon);
                    appName.setText(app.getName());
                    appDescription.setText(app.getDescription());

                    headerVoters.setText(activity.getResources().getQuantityString(R.plurals.header_voters, Integer.valueOf(app.getVotesCount()),
                            Integer.valueOf(app.getVotesCount())));
                    votersAdapter = new VotersAdapter(activity, app.getVotes());
                    votersAvatars.setAdapter(votersAdapter);

                    if (userHasPermissions()) {
                        labelComment.setVisibility(View.GONE);
                        commentBox.setVisibility(View.VISIBLE);
                    } else {
                        labelComment.setVisibility(View.VISIBLE);
                        commentBox.setVisibility(View.INVISIBLE);
                    }

                    userId = SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID);
                }
            }
        });

        ApiClient.getClient(getActivity()).getAppComments(appId, userId, 1, 3, new Callback<Comments>() {
            @Override
            public void success(Comments comments, Response response) {
                if (comments.getTotalCount() == 0) {
                    view.findViewById(R.id.label_no_comments).setVisibility(View.VISIBLE);
                }

                commentsAdapter = new CommentsAdapter(activity, comments, commentsList);
                commentsList.setAdapter(commentsAdapter);

                headerComments.setText(activity.getResources().getQuantityString(R.plurals.header_comments, comments.getTotalCount(), comments.getTotalCount()));

                userId = SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void updateVoters(AppVoteEvent event) {
        user = new User();
        user.setId(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID));
        user.setProfilePicture(SharedPreferencesHelper.getStringPreference(Constants.KEY_PROFILE_IMAGE));

        if(event.isVote()) {
            votersAdapter.addCreatorIfNotVoter(user);

        } else {
            votersAdapter.removeCreator(user);
        }
        voteBtn.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        commentsList.invalidateViews();
        headerVoters.setText(activity.getResources().getQuantityString(R.plurals.header_voters, votersAdapter.getTotalVoters(), votersAdapter.getTotalVoters()));
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

            case R.id.label_comment:
                LoginUtils.showLoginFragment(activity);
                break;

            case R.id.show_comments:
                hideDetails();
                break;

            case R.id.hide_comments:
                showDetails();
                break;

            case R.id.send_comment:
                if (!userHasPermissions()) {
                    LoginUtils.showLoginFragment(activity);
                    return;
                }

                NewComment comment = new NewComment();

                if (commentBox.getText().length() > 0) {
                    if (replyToComment != null) {
                        String replyToName = String.format(getString(R.string.reply_to), replyToComment.getUser().getUsername());
                        int replyToNameLength = replyToName.length();

                        if (commentBox.getText().length() > replyToNameLength &&
                                replyToName.toLowerCase().equals(commentBox.getText().toString().substring(0, replyToNameLength).toLowerCase())) {
                            if (replyToComment.getParentId() == null) {
                                comment.setParentId(replyToComment.getId());
                            } else {
                                comment.setParentId(replyToComment.getParentId());
                            }
                            FlurryAgent.logEvent(TrackingEvents.UserSentReplyComment);
                        } else {
                            FlurryAgent.logEvent(TrackingEvents.UserSentComment);
                        }
                    } else {
                        commentBox.setHint(R.string.comment_entry_hint);
                    }

                    comment.setText(commentBox.getText().toString());
                    comment.setAppId(app.getId());
                    comment.setUserId(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID));

                    ApiClient.getClient(getActivity()).sendComment(comment, new Callback<NewComment>() {
                        @Override
                        public void success(NewComment comment, Response response) {
                            if (response.getStatus() == 200) {
                                ApiClient.getClient(getActivity()).getAppComments(app.getId(), SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID),
                                        1, 3, new Callback<Comments>() {
                                            @Override
                                            public void success(Comments comments, Response response) {
                                                if (comments != null) {
                                                    commentsAdapter.resetAdapter(comments);
                                                    headerComments.setText(activity.getResources().getQuantityString(R.plurals.header_comments, commentsAdapter.getCount(), commentsAdapter.getCount()));

                                                    view.findViewById(R.id.label_no_comments).setVisibility(View.GONE);
                                                }
                                            }
                                        });
                            }
                        }
                    });
                }

                closeKeyboard(v);
                commentBox.getText().clear();
                resizeCommentBox(true);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        replyToComment = commentsAdapter.getComment(position);
        String replyName = String.format(getString(R.string.reply_to), replyToComment.getUser().getUsername()) + " ";

        commentBox.getText().clear();
        commentBox.setText(replyName);
        commentBox.setSelection(replyName.length());
        hideDetails();
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

        try {
            callback = (OnAppVoteListener) activity;
        } catch (ClassCastException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        closeKeyboard(commentBox);
    }

    public void showDetails() {
        ((ActionBarActivity) activity).getSupportActionBar().setTitle(R.string.title_app_details);
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        boxDetails.setVisibility(View.VISIBLE);

        params.addRule(RelativeLayout.BELOW, boxDetails.getId());
        boxComments.setLayoutParams(params);

        resizeCommentBox(true);
        isCommentsBoxOpened = false;
        closeKeyboard(commentBox);

        showAllComments.setVisibility(View.VISIBLE);
        hideAllComments.setVisibility(View.INVISIBLE);
    }

    private void hideDetails() {
        ((ActionBarActivity) activity).getSupportActionBar().setTitle(R.string.title_close);
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        boxDetails.setVisibility(View.GONE);
        boxComments.setLayoutParams(params);

        resizeCommentBox(false);
        isCommentsBoxOpened = true;

        showAllComments.setVisibility(View.INVISIBLE);
        hideAllComments.setVisibility(View.VISIBLE);
    }

    public boolean isCommentsBoxOpened() {
        return isCommentsBoxOpened;
    }

    private void resizeCommentBox(boolean reset) {
        if (reset) {
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, commentBoxHeight);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            commentBox.setLayoutParams(params);
            labelComment.setLayoutParams(params);
        } else {
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2 * commentBoxHeight);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            commentBox.setLayoutParams(params);
            labelComment.setLayoutParams(params);
        }
    }

    private void showKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
    }

    private void closeKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void openAppOnGooglePlay() {
        if (app == null) {
            Crashlytics.log("App is null!");
            return;
        }
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(app.getShortUrl()));
        marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(marketIntent);
    }

    private boolean userHasPermissions() {
        return LoginProviderFactory.get(activity).isUserLoggedIn();
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (ConnectivityUtils.isNetworkAvailable(activity)) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                if (endOfList) {
                    FlurryAgent.logEvent(TrackingEvents.UserScrolledDownCommentList);
                    commentsAdapter.loadMore(appId, userId, headerComments);

                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        endOfList = (firstVisibleItem + visibleItemCount) == totalItemCount;
    }
}
