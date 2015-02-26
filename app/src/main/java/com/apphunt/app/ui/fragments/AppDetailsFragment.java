package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.AppHuntApiClient;
import com.apphunt.app.api.Callback;
import com.apphunt.app.api.models.App;
import com.apphunt.app.api.models.Comment;
import com.apphunt.app.api.models.DetailedApp;
import com.apphunt.app.api.models.User;
import com.apphunt.app.api.models.Vote;
import com.apphunt.app.ui.adapters.CommentsAdapter;
import com.apphunt.app.ui.adapters.VotersAdapter;
import com.apphunt.app.ui.interfaces.OnAppVoteListener;
import com.apphunt.app.ui.listview_items.Item;
import com.apphunt.app.ui.listview_items.comments.CommentItem;
import com.apphunt.app.ui.listview_items.comments.SubCommentItem;
import com.apphunt.app.ui.widgets.AvatarImageView;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.TrackingEvents;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import it.appspice.android.AppSpice;
import retrofit.client.Response;

public class AppDetailsFragment extends BaseFragment implements OnClickListener {

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

                    headerComments.setText(String.format(getString(R.string.header_comments), detailedApp.getCommentsData().getTotalCount()));
                    commentsAdapter = new CommentsAdapter(activity, detailedApp.getCommentsData().getComments());
                    commentsList.setAdapter(commentsAdapter);
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
        }
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return AnimationUtils.loadAnimation(activity, R.anim.slide_in_left);
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
}
