package com.apphunt.app.ui.adapters.collections;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
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
import butterknife.Optional;

/**
 * Created by nmp on 15-6-26.
 */
public class CollectionsAdapter extends BaseAdapter {
    public static final String TAG = CollectionsAdapter.class.getSimpleName();
    private int layout;

    private List<AppsCollection> appsCollections;

    public CollectionsAdapter(List<AppsCollection> appsCollections) {
        this.appsCollections = appsCollections;
    }

    public CollectionsAdapter(int layout, List<AppsCollection> appsCollections) {
        this.appsCollections = appsCollections;
        this.layout = layout;
    }

    public void updateData(List<AppsCollection> appsCollections) {
        this.appsCollections = appsCollections;
        notifyDataSetChanged();
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

    public void removeCollection(String id) {
        for(int i=0; i < appsCollections.size(); i++) {
            AppsCollection collection = appsCollections.get(i);
            if(collection.getId().equals(id)) {
                appsCollections.remove(i);
                notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final ViewHolder viewHolder;
        final AppsCollection appsCollection = appsCollections.get(position);
        if(convertView == null) {
            int layoutId = layout != 0 ? layout : R.layout.layout_collection_item;
            convertView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.name.setText(appsCollection.getName());
        viewHolder.createdBy.setText(appsCollection.getCreatedBy().getName());
        viewHolder.voteButton.setCollection(appsCollection);
        if(viewHolder.favouriteButton != null) {
            viewHolder.favouriteButton.setCollection(appsCollection);
        }

        Picasso.with(parent.getContext())
                .load(appsCollection.getCreatedBy().getProfilePicture())
                .into(viewHolder.createdByImage);

        Picasso.with(parent.getContext())
                .load(R.drawable.banner)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        BitmapDrawable ob = new BitmapDrawable(parent.getContext().getResources(), bitmap);
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                            viewHolder.banner.setBackground(ob);
                        } else {
                            viewHolder.banner.setBackgroundDrawable(ob);
                        }
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

        return convertView;
    }

    public void addCollection(AppsCollection collection) {
        appsCollections.add(collection);
        notifyDataSetChanged();
    }

    public void addAllCollections(List<AppsCollection> collections) {
        appsCollections.addAll(collections);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        @InjectView(R.id.banner)
        LinearLayout banner;

        @InjectView(R.id.collection_name)
        TextView name;

        @InjectView(R.id.created_by_image)
        Target createdByImage;

        @InjectView(R.id.created_by)
        TextView createdBy;

        @InjectView(R.id.vote_btn)
        VoteCollectionButton voteButton;

        @Optional
        @InjectView(R.id.favourite_collection)
        FavouriteCollectionButton favouriteButton;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
