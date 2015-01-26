package com.shtaigaway.apphunt.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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

import com.facebook.Session;
import com.facebook.android.Facebook;
import com.koushikdutta.ion.Ion;
import com.shtaigaway.apphunt.R;
import com.shtaigaway.apphunt.api.AppHuntApiClient;
import com.shtaigaway.apphunt.api.Callback;
import com.shtaigaway.apphunt.api.models.App;
import com.shtaigaway.apphunt.api.models.AppsList;
import com.shtaigaway.apphunt.api.models.Vote;
import com.shtaigaway.apphunt.app.AppItem;
import com.shtaigaway.apphunt.app.Item;
import com.shtaigaway.apphunt.app.MoreAppsItem;
import com.shtaigaway.apphunt.app.SeparatorItem;
import com.shtaigaway.apphunt.ui.LoginFragment;
import com.shtaigaway.apphunt.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit.client.Response;

public class TrendingAppsAdapter extends BaseAdapter {

    private Context ctx;
    private ListView listView;
    private ArrayList<Item> items = new ArrayList<>();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Calendar calendar = Calendar.getInstance();
    private Calendar today = Calendar.getInstance();

    public TrendingAppsAdapter(Context ctx, ListView listView) {
        this.ctx = ctx;
        this.listView = listView;

        // TODO: To be removed!!! For TEST reasons
        today.add(Calendar.DATE, -3);
        calendar.add(Calendar.DATE, -3);
        // TODO: To be removed!!! For TEST reasons

        getAppsForTodayAndYesterday();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolderItem viewHolderItem = null;
        ViewHolderSeparator viewHolderSeparator = null;
        ViewHolderMoreApps viewHolderMoreApps = null;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (getItemViewType(position) == Constants.ItemType.ITEM.getValue()) {
                viewHolderItem = new ViewHolderItem();

                view = inflater.inflate(R.layout.layout_app_item, parent, false);
                viewHolderItem.layout = (RelativeLayout) view.findViewById(R.id.item);
                viewHolderItem.icon = (ImageView) view.findViewById(R.id.icon);
                viewHolderItem.title = (TextView) view.findViewById(R.id.name);
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

            Ion.with(viewHolderItem.icon)
                    .load(app.getIcon());

            viewHolderItem.title.setText(app.getName());
            viewHolderItem.description.setText(app.getDescription());
            viewHolderItem.vote.setText(app.getVotesCount());

            viewHolderItem.vote.setOnClickListener(null);
            viewHolderItem.vote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    Session session = Session.getActiveSession();

                    if (session != null && session.isOpened()) {
                        AppHuntApiClient.getClient().vote(app.getId(), new Vote("54be5d68e4b0d3cacca686c5"), new Callback<Vote>() {
                            @Override
                            public void success(Vote vote, Response response) {
                                if (response != null && response.getStatus() == 200) {
                                    app.setVotesCount(vote.getVotes());

                                    ((Button) v).setText(vote.getVotes());
                                    v.setClickable(false);
                                } else {
                                    // TODO: Possibly notify the user that he cannot vote twice for the same app
                                }
                            }
                        });
                    } else {
                        Fragment loginFragment = new LoginFragment();
                        ((ActionBarActivity) ctx).getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.bounce, R.anim.slide_out_top)
                                .replace(R.id.container, loginFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            });

            viewHolderItem.layout.setOnClickListener(null);
            viewHolderItem.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(((AppItem) getItem(position)).getData().getShortUrl()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(intent);
                }
            });
        } else if (getItemViewType(position) == Constants.ItemType.SEPARATOR.getValue() && viewHolderSeparator != null) {
            viewHolderSeparator.header.setText(((SeparatorItem) getItem(position)).getData());
        } else if (getItemViewType(position) == Constants.ItemType.MORE_APPS.getValue() && viewHolderMoreApps != null) {
            viewHolderMoreApps.moreApps.setOnClickListener(null);
            viewHolderMoreApps.moreApps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadMoreApps(position);
                }
            });
        }

        return view;
    }

    private void getAppsForTodayAndYesterday() {
        AppHuntApiClient.getClient().getApps(dateFormat.format(today.getTime()), 1, 5, Constants.PLATFORM, new Callback<AppsList>() {
            @Override
            public void success(AppsList appsList, Response response) {
                if (appsList.getTotalCount() > 0) {
                    items.add(new SeparatorItem("Today"));

                    for (App app : appsList.getApps()) {
                        items.add(new AppItem(app));
                    }

                    if (appsList.haveMoreApps())
                        items.add(new MoreAppsItem(1, 5, dateFormat.format(today.getTime())));

                    calendar.add(Calendar.DATE, -1);

                    AppHuntApiClient.getClient().getApps(dateFormat.format(calendar.getTime()), 1, 5, Constants.PLATFORM, new Callback<AppsList>() {
                        @Override
                        public void success(AppsList appsList, Response response) {
                            items.add(new SeparatorItem("Yesterday"));

                            for (App app : appsList.getApps()) {
                                items.add(new AppItem(app));
                            }

                            if (appsList.haveMoreApps())
                                items.add(new MoreAppsItem(1, 5, dateFormat.format(calendar.getTime())));

                            notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    public void getAppsForNextDate() {
        calendar.add(Calendar.DATE, -1);

        final String date = dateFormat.format(calendar.getTime());

        AppHuntApiClient.getClient().getApps(date, 1, 5, Constants.PLATFORM, new Callback<AppsList>() {
            @Override
            public void success(AppsList appsList, Response response) {
                ArrayList<Item> items = new ArrayList<>();

                if (appsList.getTotalCount() > 0) {
                    try {
                        if (dateFormat.format(today.getTime()).equals(date)) {
                            items.add(new SeparatorItem("Today"));
                        } else {
                            items.add(new SeparatorItem(date));
                        }
                    } catch (Exception e) {
                        Log.e("Exception", e.getMessage());
                    }

                    for (App app : appsList.getApps()) {
                        items.add(new AppItem(app));
                    }

                    if (appsList.haveMoreApps())
                        items.add(new MoreAppsItem(1, 5, date));

                    addItems(items);
                }
            }
        });
    }

    public void addItems(ArrayList<Item> items) {
        if (items.size() == 0) {
            this.items = items;
        } else {
            this.items.addAll(items);
            notifyDataSetChanged();

            listView.smoothScrollToPosition(this.items.size());
        }
    }

    private void loadMoreApps(final int position) {
        final MoreAppsItem item = (MoreAppsItem) getItem(position);
        AppHuntApiClient.getClient().getApps(item.getDate(), item.getNextPage(), item.getItems(), Constants.PLATFORM, new Callback<AppsList>() {
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
