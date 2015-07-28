package com.apphunt.app.ui.adapters.collections;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.ui.views.collection.FavouriteCollectionButton;
import com.apphunt.app.ui.views.vote.CollectionVoteButton;
import com.apphunt.app.utils.ui.NavUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by nmp on 15-6-26.
 */
public class CollectionsAdapter extends BaseAdapter {
    public static final String TAG = CollectionsAdapter.class.getSimpleName();
    private List<AppsCollection> appsCollections;
    private Context context;

    public CollectionsAdapter(Context context,  List<AppsCollection> appsCollections) {
        this.appsCollections = appsCollections;
        this.context = context;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_collection_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.getInstance((AppCompatActivity) parent.getContext())
                        .presentViewCollectionFragment(appsCollection);
            }
        });
        viewHolder.name.setText(appsCollection.getName());
        viewHolder.createdBy.setText(appsCollection.getCreatedBy().getName());
        viewHolder.voteButton.setCollection(appsCollection);
        viewHolder.favouriteButton.setCollection(appsCollection);
        if(appsCollection.isOwnedByCurrentUser((Activity) parent.getContext())) {
            viewHolder.favouriteButton.setVisibility(View.GONE);
        }

        final Resources resources = context.getResources();
        Picasso.with(context)
                .load(appsCollection.getCreatedBy().getProfilePicture())
                .placeholder(R.drawable.placeholder_avatar)
                .resize(resources.getDimensionPixelSize(R.dimen.collection_creator_image_size),
                        resources.getDimensionPixelSize(R.dimen.collection_creator_image_size))
                .into(viewHolder.createdByImage);

        final ViewTreeObserver viewTree = viewHolder.banner.getViewTreeObserver();
        viewTree.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    viewTree.removeGlobalOnLayoutListener(this);
                } else {
                    viewTree.removeOnGlobalLayoutListener(this);
                }

                Picasso.with(context)
                        .load(appsCollection.getPicture())
                        .resize(viewHolder.banner.getWidth(), resources.getDimensionPixelSize(R.dimen.collection_banner_height))
                        .into(viewHolder.banner);
            }
        });

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
        @InjectView(R.id.card_view)
        CardView cardView;

        @InjectView(R.id.banner_background)
        ImageView banner;

        @InjectView(R.id.collection_name)
        TextView name;

        @InjectView(R.id.created_by_image)
        CircleImageView createdByImage;

        @InjectView(R.id.created_by)
        TextView createdBy;

        @InjectView(R.id.vote_btn)
        CollectionVoteButton voteButton;

        @InjectView(R.id.favourite_collection)
        FavouriteCollectionButton favouriteButton;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
