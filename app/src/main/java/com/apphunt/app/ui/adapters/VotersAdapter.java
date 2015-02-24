package com.apphunt.app.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.apphunt.app.R;
import com.apphunt.app.api.models.Vote;
import com.apphunt.app.ui.widgets.AvatarImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

public class VotersAdapter extends BaseAdapter {

    private final Context ctx;
    private ArrayList<Vote> voters = new ArrayList<>();
    private int maxVotersCount;

    public VotersAdapter(Context ctx, ArrayList<Vote> voters) {
        this.ctx = ctx;
        this.voters = voters;

        maxVotersCount = (floatToDP(ctx.getResources().getDisplayMetrics().widthPixels) -
                (2 * floatToDP(ctx.getResources().getDimension(R.dimen.details_box_voters_padding_sides)))) /
                floatToDP(ctx.getResources().getDimension(R.dimen.details_voters_avatar_cell));
        maxVotersCount *= 2;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.layout_voter_avatar, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.avatar = (AvatarImageView) view.findViewById(R.id.avatar);
            
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Picasso.with(ctx)
                .load(((Vote) getItem(position)).getUser().getProfilePicture())
                .into(viewHolder.avatar);

        return view;
    }

    public int floatToDP(float size) {
        return (int) (size * ctx.getResources().getDisplayMetrics().density);
    }
    
    private static class ViewHolder {
        Target avatar;
    }
}