package com.apphunt.app.ui.adapters.collections;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.collections.apps.CollectionBanner;
import com.apphunt.app.api.apphunt.models.collections.apps.CollectionBannersList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 7/7/15.
 * *
 * * NaughtySpirit 2015
 */
public class CollectionBannersAdapter extends BaseAdapter {

    private static final String TAG = CollectionBannersAdapter.class.getSimpleName();

    private Context ctx;
    private ArrayList<CollectionBanner> banners = new ArrayList<>();

    public CollectionBannersAdapter(Context ctx, CollectionBannersList banners) {
        this.ctx = ctx;
        this.banners = banners.getBanners();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder = null;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.layout_collection_banner_item, parent, false);
            viewHolder = new ViewHolder(view);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Picasso.with(ctx).load(getItem(position).getBannerUrl()).into(viewHolder.bannerImage);

        return view;
    }

    @Override
    public int getCount() {
        return banners.size();
    }

    @Override
    public CollectionBanner getItem(int i) {
        return banners.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    static class ViewHolder {

        @InjectView(R.id.banner_image)
        ImageView bannerImage;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }
}
