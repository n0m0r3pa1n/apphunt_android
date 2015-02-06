package com.apphunt.app.ui.adapters;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apphunt.app.R;

import java.util.ArrayList;
import java.util.List;


public class UserAppsAdapter extends BaseAdapter {
    private static final String TAG = UserAppsAdapter.class.getName();

    private List<ApplicationInfo> appsList = new ArrayList<>();
    private Context ctx;
    private PackageManager packageManager;

    public UserAppsAdapter(Context ctx, List<ApplicationInfo> appsList) {
        this.ctx = ctx;
        this.appsList = appsList;
        this.packageManager = ctx.getPackageManager();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view=convertView;
        ViewHolder viewHolder;

        ApplicationInfo data = getItem(position);

        if(view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.layout_user_app_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) view.findViewById(R.id.app_icon);
            viewHolder.name = (TextView) view.findViewById(R.id.app_name);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.icon.setImageDrawable(data.loadIcon(packageManager));
        viewHolder.name.setText(data.loadLabel(packageManager));

        return view;
    }

    @Override
    public int getCount() {
        return appsList.size();
    }

    @Override
    public ApplicationInfo getItem(int position) {
        return appsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView icon;
        TextView name;
    }
}