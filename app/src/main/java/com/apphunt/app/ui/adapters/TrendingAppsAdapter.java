package com.apphunt.app.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.AppHuntApiClient;
import com.apphunt.app.api.Callback;
import com.apphunt.app.api.models.App;
import com.apphunt.app.api.models.AppsList;
import com.apphunt.app.api.models.Vote;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.smart_rate.SmartRate;
import com.apphunt.app.ui.listview_items.AppItem;
import com.apphunt.app.ui.listview_items.Item;
import com.apphunt.app.ui.listview_items.MoreAppsItem;
import com.apphunt.app.ui.listview_items.SeparatorItem;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.FacebookUtils;
import com.apphunt.app.utils.LoadersUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.TrackingEvents;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import it.appspice.android.AppSpice;
import retrofit.client.Response;

public class TrendingAppsAdapter extends BaseAdapter {

    private static final String TAG = TrendingAppsAdapter.class.getName();

    private Context ctx;
    private ListView listView;
    private ArrayList<Item> items = new ArrayList<>();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
    private Calendar calendar = Calendar.getInstance();
    private Calendar today = Calendar.getInstance();

    private boolean success = false;
    private int noAppsDays = 0;

    private ViewHolderItem viewHolderItem = null;
    private ViewHolderSeparator viewHolderSeparator = null;
    private ViewHolderMoreApps viewHolderMoreApps = null;

    public TrendingAppsAdapter(Context ctx, ListView listView) {
        this.ctx = ctx;
        this.listView = listView;

        LoadersUtils.showCenterLoader((Activity) ctx);

        getApps();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (getItemViewType(position) == Constants.ItemType.ITEM.getValue()) {
                viewHolderItem = new ViewHolderItem();

                view = inflater.inflate(R.layout.layout_app_item, parent, false);
                viewHolderItem.layout = (RelativeLayout) view.findViewById(R.id.item);
                viewHolderItem.icon = (ImageView) view.findViewById(R.id.app_icon);
                viewHolderItem.title = (TextView) view.findViewById(R.id.app_name);
                viewHolderItem.description = (TextView) view.findViewById(R.id.description);
                viewHolderItem.vote = (Button) view.findViewById(R.id.vote);

                view.setTag(viewHolderItem);
            } else if (getItemViewType(position) == Constants.ItemType.SEPARATOR.getValue()) {
                viewHolderSeparator = new ViewHolderSeparator();

                view = inflater.inflate(R.layout.layout_app_list_header, parent, false);
                viewHolderSeparator.header = (TextView) view.findViewById(R.id.header);

                view.setTag(viewHolderSeparator);
            } else if (getItemViewType(position) == Constants.ItemType.MORE_APPS.getValue()) {
                viewHolderMoreApps = new ViewHolderMoreApps();

                view = inflater.inflate(R.layout.layout_more_apps, parent, false);
                viewHolderMoreApps.moreApps = (ImageButton) view.findViewById(R.id.more_apps);

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

            viewHolderItem.title.setText(app.getName());
            viewHolderItem.description.setText(app.getDescription());
            viewHolderItem.vote.setText(app.getVotesCount());

            viewHolderItem.vote.setOnClickListener(null);
            viewHolderItem.vote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (LoginProviderFactory.get((Activity) ctx).isUserLoggedIn()) {
                        if (app.isHasVoted()) {
                            AppHuntApiClient.getClient().downVote(app.getId(), SharedPreferencesHelper.getStringPreference(ctx, Constants.KEY_USER_ID), new Callback<Vote>() {
                                @Override
                                public void success(Vote vote, Response response) {
                                    AppSpice.createEvent(TrackingEvents.UserDownVoted).track();
                                    app.setVotesCount(vote.getVotes());
                                    app.setHasVoted(false);
                                    ((Button) v).setText(vote.getVotes());
                                    v.setBackgroundResource(R.drawable.btn_vote);
                                }
                            });
                        } else {
                            AppHuntApiClient.getClient().vote(app.getId(), SharedPreferencesHelper.getStringPreference(ctx, Constants.KEY_USER_ID), new Callback<Vote>() {
                                @Override
                                public void success(Vote vote, Response response) {
                                    AppSpice.createEvent(TrackingEvents.UserVoted).track();
                                    app.setVotesCount(vote.getVotes());
                                    app.setHasVoted(true);
                                    ((Button) v).setText(vote.getVotes());
                                    v.setBackgroundResource(R.drawable.btn_down_vote);
                                    SmartRate.show(Constants.SMART_RATE_LOCATION_APP_VOTED);
                                }
                            });
                        }

                    } else {
                        FacebookUtils.showLoginFragment(ctx);
                    }

                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                }
            });
            viewHolderItem.vote.setBackgroundResource((app.isHasVoted() ? R.drawable.btn_down_vote : R.drawable.btn_vote));

            viewHolderItem.layout.setOnClickListener(null);
            viewHolderItem.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        AppSpice.createEvent(TrackingEvents.UserOpenedAppFromList).track();
                        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(((AppItem) getItem(position)).getData().getShortUrl()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ctx.startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Couldn't get the shortUrl");
                    }

                }
            });
        } else if (getItemViewType(position) == Constants.ItemType.SEPARATOR.getValue() && viewHolderSeparator != null) {
            viewHolderSeparator.header.setText(((SeparatorItem) getItem(position)).getData());
        } else if (getItemViewType(position) == Constants.ItemType.MORE_APPS.getValue() && viewHolderMoreApps != null) {
            viewHolderMoreApps.moreApps.setOnClickListener(null);
            viewHolderMoreApps.moreApps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppSpice.createEvent(TrackingEvents.UserRequestedMoreApps).track();
                    loadMoreApps(position);
                }
            });
        }

        return view;
    }

    private void getApps() {
        String userId = SharedPreferencesHelper.getStringPreference(ctx, Constants.KEY_USER_ID);
        AppHuntApiClient.getClient().getApps(userId, dateFormat.format(calendar.getTime()), 1, 5, Constants.PLATFORM, new Callback<AppsList>() {
            @Override
            public void success(AppsList appsList, Response response) {
                calendar.add(Calendar.DATE, -1);
                if (appsList.getTotalCount() > 0) {
                    notifyAdapter(appsList);
                    if (!success) {
                        getApps();
                        success = true;
                    }
                } else {
                    getApps();
                }
            }
        });
    }

    private void notifyAdapter(AppsList appsList) {
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
    }

    public void getAppsForNextDate() {
        LoadersUtils.showBottomLoader((Activity) ctx);

        calendar.add(Calendar.DATE, -1);

        String date = dateFormat.format(calendar.getTime());

        AppHuntApiClient.getClient().getApps(SharedPreferencesHelper.getStringPreference(ctx, Constants.KEY_USER_ID),
                date, 1, 5, Constants.PLATFORM, new Callback<AppsList>() {
                    @Override
                    public void success(AppsList appsList, Response response) {
                        ArrayList<Item> items = new ArrayList<>();

                        if (appsList.getTotalCount() > 0) {
                            items.add(new SeparatorItem(appsList.getDate()));

                            for (App app : appsList.getApps()) {
                                items.add(new AppItem(app));
                            }

                            if (appsList.haveMoreApps())
                                items.add(new MoreAppsItem(1, 5, appsList.getDate()));

                            addItems(items);
                        } else if (noAppsDays < 5) {
                            getAppsForNextDate();
                            ++noAppsDays;
                        } else {
                            LoadersUtils.hideBottomLoader((Activity) ctx);
                            noAppsDays = 0;
                        }
                    }
                });
    }

    public void addItems(ArrayList<Item> items) {
        this.items.addAll(items);
        notifyDataSetChanged();

        LoadersUtils.hideBottomLoader((Activity) ctx);
        listView.smoothScrollToPosition(this.items.size() - 3);
    }

    private void loadMoreApps(final int position) {
        final MoreAppsItem item = (MoreAppsItem) getItem(position);
        AppHuntApiClient.getClient().getApps(SharedPreferencesHelper.getStringPreference(ctx, Constants.KEY_USER_ID),
                item.getDate(), item.getNextPage(), item.getItems(), Constants.PLATFORM, new Callback<AppsList>() {
                    @Override
                    public void success(AppsList appsList, Response response) {
                        if (!appsList.haveMoreApps()) {
                            items.remove(position);
                        } else {
                            item.setPage(item.getNextPage());
                        }

                        ArrayList<AppItem> newItems = new ArrayList<>();

                        for (App app : appsList.getApps()) {
                            newItems.add(new AppItem(app));
                        }

                        items.addAll(position, newItems);

                        notifyDataSetChanged();
                        listView.smoothScrollToPosition(position + newItems.size());
                    }
                });
    }

    public void resetAdapter() {
        success = false;
        items.clear();
        notifyDataSetChanged();

        calendar = Calendar.getInstance();
        today = Calendar.getInstance();

        getApps();
    }

    public void clearAdapter() {
        success = false;
        items.clear();
        notifyDataSetChanged();

        calendar = Calendar.getInstance();
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

    private static class ViewHolderItem {
        RelativeLayout layout;
        ImageView icon;
        TextView title;
        TextView description;
        Button vote;
    }

    private static class ViewHolderSeparator {
        TextView header;
    }

    private static class ViewHolderMoreApps {
        ImageButton moreApps;
    }
}