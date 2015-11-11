package com.apphunt.app.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
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
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.ui.interfaces.OnItemClickListener;
import com.apphunt.app.ui.models.drawer.DrawerItem;
import com.apphunt.app.ui.models.drawer.DrawerLabel;
import com.apphunt.app.ui.models.drawer.DrawerMenu;
import com.apphunt.app.utils.FlurryWrapper;
import com.apphunt.app.utils.LoginUtils;
import com.apphunt.app.utils.ui.NavUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

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
                    final User user = LoginProviderFactory.get((Activity) ctx).getUser();
                    HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                    Picasso.with(ctx).load(user.getProfilePicture()).into(headerViewHolder.profileImage);

                    if (user.getCoverPicture() != null && !user.getCoverPicture().isEmpty()) {
                        Picasso.with(ctx).load(user.getCoverPicture()).into(headerViewHolder.cover);
                    } else {
                        Picasso.with(ctx).load(R.drawable.header_bg).into(headerViewHolder.cover);
                    }

                    View.OnClickListener listener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FlurryWrapper.logEvent(TrackingEvents.UserOpenedOwnProfileFromDrawer);
                            NavUtils.getInstance((AppCompatActivity) ctx).presentUserProfileFragment(
                                    user.getId(), user.getName()
                            );
                        }
                    };

                    headerViewHolder.cover.setOnClickListener(listener);
                    headerViewHolder.profileImage.setOnClickListener(listener);
                    headerViewHolder.username.setText(user.getUsername());
                    headerViewHolder.email.setText(user.getEmail());
                    headerViewHolder.logoutButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            LoginProviderFactory.get((Activity) ctx).logout();
                            FlurryWrapper.logEvent(TrackingEvents.UserLoggedOut);
                        }
                    });
                } else {
                    HeaderLoggedOutViewHolder headerViewHolder = (HeaderLoggedOutViewHolder) holder;
                    headerViewHolder.profileImage.setOnClickListener(null);
                    Picasso.with(ctx).load(R.drawable.avatar_placeholder).into(headerViewHolder.profileImage);
                    headerViewHolder.loginButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FlurryWrapper.logEvent(TrackingEvents.UserClickedLoginButton);
                            LoginUtils.showLoginFragment(false);
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

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.cover_picture)
        ImageView cover;

        @InjectView(R.id.profile_image)
        CircleImageView profileImage;

        @InjectView(R.id.username)
        TextView username;

        @InjectView(R.id.email)
        TextView email;

        @InjectView(R.id.logout_button)
        ImageButton logoutButton;

        public HeaderViewHolder(View rootView) {
            super(rootView);
            ButterKnife.inject(this, rootView);
        }
    }

    static class HeaderLoggedOutViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.profile_image)
        CircleImageView profileImage;

        @InjectView(R.id.login_button)
        Button loginButton;

        public HeaderLoggedOutViewHolder(View rootView) {
            super(rootView);
            ButterKnife.inject(this, rootView);
        }
    }

    static class DividerViewHolder extends RecyclerView.ViewHolder {

        public DividerViewHolder(View rootView) {
            super(rootView);
        }
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.item)
        TextView itemTextView;

        @InjectView(R.id.iv_icon)
        ImageView itemImageView;

        public MenuViewHolder(View rootView) {
            super(rootView);
            ButterKnife.inject(this, rootView);
        }
    }

    static class SubMenuViewHolder extends MenuViewHolder {
        public SubMenuViewHolder(View rootView) {
            super(rootView);
        }
    }

    static class LabelViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.label)
        TextView label;

        public LabelViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }


}
