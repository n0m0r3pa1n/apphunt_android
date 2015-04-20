package com.apphunt.app.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apphunt.app.MainActivity;
import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.AppHuntApiClient;
import com.apphunt.app.api.apphunt.Callback;
import com.apphunt.app.api.apphunt.models.App;
import com.apphunt.app.api.apphunt.models.AppsList;
import com.apphunt.app.api.apphunt.models.Vote;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.smart_rate.SmartRate;
import com.apphunt.app.ui.fragments.AppDetailsFragment;
import com.apphunt.app.ui.listview_items.AppItem;
import com.apphunt.app.ui.listview_items.Item;
import com.apphunt.app.ui.listview_items.MoreAppsItem;
import com.apphunt.app.ui.listview_items.SeparatorItem;
import com.apphunt.app.ui.widgets.AvatarImageView;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.FacebookUtils;
import com.apphunt.app.utils.LoadersUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.TrackingEvents;
import com.flurry.android.FlurryAgent;
import com.quentindommerc.superlistview.SuperListview;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit.client.Response;

public class TrendingAppsAdapter extends BaseAdapter {

    private static final String TAG = TrendingAppsAdapter.class.getName();

    private Context ctx;
    private SuperListview listView;
    private ArrayList<Item> items = new ArrayList<>();
    private ArrayList<Item> backup = new ArrayList<>();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
    private Calendar calendar = Calendar.getInstance();
    private Calendar today = Calendar.getInstance();

    private boolean success = false;
    private int noAppsDays = 0;

    private ViewHolderItem viewHolderItem = null;
    private ViewHolderSeparator viewHolderSeparator = null;
    private ViewHolderMoreApps viewHolderMoreApps = null;
    private int selectedAppPosition = -1;

    public TrendingAppsAdapter(Context ctx, SuperListview listView) {
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
                viewHolderItem.commentsCount = (TextView) view.findViewById(R.id.comments_count);
                viewHolderItem.creatorImageView = (AvatarImageView) view.findViewById(R.id.creator_avatar);
                viewHolderItem.creatorUsername = (TextView) view.findViewById(R.id.creator_name);
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
            viewHolderItem.commentsCount.setText(String.valueOf(app.getCommentsCount()));
            Picasso.with(ctx)
                    .load(app.getCreatedBy().getProfilePicture())
                    .into(viewHolderItem.creatorImageView);
            viewHolderItem.creatorUsername.setText("by " + app.getCreatedBy().getUsername());

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
                                    Map<String, String> params = new HashMap<>();
                                    params.put("appId", app.getId());
                                    FlurryAgent.logEvent(TrackingEvents.UserDownVotedApp, params);
                                    app.setVotesCount(vote.getVotes());
                                    app.setHasVoted(false);
                                    ((Button) v).setText(vote.getVotes());
                                    ((Button) v).setTextColor(ctx.getResources().getColor(R.color.vote_btn_text));
                                    v.setBackgroundResource(R.drawable.btn_vote);
                                }
                            });
                        } else {
                            AppHuntApiClient.getClient().vote(app.getId(), SharedPreferencesHelper.getStringPreference(ctx, Constants.KEY_USER_ID), new Callback<Vote>() {
                                @Override
                                public void success(Vote vote, Response response) {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("appId", app.getId());
                                    FlurryAgent.logEvent(TrackingEvents.UserVotedApp, params);
                                    app.setVotesCount(vote.getVotes());
                                    app.setHasVoted(true);
                                    ((Button) v).setText(vote.getVotes());
                                    v.setBackgroundResource(R.drawable.btn_voted);
                                    ((Button) v).setTextColor(ctx.getResources().getColor(R.color.vote_btn_voted_text));
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
            if (app.isHasVoted()) {
                viewHolderItem.vote.setBackgroundResource(R.drawable.btn_voted);
                viewHolderItem.vote.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                viewHolderItem.vote.setBackgroundResource(R.drawable.btn_vote);
                viewHolderItem.vote.setTextColor(Color.parseColor("#2f90de"));
            }

            viewHolderItem.layout.setOnClickListener(null);
            viewHolderItem.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        FlurryAgent.logEvent(TrackingEvents.UserOpenedAppFromList);

                        App app = ((AppItem) getItem(position)).getData();

                        AppDetailsFragment detailsFragment = new AppDetailsFragment();

                        Bundle extras = new Bundle();
                        extras.putString(Constants.KEY_APP_ID, app.getId());
                        extras.putString(Constants.KEY_APP_NAME, app.getName());
                        extras.putInt(Constants.KEY_ITEM_POSITION, position);
                        detailsFragment.setArguments(extras);

                        ((FragmentActivity) ctx).getSupportFragmentManager().beginTransaction()
                                .add(R.id.container, detailsFragment, Constants.TAG_APP_DETAILS_FRAGMENT)
                                .addToBackStack(Constants.TAG_APP_DETAILS_FRAGMENT)
                                .commit();
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
                    FlurryAgent.logEvent(TrackingEvents.UserRequestedMoreApps);
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
                if (appsList.getTotalCount() > 0) {
                    notifyAdapter(appsList);
                    if (!success) {
                        calendar.add(Calendar.DATE, -1);
                        getApps();
                        success = true;
                    }
                } else {
                    calendar.add(Calendar.DATE, -1);
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
        ((MainActivity) ctx).findViewById(R.id.reload).setVisibility(View.GONE);

        if (selectedAppPosition > -1) {
            listView.getList().smoothScrollToPosition(selectedAppPosition);
        }
    }

    public void getAppsForNextDate() {
        LoadersUtils.showBottomLoader((Activity) ctx, true);

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
        listView.getList().smoothScrollToPosition(this.items.size() - items.size() + 1);
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
                        listView.getList().smoothScrollToPosition(position + newItems.size());
                    }
                });
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
        success = false;
        items.clear();
        notifyDataSetChanged();

        calendar = Calendar.getInstance();
        today = Calendar.getInstance();

        getApps();
    }

    public void resetAdapter(int position) {
        this.selectedAppPosition = position;

        resetAdapter();
    }

    public void clearAdapter() {
        success = false;
        items.clear();
        notifyDataSetChanged();

        calendar = Calendar.getInstance();
        today = Calendar.getInstance();
    }

    public boolean couldLoadMoreApps() {
        return (backup.size() == 0);
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
        TextView commentsCount;
        Target creatorImageView;
        TextView creatorUsername;
        Button vote;
    }

    private static class ViewHolderSeparator {
        TextView header;
    }

    private static class ViewHolderMoreApps {
        ImageButton moreApps;
    }
}