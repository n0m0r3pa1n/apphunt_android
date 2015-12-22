package com.apphunt.app.ui.adapters;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.apps.BaseApp;
import com.apphunt.app.api.apphunt.models.posts.BlogPost;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.ui.fragments.TrendingAppsFragment;
import com.apphunt.app.ui.views.CreatorView;
import com.apphunt.app.ui.views.vote.AppVoteButton;
import com.apphunt.app.utils.FlurryWrapper;
import com.apphunt.app.utils.StringUtils;
import com.apphunt.app.utils.ui.NavUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nmp on 15-5-26.
 */
public class TrendingAppsAdapter extends RecyclerView.Adapter<TrendingAppsAdapter.ViewHolder> {
    public static final String TAG = TrendingAppsAdapter.class.getSimpleName();
    private List<BaseApp> apps = new ArrayList<BaseApp>();
    private Context context;

    public TrendingAppsAdapter(Context context, List<BaseApp> apps) {
        this.apps = apps;
        this.context = context;
    }

    @Override
    public TrendingAppsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_app_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrendingAppsAdapter.ViewHolder holder, int position) {
        final BaseApp app = apps.get(position);

        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, context.getResources().getDimension(R.dimen.list_item_icon_size), context.getResources().getDisplayMetrics());

        Picasso.with(context).load(app.getIcon()).resize(size, size).into(holder.icon);

        holder.title.setText(StringUtils.htmlDecodeString(app.getName()));
        if(!app.getCategories().isEmpty()) {
            holder.category.setText(app.getCategories().get(0));
        }

        User createdBy = app.getCreatedBy();
        holder.creatorView.setUserWithText(createdBy.getId(), createdBy.getProfilePicture(), "by", createdBy.getName());

        holder.vote.setApp(app);
        holder.vote.setTrackingScreen(TrendingAppsFragment.TAG);
        holder.commentsCount.setText(app.getCommentsCount() + "");

        View.OnClickListener detailsClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    NavUtils.getInstance((AppCompatActivity) context).presentAppDetailsFragment(app.getId());
                } catch (Exception e) {
                    Log.e(TAG, "Couldn't get the shortUrl");
                    e.printStackTrace();
                }
            }
        };

        holder.layout.setOnClickListener(null);
        holder.layout.setOnClickListener(detailsClickListener);

    }

    private void openAppDetailsFragment(BaseApp app) {
        FlurryWrapper.logEvent(TrackingEvents.UserOpenedAppDetailsFromTopApps);
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

    public void addAll(ArrayList<BaseApp> apps) {
        this.apps.addAll(apps);
        notifyDataSetChanged();
    }

    public void clearItems() {
        this.apps.clear();
        notifyDataSetChanged();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
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

        @InjectView(R.id.comments_count)
        TextView commentsCount;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
