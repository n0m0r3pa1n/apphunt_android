package com.apphunt.app.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.ui.models.DrawerItem;
import com.apphunt.app.ui.models.DrawerLabel;
import com.apphunt.app.ui.models.DrawerMenu;
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.LoginUtils;
import com.apphunt.app.utils.StringUtils;
import com.apphunt.app.utils.TrackingEvents;
import com.flurry.android.FlurryAgent;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

public class DrawerItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = DrawerItemAdapter.class.getSimpleName();
    private Context ctx;
    private int mSelectedPosition;
    private int prevSelectedPosition;
    private View mSelectedView;

    private final List<DrawerItem> drawerItems;

    private OnItemClickListener listener;

    public DrawerItemAdapter(Context ctx, List<DrawerItem> drawerItems) {
        this.ctx = ctx;
        this.drawerItems = drawerItems;
        BusProvider.getInstance().register(this);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (DrawerItem.Type.values()[viewType]) {
            case HEADER:
                View headerRootView;
                if (LoginProviderFactory.get((Activity) ctx).isUserLoggedIn()) {
                    headerRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_header, parent, false);
                    return new HeaderViewHolder(headerRootView);
                } else {
                    headerRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_header_not_logged, parent, false);
                    return new HeaderLoggedOutViewHolder(headerRootView);
                }
            case DIVIDER:
                View dividerRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_divider, parent, false);
                return new DividerViewHolder(dividerRootView);
            case MENU:
                View menuRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_menu, parent, false);
                menuRootView.setBackgroundResource(R.drawable.row_selector);
                return new MenuViewHolder(menuRootView);
            case SUBMENU:
                View subMenuView = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_submenu, parent, false);
                subMenuView.setBackgroundResource(R.drawable.row_selector);
                return new SubMenuViewHolder(subMenuView);
            case LABEL:
                View label = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_label, parent, false);
                return new LabelViewHolder(label);
            default: return null;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (mSelectedPosition == position) {
            if (mSelectedView != null) {
                mSelectedView.setSelected(false);
            }
            mSelectedPosition = position;
            mSelectedView = holder.itemView;

            if (mSelectedPosition == Constants.SETTINGS - 1 || mSelectedPosition == Constants.SUGGESTIONS - 1) {
                mSelectedView.setSelected(false);
            } else {
                mSelectedView.setSelected(true);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedView != null) {
                    mSelectedView.setSelected(false);
                }
                mSelectedPosition = position;
                prevSelectedPosition = mSelectedPosition;
                mSelectedView = view;
                view.setSelected(true);
                if (listener != null)
                    listener.onClick(view, position);
            }
        });
        DrawerItem drawerItem = drawerItems.get(position);
        switch (drawerItem.getType()) {
            case HEADER:
                if (LoginProviderFactory.get((Activity) ctx).isUserLoggedIn()) {
                    User user = LoginProviderFactory.get((Activity) ctx).getUser();
                    HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                    Picasso.with(ctx).load(user.getProfilePicture()).into(headerViewHolder.profileImage);

                    if (user.getCoverPicture() != null && !user.getCoverPicture().isEmpty()) {
                        Picasso.with(ctx).load(user.getCoverPicture()).into(headerViewHolder.cover);
                    } else {
                        Picasso.with(ctx).load(R.drawable.header_bg).into(headerViewHolder.cover);
                    }

                    headerViewHolder.username.setText(user.getUsername());
                    headerViewHolder.email.setText(user.getEmail());
                    headerViewHolder.logoutButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            LoginProviderFactory.get((Activity) ctx).logout();
                            FlurryAgent.logEvent(TrackingEvents.UserLoggedOut);
                        }
                    });
                } else {
                    HeaderLoggedOutViewHolder headerViewHolder = (HeaderLoggedOutViewHolder) holder;
                    Picasso.with(ctx).load(R.drawable.avatar_placeholder).into(headerViewHolder.profileImage);
                    headerViewHolder.loginButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            LoginUtils.showLoginFragment(ctx);
                        }
                    });
                }

                break;

            case MENU:
                MenuViewHolder menuViewHolder = (MenuViewHolder) holder;
                DrawerMenu drawerMenu = (DrawerMenu) drawerItem;
                menuViewHolder.itemTextView.setText(drawerMenu.getText());
                menuViewHolder.itemImageView.setImageResource(drawerMenu.getIconRes());
                break;
            case LABEL:
                LabelViewHolder labelViewHolder = (LabelViewHolder) holder;
                DrawerLabel label = (DrawerLabel) drawerItem;
                labelViewHolder.label.setText(label.getLabel());
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return drawerItems.get(position).getType().ordinal();
    }

    @Override
    public int getItemCount() {
        return drawerItems.size();
    }

    public void selectPosition(int position) {
        mSelectedPosition = position;
        notifyItemChanged(position);
    }

    public int getPrevSelectedPosition() {
        return prevSelectedPosition;
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {

        private ImageView cover;
        private Target profileImage;
        private TextView username;
        private TextView email;
        private ImageButton logoutButton;

        public HeaderViewHolder(View rootView) {
            super(rootView);

            cover = (ImageView) rootView.findViewById(R.id.cover_picture);
            profileImage = (Target) rootView.findViewById(R.id.profile_image);
            username = (TextView) rootView.findViewById(R.id.username);
            email = (TextView) rootView.findViewById(R.id.email);
            logoutButton = (ImageButton) rootView.findViewById(R.id.logout_button);
        }
    }

    private static class HeaderLoggedOutViewHolder extends RecyclerView.ViewHolder {

        private Target profileImage;
        private Button loginButton;

        public HeaderLoggedOutViewHolder(View rootView) {
            super(rootView);

            profileImage = (Target) rootView.findViewById(R.id.profile_image);
            loginButton = (Button) rootView.findViewById(R.id.login_button);
        }
    }

    private static class DividerViewHolder extends RecyclerView.ViewHolder {

        public DividerViewHolder(View rootView) {
            super(rootView);
        }
    }

    private static class MenuViewHolder extends RecyclerView.ViewHolder {

        private TextView itemTextView;
        private ImageView itemImageView;

        public MenuViewHolder(View rootView) {
            super(rootView);
            itemTextView = (TextView) rootView.findViewById(R.id.item);
            itemImageView = (ImageView) rootView.findViewById(R.id.iv_icon);
        }
    }

    private static class SubMenuViewHolder extends MenuViewHolder {
        public SubMenuViewHolder(View rootView) {
            super(rootView);
        }
    }

    private static class LabelViewHolder extends RecyclerView.ViewHolder {
        private TextView label;
        public LabelViewHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.label);
        }
    }

    public interface OnItemClickListener {
        void onClick(View view, int position);
    }
}
