package com.apphunt.app.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiClient;
import com.apphunt.app.api.apphunt.client.ApiService;
import com.apphunt.app.api.apphunt.models.comments.Comment;
import com.apphunt.app.api.apphunt.models.comments.Comments;
import com.apphunt.app.api.apphunt.models.comments.NewComment;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.events.api.apps.LoadAppCommentsApiEvent;
import com.apphunt.app.event_bus.events.ui.ReloadCommentsEvent;
import com.apphunt.app.event_bus.events.ui.auth.LoginEvent;
import com.apphunt.app.ui.adapters.CommentsAdapter;
import com.apphunt.app.ui.fragments.base.BackStackFragment;
import com.apphunt.app.ui.interfaces.OnEndReachedListener;
import com.apphunt.app.ui.views.containers.ScrollListView;
import com.apphunt.app.utils.LoginUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.flurry.android.FlurryAgent;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by nmp on 15-7-28.
 */
public class CommentsFragment extends BackStackFragment implements AdapterView.OnItemClickListener {
    private static final String KEY_APP_ID = "APP_ID";

    private String appId;

    private OnCommentEnteredListener listener;
    private CommentsAdapter commentsAdapter;
    private Comment replyToComment;

    @InjectView(R.id.box_comments)
    RelativeLayout boxComments;

    @InjectView(R.id.header_comments)
    TextView headerComments;

    @InjectView(R.id.comments_count)
    ScrollListView commentsList;

    @InjectView(R.id.label_comment)
    TextView labelComment;

    @InjectView(R.id.label_no_comments)
    TextView labelNoComment;

    @InjectView(R.id.comment_entry)
    EditText commentBox;

    @InjectView(R.id.send_comment)
    TextView send;

    @InjectView(R.id.loading)
    CircularProgressBar loading;

    public static CommentsFragment newInstance(String appId) {
        CommentsFragment fragment = new CommentsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_APP_ID, appId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        appId = getArguments().getString(KEY_APP_ID);
        loadData();

        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        ButterKnife.inject(this, view);

        commentsList.setOnItemClickListener(this);
        commentsList.setOnEndReachedListener(new OnEndReachedListener() {
            @Override
            public void onEndReached() {
                FlurryAgent.logEvent(TrackingEvents.UserScrolledDownCommentList);
                commentsAdapter.loadMore(appId, SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID));
            }
        });


        if (userHasPermissions()) {
            labelComment.setVisibility(View.GONE);
            commentBox.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_up);
        } else {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_down);
        }
    }

    @Override
    public int getTitle() {
        return R.string.title_comments;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideSoftKeyboard();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        replyToComment = commentsAdapter.getComment(position);
        String replyName = String.format(getResources().getString(R.string.reply_to), replyToComment.getUser().getUsername()) + " ";

        commentBox.getText().clear();
        commentBox.setText(replyName);
        commentBox.setSelection(replyName.length());
    }



    @OnClick(R.id.send_comment)
    public void sendComment() {
        if (!userHasPermissions()) {
            LoginUtils.showLoginFragment(getActivity(), false, R.string.login_info_comment);
            return;
        }

        NewComment comment = new NewComment();
        if (commentBox.getText().length() > 0) {
            if (replyToComment != null) {
                String replyToName = String.format(getResources().getString(R.string.reply_to), replyToComment.getUser().getUsername());
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
            comment.setUserId(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID));
            comment.setAppId(appId);
            ApiClient.getClient(getActivity()).sendComment(comment);
        }

        closeKeyboard(send);
        commentBox.getText().clear();
        if(this.listener != null) {
            listener.onCommentEntered(comment);
        }

    }

    private boolean userHasPermissions() {
        return LoginProviderFactory.get(getActivity()).isUserLoggedIn();
    }

    private void closeKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void loadData() {
        String userId = SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID);
        ApiService.getInstance(getActivity()).loadAppComments(appId, userId, 1, Constants.COMMENTS_PAGE_SIZE);
    }

    @Subscribe
    public void refreshAppComments(ReloadCommentsEvent event) {
        ApiService.getInstance(getActivity()).reloadAppComments(appId);
    }

    @Subscribe
    public void onUserLogin(LoginEvent event) {
        if (userHasPermissions()) {
            labelComment.setVisibility(View.GONE);
            commentBox.setVisibility(View.VISIBLE);
            commentsAdapter = null;
            loadData();
        }
    }

    @Subscribe
    public void onAppCommentsLoaded(LoadAppCommentsApiEvent event) {
        commentsList.hideBottomLoader();
        Comments comments = event.getComments();
        if(comments == null || comments.getComments() == null) {
            return;
        }

        if(comments.getComments().size() == 0) {
            loading.setVisibility(View.GONE);
            return;
        }

        headerComments.setText(comments.getTotalCount() + " comments");
        loading.setVisibility(View.GONE);
        commentsList.setVisibility(View.VISIBLE);
        if(commentsAdapter == null || event.shouldReload()) {
            commentsAdapter = new CommentsAdapter(getActivity(), event.getComments(), commentsList.getListView());
            commentsList.setAdapter(commentsAdapter, event.getComments().getTotalCount());
        } else {
            commentsAdapter.addItems(comments);
        }
    }

    public void setOnCommentEnteredListener(OnCommentEnteredListener listener) {
        this.listener = listener;
    }

    interface OnCommentEnteredListener {
        void onCommentEntered(NewComment comment);
    }
}
