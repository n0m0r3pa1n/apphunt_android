package com.shtaigaway.apphunt.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.shtaigaway.apphunt.R;
import com.shtaigaway.apphunt.api.AppHuntApiClient;
import com.shtaigaway.apphunt.api.Callback;
import com.shtaigaway.apphunt.api.EmptyCallback;
import com.shtaigaway.apphunt.api.models.App;
import com.shtaigaway.apphunt.api.models.Vote;
import com.shtaigaway.apphunt.app.AppItem;
import com.shtaigaway.apphunt.app.Item;
import com.shtaigaway.apphunt.app.SeparatorItem;
import com.shtaigaway.apphunt.utils.Constants;

import java.util.ArrayList;

import retrofit.client.Response;

public class TrendingAppsAdapter extends BaseAdapter {

    private Context ctx;
    private ListView listView;
    private ArrayList<Item> items = new ArrayList<>();

    private int selectedPosition;

    public TrendingAppsAdapter(Context ctx, ListView listView) {
        this.ctx = ctx;
        this.listView = listView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolderItem viewHolderItem = null;
        ViewHolderSeparator viewHolderSeparator = null;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (getItemViewType(position) == Constants.ItemType.ITEM.getValue()) {
                viewHolderItem = new ViewHolderItem();

                view = inflater.inflate(R.layout.layout_app_item, parent, false);
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
            }
        } else {
            if (getItemViewType(position) == Constants.ItemType.ITEM.getValue()) {
                viewHolderItem = (ViewHolderItem) view.getTag();
            } else if (getItemViewType(position) == Constants.ItemType.SEPARATOR.getValue()) {
                viewHolderSeparator = (ViewHolderSeparator) view.getTag();
            }
        }

        if (getItemViewType(position) == Constants.ItemType.ITEM.getValue()) {
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
                }
            });
        } else if (getItemViewType(position) == Constants.ItemType.SEPARATOR.getValue()) {
            viewHolderSeparator.header.setText(((SeparatorItem) getItem(position)).getData());
        }

        return view;
    }

    public void addItems(ArrayList<Item> items) {
        if (items.size() == 0) {
            this.items = items;
        } else {
            this.items.addAll(items);
            notifyDataSetChanged();

            Log.e("pos", "Pos: " + Integer.valueOf(this.items.size() - items.size()));
            listView.smoothScrollToPosition(this.items.size());
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
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
        ImageView icon;
        TextView title;
        TextView description;
        Button vote;
    }

    private static class ViewHolderSeparator {
        TextView header;
    }
}
