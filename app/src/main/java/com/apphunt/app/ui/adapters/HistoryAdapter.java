package com.apphunt.app.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.ui.models.history.row.HeaderHistoryRow;
import com.apphunt.app.ui.models.history.row.base.HistoryRowComponent;
import com.apphunt.app.utils.FlurryWrapper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG = HistoryAdapter.class.getSimpleName();

    private List<HistoryRowComponent> rows;
    private final Context context;

    public HistoryAdapter(Context context, List<HistoryRowComponent> rows) {
        this.rows = rows;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;
        if(viewType == Constants.ItemType.ITEM.getValue()) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_history_row_item, parent, false);
            viewHolder = new ViewHolder(view);
        } else if(viewType == Constants.ItemType.SEPARATOR.getValue()) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_history_row_header, parent, false);
            viewHolder = new HeaderViewHolder(view);
        }


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == Constants.ItemType.ITEM.getValue()) {
            final HistoryRowComponent row = rows.get(position);
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.eventText.setText(row.getText());

            Picasso.with(context)
                    .load(row.getUser().getProfilePicture())
                    .placeholder(R.drawable.avatar_placeholder)
                    .into(viewHolder.userProfilePicture);


            Picasso.with(context)
                    .load(row.getIconResId())
                    .into(viewHolder.icon);

            viewHolder.followerIconContainer.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    FlurryWrapper.logEvent(TrackingEvents.UserOpenedUserProfileFromHistory);
                    row.openUserProfile();
                }
            });

            viewHolder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: ");
                    FlurryWrapper.logEvent(TrackingEvents.UserOpenedHistoryEvent, new HashMap<String, String>(){{
                        put("type", row.getType().name());
                    }});
                    row.openEvent();
                }
            });

            if(row.isUnseen()) {
                viewHolder.container.setBackgroundColor(Color.parseColor("#3223A3EB"));
            } else {
                viewHolder.container.setBackgroundColor(context.getResources().getColor(R.color.bg_secondary));
            }

            if(row.isFollowRow()) {
                viewHolder.followerIcon.setVisibility(View.VISIBLE);
            } else {
                viewHolder.followerIcon.setVisibility(View.INVISIBLE);
            }
        } else if(getItemViewType(position) == Constants.ItemType.SEPARATOR.getValue()) {
            HeaderHistoryRow row = (HeaderHistoryRow) rows.get(position);
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.date.setText(row.getText());
        }
    }

    @Override
    public int getItemCount() {
        return rows == null ? 0 : rows.size();
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public List<HistoryRowComponent> getRows() {
        return rows;
    }

    public HistoryRowComponent getRow(int position) {
        return rows != null && rows.size()  > position ? rows.get(position) : null;
    }

    public void markUnseenEvents(List<String> eventIds) {
        for(HistoryRowComponent component : rows) {
            for(String id : eventIds) {
                if(component.getId().equals(id)) {
                    component.setIsUnseen(true);
                    break;
                }
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position).getType().getValue();
    }

    public void addRows(List<HistoryRowComponent> rows) {
        this.rows.addAll(rows);
        notifyDataSetChanged();
    }

    public void reset() {
        rows = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addRow(int position, HistoryRowComponent rowComponent) {
        rows.add(position, rowComponent);
        notifyDataSetChanged();
    }

    public void markEventsAsSeen() {
        for(HistoryRowComponent row : rows) {
            row.setIsUnseen(false);
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.event_container)
        RelativeLayout container;

        @InjectView(R.id.event_icon)
        ImageView icon;

        @InjectView(R.id.user_picture)
        CircleImageView userProfilePicture;

        @InjectView(R.id.event_text)
        TextView eventText;

        @InjectView(R.id.icons_container)
        FrameLayout followerIconContainer;

        @InjectView(R.id.follower_icon)
        FrameLayout followerIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.date)
        TextView date;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
