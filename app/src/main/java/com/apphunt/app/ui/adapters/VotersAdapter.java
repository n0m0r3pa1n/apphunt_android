package com.apphunt.app.ui.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.api.apphunt.models.votes.AppVote;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class VotersAdapter extends BaseAdapter {

    private final Context ctx;
    private List<AppVote> voters = new ArrayList<>();
    private int maxVotersCount;

    public VotersAdapter(Context ctx, List<AppVote> voters) {
        this.ctx = ctx;
        this.voters = voters;

        maxVotersCount = getVotersScreenFitNumber(ctx);
        if (voters.size() > maxVotersCount) {
            maxVotersCount *= 2;
        }
    }

    private int getVotersScreenFitNumber(Context ctx) {
        return (floatToDP(ctx.getResources().getDisplayMetrics().widthPixels) -
                (2 * floatToDP(ctx.getResources().getDimension(R.dimen.details_box_voters_padding_sides)))) /
                floatToDP(ctx.getResources().getDimension(R.dimen.details_voters_avatar_cell));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.layout_voter_avatar, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        String avatarUrl = "";
        User user = ((AppVote) getItem(position)).getUser();
        if (user != null) {
            avatarUrl = user.getProfilePicture();
        }

        if (TextUtils.isEmpty(avatarUrl)) {
            Picasso.with(ctx)
                    .load(R.drawable.avatar_placeholder)
                    .placeholder(R.drawable.avatar_placeholder)
                    .into(viewHolder.avatar);
        } else {
            Picasso.with(ctx)
                    .load(avatarUrl)
                    .placeholder(R.drawable.avatar_placeholder)
                    .into(viewHolder.avatar);
        }

        return view;
    }

    @Override
    public int getCount() {
        return (voters.size() <= maxVotersCount) ? voters.size() : maxVotersCount;
    }

    @Override
    public Object getItem(int position) {
        return voters.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public int floatToDP(float size) {
        return (int) (size * ctx.getResources().getDisplayMetrics().density);
    }

    static class ViewHolder {
        @InjectView(R.id.avatar)
        Target avatar;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    public void addCreatorIfNotVoter(User user) {
        boolean isVoter = false;

        for (AppVote vote : voters) {
            if (vote.getUser().getId().equals(user.getId())) {
                isVoter = true;
            }
        }

        if (!isVoter) {
            AppVote vote = new AppVote(user.getId());
            vote.setUser(user);
            voters.add(vote);
            notifyDataSetChanged();
        }
    }

    public void removeCreator(User user) {
        for (AppVote vote : voters) {
            if (vote.getUser().getId().equals(user.getId())) {
                voters.remove(vote);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public int getTotalVoters() {
        return voters.size();
    }
}