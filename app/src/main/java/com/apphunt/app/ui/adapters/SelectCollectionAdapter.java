package com.apphunt.app.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.ui.views.vote.VoteCollectionButton;
import com.apphunt.app.ui.widgets.AvatarImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 6/26/15.
 * *
 * * NaughtySpirit 2015
 */
public class SelectCollectionAdapter extends RecyclerView.Adapter<SelectCollectionAdapter.ViewHolder> {

    private Context ctx;
    private List<AppsCollection> collections = new ArrayList<>();

    public SelectCollectionAdapter(Context ctx, List<AppsCollection> collections) {
        this.ctx = ctx;
        this.collections = collections;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.layout_select_collection_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AppsCollection collection = collections.get(position);

        holder.name.setText(collection.getName());
        holder.createdBy.setText(collection.getCreatedBy().getUsername());
        Picasso.with(ctx).load(collection.getPicture()).into(holder.createdByAvatar);
        Picasso.with(ctx).load(collection.getPicture()).into(holder.banner);
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.banner)
        ImageView banner;

        @InjectView(R.id.collection_name)
        TextView name;

        @InjectView(R.id.created_by_image)
        Target createdByAvatar;

        @InjectView(R.id.created_by)
        TextView createdBy;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
