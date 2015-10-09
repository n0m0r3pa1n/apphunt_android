package com.apphunt.app.ui.fragments.profile.tabs;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.models.Pagination;
import com.apphunt.app.api.apphunt.models.comments.ProfileComments;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.users.GetUserCommentsApiEvent;
import com.apphunt.app.ui.adapters.dividers.SimpleDividerItemDecoration;
import com.apphunt.app.ui.adapters.profile.ProfileCommentsAdapter;
import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.ui.interfaces.OnEndReachedListener;
import com.apphunt.app.ui.views.containers.ScrollRecyclerView;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class CommentsFragment extends BaseFragment {

    private AppCompatActivity activity;
    private ProfileCommentsAdapter adapter;

    private String profileId;
    private String userId;
    private int currentPage = 0;

    @InjectView(R.id.items)
    ScrollRecyclerView items;

    @InjectView(R.id.loading)
    CircularProgressBar loader;

    @InjectView(R.id.vs_no_comments)
    ViewStub vsNoComments;

    public static CommentsFragment newInstance(String profileId) {

        Bundle args = new Bundle();
        args.putString(Constants.KEY_USER_PROFILE, profileId);
        CommentsFragment fragment = new CommentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_comments, container, false);
        ButterKnife.inject(this, view);
        profileId = getArguments().getString(Constants.KEY_USER_PROFILE);
        if(LoginProviderFactory.get(activity).isUserLoggedIn()) {
            userId = LoginProviderFactory.get(activity).getUser().getId();
        }
        getComments();
        items.setOnEndReachedListener(new OnEndReachedListener() {
            @Override
            public void onEndReached() {
                getComments();
            }
        });
        items.getRecyclerView().addItemDecoration(new SimpleDividerItemDecoration(activity));
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (AppCompatActivity) activity;
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void onUserComments(GetUserCommentsApiEvent event) {
        loader.setVisibility(View.GONE);
        items.hideBottomLoader();

        ProfileComments comments = event.getComments();
        if(comments == null || comments.getComments() == null || comments.getComments().size() == 0) {
            vsNoComments.setVisibility(View.VISIBLE);
            return;
        }

        if(adapter == null) {
            adapter = new ProfileCommentsAdapter(getActivity(), comments.getComments());
            items.setAdapter(adapter, comments.getTotalCount());
        } else {
            adapter.addItems(comments.getComments());
        }
    }

    private void getComments() {
        currentPage++;
        ApiClient.getClient(activity).getUserComments(profileId, userId, new Pagination(currentPage, Constants.COMMENTS_PAGE_SIZE));
    }

}
