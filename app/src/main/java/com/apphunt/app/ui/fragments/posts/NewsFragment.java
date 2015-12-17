package com.apphunt.app.ui.fragments.posts;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.models.posts.BlogPost;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.posts.GetBlogPostApiEvent;
import com.apphunt.app.event_bus.events.api.posts.GetFeaturedImageApiEvent;
import com.apphunt.app.ui.adapters.NewsAdapter;
import com.apphunt.app.ui.adapters.dividers.SimpleDividerItemDecoration;
import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.ui.interfaces.OnEndReachedListener;
import com.apphunt.app.ui.views.containers.ScrollRecyclerView;
import com.apphunt.app.utils.FlurryWrapper;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by nmp on 15-12-17.
 */
public class NewsFragment extends BaseFragment {
    private int currentPage;

    private Activity activity;
    private NewsAdapter adapter;

    @InjectView(R.id.news_list)
    ScrollRecyclerView newsList;

    @InjectView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @InjectView(R.id.loading)
    CircularProgressBar loading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.inject(this, view);
        loadNews();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                FlurryWrapper.logEvent(TrackingEvents.UserRefreshedNews);
                currentPage = 0;
                adapter = null;
                loadNews();
            }
        });

        newsList.setOnEndReachedListener(new OnEndReachedListener() {
            @Override
            public void onEndReached() {
                loadNews();
            }
        });
        return view;
    }

    private void loadNews() {
        currentPage++;
        ApiClient.getClient(activity).getBlogPosts(currentPage, 5);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public int getTitle() {
        return R.string.news_title;
    }

    @Subscribe
    public void onBlogPostsReceived(GetBlogPostApiEvent event) {
        loading.setVisibility(View.GONE);
        if(event.getBlogPosts() == null || event.getBlogPosts().size() == 0) {
            return;
        }

        if(adapter == null) {
            adapter = new NewsAdapter(activity, event.getBlogPosts());
            newsList.setAdapter(adapter, -1);
            newsList.getRecyclerView().addItemDecoration(new SimpleDividerItemDecoration(activity));
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        } else {
            adapter.addAll(event.getBlogPosts());
        }

        for(BlogPost blogPost : event.getBlogPosts()) {
            if(blogPost.getFeaturedImage() != 0) {
                ApiClient.getClient(activity).getBlogPostFeaturedImage(blogPost.getId(), blogPost.getFeaturedImage());
            }
        }
    }

    @Subscribe
    public void onFeaturedImageReceived(GetFeaturedImageApiEvent event) {
        if(adapter != null) {
            adapter.setFeaturedImage(event.getPostId(), event.getImageUrl());
        }
    }
}
