package com.apphunt.app.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.ui.interfaces.OnItemClickListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 6/26/15.
 * *
 * * NaughtySpirit 2015
 */
public class SelectCollectionAdapter extends RecyclerView.Adapter<SelectCollectionAdapter.ViewHolder> {

    private Context ctx;
    private List<AppsCollection> collections = new ArrayList<>();
    private OnItemClickListener listener;

    public SelectCollectionAdapter(Context ctx, List<AppsCollection> collections) {
        this.ctx = ctx;
        this.collections = collections;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.layout_select_collection_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        AppsCollection collection = collections.get(position);

        holder.name.setText(collection.getName());
        holder.createdBy.setText(collection.getCreatedBy().getUsername());
        holder.votesCount.setText(collection.getVotesCount() + "");
        Picasso.with(ctx).load(collection.getPicture()).into(holder.createdByAvatar);
        Picasso.with(ctx).load(collection.getPicture()).into(holder.banner);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }

    public String getCollectionId(int position) {
        return collections.get(position).getId();
    }

    public AppsCollection getCollection(int position) {
        return collections.get(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.card_view)
        View layout;

        @InjectView(R.id.banner)
        ImageView banner;

        @InjectView(R.id.collection_name)
        TextView name;

        @InjectView(R.id.created_by_image)
        Target createdByAvatar;

        @InjectView(R.id.created_by)
        TextView createdBy;

        @InjectView(R.id.votes_count)
        TextView votesCount;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
