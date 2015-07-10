package com.apphunt.app.ui.adapters.collections;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.apps.BaseApp;
import com.apphunt.app.ui.interfaces.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CollectionAppsAdapter extends RecyclerView.Adapter<CollectionAppsAdapter.ViewHolder> {
    private List<BaseApp> apps;
    private Context context;
    private LayoutInflater inflater;
    private boolean isEdit = false;
    private OnItemClickListener listener;

    public CollectionAppsAdapter(Context context, List<BaseApp> apps) {
        this.apps = apps;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setEditable(boolean isEdit) {
        this.isEdit = isEdit;
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_collection_app, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        BaseApp app = apps.get(position);

        holder.title.setText(app.getName());
        holder.createdBy.setText(app.getCreatedBy().getName());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apps.remove(position);
                notifyDataSetChanged();
            }
        });


        if(isEdit) {
            holder.delete.setVisibility(View.VISIBLE);
        } else {
            holder.delete.setVisibility(View.INVISIBLE);
        }
        Picasso.with(context).load(app.getIcon()).into(holder.icon);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v, position);
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    public List<BaseApp> getItems() {
        return apps;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.card_view)
        CardView cardView;

        @InjectView(R.id.icon)
        ImageView icon;

        @InjectView(R.id.title)
        TextView title;

        @InjectView(R.id.created_by)
        TextView createdBy;

        @InjectView(R.id.delete)
        ImageView delete;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
