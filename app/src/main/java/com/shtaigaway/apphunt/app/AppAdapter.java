package com.shtaigaway.apphunt.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shtaigaway.apphunt.R;

import java.util.List;

/**
 * Created by Naughty Spirit
 * on 1/15/15.
 */
public class AppAdapter extends ArrayAdapter<App> {

    public AppAdapter(Context context,
                      List<App> apps) {
        super(context, R.layout.app_item, apps);
    }

    static class ViewHolder {
        public TextView appName;
        public ImageView appIcon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.app_item, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.appName = (TextView) rowView.findViewById(R.id.app_name);
            viewHolder.appIcon = (ImageView) rowView
                    .findViewById(R.id.app_icon);
            rowView.setTag(viewHolder);
        }

        App app = getItem(position);
        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.appName.setText(app.getName());
        holder.appIcon.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.ic_launcher));

        return rowView;

    }
}