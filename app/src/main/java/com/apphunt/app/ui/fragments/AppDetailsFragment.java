package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.AppHuntApiClient;
import com.apphunt.app.api.Callback;
import com.apphunt.app.api.models.App;
import com.apphunt.app.api.models.Comment;
import com.apphunt.app.api.models.Comments;
import com.apphunt.app.api.models.DetailedApp;
import com.apphunt.app.api.models.NewComment;
import com.apphunt.app.api.models.User;
import com.apphunt.app.api.models.Vote;
import com.apphunt.app.ui.adapters.CommentsAdapter;
import com.apphunt.app.ui.adapters.VotersAdapter;
import com.apphunt.app.ui.interfaces.OnAppVoteListener;
import com.apphunt.app.ui.widgets.AvatarImageView;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.TrackingEvents;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import it.appspice.android.AppSpice;
import retrofit.client.Response;

public class AppDetailsFragment extends BaseFragment implements OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = AppDetailsFragment.class.getName();
    private View view;
    private String appId;
    private Activity activity;
    private OnAppVoteListener callback;

    // TODO: DEV
    private App testApp = new App();
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
    
    private TextView closeComments;
    private Animation enterAnimation;
    private int commentBoxHeight;
    private RelativeLayout.LayoutParams params;
    private Comment replyToComment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appId = getArguments().getString(Constants.KEY_APP_ID);
        itemPosition = getArguments().getInt(Constants.KEY_ITEM_POSITION);
        userId = SharedPreferencesHelper.getStringPreference(activity, Constants.KEY_USER_ID);

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

        headerVoters = (TextView) view.findViewById(R.id.header_voters);
        avatars = (GridView) view.findViewById(R.id.voters);

        headerComments = (TextView) view.findViewById(R.id.header_comments);
        commentsList = (ListView) view.findViewById(R.id.comments);
        commentsList.setOnItemClickListener(this);
        
        boxDetails = (RelativeLayout) view.findViewById(R.id.box_details);
        boxComments = (RelativeLayout) view.findViewById(R.id.box_comments);
        
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
        
        closeComments = (TextView) view.findViewById(R.id.close_comments);
        closeComments.setOnClickListener(this);
        
        send = (TextView) view.findViewById(R.id.send_comment);
        send.setOnClickListener(this);

        commentBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideDetails();
                return false;
            }
        });
        
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
    
    private void loadData() {
        AppHuntApiClient.getClient().getDetailedApp(userId, appId, 20, new Callback<DetailedApp>() {
            @Override
            public void success(DetailedApp detailedApp, Response response) {
                if (detailedApp != null) {
                    app = detailedApp.getApp();

                    Picasso.with(activity)
                            .load(detailedApp.getApp().getCreatedBy().getProfilePicture())
                            .into(creator);
                    creatorName.setText(String.format(getString(R.string.posted_by),
                            detailedApp.getApp().getCreatedBy().getName()));

                    if (detailedApp.getApp().isHasVoted()) {
                        vote.setTextColor(getResources().getColor(R.color.bg_secondary));
                        vote.setBackgroundResource(R.drawable.btn_voted_v2);
                    } else {
                        vote.setTextColor(getResources().getColor(R.color.bg_primary));
                        vote.setBackgroundResource(R.drawable.btn_vote);
                    }
                    vote.setText(detailedApp.getApp().getVotesCount());
                    vote.setOnClickListener(AppDetailsFragment.this);

                    Picasso.with(activity)
                            .load(detailedApp.getApp().getIcon())
                            .into(icon);
                    appName.setText(detailedApp.getApp().getName());
                    appDescription.setText(detailedApp.getApp().getDescription());

                    headerVoters.setText(String.format(getString(R.string.header_voters), detailedApp.getApp().getVotesCount()));
                    votersAdapter = new VotersAdapter(activity, detailedApp.getApp().getVotes());
                    avatars.setAdapter(votersAdapter);

                    commentsAdapter = new CommentsAdapter(activity, detailedApp.getCommentsData().getComments());
                    commentsList.setAdapter(commentsAdapter);

                    headerComments.setText(String.format(getString(R.string.header_comments), commentsAdapter.getCount()));
                }
            }
        });
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.vote:
                if (app.isHasVoted()) {
                    AppHuntApiClient.getClient().downVote(app.getId(), SharedPreferencesHelper.getStringPreference(activity, Constants.KEY_USER_ID), new Callback<Vote>() {
                        @Override
                        public void success(Vote voteResult, Response response) {
                            AppSpice.createEvent(TrackingEvents.UserDownVoted).track();
                            app.setVotesCount(voteResult.getVotes());
                            app.setHasVoted(false);
                            vote.setText(voteResult.getVotes());
                            vote.setTextColor(activity.getResources().getColor(R.color.bg_primary));
                            vote.setBackgroundResource(R.drawable.btn_vote);
                            
                            user = new User();
                            user.setId(SharedPreferencesHelper.getStringPreference(activity, Constants.KEY_USER_ID));
                            user.setProfilePicture(SharedPreferencesHelper.getStringPreference(activity, Constants.KEY_PROFILE_IMAGE));
                            votersAdapter.removeCreator(user);
                            headerVoters.setText(String.format(getString(R.string.header_voters), votersAdapter.getTotalVoters()));
                        }
                    });
                } else {
                    AppHuntApiClient.getClient().vote(app.getId(), SharedPreferencesHelper.getStringPreference(activity, Constants.KEY_USER_ID), new Callback<Vote>() {
                        @Override
                        public void success(Vote voteResult, Response response) {
                            AppSpice.createEvent(TrackingEvents.UserVoted).track();
                            app.setVotesCount(voteResult.getVotes());
                            app.setHasVoted(true);
                            vote.setText(voteResult.getVotes());
                            vote.setBackgroundResource(R.drawable.btn_voted_v2);
                            vote.setTextColor(activity.getResources().getColor(R.color.bg_secondary));

                            user = new User();
                            user.setId(SharedPreferencesHelper.getStringPreference(activity, Constants.KEY_USER_ID));
                            user.setProfilePicture(SharedPreferencesHelper.getStringPreference(activity, Constants.KEY_PROFILE_IMAGE));
                            votersAdapter.addCreatorIfNotVoter(user);
                            headerVoters.setText(String.format(getString(R.string.header_voters), votersAdapter.getTotalVoters()));
                        }
                    });
                }

                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                isVoted = true;
                break;

            case R.id.send_comment:
                NewComment comment = new NewComment();
                
                if (commentBox.getText().length() > 0) {
                    if (replyToComment != null) {
                        String replyToName = String.format(getString(R.string.reply_to), replyToComment.getUser().getName()).split(" ")[0];
                        int replyToNameLength = replyToName.length();

                        if (commentBox.getText().length() > replyToNameLength &&
                                replyToName.equals(commentBox.getText().toString().substring(0, replyToNameLength))) {
                            if (replyToComment.getParentId() == null) {
                                comment.setParentId(replyToComment.getId());
                            } else {
                                comment.setParentId(replyToComment.getParentId());
                            }
                        }
                    }

                    comment.setText(commentBox.getText().toString());
                    comment.setAppId(app.getId());
                    comment.setUserId(SharedPreferencesHelper.getStringPreference(activity, Constants.KEY_USER_ID));

                    AppHuntApiClient.getClient().sendComment(comment, new Callback<NewComment>() {
                        @Override
                        public void success(NewComment comment, Response response) {
                            if (response.getStatus() == 200) {
                                AppHuntApiClient.getClient().getAppComments(app.getId(), SharedPreferencesHelper.getStringPreference(activity, Constants.KEY_USER_ID),
                                        1, 10, new Callback<Comments>() {
                                            @Override
                                            public void success(Comments comments, Response response) {
                                                if (comments != null) {
                                                    commentsAdapter.resetAdapter(comments.getComments());
                                                    headerComments.setText(String.format(getString(R.string.header_comments), commentsAdapter.getCount()));
                                                }
                                            }
                                        });
                            }
                        }
                    });
                }
                
                commentBox.getText().clear();
                resizeCommentBox(true);
                closeKeyboard();
                break;
            
            case R.id.close_comments:
                showDetails();
                resizeCommentBox(true);
                closeKeyboard();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        replyToComment = commentsAdapter.getComment(position);
        String replyName = String.format(getString(R.string.reply_to), replyToComment.getUser().getName().split(" ")[0]) + " ";
        
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

        if (isVoted) callback.onAppVote(itemPosition);
    }

    private void showDetails() {
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        boxDetails.setVisibility(View.VISIBLE);
        closeComments.setVisibility(View.INVISIBLE);

        params.addRule(RelativeLayout.BELOW, boxDetails.getId());
        boxComments.setLayoutParams(params);

        resizeCommentBox(true);
    }

    private void hideDetails() {
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        boxDetails.setVisibility(View.GONE);
        closeComments.setVisibility(View.VISIBLE);
        boxComments.setLayoutParams(params);

        resizeCommentBox(false);
    }

    private void resizeCommentBox(boolean reset) {
        if (reset) {
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, commentBoxHeight);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            commentBox.setLayoutParams(params);
        } else {
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2 * commentBoxHeight);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            commentBox.setLayoutParams(params);
        }
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(commentBox.getWindowToken(), 0);
    }
}
