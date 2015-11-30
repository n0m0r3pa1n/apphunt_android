package com.apphunt.app.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apphunt.app.MainActivity;
import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.ads.Ad;
import com.apphunt.app.api.apphunt.models.apps.App;
import com.apphunt.app.api.apphunt.models.apps.AppsList;
import com.apphunt.app.api.apphunt.models.apps.BaseApp;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.ui.fragments.TrendingAppsFragment;
import com.apphunt.app.ui.listview_items.AppItem;
import com.apphunt.app.ui.listview_items.Item;
import com.apphunt.app.ui.listview_items.SeparatorItem;
import com.apphunt.app.ui.views.AdView;
import com.apphunt.app.ui.views.CreatorView;
import com.apphunt.app.ui.views.vote.AppVoteButton;
import com.apphunt.app.utils.FlurryWrapper;
import com.apphunt.app.utils.LoginUtils;
import com.apphunt.app.utils.StringUtils;
import com.apphunt.app.utils.ui.NavUtils;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TrendingAppsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = TrendingAppsAdapter.class.getName();

    private Context ctx;
    private ArrayList<Item> items = new ArrayList<>();
    private ArrayList<Item> backup = new ArrayList<>();

    private Calendar today = Calendar.getInstance();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d", Locale.ENGLISH);

    private int allAppsSize = 0;
    private int previousAppsSize = 0;
    private String lastLoadedDate = "";

    private RecyclerView recyclerView;

    public TrendingAppsAdapter(Context ctx, RecyclerView recyclerView) {
        this.ctx = ctx;
        this.recyclerView = recyclerView;
    }

    public void addItem(int position, Item item) {
        items.add(position, item);
    }

    public void notifyAdapter(AppsList appsList) {

        if(appsList.getDate().equals(lastLoadedDate)) {
            displayMoreApps(appsList);
        } else {
            displayAppsForDay(appsList);
        }
        lastLoadedDate = appsList.getDate();
    }

    private void displayAppsForDay(AppsList appsList) {
        if(appsList == null) {
            return;
        }

        Calendar yesterday = Calendar.getInstance();
        yesterday.setTime(today.getTime());
        yesterday.add(Calendar.DATE, -1);
        if (dateFormat.format(today.getTime()).equals(appsList.getDate())) {
            items.add(new SeparatorItem(ctx.getString(R.string.list_view_header_today)));
        } else if (dateFormat.format(yesterday.getTime()).equals(appsList.getDate())) {
            items.add(new SeparatorItem(ctx.getString(R.string.list_view_header_yesterday)));
        } else {
            items.add(new SeparatorItem(appsList.getDate()));
        }

        for (App app : appsList.getApps()) {
            items.add(new AppItem(app));
        }

        notifyDataSetChanged();
        ((MainActivity) ctx).findViewById(R.id.reload).setVisibility(View.GONE);
    }

    private void displayMoreApps(AppsList appsList) {
        ArrayList<AppItem> newItems = new ArrayList<>();

        for (App app : appsList.getApps()) {
            newItems.add(new AppItem(app));
        }

        items.addAll(newItems);

        notifyDataSetChanged();
    }

    public void showSearchResult(List<App> apps) {
        backup.addAll(items);
        items.clear();

        for (App app : apps) {
            items.add(new AppItem(app));
        }

        notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(0);
        if (apps.isEmpty()) {
            FlurryWrapper.logEvent(TrackingEvents.UserFoundNoResults);
            Toast.makeText(ctx, R.string.no_results_found, Toast.LENGTH_LONG).show();
        }
    }

    public void clearSearch() {
        if (backup.size() > 0) {
            items.clear();
            items.addAll(backup);
            backup.clear();

            notifyDataSetChanged();
        }
    }

    public void resetAdapter() {
        allAppsSize = 0;
        previousAppsSize = 0;
        items.clear();
        notifyDataSetChanged();

        today = Calendar.getInstance();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolderItem = null;
        if (viewType == Constants.ItemType.ITEM.getValue()) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_app_item, parent, false);
            viewHolderItem = new ViewHolderItem(view);
        } else if (viewType == Constants.ItemType.SEPARATOR.getValue()) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_app_list_header, parent, false);
            viewHolderItem = new ViewHolderSeparator(view);
        } else if(viewType == Constants.ItemType.AD.getValue()) {
            view = new AdView(ctx);
            viewHolderItem = new ViewHolderAd(view);
        }

        return viewHolderItem;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) == Constants.ItemType.ITEM.getValue()) {
            final ViewHolderItem viewHolderItem = (ViewHolderItem) holder;
            final BaseApp app = ((AppItem) getItem(position)).getData();

            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ctx.getResources().getDimension(R.dimen.list_item_icon_size), ctx.getResources().getDisplayMetrics());

            Picasso.with(ctx).load(app.getIcon()).resize(size, size).into(viewHolderItem.icon);

            viewHolderItem.title.setText(StringUtils.htmlDecodeString(app.getName()));
            if(!app.getCategories().isEmpty()) {
                viewHolderItem.category.setText(app.getCategories().get(0));
            }

            User createdBy = app.getCreatedBy();
            viewHolderItem.creatorView.setUserWithText(createdBy.getId(), createdBy.getProfilePicture(), "by", createdBy.getName());

            viewHolderItem.vote.setBaseApp((App) app);
            viewHolderItem.vote.setTrackingScreen(TrendingAppsFragment.TAG);

            viewHolderItem.addToCollection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (LoginProviderFactory.get((Activity) ctx).isUserLoggedIn()) {
                        NavUtils.getInstance((AppCompatActivity) ctx).presentSelectCollectionFragment((App) app);
                    } else {
                        LoginUtils.showLoginFragment(false, R.string.login_info_add_to_collection);
                    }
                }
            });

            View.OnClickListener detailsClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        NavUtils.getInstance((AppCompatActivity) ctx).presentAppDetailsFragment(app.getId());
                    } catch (Exception e) {
                        Log.e(TAG, "Couldn't get the shortUrl");
                        e.printStackTrace();
                    }
                }
            };

            viewHolderItem.layout.setOnClickListener(null);
            viewHolderItem.layout.setOnClickListener(detailsClickListener);

        } else if (getItemViewType(position) == Constants.ItemType.SEPARATOR.getValue()) {
            ViewHolderSeparator viewHolderSeparator = (ViewHolderSeparator) holder;
            viewHolderSeparator.header.setText(((SeparatorItem) getItem(position)).getData());
        }
    }

    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType().ordinal();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //region ViewHolders
    static class ViewHolderItem extends RecyclerView.ViewHolder{
        @InjectView(R.id.item)
        LinearLayout layout;

        @InjectView(R.id.app_icon)
        ImageView icon;

        @InjectView(R.id.app_name)
        TextView title;

        @InjectView(R.id.category)
        TextView category;


        @InjectView(R.id.creator_container)
        CreatorView creatorView;

        @InjectView(R.id.btn_vote)
        AppVoteButton vote;

        @InjectView(R.id.add_to_collection)
        Button addToCollection;

        public ViewHolderItem(View view) {
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

    static class ViewHolderAd extends RecyclerView.ViewHolder{
        public ViewHolderAd(View view) {
            super(view);
        }
    }
}