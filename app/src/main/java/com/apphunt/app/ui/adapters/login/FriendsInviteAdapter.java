package com.apphunt.app.ui.adapters.login;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.twitter.AppHuntTwitterApiClient;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.auth.models.Friend;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.utils.DeepLinkingUtils;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.client.Response;

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

    public FriendsInviteAdapter(Context ctx, ArrayList<Friend> friends) {
        this.ctx = ctx;
        this.friends = friends;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_friend_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Friend friend = friends.get(position);
        final ViewHolder viewHolder = (ViewHolder) holder;

        Picasso.with(ctx)
                .load(friend.getProfileImage())
                .into(viewHolder.profileImage);

        viewHolder.name.setText(friend.getName());

        viewHolder.invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<DeepLinkingUtils.DeepLinkingParam> params = new ArrayList<>();
                params.add(new DeepLinkingUtils.DeepLinkingParam(Constants.KEY_SENDER_ID, LoginProviderFactory.get((Activity) ctx).getUser().getId()));
                params.add(new DeepLinkingUtils.DeepLinkingParam(Constants.KEY_SENDER_NAME, LoginProviderFactory.get((Activity) ctx).getUser().getName()));
                params.add(new DeepLinkingUtils.DeepLinkingParam(Constants.KEY_SENDER_PROFILE_IMAGE_URL, LoginProviderFactory.get((Activity) ctx).getUser().getProfilePicture()));

                Log.e(TAG, friend.getId());

                AppHuntTwitterApiClient twitterApiClient = new AppHuntTwitterApiClient(Twitter.getSessionManager().getActiveSession());
                twitterApiClient.getFriendsService().sendDirectMessage(friend.getId(),
                        "Hey, " + friend.getName() + "! Check out AppHunt cool app: " + DeepLinkingUtils.getInstance((AppCompatActivity) ctx).generateShortUrl(params), new Callback<Response>() {
                            @Override
                            public void success(Result<Response> result) {
                                Log.e(TAG, "success");
                                // TODO: Present notification fragment for successful invite
                                ((AppCompatActivity) ctx).getSupportFragmentManager().popBackStack();
                            }

                            @Override
                            public void failure(TwitterException e) {
                                // TODO: Present notification fragment for error and don't pop the fragment
                                Log.e(TAG, "failure: " + e.toString());
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.profile_image)
        CircleImageView profileImage;

        @InjectView(R.id.name)
        TextView name;

        @InjectView(R.id.invite)
        Button invite;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
