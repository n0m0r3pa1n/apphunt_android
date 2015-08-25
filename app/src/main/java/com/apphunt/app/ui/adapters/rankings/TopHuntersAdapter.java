package com.apphunt.app.ui.adapters.rankings;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.collections.hunters.Hunter;
import com.apphunt.app.api.apphunt.models.collections.hunters.HuntersCollection;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.utils.ui.NavUtils;
import com.flurry.android.FlurryAgent;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by nmp on 15-6-8.
 */
public class TopHuntersAdapter extends RecyclerView.Adapter<TopHuntersAdapter.ViewHolder> {

    private HuntersCollection collection;
    private Context context;

    public TopHuntersAdapter(Context context, HuntersCollection collection) {
        this.collection = collection;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_collection_hunter, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Hunter hunter = collection.getHunters().get(position);

        holder.place.setText(String.valueOf(position + 1));
        holder.name.setText(hunter.getUser().getName());
        holder.score.setText("Score " + hunter.getScore());
        holder.username.setText("@" + hunter.getUser().getUsername());
        holder.appsCount.setText(String.valueOf(hunter.getAddedApps()));
        holder.commentsCount.setText(String.valueOf(hunter.getComments()));
        holder.collectionsCount.setText(String.valueOf(hunter.getCollections()));
        holder.votesCount.setText(String.valueOf(hunter.getVotes()));

        Picasso.with(context)
                .load(hunter.getUser().getProfilePicture())
                .placeholder(R.drawable.placeholder_avatar)
                .into(holder.picture);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlurryAgent.logEvent(TrackingEvents.UserOpenedProfileFromTopHunters);
                NavUtils.getInstance((AppCompatActivity) context)
                        .presentUserProfileFragment(hunter.getUser().getId(), hunter.getUser().getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return collection.getHunters().size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.card_view)
        View cardView;

        @InjectView(R.id.place)
        TextView place;

        @InjectView(R.id.name)
        TextView name;

        @InjectView(R.id.username)
        TextView username;

        @InjectView(R.id.score)
        TextView score;

        @InjectView(R.id.picture)
        CircleImageView picture;

        @InjectView(R.id.apps_count)
        TextView appsCount;

        @InjectView(R.id.comments_count)
        TextView commentsCount;

        @InjectView(R.id.collections_count)
        TextView collectionsCount;

        @InjectView(R.id.votes_count)
        TextView votesCount;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
