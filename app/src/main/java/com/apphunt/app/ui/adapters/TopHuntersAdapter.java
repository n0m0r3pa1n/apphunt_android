package com.apphunt.app.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.collections.hunters.Hunter;
import com.apphunt.app.api.apphunt.models.collections.hunters.HuntersCollection;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nmp on 15-6-8.
 */
public class TopHuntersAdapter extends RecyclerView.Adapter<TopHuntersAdapter.ViewHolder> {

    private HuntersCollection collection;
    private Context context;

    public TopHuntersAdapter(Context context, HuntersCollection collection) {
        this.collection = collection;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_collection_hunter, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Hunter hunter = collection.getHunters().get(position);

        holder.name.setText(hunter.getUser().getName());
        holder.score.setText("Score " + hunter.getScore());
        Picasso.with(context).load(hunter.getUser().getProfilePicture()).into(holder.picture);
    }

    @Override
    public int getItemCount() {
        return collection.getHunters().size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.name)
        TextView name;

        @InjectView(R.id.score)
        TextView score;

        @InjectView(R.id.picture)
        Target picture;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
