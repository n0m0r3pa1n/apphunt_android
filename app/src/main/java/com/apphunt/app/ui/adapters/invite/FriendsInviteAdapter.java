package com.apphunt.app.ui.adapters.invite;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.auth.models.Friend;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/25/15.
 * *
 * * NaughtySpirit 2015
 */
public class FriendsInviteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = FriendsInviteAdapter.class.getSimpleName();

    private Context ctx;
    private ArrayList<Friend> friends = new ArrayList<>();
    private ArrayList<Boolean> positionsArray = new ArrayList<>();

    public FriendsInviteAdapter(Context ctx, ArrayList<Friend> friends) {
        this.ctx = ctx;
        this.friends = friends;

        for (int i = 0; i < friends.size(); i++) {
            positionsArray.add(i, false);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_friend_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Friend friend = friends.get(position);
        final ViewHolder viewHolder = (ViewHolder) holder;

        Picasso.with(ctx)
                .load(friend.getProfileImage())
                .into(viewHolder.profileImage);

        viewHolder.name.setText(friend.getName());

        viewHolder.invite.setTag(String.valueOf(position));
        viewHolder.invite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int pos = Integer.valueOf(compoundButton.getTag().toString());
                positionsArray.set(pos, b);
            }
        });
        viewHolder.invite.setChecked(positionsArray.get(position));
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public ArrayList<Friend> getSelectedFriends() {
        ArrayList<Friend> selectedFriends = new ArrayList<>();
        for (int i = 0; i < positionsArray.size(); i++) {
            if (positionsArray.get(i)) {
                selectedFriends.add(friends.get(i));
            }
        }
        return selectedFriends;
    }

    public void selectAllItems() {
        for (int i = 0; i < friends.size(); i++) {
            positionsArray.set(i, true);
        }

        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.profile_image)
        CircleImageView profileImage;

        @InjectView(R.id.name)
        TextView name;

        @InjectView(R.id.sw_invite)
        Switch invite;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
