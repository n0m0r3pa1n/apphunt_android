package com.apphunt.app.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.apps.App;
import com.apphunt.app.api.apphunt.models.apps.BaseApp;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.apps.AppsSearchResultEvent;
import com.apphunt.app.event_bus.events.api.collections.CollectionsSearchResultEvent;
import com.apphunt.app.ui.listview_items.AppItem;
import com.apphunt.app.ui.listview_items.CollectionItem;
import com.apphunt.app.ui.listview_items.Item;
import com.apphunt.app.ui.listview_items.SeparatorItem;
import com.apphunt.app.ui.views.collection.FavouriteCollectionButton;
import com.apphunt.app.ui.views.vote.AppVoteButton;
import com.apphunt.app.ui.views.vote.CollectionVoteButton;
import com.apphunt.app.utils.LoginUtils;
import com.apphunt.app.utils.StringUtils;
import com.apphunt.app.utils.ui.NavUtils;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/8/15.
 * *
 * * NaughtySpirit 2015
 */
public class SearchResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = SearchResultsAdapter.class.getSimpleName();
    private Context ctx;

    private RecyclerView recyclerView;
    private final RelativeLayout noResultsLayout;
    private ArrayList<Item> items = new ArrayList<>();

    private int width;
    private static final int COMPAT_PADDING = 5;

    public SearchResultsAdapter(Context ctx, RecyclerView recyclerView, RelativeLayout noResultsLayout) {
        this.ctx = ctx;
        this.recyclerView = recyclerView;
        this.noResultsLayout = noResultsLayout;

        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        width = size.x;

        BusProvider.getInstance().register(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolderItem = null;

        if (viewType == Constants.ItemType.ITEM.getValue()) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_app_item, parent, false);
            viewHolderItem = new ViewHolderApp(view);
        } else if (viewType == Constants.ItemType.SEPARATOR.getValue()) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_app_list_header, parent, false);
            viewHolderItem = new ViewHolderSeparator(view);
        } else if (viewType == Constants.ItemType.COLLECTION.getValue()) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_collection_item, parent, false);
            viewHolderItem = new ViewHolderCollection(view);
        }

        return viewHolderItem;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == Constants.ItemType.ITEM.getValue()) {
            final ViewHolderApp viewHolderItem = (ViewHolderApp) holder;
            final BaseApp app = ((AppItem) getItem(position)).getData();

            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ctx.getResources().getDimension(R.dimen.list_item_icon_size), ctx.getResources().getDisplayMetrics());

            Picasso.with(ctx).load(app.getIcon()).resize(size, size).into(viewHolderItem.icon);

            viewHolderItem.title.setText(StringUtils.htmlDecodeString(app.getName()));
            if(!app.getCategories().isEmpty()) {
                viewHolderItem.category.setText(app.getCategories().get(0));
            }
            Picasso.with(ctx)
                    .load(app.getCreatedBy().getProfilePicture())
                    .placeholder(R.drawable.placeholder_avatar)
                    .into(viewHolderItem.creatorImageView);
            viewHolderItem.creatorUsername.setText("by " + app.getCreatedBy().getUsername());
            viewHolderItem.vote.setBaseApp((App) app);

            viewHolderItem.addToCollection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (LoginProviderFactory.get((Activity) ctx).isUserLoggedIn()) {
                        NavUtils.getInstance((AppCompatActivity) ctx).presentSelectCollectionFragment((App) app);
                    } else {
                        LoginUtils.showLoginFragment(ctx, false, R.string.login_info_add_to_collection);
                    }
                }
            });

            View.OnClickListener detailsClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        NavUtils.getInstance((AppCompatActivity) ctx).presentAppDetailsFragment(app);
                    } catch (Exception e) {
                        Log.e(TAG, "Couldn't get the shortUrl");
                        e.printStackTrace();
                    }
                }
            };

            viewHolderItem.layout.setOnClickListener(null);
            viewHolderItem.layout.setOnClickListener(detailsClickListener);
        } else if (getItemViewType(position) == Constants.ItemType.COLLECTION.getValue()) {
            final ViewHolderCollection viewHolderCollection = (ViewHolderCollection) holder;
            final AppsCollection appsCollection = ((CollectionItem) getItem(position)).getData();


            viewHolderCollection.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavUtils.getInstance((AppCompatActivity) ctx)
                            .presentViewCollectionFragment(appsCollection);
                }
            });
            viewHolderCollection.name.setText(appsCollection.getName());
            viewHolderCollection.createdBy.setText(appsCollection.getCreatedBy().getName());
            viewHolderCollection.voteButton.setCollection(appsCollection);
            viewHolderCollection.favouriteButton.setCollection(appsCollection);
            if(appsCollection.isOwnedByCurrentUser((Activity) ctx)) {
                viewHolderCollection.favouriteButton.setVisibility(View.GONE);
            }

            String tags = "";
            for (int i = 0; i < appsCollection.getTags().size(); i++) {
                if (i > 0 && i < appsCollection.getTags().size()) {
                    tags += ", ";
                }

                tags += appsCollection.getTags().get(i);
            }
            viewHolderCollection.tags.setText(String.format(ctx.getString(R.string.tags), (!TextUtils.isEmpty(tags) ? tags : "none")));

            final Resources resources = ctx.getResources();
            Picasso.with(ctx)
                    .load(appsCollection.getCreatedBy().getProfilePicture())
                    .placeholder(R.drawable.placeholder_avatar)
                    .resize(resources.getDimensionPixelSize(R.dimen.collection_creator_image_size),
                            resources.getDimensionPixelSize(R.dimen.collection_creator_image_size))
                    .into(viewHolderCollection.createdByImage);

            Picasso.with(ctx)
                    .load(appsCollection.getPicture())
                    .resize(width - COMPAT_PADDING, resources.getDimensionPixelSize(R.dimen.collection_banner_height))
                    .into(viewHolderCollection.banner);
        } else if (getItemViewType(position) == Constants.ItemType.SEPARATOR.getValue()) {
            ViewHolderSeparator viewHolderSeparator = (ViewHolderSeparator) holder;
            viewHolderSeparator.header.setText(((SeparatorItem) getItem(position)).getData());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType().ordinal();
    }

    public Object getItem(int position) {
        return items.get(position);
    }

    @Subscribe
    public void onAppsSearchResultsEvent(AppsSearchResultEvent event) {
        if (event.getResult().getTotalCount() > 0) {
            items.add(new SeparatorItem("Applications"));
            for (BaseApp app : event.getResult().getResults()) {
                items.add(new AppItem(app));
            }
        }
    }

    @Subscribe
    public void onCollectionsSearchResultsEvent(CollectionsSearchResultEvent event) {
        if (event.getResult().getTotalCount() > 0) {
            items.add(new SeparatorItem("Collections"));
            for (AppsCollection collection : event.getResult().getResults()) {
                items.add(new CollectionItem(collection));
            }
        }

    }

    static class ViewHolderApp extends RecyclerView.ViewHolder {
        @InjectView(R.id.item)
        LinearLayout layout;

        @InjectView(R.id.app_icon)
        ImageView icon;

        @InjectView(R.id.app_name)
        TextView title;

        @InjectView(R.id.category)
        TextView category;

        @InjectView(R.id.creator_avatar)
        CircleImageView creatorImageView;

        @InjectView(R.id.creator_name)
        TextView creatorUsername;

        @InjectView(R.id.btn_vote)
        AppVoteButton vote;

        @InjectView(R.id.add_to_collection)
        Button addToCollection;

        public ViewHolderApp(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    static class ViewHolderCollection extends RecyclerView.ViewHolder {
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

        @InjectView(R.id.tags)
        TextView tags;

        @InjectView(R.id.vote_btn)
        CollectionVoteButton voteButton;

        @InjectView(R.id.favourite_collection)
        FavouriteCollectionButton favouriteButton;

        public ViewHolderCollection(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    static class ViewHolderSeparator extends RecyclerView.ViewHolder {
        @InjectView(R.id.header)
        TextView header;

        public ViewHolderSeparator(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
