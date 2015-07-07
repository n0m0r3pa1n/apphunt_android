package com.apphunt.app.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apphunt.app.MainActivity;
import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.client.ApiService;
import com.apphunt.app.api.apphunt.models.apps.App;
import com.apphunt.app.api.apphunt.models.apps.AppsList;
import com.apphunt.app.ui.fragments.AppDetailsFragment;
import com.apphunt.app.ui.listview_items.AppItem;
import com.apphunt.app.ui.listview_items.Item;
import com.apphunt.app.ui.listview_items.MoreAppsItem;
import com.apphunt.app.ui.listview_items.SeparatorItem;
import com.apphunt.app.ui.views.vote.AppVoteButton;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.StringUtils;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.utils.ui.LoadersUtils;
import com.apphunt.app.utils.ui.NavUtils;
import com.flurry.android.FlurryAgent;
import com.quentindommerc.superlistview.SuperListview;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TrendingAppsAdapter extends BaseAdapter {

    private static final String TAG = TrendingAppsAdapter.class.getName();

    private Context ctx;
    private SuperListview listView;
    private MoreAppsItem moreAppsItem;
    private ArrayList<Item> items = new ArrayList<>();
    private ArrayList<Item> backup = new ArrayList<>();

    private Calendar today = Calendar.getInstance();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d", Locale.ENGLISH);

    private boolean isMoreItemsPressed = false;

    private int allAppsSize = 0;
    private int previousAppsSize = 0;
    private int moreAppsItemPosition = 0;
    private int selectedAppPosition = -1;
    private ViewHolderSeparator viewHolderSeparator = null;
    private ViewHolderMoreApps viewHolderMoreApps = null;

    public TrendingAppsAdapter(Context ctx, SuperListview listView) {
        this.ctx = ctx;
        this.listView = listView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolderItem viewHolderItem = null;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (getItemViewType(position) == Constants.ItemType.ITEM.getValue()) {
                view = inflater.inflate(R.layout.layout_app_item, parent, false);
                viewHolderItem = new ViewHolderItem(view);
                view.setTag(viewHolderItem);
            } else if (getItemViewType(position) == Constants.ItemType.SEPARATOR.getValue()) {
                view = inflater.inflate(R.layout.layout_app_list_header, parent, false);
                viewHolderSeparator = new ViewHolderSeparator(view);
                view.setTag(viewHolderSeparator);
            } else if (getItemViewType(position) == Constants.ItemType.MORE_APPS.getValue()) {
                view = inflater.inflate(R.layout.layout_more_apps, parent, false);
                viewHolderMoreApps = new ViewHolderMoreApps(view);
                view.setTag(viewHolderMoreApps);
            }
        } else {
            if (getItemViewType(position) == Constants.ItemType.ITEM.getValue()) {
                viewHolderItem = (ViewHolderItem) view.getTag();
            } else if (getItemViewType(position) == Constants.ItemType.SEPARATOR.getValue()) {
                viewHolderSeparator = (ViewHolderSeparator) view.getTag();
            } else if (getItemViewType(position) == Constants.ItemType.MORE_APPS.getValue()) {
                viewHolderMoreApps = (ViewHolderMoreApps) view.getTag();
            }
        }

        if (getItemViewType(position) == Constants.ItemType.ITEM.getValue() && viewHolderItem != null) {
            final App app = ((AppItem) getItem(position)).getData();

            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ctx.getResources().getDimension(R.dimen.list_item_icon_size), ctx.getResources().getDisplayMetrics());

            Picasso.with(ctx).load(app.getIcon()).resize(size, size).into(viewHolderItem.icon);

            viewHolderItem.title.setText(StringUtils.htmlDecodeString(app.getName()));
            viewHolderItem.category.setText(app.getCategory());
            Picasso.with(ctx)
                    .load(app.getCreatedBy().getProfilePicture())
                    .into(viewHolderItem.creatorImageView);
            viewHolderItem.creatorUsername.setText("by " + app.getCreatedBy().getUsername());
            viewHolderItem.vote.setBaseApp(app);

            viewHolderItem.addToCollection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavUtils.getInstance((AppCompatActivity) ctx).presentSelectCollectionFragment(app);
                }
            });

            View.OnClickListener detailsClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        App app = ((AppItem) getItem(position)).getData();
                        NavUtils.getInstance((AppCompatActivity) ctx).presentAppDetailsFragment(app);
                    } catch (Exception e) {
                        Log.e(TAG, "Couldn't get the shortUrl");
                    }
                }
            };

            viewHolderItem.layout.setOnClickListener(null);
            viewHolderItem.layout.setOnClickListener(detailsClickListener);

        } else if (getItemViewType(position) == Constants.ItemType.SEPARATOR.getValue() && viewHolderSeparator != null) {
            viewHolderSeparator.header.setText(((SeparatorItem) getItem(position)).getData());
        } else if (getItemViewType(position) == Constants.ItemType.MORE_APPS.getValue() && viewHolderMoreApps != null) {
            viewHolderMoreApps.moreApps.setOnClickListener(null);
            viewHolderMoreApps.moreApps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlurryAgent.logEvent(TrackingEvents.UserRequestedMoreApps);
                    loadMoreApps(position);
                }
            });
        }

        return view;
    }

    public void notifyAdapter(AppsList appsList) {
        if(isMoreItemsPressed) {
            displayMoreApps(appsList);
            return;
        }

        displayAppsForPreviousDay(appsList);
    }

    private void displayAppsForPreviousDay(AppsList appsList) {
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

        if (appsList.haveMoreApps())
            items.add(new MoreAppsItem(1, 5, appsList.getDate()));

        notifyDataSetChanged();
        LoadersUtils.hideCenterLoader((Activity) ctx);
        ((MainActivity) ctx).findViewById(R.id.reload).setVisibility(View.GONE);

        if (selectedAppPosition > -1) {
            listView.getList().smoothScrollToPosition(selectedAppPosition);
        } else {
            scrollToFirstAppForNextDay();
        }
    }

    private void scrollToFirstAppForNextDay() {
        if(allAppsSize != 0) {
            previousAppsSize = allAppsSize + 1;
        }
        allAppsSize = items.size();
        listView.getList().smoothScrollToPosition(previousAppsSize);
    }

    private void displayMoreApps(AppsList appsList) {
        if (!appsList.haveMoreApps()) {
            items.remove(moreAppsItem);
        } else {
            moreAppsItem.setPage(moreAppsItem.getNextPage());
        }

        ArrayList<AppItem> newItems = new ArrayList<>();

        for (App app : appsList.getApps()) {
            newItems.add(new AppItem(app));
        }

        items.addAll(moreAppsItemPosition, newItems);

        notifyDataSetChanged();
        listView.getList().smoothScrollToPosition(moreAppsItemPosition + newItems.size());
        isMoreItemsPressed = false;
    }

    private void loadMoreApps(int position) {
        moreAppsItem = (MoreAppsItem) getItem(position);
        moreAppsItemPosition = position;
        isMoreItemsPressed = true;
        ApiService.getInstance(ctx).loadMoreApps(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID),
                moreAppsItem.getDate(), Constants.PLATFORM, moreAppsItem.getNextPage(), moreAppsItem.getItems());
    }

    public void showSearchResult(ArrayList<App> apps) {
        backup.addAll(items);
        items.clear();

        for (App app : apps) {
            items.add(new AppItem(app));
        }

        notifyDataSetChanged();
        listView.getList().smoothScrollToPosition(0);
        if (apps.isEmpty()) {
            FlurryAgent.logEvent(TrackingEvents.UserFoundNoResults);
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
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType().getValue();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //region ViewHolders
    static class ViewHolderItem {
        @InjectView(R.id.item)
        LinearLayout layout;

        @InjectView(R.id.app_icon)
        ImageView icon;

        @InjectView(R.id.app_name)
        TextView title;

        @InjectView(R.id.category)
        TextView category;

        @InjectView(R.id.creator_avatar)
        Target creatorImageView;

        @InjectView(R.id.creator_name)
        TextView creatorUsername;

        @InjectView(R.id.btn_vote)
        AppVoteButton vote;

        @InjectView(R.id.add_to_collection)
        ImageButton addToCollection;

        public ViewHolderItem(View view) {
            ButterKnife.inject(this, view);
        }
    }

    static class ViewHolderSeparator {
        @InjectView(R.id.header)
        TextView header;

        public ViewHolderSeparator(View view) {
            ButterKnife.inject(this, view);
        }
    }

    static class ViewHolderMoreApps {
        @InjectView(R.id.more_apps)
        ImageButton moreApps;

        public ViewHolderMoreApps(View view) {
            ButterKnife.inject(this, view);
        }
    }
    //endregion
}