package com.apphunt.app.ui.adapters.rankings;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.apps.BaseApp;
import com.apphunt.app.ui.fragments.AppDetailsFragment;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.utils.StringUtils;
import com.apphunt.app.constants.TrackingEvents;
import com.flurry.android.FlurryAgent;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nmp on 15-5-26.
 */
public class TopAppsAdapter extends RecyclerView.Adapter<TopAppsAdapter.ViewHolder> {
    public static final String TAG = TopAppsAdapter.class.getSimpleName();
    private List<BaseApp> apps = new ArrayList<BaseApp>();
    private Context context;

    public TopAppsAdapter(Context context, List<BaseApp> apps) {
        this.apps = apps;
        this.context = context;
    }

    @Override
    public TopAppsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_collection_top_apps_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TopAppsAdapter.ViewHolder holder, int position) {
        final BaseApp app = apps.get(position);

        int appPosition = position + 1;
        String name = StringUtils.htmlDecodeString(app.getName());
        holder.name.setMaxLines(2);
        holder.name.setText(name);
        holder.position.setText(appPosition + "");
        holder.createdByName.setText("by " + app.getCreatedBy().getName());
        Picasso.with(context).load(app.getIcon().replace("w300", "w512")).into(holder.icon);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FlurryAgent.logEvent(TrackingEvents.UserOpenedAppDetailsFromTopApps);
                AppDetailsFragment detailsFragment = new AppDetailsFragment();
                String title = context.getString(R.string.title_top_apps);

                try {
                    title = ((ActionBarActivity) context).getSupportActionBar().getTitle().toString();
                } catch (NullPointerException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                detailsFragment.setPreviousTitle(title);

                Bundle extras = new Bundle();
                extras.putString(Constants.KEY_APP_ID, app.getId());
                detailsFragment.setArguments(extras);

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, detailsFragment, Constants.TAG_APP_DETAILS_FRAGMENT)
                        .addToBackStack(Constants.TAG_APP_DETAILS_FRAGMENT)
                        .commit();
            }
        });
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.app_name)
        public TextView name;

        @InjectView(R.id.created_by)
        public TextView createdByName;

        @InjectView(R.id.position)
        public TextView position;

        @InjectView(R.id.icon)
        public ImageView icon;

        @InjectView(R.id.card_view)
        public RelativeLayout cardView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
