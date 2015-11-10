package com.apphunt.app.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiService;
import com.apphunt.app.api.apphunt.models.comments.Comment;
import com.apphunt.app.api.apphunt.models.comments.Comments;
import com.apphunt.app.auth.AnonymousLoginProvider;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.ui.ReplyToCommentEvent;
import com.apphunt.app.ui.listview_items.Item;
import com.apphunt.app.ui.listview_items.comments.CommentItem;
import com.apphunt.app.ui.listview_items.comments.SubCommentItem;
import com.apphunt.app.ui.views.vote.CommentVoteButton;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.StringUtils;
import com.apphunt.app.utils.ui.NavUtils;
import com.flurry.android.FlurryAgent;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends BaseAdapter {
    public static final String TAG = CommentsAdapter.class.getSimpleName();

    private ListView listView;
    private EditText commentBox;
    private String userId;

    private Context ctx;
    private ArrayList<Item> items = new ArrayList<>();
    private CommentViewHolder commentViewHolder = null;
    private SubCommentViewHolder subCommentViewHolder = null;
    private LayoutInflater inflater;
    private int page;
    private int totalPages;

    public CommentsAdapter(Context ctx, Comments comments, ListView listView, EditText commentBox) {
        this.ctx = ctx;
        this.listView = listView;
        this.commentBox = commentBox;

        addItems(comments);
        totalPages = comments.getTotalPages();
        page = comments.getPage();
        userId = SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID);

        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            if (getItemViewType(position) == 0) {
                view = inflater.inflate(R.layout.layout_comment, parent, false);
                commentViewHolder = new CommentViewHolder(view);
                view.setTag(commentViewHolder);
            } else if (getItemViewType(position) == 1) {
                view = inflater.inflate(R.layout.layout_subcomment, parent, false);
                subCommentViewHolder = new SubCommentViewHolder(view);
                view.setTag(subCommentViewHolder);
            }
        } else {
            if (getItemViewType(position) == 0) {
                commentViewHolder = (CommentViewHolder) view.getTag();
            } else if (getItemViewType(position) == 1) {
                subCommentViewHolder = (SubCommentViewHolder) view.getTag();
            }
        }

        if (getItemViewType(position) == 0 && commentViewHolder != null) {
            final Comment comment = ((CommentItem) getItem(position)).getData();

            Picasso.with(ctx)
                    .load(comment.getUser().getProfilePicture())
                    .placeholder(R.drawable.placeholder_avatar)
                    .into(commentViewHolder.avatar);

            commentViewHolder.name.setText(comment.getUser().getUsername());
            commentViewHolder.comment.setText(comment.getText());
            commentViewHolder.vote.setComment(comment);
            commentViewHolder.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (comment.getUser().getLoginType().equals(AnonymousLoginProvider.PROVIDER_NAME)) {
                        return;
                    }

                    FlurryAgent.logEvent(TrackingEvents.UserOpenedProfileFromComment);
                    NavUtils.getInstance((AppCompatActivity) ctx)
                            .presentUserProfileFragment(comment.getUser().getId(), comment.getUser().getName());
                }
            });

            commentViewHolder.reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectAndPutReplyName(comment);
                }
            });

            commentViewHolder.timestamp.setText(StringUtils.getTimeDifferenceString(comment.getCreatedAt()));
        } else if (getItemViewType(position) == 1 && subCommentViewHolder != null) {
            final Comment comment = ((SubCommentItem) getItem(position)).getData();

            Picasso.with(ctx)
                    .load(comment.getUser().getProfilePicture())
                    .placeholder(R.drawable.placeholder_avatar)
                    .into(subCommentViewHolder.avatar);

            subCommentViewHolder.name.setText(comment.getUser().getUsername());
            subCommentViewHolder.comment.setText(comment.getText());
            subCommentViewHolder.vote.setComment(comment);
            subCommentViewHolder.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (comment.getUser().getLoginType().equals(AnonymousLoginProvider.PROVIDER_NAME)) {
                        return;
                    }

                    FlurryAgent.logEvent(TrackingEvents.UserOpenedProfileFromComment);
                    NavUtils.getInstance((AppCompatActivity) ctx)
                            .presentUserProfileFragment(comment.getUser().getId(), comment.getUser().getName());
                }
            });

            subCommentViewHolder.reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectAndPutReplyName(comment);
                }
            });

            subCommentViewHolder.timestamp.setText(StringUtils.getTimeDifferenceString(comment.getCreatedAt()));
        }

        return view;
    }

    private void selectAndPutReplyName(Comment comment) {
        BusProvider.getInstance().post(new ReplyToCommentEvent(comment));

        if (commentBox == null) {
            return;
        }

        String replyName = String.format(ctx.getResources().getString(R.string.reply_to), comment.getUser().getUsername()) + " ";

        commentBox.getText().clear();
        commentBox.setText(replyName);
        commentBox.setSelection(replyName.length());
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (items.get(position).getType().getValue() == Constants.ItemType.COMMENT.getValue()) ? 0 : 1;
    }

    public Comment getComment(int position) {
        Item item = items.get(position);
        return (item instanceof CommentItem) ? ((CommentItem) item).getData() : ((SubCommentItem) item).getData();
    }

    public void addItems(Comments comments) {
        for (Comment c : comments.getComments()) {
            items.add(new CommentItem(c));

            if (c.getChildren().size() > 0) {
                for (Comment subC : c.getChildren()) {
                    items.add(new SubCommentItem(subC));
                }
            }
        }

        page = comments.getPage();
        notifyDataSetChanged();

        if(listView != null) {
            listView.smoothScrollToPosition(items.size() - 2);
        }
        totalPages = comments.getTotalPages();
    }

    public void loadMore(String appId, String userId) {
        if (page < totalPages) {
            ApiService.getInstance(ctx).loadAppComments(appId, userId, page + 1, Constants.COMMENTS_PAGE_SIZE);
        }
    }

    public void resetAdapter(Comments comments) {
        items.clear();
        addItems(comments);
    }

    private boolean userHasPermissions() {
        return LoginProviderFactory.get((Activity) ctx).isUserLoggedIn();
    }

    static class CommentViewHolder {
        @InjectView(R.id.layout)
        RelativeLayout layout;

        @InjectView(R.id.avatar)
        CircleImageView avatar;

        @InjectView(R.id.name)
        TextView name;

        @InjectView(R.id.timestamp)
        TextView timestamp;

        @InjectView(R.id.score_text)
        TextView comment;

        @InjectView(R.id.vote_btn)
        CommentVoteButton vote;

        @InjectView(R.id.reply)
        Button reply;

        @InjectView(R.id.divider)
        View divider;

        public CommentViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    static class SubCommentViewHolder extends CommentViewHolder {
        public SubCommentViewHolder(View view) {
            super(view);
        }
    }
}
