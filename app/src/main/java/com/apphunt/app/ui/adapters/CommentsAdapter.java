package com.apphunt.app.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.AppHuntApiClient;
import com.apphunt.app.api.apphunt.Callback;
import com.apphunt.app.api.apphunt.models.Comment;
import com.apphunt.app.api.apphunt.models.Comments;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.ui.listview_items.Item;
import com.apphunt.app.ui.listview_items.comments.CommentItem;
import com.apphunt.app.ui.listview_items.comments.SubCommentItem;
import com.apphunt.app.ui.views.vote.CommentVoteButton;
import com.apphunt.app.ui.widgets.AvatarImageView;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import retrofit.client.Response;

public class CommentsAdapter extends BaseAdapter {

    private static final String TAG = CommentsAdapter.class.getName();
    private ListView listView;
    private String userId;

    private Context ctx;
    private ArrayList<Item> items = new ArrayList<>();
    private CommentViewHolder commentViewHolder = null;
    private SubCommentViewHolder subCommentViewHolder = null;
    private LayoutInflater inflater;
    private int page;
    private int totalPages;

    public CommentsAdapter(Context ctx, Comments comments, ListView listView) {
        this.ctx = ctx;
        this.listView = listView;

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
                commentViewHolder = new CommentViewHolder();
                view = inflater.inflate(R.layout.layout_comment, parent, false);

                commentViewHolder.layout = (RelativeLayout) view.findViewById(R.id.layout);
                commentViewHolder.avatar = (AvatarImageView) view.findViewById(R.id.avatar);
                commentViewHolder.name = (TextView) view.findViewById(R.id.name);
                commentViewHolder.comment = (TextView) view.findViewById(R.id.text);
                commentViewHolder.vote = (CommentVoteButton) view.findViewById(R.id.vote_btn);

                view.setTag(commentViewHolder);
            } else if (getItemViewType(position) == 1) {
                view = inflater.inflate(R.layout.layout_subcomment, parent, false);
                subCommentViewHolder = new SubCommentViewHolder();

                subCommentViewHolder.layout = (RelativeLayout) view.findViewById(R.id.layout);
                subCommentViewHolder.avatar = (AvatarImageView) view.findViewById(R.id.avatar);
                subCommentViewHolder.name = (TextView) view.findViewById(R.id.name);
                subCommentViewHolder.comment = (TextView) view.findViewById(R.id.text);
                subCommentViewHolder.vote = (CommentVoteButton) view.findViewById(R.id.vote_btn);

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
                    .into(commentViewHolder.avatar);

            commentViewHolder.name.setText(comment.getUser().getUsername());
            commentViewHolder.comment.setText(comment.getText());

            if (comment.getUser().getId().equals(userId)) {
                commentViewHolder.layout.setBackgroundResource(R.color.bg_own_comment);
            } else {
                commentViewHolder.layout.setBackgroundResource(android.R.color.transparent);
            }

            commentViewHolder.vote.setComment(comment);
        } else if (getItemViewType(position) == 1 && subCommentViewHolder != null) {
            final Comment comment = ((SubCommentItem) getItem(position)).getData();

            Picasso.with(ctx)
                    .load(comment.getUser().getProfilePicture())
                    .into(subCommentViewHolder.avatar);

            subCommentViewHolder.name.setText(comment.getUser().getUsername());
            subCommentViewHolder.comment.setText(comment.getText());


            if (comment.getUser().getId().equals(userId)) {
                subCommentViewHolder.layout.setBackgroundResource(R.color.bg_own_comment);
            } else {
                subCommentViewHolder.layout.setBackgroundResource(android.R.color.transparent);
            }

            subCommentViewHolder.vote.setComment(comment);
        }

        return view;
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
    }

    public void loadMore(String appId, String userId, final TextView header) {
        if (page < totalPages) {
            AppHuntApiClient.getClient().getAppComments(appId, userId, page + 1, 3, new Callback<Comments>() {
                @Override
                public void success(Comments comments, Response response) {
                    addItems(comments);
                    listView.smoothScrollToPosition(items.size() - 3);
                    page = comments.getPage();
                    totalPages = comments.getTotalPages();
                    header.setText(ctx.getResources().getQuantityString(R.plurals.header_comments, getCount(), getCount()));
                }
            });
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
        RelativeLayout layout;
        Target avatar;
        TextView name;
        TextView comment;
        CommentVoteButton vote;
    }

    static class SubCommentViewHolder {
        RelativeLayout layout;
        Target avatar;
        TextView name;
        TextView comment;
        CommentVoteButton vote;
    }
}
