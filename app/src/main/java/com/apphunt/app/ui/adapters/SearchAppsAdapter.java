package com.apphunt.app.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.apps.App;
import com.apphunt.app.api.apphunt.models.apps.BaseApp;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.utils.LoginUtils;
import com.apphunt.app.utils.StringUtils;
import com.apphunt.app.utils.ui.NavUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchAppsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private final List<App> apps;
    private final Context ctx;

    public SearchAppsAdapter(Context ctx, List<App> apps) {
        this.apps = apps;
        this.ctx = ctx;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View  view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_app_item, parent, false);
        return new TrendingAppsAdapter.ViewHolderItem(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final TrendingAppsAdapter.ViewHolderItem viewHolderItem = (TrendingAppsAdapter.ViewHolderItem) holder;
        final BaseApp app = apps.get(position);

        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ctx.getResources().getDimension(R.dimen.list_item_icon_size), ctx.getResources().getDisplayMetrics());

        Picasso.with(ctx).load(app.getIcon()).resize(size, size).into(viewHolderItem.icon);

        viewHolderItem.title.setText(StringUtils.htmlDecodeString(app.getName()));
        if(!app.getCategories().isEmpty()) {
            viewHolderItem.category.setText(app.getCategories().get(0));
        }

        User createdBy = app.getCreatedBy();
        viewHolderItem.creatorView.setUserWithText(createdBy.getId(), createdBy.getProfilePicture(), "by", createdBy.getName());
        viewHolderItem.vote.setBaseApp((App) app);

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
                    e.printStackTrace();
                }
            }
        };

        viewHolderItem.layout.setOnClickListener(null);
        viewHolderItem.layout.setOnClickListener(detailsClickListener);
    }

    public void addApps(List<App> addedApps) {
        apps.addAll(addedApps);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    public Object getItem(int position) {
        return apps.get(position);
    }

}
