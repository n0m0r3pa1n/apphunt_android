package com.apphunt.app.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.models.Comment;
import com.apphunt.app.ui.listview_items.Item;
import com.apphunt.app.ui.listview_items.comments.CommentItem;
import com.apphunt.app.ui.listview_items.comments.SubCommentItem;
import com.apphunt.app.ui.widgets.AvatarImageView;
import com.apphunt.app.utils.Constants;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

public class CommentsAdapter extends BaseAdapter {

    private static final String TAG = CommentsAdapter.class.getName();
    
    private Context ctx;
    private ArrayList<Item> items = new ArrayList<>();
    private CommentViewHolder commentViewHolder = null;
    private SubCommentViewHolder subCommentViewHolder = null;
    private LayoutInflater inflater;

    public CommentsAdapter(Context ctx, ArrayList<Comment> comments) {
        this.ctx = ctx;

        for (Comment c : comments) {
            items.add(new CommentItem(c));

            if (c.getChildren().size() > 0) {
                for (Comment subC: c.getChildren()) {
                    items.add(new SubCommentItem(subC));
                }
            }
        }

        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        
        if (view == null) {
            if (getItemViewType(position) == 0) {
                commentViewHolder = new CommentViewHolder();
                view = inflater.inflate(R.layout.layout_comment, parent, false);

                commentViewHolder.avatar = (AvatarImageView) view.findViewById(R.id.avatar);
                commentViewHolder.name = (TextView) view.findViewById(R.id.name);
                commentViewHolder.comment = (TextView) view.findViewById(R.id.comment);
                
                view.setTag(commentViewHolder);
            }
            else if (getItemViewType(position) == 1) {
                view = inflater.inflate(R.layout.layout_subcomment, parent, false);
                subCommentViewHolder = new SubCommentViewHolder();

                subCommentViewHolder.avatar = (AvatarImageView) view.findViewById(R.id.avatar);
                subCommentViewHolder.name = (TextView) view.findViewById(R.id.name);
                subCommentViewHolder.comment = (TextView) view.findViewById(R.id.comment);

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
            Comment comment = ((CommentItem) getItem(position)).getData();

            Picasso.with(ctx)
                    .load(comment.getUser().getProfilePicture())
                    .into(commentViewHolder.avatar);

            commentViewHolder.name.setText(String.format(ctx.getString(R.string.commenter_name), comment.getUser().getName()));
            commentViewHolder.comment.setText(comment.getText());

        }
        else if (getItemViewType(position) == 1 && subCommentViewHolder != null) {
            Comment comment = ((SubCommentItem) getItem(position)).getData();

            Picasso.with(ctx)
                    .load(comment.getUser().getProfilePicture())
                    .into(subCommentViewHolder.avatar);

            subCommentViewHolder.name.setText(comment.getUser().getName());
            subCommentViewHolder.comment.setText(comment.getText());
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
    
    static class CommentViewHolder {
        Target avatar;
        TextView name;
        TextView comment;
    }

    static class SubCommentViewHolder {
        Target avatar;
        TextView name;
        TextView comment;
    }
}
