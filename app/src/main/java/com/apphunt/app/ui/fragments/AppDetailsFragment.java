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
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.AppHuntApiClient;
import com.apphunt.app.api.apphunt.Callback;
import com.apphunt.app.api.apphunt.models.App;
import com.apphunt.app.api.apphunt.models.Comment;
import com.apphunt.app.api.apphunt.models.Comments;
import com.apphunt.app.api.apphunt.models.NewComment;
import com.apphunt.app.api.apphunt.models.User;
import com.apphunt.app.api.apphunt.models.Vote;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.ui.adapters.CommentsAdapter;
import com.apphunt.app.ui.adapters.VotersAdapter;
import com.apphunt.app.ui.interfaces.OnAppVoteListener;
import com.apphunt.app.ui.widgets.AvatarImageView;
import com.apphunt.app.utils.ConnectivityUtils;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.FacebookUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.TrackingEvents;
import com.flurry.android.FlurryAgent;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.Map;

import retrofit.client.Response;

public class AppDetailsFragment extends BaseFragment implements OnClickListener, AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    private static final String TAG = AppDetailsFragment.class.getName();
    private View view;
    private String appId;
    private Activity activity;
    private OnAppVoteListener callback;

    // TODO: DEV
    private ImageView icon;
    private TextView appName;
    private TextView appDescription;
    private Target creator;
    private String userId;
    private Button vote;
    private TextView creatorName;
    private TextView headerVoters;
    private GridView avatars;
    private App app;
    private int itemPosition;
    private boolean isVoted;
    private VotersAdapter votersAdapter;
    private User user;
    private ListView commentsList;
    private CommentsAdapter commentsAdapter;
    private TextView headerComments;
    private RelativeLayout boxDetails;
    private RelativeLayout boxComments;
    private TextView send;
    private EditText commentBox;

    private Animation enterAnimation;
    private int commentBoxHeight;
    private RelativeLayout.LayoutParams params;
    private Comment replyToComment;
    private RelativeLayout boxDesc;
    private TextView labelComment;
    private boolean isCommentsBoxOpened = false;
    private boolean endOfList;
    private TextView showAllComments;
    private TextView hideAllComments;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        appId = getArguments().getString(Constants.KEY_APP_ID);
        itemPosition = getArguments().getInt(Constants.KEY_ITEM_POSITION);

        Map<String, String> params = new HashMap<>();
        params.put("appId", appId);
        FlurryAgent.logEvent(TrackingEvents.UserViewedAppDetails, params);

        setTitle(R.string.title_app_details);
        isVoted = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_app_details, container, false);

        initUI();

        return view;
    }

    private void initUI() {
        creator = (AvatarImageView) view.findViewById(R.id.creator_avatar);
        creatorName = (TextView) view.findViewById(R.id.creator_name);
        vote = (Button) view.findViewById(R.id.vote);

        icon = (ImageView) view.findViewById(R.id.icon);
        appName = (TextView) view.findViewById(R.id.app_name);
        appDescription = (TextView) view.findViewById(R.id.desc);
        boxDesc = (RelativeLayout) view.findViewById(R.id.box_desc);
        boxDesc.setOnClickListener(this);

        headerVoters = (TextView) view.findViewById(R.id.header_voters);
        avatars = (GridView) view.findViewById(R.id.voters);

        headerComments = (TextView) view.findViewById(R.id.header_comments);
        commentsList = (ListView) view.findViewById(R.id.comments_count);
        commentsList.setOnItemClickListener(this);
        commentsList.setOnScrollListener(this);

        boxDetails = (RelativeLayout) view.findViewById(R.id.box_details);
        boxComments = (RelativeLayout) view.findViewById(R.id.box_comments);

        showAllComments = (TextView) view.findViewById(R.id.show_comments);
        showAllComments.setOnClickListener(this);

        hideAllComments = (TextView) view.findViewById(R.id.hide_comments);
        hideAllComments.setOnClickListener(this);

        commentBox = (EditText) view.findViewById(R.id.comment_entry);
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

        labelComment = (TextView) view.findViewById(R.id.label_comment);
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
        userId = SharedPreferencesHelper.getStringPreference(activity, Constants.KEY_USER_ID);
        AppHuntApiClient.getClient().getDetailedApp(userId, appId, new Callback<App>() {
            @Override
            public void success(App app, Response response) {
                if (!isAdded()) {
                    return;
                }
                if (app != null) {
                    AppDetailsFragment.this.app = app;

                    Picasso.with(activity)
                            .load(app.getCreatedBy().getProfilePicture())
                            .into(creator);
                    creatorName.setText(String.format(getString(R.string.posted_by),
                            app.getCreatedBy().getUsername()));

                    if (app.isHasVoted()) {
                        vote.setTextColor(getResources().getColor(R.color.bg_secondary));
                        vote.setBackgroundResource(R.drawable.btn_voted_v2);
                    } else {
                        vote.setTextColor(getResources().getColor(R.color.bg_primary));
                        vote.setBackgroundResource(R.drawable.btn_vote);
                    }
                    vote.setText(app.getVotesCount());
                    vote.setOnClickListener(AppDetailsFragment.this);

                    Picasso.with(activity)
                            .load(app.getIcon())
                            .into(icon);
                    appName.setText(app.getName());
                    appDescription.setText(app.getDescription());

                    headerVoters.setText(activity.getResources().getQuantityString(R.plurals.header_voters, Integer.valueOf(app.getVotesCount()),
                            Integer.valueOf(app.getVotesCount())));
                    votersAdapter = new VotersAdapter(activity, app.getVotes());
                    avatars.setAdapter(votersAdapter);

                    if (userHasPermissions()) {
                        labelComment.setVisibility(View.GONE);
                        commentBox.setVisibility(View.VISIBLE);
                    } else {
                        labelComment.setVisibility(View.VISIBLE);
                        commentBox.setVisibility(View.INVISIBLE);
                    }

                    userId = SharedPreferencesHelper.getStringPreference(activity, Constants.KEY_USER_ID);
                }
            }
        });

        AppHuntApiClient.getClient().getAppComments(appId, userId, 1, 3, new Callback<Comments>() {
            @Override
            public void success(Comments comments, Response response) {
                if (comments.getTotalCount() == 0) {
                    view.findViewById(R.id.label_no_comments).setVisibility(View.VISIBLE);
                }

                commentsAdapter = new CommentsAdapter(activity, comments, commentsList);
                commentsList.setAdapter(commentsAdapter);

                headerComments.setText(activity.getResources().getQuantityString(R.plurals.header_comments, comments.getTotalCount(), comments.getTotalCount()));

                userId = SharedPreferencesHelper.getStringPreference(activity, Constants.KEY_USER_ID);
            }
        });
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.vote:
                if (userHasPermissions()) {
                    if (app.isHasVoted()) {
                        AppHuntApiClient.getClient().downVote(app.getId(), SharedPreferencesHelper.getStringPreference(activity, Constants.KEY_USER_ID), new Callback<Vote>() {
                            @Override
                            public void success(Vote voteResult, Response response) {
                                FlurryAgent.logEvent(TrackingEvents.UserDownVotedAppFromDetails);
                                app.setVotesCount(voteResult.getVotes());
                                app.setHasVoted(false);
                                vote.setText(voteResult.getVotes());
                                vote.setTextColor(activity.getResources().getColor(R.color.bg_primary));
                                vote.setBackgroundResource(R.drawable.btn_vote);

                                user = new User();
                                user.setId(SharedPreferencesHelper.getStringPreference(activity, Constants.KEY_USER_ID));
                                user.setProfilePicture(SharedPreferencesHelper.getStringPreference(activity, Constants.KEY_PROFILE_IMAGE));
                                votersAdapter.removeCreator(user);
                                commentsList.invalidateViews();
                                headerVoters.setText(activity.getResources().getQuantityString(R.plurals.header_voters, votersAdapter.getTotalVoters(), votersAdapter.getTotalVoters()));
                            }
                        });
                    } else {
                        AppHuntApiClient.getClient().vote(app.getId(), SharedPreferencesHelper.getStringPreference(activity, Constants.KEY_USER_ID), new Callback<Vote>() {
                            @Override
                            public void success(Vote voteResult, Response response) {
                                FlurryAgent.logEvent(TrackingEvents.UserVotedAppFromDetails);
                                app.setVotesCount(voteResult.getVotes());
                                app.setHasVoted(true);
                                vote.setText(voteResult.getVotes());
                                vote.setBackgroundResource(R.drawable.btn_voted_v2);
                                vote.setTextColor(activity.getResources().getColor(R.color.bg_secondary));

                                user = new User();
                                user.setId(SharedPreferencesHelper.getStringPreference(activity, Constants.KEY_USER_ID));
                                user.setProfilePicture(SharedPreferencesHelper.getStringPreference(activity, Constants.KEY_PROFILE_IMAGE));
                                votersAdapter.addCreatorIfNotVoter(user);
                                commentsList.invalidateViews();
                                headerVoters.setText(activity.getResources().getQuantityString(R.plurals.header_voters, votersAdapter.getTotalVoters(), votersAdapter.getTotalVoters()));
                            }
                        });
                    }
                } else {
                    FacebookUtils.showLoginFragment(activity);
                }

                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                isVoted = true;
                break;

            case R.id.box_desc:
                FlurryAgent.logEvent(TrackingEvents.UserOpenedAppInMarket);
                openAppOnGooglePlay();
                break;

            case R.id.label_comment:
                FacebookUtils.showLoginFragment(activity);
                break;

            case R.id.show_comments:
                hideDetails();
                break;

            case R.id.hide_comments:
                showDetails();
                break;

            case R.id.send_comment:
                if (!userHasPermissions()) {
                    FacebookUtils.showLoginFragment(activity);
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
                    comment.setUserId(SharedPreferencesHelper.getStringPreference(activity, Constants.KEY_USER_ID));

                    AppHuntApiClient.getClient().sendComment(comment, new Callback<NewComment>() {
                        @Override
                        public void success(NewComment comment, Response response) {
                            if (response.getStatus() == 200) {
                                AppHuntApiClient.getClient().getAppComments(app.getId(), SharedPreferencesHelper.getStringPreference(activity, Constants.KEY_USER_ID),
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
        if (isVoted) callback.onAppVote(itemPosition);
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
            Log.e(TAG, "Null app");
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
