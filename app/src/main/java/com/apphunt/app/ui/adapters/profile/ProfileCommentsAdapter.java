package com.apphunt.app.ui.adapters.profile;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.comments.Comment;
import com.apphunt.app.api.apphunt.models.comments.ProfileComment;
import com.apphunt.app.utils.ui.NavUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProfileCommentsAdapter extends RecyclerView.Adapter<ProfileCommentsAdapter.ViewHolder> {

    private final Context context;
    private final List<ProfileComment> commentList;

    public ProfileCommentsAdapter(Context context, List<ProfileComment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_profile_comment, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ProfileComment comment = commentList.get(position);
        holder.commentText.setText(comment.getText());
        holder.commentVotes.setText(comment.getVotesCount() + "");
        holder.appCategory.setText(comment.getApp().getCategories().get(0));
        holder.appTitle.setText(comment.getApp().getName());
        Picasso.with(context).load(comment.getApp().getIcon()).into(holder.appIcon);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.getInstance((AppCompatActivity) context).presentAppDetailsFragment(comment.getApp());
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void addItems(List<ProfileComment> comments) {
        this.commentList.addAll(comments);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.container)
        RelativeLayout container;

        @InjectView(R.id.app_icon)
        ImageView appIcon;

        @InjectView(R.id.app_title)
        TextView appTitle;

        @InjectView(R.id.app_category)
        TextView appCategory;

        @InjectView(R.id.comment_text)
        TextView commentText;

        @InjectView(R.id.comments_votes)
        TextView commentVotes;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
