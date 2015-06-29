package com.apphunt.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.ui.views.FavouriteCollectionButton;
import com.apphunt.app.ui.views.vote.VoteCollectionButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nmp on 15-6-26.
 */
public class CollectionsAdapter extends BaseAdapter {
    List<AppsCollection> appsCollections;

    public CollectionsAdapter(List<AppsCollection> appsCollections) {
        this.appsCollections = appsCollections;
    }

    @Override
    public int getCount() {
        return appsCollections.size();
    }

    @Override
    public Object getItem(int position) {
        return appsCollections.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        AppsCollection appsCollection = appsCollections.get(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_collection_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(appsCollection.getName());
        viewHolder.createdBy.setText(appsCollection.getCreatedBy().getName());
        viewHolder.voteButton.setCollection(appsCollection);
        viewHolder.favouriteButton.setCollection(appsCollection);

        Picasso.with(parent.getContext())
                .load(appsCollection.getCreatedBy().getProfilePicture())
                .into(viewHolder.createdByImage);



        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.collection_name)
        TextView name;

        @InjectView(R.id.created_by_image)
        Target createdByImage;

        @InjectView(R.id.created_by)
        TextView createdBy;

        @InjectView(R.id.vote_btn)
        VoteCollectionButton voteButton;

        @InjectView(R.id.favourite_collection)
        FavouriteCollectionButton favouriteButton;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
