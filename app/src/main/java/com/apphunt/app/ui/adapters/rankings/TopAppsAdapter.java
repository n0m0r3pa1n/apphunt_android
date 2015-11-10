package com.apphunt.app.ui.adapters.rankings;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.apps.BaseApp;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.ui.views.app.DownloadButton;
import com.apphunt.app.utils.StringUtils;
import com.apphunt.app.utils.ui.NavUtils;
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
        holder.description.setText(app.getDescription());
        holder.position.setText(appPosition + "");
        holder.category.setText(app.getCategories().get(0));
        Picasso.with(context).load(app.getIcon().replace("w300", "w512")).into(holder.icon);

        holder.detailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAppDetailsFragment(app);
            }
        });

        holder.installBtn.setAppPackage(app.getPackageName());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAppDetailsFragment(app);
            }
        });
    }

    private void openAppDetailsFragment(BaseApp app) {
        FlurryAgent.logEvent(TrackingEvents.UserOpenedAppDetailsFromTopApps);
        NavUtils.getInstance((AppCompatActivity) context).presentAppDetailsFragment(app.getId());
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
        @InjectView(R.id.card_view)
        CardView cardView;

        @InjectView(R.id.app_name)
        public TextView name;

        @InjectView(R.id.description)
        public TextView description;

        @InjectView(R.id.category)
        public TextView category;

        @InjectView(R.id.position)
        public TextView position;

        @InjectView(R.id.icon)
        public ImageView icon;

        @InjectView(R.id.install_btn)
        public DownloadButton installBtn;

        @InjectView(R.id.details_btn)
        public Button detailsBtn;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
