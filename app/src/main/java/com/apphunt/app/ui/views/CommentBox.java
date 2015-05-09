package com.apphunt.app.ui.views;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.Comment;
import com.apphunt.app.api.apphunt.models.Comments;
import com.apphunt.app.api.apphunt.models.NewComment;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.ui.adapters.CommentsAdapter;
import com.apphunt.app.utils.ConnectivityUtils;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.LoginUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.TrackingEvents;
import com.flurry.android.FlurryAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class CommentBox extends RelativeLayout implements  AbsListView.OnScrollListener, AdapterView.OnItemClickListener {

    private int commentBoxHeight;
    private int belowId;
    private boolean isCommentsBoxOpened = false;
    private boolean endOfList;
    private String appId;

    private RelativeLayout.LayoutParams params;
    private LayoutInflater inflater;

    private CommentsAdapter commentsAdapter;
    private Comment replyToComment;
    private OnDisplayCommentBox callback;

    @InjectView(R.id.box_comments)
    RelativeLayout boxComments;

    @InjectView(R.id.header_comments)
    TextView headerComments;

    @InjectView(R.id.show_comments)
    TextView showAllComments;

    @InjectView(R.id.hide_comments)
    TextView hideAllComments;

    @InjectView(R.id.comments_count)
    ListView commentsList;

    @InjectView(R.id.label_comment)
    TextView labelComment;

    @InjectView(R.id.label_no_comments)
    TextView labelNoComment;

    @InjectView(R.id.comment_entry)
    EditText commentBox;

    @InjectView(R.id.send_comment)
    TextView send;


    public CommentBox(Context context) {
        super(context);
        if (!isInEditMode()) {
            init(context);
        }
    }

    public CommentBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(context);
        }
    }

    public CommentBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            init(context);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        closeKeyboard(commentBox);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (ConnectivityUtils.isNetworkAvailable(getContext())) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                if (endOfList) {
                    FlurryAgent.logEvent(TrackingEvents.UserScrolledDownCommentList);
                    commentsAdapter.loadMore(appId, SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID));
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        endOfList = (firstVisibleItem + visibleItemCount) == totalItemCount;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        replyToComment = commentsAdapter.getComment(position);
        String replyName = String.format(getResources().getString(R.string.reply_to), replyToComment.getUser().getUsername()) + " ";

        commentBox.getText().clear();
        commentBox.setText(replyName);
        commentBox.setSelection(replyName.length());
        showBox();
    }

    @OnClick(R.id.show_comments)
    public void showComments() {
        showBox();
    }

    @OnClick(R.id.label_comment)
    public void showLoginFragment() {
        LoginUtils.showLoginFragment(getContext());
    }

    @OnClick(R.id.hide_comments)
    public void hideComments() {
        hideBox();
    }

    @OnClick(R.id.send_comment)
    public void sendComment() {
        if (!userHasPermissions()) {
            LoginUtils.showLoginFragment(getContext());
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
//
//            ApiClient.getClient(getActivity()).sendComment(comment, new Callback<NewComment>() {
//                @Override
//                public void success(NewComment comment, Response response) {
//                    if (response.getStatus() == 200) {
//                        ApiClient.getClient(getActivity()).getAppComments(app.getId(),
//                                SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID),
//                                1, 3);
//                        //TODO
////                                ApiClient.getClient(getActivity()).getAppComments(app.getId(), SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID),
////                                        1, 3, new Callback<Comments>() {
////                                            @Override
////                                            public void success(Comments comments, Response response) {
////                                                if (comments != null) {
////                                                    commentsAdapter.resetAdapter(comments);
////                                                    headerComments.setText(activity.getResources().getQuantityString(R.plurals.header_comments, commentsAdapter.getCount(), commentsAdapter.getCount()));
////
////                                                    view.findViewById(R.id.label_no_comments).setVisibility(View.GONE);
////                                                }
////                                            }
////                                        });
//                    }
//                }
//            });
        }

        closeKeyboard(send);
        commentBox.getText().clear();
        resizeCommentBox(true);

    }

    protected void init(Context context) {
        if(inflater == null) {
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        View view = inflater.inflate(R.layout.view_comment_box, this, true);
        ButterKnife.inject(this, view);

        commentsList.setOnItemClickListener(this);
        commentsList.setOnScrollListener(this);
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
                showBox();
                return false;
            }
        });

        if (userHasPermissions()) {
            labelComment.setVisibility(View.GONE);
            commentBox.setVisibility(View.VISIBLE);
        }

    }

    public void setComments(Comments comments) {
        if (comments.getTotalCount() == 0) {
            labelNoComment.setVisibility(View.VISIBLE);
        }
        if(commentsAdapter == null) {
            commentsAdapter = new CommentsAdapter(getContext(), comments, commentsList);
            commentsList.setAdapter(commentsAdapter);
        } else {
            commentsAdapter.addItems(comments);
        }

        headerComments.setText(getResources().getQuantityString(R.plurals.header_comments, comments.getTotalCount(), comments.getTotalCount()));
    }

    public void invalidateViews() {
        commentsList.invalidateViews();
    }

    public void checkIfUserCanComment() {
        if (userHasPermissions()) {
            labelComment.setVisibility(View.GONE);
            commentBox.setVisibility(View.VISIBLE);
        } else {
            labelComment.setVisibility(View.VISIBLE);
            commentBox.setVisibility(View.INVISIBLE);
        }
    }

    public void setBelowId(int id) {
        belowId = id;
    }

    public void setAppId(String id) {
        appId = id;
    }

    public void addOnDisplayCommentsBoxListener(OnDisplayCommentBox listener) {
        callback = listener;
    }

    public void removeOnDisplayCommentsBoxListener(OnDisplayCommentBox listener) {
        callback = null;
    }

    public void hideBox() {
        ((ActionBarActivity) getContext()).getSupportActionBar().setTitle(R.string.title_app_details);
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, belowId);

        boxComments.setLayoutParams(params);
        if(callback != null) {
            callback.onCommentsBoxDisplayed(false);
        }
        resizeCommentBox(true);
        isCommentsBoxOpened = false;
        closeKeyboard(commentBox);

        showAllComments.setVisibility(View.VISIBLE);
        hideAllComments.setVisibility(View.INVISIBLE);
    }


    public void showBox() {
        ((ActionBarActivity) getContext()).getSupportActionBar().setTitle(R.string.title_close);
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        boxComments.setLayoutParams(params);
        if(callback != null) {
            callback.onCommentsBoxDisplayed(true);
        }
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

    private boolean userHasPermissions() {
        return LoginProviderFactory.get((Activity)getContext()).isUserLoggedIn();
    }

    private void closeKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public interface OnDisplayCommentBox {
        void onCommentsBoxDisplayed(boolean isBoxFullscreen);
    }

}
