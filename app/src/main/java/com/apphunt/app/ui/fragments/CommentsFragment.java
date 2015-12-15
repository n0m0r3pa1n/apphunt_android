package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.clients.rest.ApiService;
import com.apphunt.app.api.apphunt.models.comments.Comment;
import com.apphunt.app.api.apphunt.models.comments.Comments;
import com.apphunt.app.api.apphunt.models.comments.NewComment;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.events.api.apps.LoadAppCommentsApiEvent;
import com.apphunt.app.event_bus.events.ui.ReloadCommentsEvent;
import com.apphunt.app.event_bus.events.ui.ReplyToCommentEvent;
import com.apphunt.app.ui.adapters.CommentsAdapter;
import com.apphunt.app.ui.fragments.base.BackStackFragment;
import com.apphunt.app.ui.interfaces.OnEndReachedListener;
import com.apphunt.app.ui.views.containers.ScrollListView;
import com.apphunt.app.utils.FlurryWrapper;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by nmp on 15-7-28.
 */
public class CommentsFragment extends BackStackFragment {
    private static final String KEY_APP_ID = "APP_ID";

    private String appId;

    private AppCompatActivity activity;
    private OnCommentEnteredListener listener;
    private CommentsAdapter commentsAdapter;
    private Comment replyToComment;

    @InjectView(R.id.box_comments)
    RelativeLayout boxComments;

    @InjectView(R.id.comments_count)
    ScrollListView commentsList;

    @InjectView(R.id.label_no_comments)
    TextView labelNoComment;

    @InjectView(R.id.avatar)
    CircleImageView avatar;

    @InjectView(R.id.comment_entry)
    EditText commentBox;

    @InjectView(R.id.send)
    Button send;

    @InjectView(R.id.loading)
    CircularProgressBar loading;

    public static CommentsFragment newInstance(String appId) {
        CommentsFragment fragment = new CommentsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_APP_ID, appId);
        fragment.setArguments(bundle);
        FlurryWrapper.logEvent(TrackingEvents.UserViewedComments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        appId = getArguments().getString(KEY_APP_ID);
        loadData();

        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        ButterKnife.inject(this, view);

        commentsList.setOnEndReachedListener(new OnEndReachedListener() {
            @Override
            public void onEndReached() {
                FlurryWrapper.logEvent(TrackingEvents.UserScrolledDownCommentList);
                commentsAdapter.loadMore(appId, SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID));
            }
        });

        Picasso.with(activity)
                .load(LoginProviderFactory.get(activity).getUser().getProfilePicture())
                .placeholder(R.drawable.avatar_placeholder)
                .into(avatar);

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (AppCompatActivity) activity;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideSoftKeyboard();
    }

    @OnClick(R.id.send)
    public void sendComment() {
        send.setEnabled(false);
        NewComment comment = new NewComment();
        if (commentBox.getText().length() > 0) {
            if (replyToComment != null) {
                String replyToName = String.format(getResources().getString(R.string.reply_to), replyToComment.getUser().getUsername());
                int replyToNameLength = replyToName.length();

                if (commentBox.getText().length() > replyToNameLength &&
                        replyToName.toLowerCase().equals(commentBox.getText().toString().substring(0, replyToNameLength).toLowerCase())) {
                    if (replyToComment.getParent() == null) {
                        comment.setParentId(replyToComment.getId());
                    } else {
                        comment.setParentId(replyToComment.getParent());
                    }
                    FlurryWrapper.logEvent(TrackingEvents.UserSentReplyComment);
                } else {
                    FlurryWrapper.logEvent(TrackingEvents.UserSentComment);
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

        send.setEnabled(true);
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
    public void onAppCommentsLoaded(LoadAppCommentsApiEvent event) {
        Comments comments = event.getComments();
        if(comments == null || comments.getComments() == null) {
            return;
        }

        if(comments.getComments().size() == 0) {
            loading.setVisibility(View.GONE);

            new Handler().postAtTime(new Runnable() {
                @Override
                public void run() {
                    showKeyboard();
                }
            }, 800);

            return;
        }

        loading.setVisibility(View.GONE);
        commentsList.setVisibility(View.VISIBLE);
        if(commentsAdapter == null) {
            commentsAdapter = new CommentsAdapter(getActivity(), event.getComments(), commentsList.getListView(), commentBox);
            commentsList.setAdapter(commentsAdapter, event.getComments().getTotalCount());
        } else {
            commentsAdapter.addItems(comments);
        }
    }



    @Subscribe
    public void onReplyCommentSelected(ReplyToCommentEvent event) {
        this.replyToComment = event.getComment();
        showKeyboard();
    }

    public void setOnCommentEnteredListener(OnCommentEnteredListener listener) {
        this.listener = listener;
    }

    public interface OnCommentEnteredListener {
        void onCommentEntered(NewComment comment);
    }

    private void showKeyboard() {
        commentBox.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(commentBox, 0);
    }
}
