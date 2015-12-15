package com.apphunt.app.ui.fragments.details;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiService;
import com.apphunt.app.api.apphunt.models.comments.Comments;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.apps.LoadAppCommentsApiEvent;
import com.apphunt.app.event_bus.events.api.apps.LoadAppDetailsApiEvent;
import com.apphunt.app.event_bus.events.ui.ReloadCommentsEvent;
import com.apphunt.app.ui.adapters.CommentsRecyclerAdapter;
import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.ui.interfaces.OnEndReachedListener;
import com.apphunt.app.ui.views.containers.ScrollRecyclerView;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by nmp on 15-12-15.
 */
public class ReviewsFragment extends BaseFragment {
    public static final String TAG = ReviewsFragment.class.getSimpleName();
    public static final int MAX_DISPLAYED_COMMENTS = 3;


    @InjectView(R.id.comments_list)
    ScrollRecyclerView commentsList;

    @InjectView(R.id.loading)
    CircularProgressBar loading;

    @InjectView(R.id.vs_no_comments)
    NestedScrollView vsNoComments;

    private Activity activity;
    private CommentsRecyclerAdapter commentsAdapter;
    private String userId;
    private String appId;
    private int currentPage;


    public ReviewsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);
        ButterKnife.inject(this, view);
        commentsList.setOnEndReachedListener(new OnEndReachedListener() {
            @Override
            public void onEndReached() {
                loadComments();
            }
        });
        return view;
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

    @Subscribe
    public void onAppDetailsLoaded(LoadAppDetailsApiEvent event) {
        userId = SharedPreferencesHelper.getStringPreference(Constants.KEY_USER_ID);
        appId = event.getBaseApp().getId();
        loadComments();
    }

    public void loadComments() {
        currentPage++;
        ApiService.getInstance(activity).loadAppComments(appId, userId, currentPage, MAX_DISPLAYED_COMMENTS);
    }

    @Subscribe
    public void onNewComment(ReloadCommentsEvent event) {
        currentPage = 1;
        commentsAdapter = null;
        ApiService.getInstance(activity).reloadAppComments(appId);
    }

    @Subscribe
    public void onAppCommentsLoaded(LoadAppCommentsApiEvent event) {
        populateComments(event.getComments());
    }

    private void populateComments(Comments comments) {
        loading.setVisibility(View.GONE);


        if((comments == null || comments.getComments() == null || comments.getComments().size() == 0)
                && commentsAdapter == null) {
            vsNoComments.setVisibility(View.VISIBLE);
            commentsList.setVisibility(View.GONE);
            return;
        }

        commentsList.setVisibility(View.VISIBLE);
        vsNoComments.setVisibility(View.GONE);


        if(commentsAdapter == null) {
            commentsAdapter = new CommentsRecyclerAdapter(activity, comments.getComments());
            commentsList.setAdapter(commentsAdapter, comments.getTotalCount());
        } else {
            commentsAdapter.addComments(comments.getComments());
        }

    }
}
