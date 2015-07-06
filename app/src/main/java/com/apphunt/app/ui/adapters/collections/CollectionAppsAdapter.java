package com.apphunt.app.ui.adapters.collections;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.apps.App;
import com.apphunt.app.api.apphunt.models.apps.BaseApp;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CollectionAppsAdapter extends BaseAdapter {
    private List<BaseApp> apps;
    private Context context;
    private LayoutInflater inflater;
    public CollectionAppsAdapter(Context context, List<BaseApp> apps) {
        this.apps = apps;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return apps.size();
    }

    @Override
    public BaseApp getItem(int position) {
        return apps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        BaseApp app = apps.get(position);
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.layout_collection_app, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText(app.getName());
        viewHolder.createdBy.setText(app.getCreatedBy().getName());
        Picasso.with(context).load(app.getIcon()).into(viewHolder.icon);

        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.icon)
        ImageView icon;

        @InjectView(R.id.title)
        TextView title;

        @InjectView(R.id.created_by)
        TextView createdBy;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
