package com.apphunt.app.ui.adapters.chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.chat.ChatUser;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nmp on 16-1-28.
 */
public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UserViewHolder> {
    private List<ChatUser> users;
    public UsersListAdapter(List<ChatUser> users) {
        this.users = users;
    }

    @Override
    public UsersListAdapter.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UsersListAdapter.UserViewHolder holder, int position) {
        holder.userName.setText(users.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.tv_user_name)
        TextView userName;


        public UserViewHolder(View rootView) {
            super(rootView);
            ButterKnife.inject(this, rootView);
        }
    }
}
