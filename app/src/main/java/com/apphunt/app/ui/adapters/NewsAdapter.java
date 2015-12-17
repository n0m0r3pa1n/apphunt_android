package com.apphunt.app.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.posts.BlogPost;
import com.apphunt.app.ui.views.BlogPostRow;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nmp on 15-12-15.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private List<BlogPost> blogPosts;
    private final Context context;

    public NewsAdapter(Context context, List<BlogPost> blogPosts) {
        this.blogPosts = blogPosts;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final BlogPost post = blogPosts.get(position);
        holder.blogPostRow.setBlogPost(post);
    }


    @Override
    public int getItemCount() {
        return blogPosts.size();
    }

    public void addAll(List<BlogPost> blogPosts) {
        this.blogPosts.addAll(blogPosts);
        notifyDataSetChanged();
    }

    public void setFeaturedImage(int postId, String imageUrl) {
        for (int i = 0; i < blogPosts.size(); i++) {
            BlogPost post = blogPosts.get(i);
            if(post.getId() == postId) {
                post.setFeaturedImageUrl(imageUrl);
                notifyItemChanged(i);
                break;
            }
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.blog_post_row)
        BlogPostRow blogPostRow;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
