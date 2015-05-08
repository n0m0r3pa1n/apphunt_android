package com.apphunt.app.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apphunt.app.MainActivity;
import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.AppHuntApiClient;
import com.apphunt.app.api.apphunt.Callback;
import com.apphunt.app.api.apphunt.models.App;
import com.apphunt.app.api.apphunt.models.AppsList;
import com.apphunt.app.ui.fragments.AppDetailsFragment;
import com.apphunt.app.ui.listview_items.AppItem;
import com.apphunt.app.ui.listview_items.Item;
import com.apphunt.app.ui.listview_items.MoreAppsItem;
import com.apphunt.app.ui.listview_items.SeparatorItem;
import com.apphunt.app.ui.views.vote.AppVoteButton;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.LoadersUtils;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.TrackingEvents;
import com.flurry.android.FlurryAgent;
import com.quentindommerc.superlistview.SuperListview;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
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

            viewHolderItem.title.setText(app.getName());
            viewHolderItem.description.setText(app.getDescription());
            viewHolderItem.commentsCount.setText(String.valueOf(app.getCommentsCount()));
            Picasso.with(ctx)
                    .load(app.getCreatedBy().getProfilePicture())
                    .into(viewHolderItem.creatorImageView);
            viewHolderItem.creatorUsername.setText("by " + app.getCreatedBy().getUsername());
            viewHolderItem.vote.setApp(app);

            View.OnClickListener detailsClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
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
            };

            viewHolderItem.layout.setOnClickListener(null);
            viewHolderItem.layout.setOnClickListener(detailsClickListener);

            viewHolderItem.details.setOnClickListener(null);
            viewHolderItem.details.setOnClickListener(detailsClickListener);

            viewHolderItem.share.setOnClickListener(null);
            final ImageView iconImageView = viewHolderItem.icon;
            final App currApp = ((AppItem)items.get(position)).getData();
            final String message = viewHolderItem.title.getText() + ". " + viewHolderItem.description.getText() + " " + app.getShortUrl();
            viewHolderItem.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri iconUri = getLocalBitmapUri(iconImageView);
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("*/*");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, iconUri);
                    ctx.startActivity(Intent.createChooser(sharingIntent, "Share using"));
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("appId", currApp.getId());
                    FlurryAgent.logEvent(TrackingEvents.UserSharedApp, params);
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

    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }



    private void getApps() {
        String userId = SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID);
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

        AppHuntApiClient.getClient().getApps(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID),
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
        AppHuntApiClient.getClient().getApps(SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID),
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

    //region ViewHolders
    static class ViewHolderItem {
        @InjectView(R.id.item)
        LinearLayout layout;

        @InjectView(R.id.app_icon)
        ImageView icon;

        @InjectView(R.id.app_name)
        TextView title;

        @InjectView(R.id.description)
        TextView description;

        @InjectView(R.id.comments_count)
        TextView commentsCount;

        @InjectView(R.id.creator_avatar)
        Target creatorImageView;

        @InjectView(R.id.creator_name)
        TextView creatorUsername;

        @InjectView(R.id.btn_vote)
        AppVoteButton vote;

        @InjectView(R.id.btn_details)
        Button details;

        @InjectView(R.id.btn_share)
        Button share;

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