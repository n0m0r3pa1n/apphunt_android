package com.apphunt.app.ui.adapters;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.comments.Comment;
import com.apphunt.app.api.apphunt.models.users.LoginType;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.ui.ReplyToCommentEvent;
import com.apphunt.app.ui.listview_items.Item;
import com.apphunt.app.ui.listview_items.comments.CommentItem;
import com.apphunt.app.ui.listview_items.comments.SubCommentItem;
import com.apphunt.app.ui.views.vote.CommentVoteButton;
import com.apphunt.app.utils.FlurryWrapper;
import com.apphunt.app.utils.StringUtils;
import com.apphunt.app.utils.ui.NavUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by nmp on 15-12-15.
 */
public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    public static final int COMMENT_TYPE = 0;
    public static final int SUBCOMMENT_TYPE = 1;
    private final List<Comment> comments;
    private final List<Item> items = new ArrayList<>();
    private final Context context;

    public CommentsRecyclerAdapter(Context context, List<Comment> comments) {
        this.comments = comments;
        this.context = context;
        populateItems(comments);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if(viewType == COMMENT_TYPE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment, parent, false);
        } else if(viewType == SUBCOMMENT_TYPE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_subcomment, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Comment comment = ((CommentItem) getItem(position)).getData();

        Picasso.with(context)
                .load(comment.getUser().getProfilePicture())
                .placeholder(R.drawable.placeholder_avatar)
                .into(holder.avatar);

        holder.name.setText(comment.getUser().getUsername());
        holder.comment.setText(comment.getText());
        holder.vote.setComment(comment);
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (comment.getUser().getLoginType().equals(LoginType.Anonymous.toString())) {
                    return;
                }

                FlurryWrapper.logEvent(TrackingEvents.UserOpenedProfileFromComment);
                NavUtils.getInstance((AppCompatActivity) context)
                        .presentUserProfileFragment(comment.getUser().getId(), comment.getUser().getName());
            }
        });

        holder.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAndPutReplyName(comment);
            }
        });

        holder.timestamp.setText(StringUtils.getTimeDifferenceString(comment.getCreatedAt()));
    }

    public Object getItem(int position) {
        return items.get(position);
    }

    private void selectAndPutReplyName(Comment comment) {
        BusProvider.getInstance().post(new ReplyToCommentEvent(comment));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void populateItems(List<Comment> comments) {
        for (Comment c : comments) {
            items.add(new CommentItem(c));

            if (c.getChildren().size() > 0) {
                for (Comment subC : c.getChildren()) {
                    items.add(new SubCommentItem(subC));
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (items.get(position).getType().getValue() == Constants.ItemType.COMMENT.getValue()) ? COMMENT_TYPE : SUBCOMMENT_TYPE;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addComments(List<Comment> comments) {
        if(comments == null || comments.size() == 0) {
            return;
        }

        this.comments.addAll(comments);
        populateItems(comments);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
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

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
