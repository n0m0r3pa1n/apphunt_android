package com.apphunt.app.ui.adapters.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.chat.ChatUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nmp on 16-1-28.
 */
public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UserViewHolder> {
    private Context context;
    private List<ChatUser> users;

    public UsersListAdapter(Context context, List<ChatUser> users) {
        this.users = users;
        this.context = context;
    }

    @Override
    public UsersListAdapter.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UsersListAdapter.UserViewHolder holder, int position) {
        final ChatUser chatUser = users.get(position);
        holder.tvName.setText(chatUser.getName());
        holder.tvUserName.setText(chatUser.getUsername());
        Picasso.with(context).load(chatUser.getProfilePicture()).into(holder.ivAvatar);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.tv_name)
        TextView tvName;

        @InjectView(R.id.tv_username)
        TextView tvUserName;

        @InjectView(R.id.iv_avatar)
        ImageView ivAvatar;


        public UserViewHolder(View rootView) {
            super(rootView);
            ButterKnife.inject(this, rootView);
        }
    }
}
