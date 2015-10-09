package com.apphunt.app.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.ui.views.widgets.AHTextView;
import com.apphunt.app.ui.views.widgets.FollowButton;
import com.apphunt.app.utils.ui.NavUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.ViewHolder> {

    private List<User> users;
    private Context context;

    public FollowersAdapter(Context context, List<User> users) {
        this.users = users;
        this.context =  context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_follower_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User user = users.get(position);
        holder.followerName.setText(user.getName());
        Picasso.with(context).load(user.getProfilePicture()).into(holder.followerAvatar);
        holder.followButton.init((Activity) context, user);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.getInstance((AppCompatActivity) context).presentUserProfileFragment(user.getId(), user.getName());
            }
        });
    }

    public void addItems(List<User> users) {
        this.users.addAll(users);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.follower_container)
        RelativeLayout container;

        @InjectView(R.id.follower_name)
        AHTextView followerName;

        @InjectView(R.id.follower_avatar)
        CircleImageView followerAvatar;

        @InjectView(R.id.follow)
        FollowButton followButton;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
