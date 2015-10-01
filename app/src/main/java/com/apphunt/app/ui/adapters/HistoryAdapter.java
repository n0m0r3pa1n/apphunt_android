package com.apphunt.app.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.ui.models.history.row.base.HistoryRow;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private final List<HistoryRow> rows;
    private final Context context;

    public HistoryAdapter(Context context, List<HistoryRow> rows) {
        this.rows = rows;
        this.context = context;
    }

    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_history_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final HistoryRow row = rows.get(position);
        holder.eventText.setText(row.getText());

        Picasso.with(context)
                .load(row.getUser().getProfilePicture())
                .placeholder(R.drawable.avatar_placeholder)
                .into(holder.userProfilePicture);


        Picasso.with(context)
                .load(row.getIconResId())
                .into(holder.icon);

        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row.openUserProfile();
            }
        });

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row.openEvent();
            }
        });
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    public void addRows(List<HistoryRow> rows) {
        this.rows.addAll(rows);
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

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
