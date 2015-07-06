package com.apphunt.app.ui.adapters.collections;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.ui.views.FavouriteCollectionButton;
import com.apphunt.app.ui.views.vote.VoteCollectionButton;
import com.apphunt.app.utils.ui.NavUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by nmp on 15-6-26.
 */
public class CollectionsAdapter extends BaseAdapter {
    public static final String TAG = CollectionsAdapter.class.getSimpleName();
    private List<AppsCollection> appsCollections;

    public CollectionsAdapter(List<AppsCollection> appsCollections) {
        this.appsCollections = appsCollections;
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_collection_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.getInstance((AppCompatActivity) parent.getContext())
                        .presentCollectionDetailsFragment(appsCollection);
            }
        });
        viewHolder.name.setText(appsCollection.getName());
        viewHolder.createdBy.setText(appsCollection.getCreatedBy().getName());
        viewHolder.voteButton.setCollection(appsCollection);
        viewHolder.favouriteButton.setCollection(appsCollection);

        Picasso.with(parent.getContext())
                .load(appsCollection.getCreatedBy().getProfilePicture())
                .into(viewHolder.createdByImage);

        Picasso.with(parent.getContext())
                .load(appsCollection.getPicture())
                .into(viewHolder.banner);

//        Picasso.with(parent.getContext())
//                .load(appsCollection.getPicture())
//                .into(new Target() {
//                    @Override
//                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                            viewHolder.banner.setBackground(new BitmapDrawable(parent.getResources(), bitmap));
//                        } else {
//                            viewHolder.banner.setBackgroundDrawable(new BitmapDrawable(parent.getResources(), bitmap));
//                        }
//                    }
//
//                    @Override
//                    public void onBitmapFailed(Drawable errorDrawable) {
//
//                    }
//
//                    @Override
//                    public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                    }
//                });

        return convertView;
    }

    @OnClick(R.id.card_view)
    public void openCollectionDetails() {

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
        @InjectView(R.id.banner_background)
        ImageView banner;

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
