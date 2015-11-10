package com.apphunt.app.ui.views.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;

import com.apphunt.app.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookButtonBase;
import com.facebook.FacebookCallback;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.internal.LoginAuthorizationType;
import com.facebook.internal.Utility;
import com.facebook.login.DefaultAudience;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.ToolTipPopup;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/10/15.
 * *
 * * NaughtySpirit 2015
 */
public class CustomFacebookButton extends FacebookButtonBase {

    private HashMap<String, Typeface> typefaces;
    private static final int DEFAULT_REQUEST_CODE;
    private static final String TAG;
    private boolean confirmLogout;
    private String loginText;
    private String logoutText;
    private CustomFacebookButton.LoginButtonProperties properties = new CustomFacebookButton.LoginButtonProperties();
    private String loginLogoutEventName = "fb_login_view_usage";
    private boolean toolTipChecked;
    private ToolTipPopup.Style toolTipStyle;
    private CustomFacebookButton.ToolTipMode toolTipMode;
    private long toolTipDisplayTime;
    private ToolTipPopup toolTipPopup;
    private AccessTokenTracker accessTokenTracker;
    private LoginManager loginManager;

    public CustomFacebookButton(Context context) {
        super(context, null, 0, 0, "fb_login_button_create", DEFAULT_REQUEST_CODE);
        this.toolTipStyle = ToolTipPopup.Style.BLUE;
        this.toolTipDisplayTime = 6000L;

        init(null);
    }

    public CustomFacebookButton(Context context, AttributeSet attrs) {
        super(context, attrs, 0, 0, "fb_login_button_create", DEFAULT_REQUEST_CODE);
        this.toolTipStyle = ToolTipPopup.Style.BLUE;
        this.toolTipDisplayTime = 6000L;

        init(attrs);
    }

    public CustomFacebookButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle, 0, "fb_login_button_create", DEFAULT_REQUEST_CODE);
        this.toolTipStyle = ToolTipPopup.Style.BLUE;
        this.toolTipDisplayTime = 6000L;

        init(attrs);
    }

    public void setDefaultAudience(DefaultAudience defaultAudience) {
        this.properties.setDefaultAudience(defaultAudience);
    }

    public DefaultAudience getDefaultAudience() {
        return this.properties.getDefaultAudience();
    }

    public void setReadPermissions(List<String> permissions) {
        this.properties.setReadPermissions(permissions);
    }

    public void setReadPermissions(String... permissions) {
        this.properties.setReadPermissions(Arrays.asList(permissions));
    }

    public void setPublishPermissions(List<String> permissions) {
        this.properties.setPublishPermissions(permissions);
    }

    public void setPublishPermissions(String... permissions) {
        this.properties.setPublishPermissions(Arrays.asList(permissions));
    }

    public void clearPermissions() {
        this.properties.clearPermissions();
    }

    public void setLoginBehavior(LoginBehavior loginBehavior) {
        this.properties.setLoginBehavior(loginBehavior);
    }

    public LoginBehavior getLoginBehavior() {
        return this.properties.getLoginBehavior();
    }

    public void setToolTipStyle(ToolTipPopup.Style toolTipStyle) {
        this.toolTipStyle = toolTipStyle;
    }

    public void setToolTipMode(CustomFacebookButton.ToolTipMode toolTipMode) {
        this.toolTipMode = toolTipMode;
    }

    public CustomFacebookButton.ToolTipMode getToolTipMode() {
        return this.toolTipMode;
    }

    public void setToolTipDisplayTime(long displayTime) {
        this.toolTipDisplayTime = displayTime;
    }

    public long getToolTipDisplayTime() {
        return this.toolTipDisplayTime;
    }

    public void dismissToolTip() {
        if(this.toolTipPopup != null) {
            this.toolTipPopup.dismiss();
            this.toolTipPopup = null;
        }

    }

    public void registerCallback(CallbackManager callbackManager, FacebookCallback<LoginResult> callback) {
        this.getLoginManager().registerCallback(callbackManager, callback);
    }

    public void registerCallback(CallbackManager callbackManager, FacebookCallback<LoginResult> callback, int requestCode) {
        this.setRequestCode(requestCode);
        this.registerCallback(callbackManager, callback);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(this.accessTokenTracker != null && !this.accessTokenTracker.isTracking()) {
            this.accessTokenTracker.startTracking();
            this.setButtonText();
        }

    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!this.toolTipChecked && !this.isInEditMode()) {
            this.toolTipChecked = true;
//            this.checkToolTipSettings();
        }

    }

    private void showToolTipPerSettings(Utility.FetchedAppSettings settings) {
        if(settings != null && settings.getNuxEnabled() && this.getVisibility() == VISIBLE) {
            String toolTipString = settings.getNuxContent();
            this.displayToolTip(toolTipString);
        }

    }

    private void displayToolTip(String toolTipString) {
        this.toolTipPopup = new ToolTipPopup(toolTipString, this);
        this.toolTipPopup.setStyle(this.toolTipStyle);
        this.toolTipPopup.setNuxDisplayTime(this.toolTipDisplayTime);
        this.toolTipPopup.show();
    }

    private void checkToolTipSettings() {
//        switch(CustomFacebookButton.SyntheticClass_1.$SwitchMap$com$facebook$login$widget$CustomFacebookButton$ToolTipMode[this.toolTipMode.ordinal()]) {
//            case 1:
//                final String appId = Utility.getMetadataApplicationId(this.getContext());
//                FacebookSdk.getExecutor().execute(new Runnable() {
//                    public void run() {
//                        final Utility.FetchedAppSettings settings = Utility.queryAppSettings(appId, false);
//                        CustomFacebookButton.this.getActivity().runOnUiThread(new Runnable() {
//                            public void run() {
//                                CustomFacebookButton.this.showToolTipPerSettings(settings);
//                            }
//                        });
//                    }
//                });
//                break;
//            case 2:
//                String toolTipString = this.getResources().getString(com.facebook.R.string.com_facebook_tooltip_default);
//                this.displayToolTip(toolTipString);
//            case 3:
//        }

    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.setButtonText();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(this.accessTokenTracker != null) {
            this.accessTokenTracker.stopTracking();
        }

        this.dismissToolTip();
    }

    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if(visibility != VISIBLE) {
            this.dismissToolTip();
        }

    }

    List<String> getPermissions() {
        return this.properties.getPermissions();
    }

    void setProperties(CustomFacebookButton.LoginButtonProperties properties) {
        this.properties = properties;
    }

    protected void configureButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super.configureButton(context, attrs, defStyleAttr, defStyleRes);
        this.setInternalOnClickListener(new CustomFacebookButton.LoginClickListener());
        this.parseLoginButtonAttributes(context, attrs, defStyleAttr, defStyleRes);
        if(this.isInEditMode()) {
            this.setBackgroundColor(this.getResources().getColor(com.facebook.R.color.com_facebook_blue));
            this.loginText = "Log in with Facebook";
        } else {
            this.accessTokenTracker = new AccessTokenTracker() {
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                    CustomFacebookButton.this.setButtonText();
                }
            };
        }

        this.setButtonText();
    }

    protected int getDefaultStyleResource() {
        return com.facebook.R.style.com_facebook_loginview_default_style;
    }

    private void parseLoginButtonAttributes(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this.toolTipMode = CustomFacebookButton.ToolTipMode.DEFAULT;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, com.facebook.R.styleable.com_facebook_login_view, defStyleAttr, defStyleRes);

        try {
            this.confirmLogout = a.getBoolean(com.facebook.R.styleable.com_facebook_login_view_com_facebook_confirm_logout, true);
            this.loginText = a.getString(com.facebook.R.styleable.com_facebook_login_view_com_facebook_login_text);
            this.logoutText = a.getString(com.facebook.R.styleable.com_facebook_login_view_com_facebook_logout_text);
            this.toolTipMode = CustomFacebookButton.ToolTipMode.fromInt(a.getInt(com.facebook.R.styleable.com_facebook_login_view_com_facebook_tooltip_mode, CustomFacebookButton.ToolTipMode.DEFAULT.getValue()));
        } finally {
            a.recycle();
        }

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Resources res = getResources();

        float density = res.getDisplayMetrics().density;
        int height = (int) (48 * density);
        int width = (int) (280 * density);
        setMeasuredDimension(width, height);
    }

    private int measureButtonWidth(String text) {
        int textWidth = this.measureTextWidth(text);
        int width = this.getCompoundPaddingLeft() + this.getCompoundDrawablePadding() + textWidth + this.getCompoundPaddingRight();
        return width;
    }

    private void setButtonText() {
        this.setText("Facebook");
    }

    LoginManager getLoginManager() {
        if(this.loginManager == null) {
            this.loginManager = LoginManager.getInstance();
        }

        return this.loginManager;
    }

    void setLoginManager(LoginManager loginManager) {
        this.loginManager = loginManager;
    }

    static {
        DEFAULT_REQUEST_CODE = CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode();
        TAG = CustomFacebookButton.class.getName();
    }

    private class LoginClickListener implements OnClickListener {
        private LoginClickListener() {
        }

        public void onClick(View v) {
            Context context = CustomFacebookButton.this.getContext();
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            if(accessToken != null) {
                if(CustomFacebookButton.this.confirmLogout) {
                    String logger = CustomFacebookButton.this.getResources().getString(com.facebook.R.string.com_facebook_loginview_log_out_action);
                    String parameters = CustomFacebookButton.this.getResources().getString(com.facebook.R.string.com_facebook_loginview_cancel_action);
                    Profile profile = Profile.getCurrentProfile();
                    String message;
                    if(profile != null && profile.getName() != null) {
                        message = String.format(CustomFacebookButton.this.getResources().getString(com.facebook.R.string.com_facebook_loginview_logged_in_as), profile.getName());
                    } else {
                        message = CustomFacebookButton.this.getResources().getString(com.facebook.R.string.com_facebook_loginview_logged_in_using_facebook);
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(message).setCancelable(true).setPositiveButton(logger, new android.content.DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            CustomFacebookButton.this.getLoginManager().logOut();
                        }
                    }).setNegativeButton(parameters, null);
                    builder.create().show();
                } else {
                    CustomFacebookButton.this.getLoginManager().logOut();
                }
            } else {
                LoginManager logger1 = CustomFacebookButton.this.getLoginManager();
                logger1.setDefaultAudience(CustomFacebookButton.this.getDefaultAudience());
                logger1.setLoginBehavior(CustomFacebookButton.this.getLoginBehavior());
                if(LoginAuthorizationType.PUBLISH.equals(CustomFacebookButton.this.properties.authorizationType)) {
                    if(CustomFacebookButton.this.getFragment() != null) {
                        logger1.logInWithPublishPermissions(CustomFacebookButton.this.getFragment(), CustomFacebookButton.this.properties.permissions);
                    } else {
                        logger1.logInWithPublishPermissions(CustomFacebookButton.this.getActivity(), CustomFacebookButton.this.properties.permissions);
                    }
                } else if(CustomFacebookButton.this.getFragment() != null) {
                    logger1.logInWithReadPermissions(CustomFacebookButton.this.getFragment(), CustomFacebookButton.this.properties.permissions);
                } else {
                    logger1.logInWithReadPermissions(CustomFacebookButton.this.getActivity(), CustomFacebookButton.this.properties.permissions);
                }
            }

            AppEventsLogger logger2 = AppEventsLogger.newLogger(CustomFacebookButton.this.getContext());
            Bundle parameters1 = new Bundle();
            parameters1.putInt("logging_in", accessToken != null?0:1);
            logger2.logSdkEvent(CustomFacebookButton.this.loginLogoutEventName, null, parameters1);
            CustomFacebookButton.this.callExternalOnClickListener(v);
        }
    }

    static class LoginButtonProperties {
        private DefaultAudience defaultAudience;
        private List<String> permissions;
        private LoginAuthorizationType authorizationType;
        private LoginBehavior loginBehavior;

        LoginButtonProperties() {
            this.defaultAudience = DefaultAudience.FRIENDS;
            this.permissions = Collections.emptyList();
            this.authorizationType = null;
            this.loginBehavior = LoginBehavior.SSO_WITH_FALLBACK;
        }

        public void setDefaultAudience(DefaultAudience defaultAudience) {
            this.defaultAudience = defaultAudience;
        }

        public DefaultAudience getDefaultAudience() {
            return this.defaultAudience;
        }

        public void setReadPermissions(List<String> permissions) {
            if(LoginAuthorizationType.PUBLISH.equals(this.authorizationType)) {
                throw new UnsupportedOperationException("Cannot call setReadPermissions after setPublishPermissions has been called.");
            } else {
                this.permissions = permissions;
                this.authorizationType = LoginAuthorizationType.READ;
            }
        }

        public void setPublishPermissions(List<String> permissions) {
            if(LoginAuthorizationType.READ.equals(this.authorizationType)) {
                throw new UnsupportedOperationException("Cannot call setPublishPermissions after setReadPermissions has been called.");
            } else if(Utility.isNullOrEmpty(permissions)) {
                throw new IllegalArgumentException("Permissions for publish actions cannot be null or empty.");
            } else {
                this.permissions = permissions;
                this.authorizationType = LoginAuthorizationType.PUBLISH;
            }
        }

        List<String> getPermissions() {
            return this.permissions;
        }

        public void clearPermissions() {
            this.permissions = null;
            this.authorizationType = null;
        }

        public void setLoginBehavior(LoginBehavior loginBehavior) {
            this.loginBehavior = loginBehavior;
        }

        public LoginBehavior getLoginBehavior() {
            return this.loginBehavior;
        }
    }

    public enum ToolTipMode {
        AUTOMATIC("automatic", 0),
        DISPLAY_ALWAYS("display_always", 1),
        NEVER_DISPLAY("never_display", 2);

        public static CustomFacebookButton.ToolTipMode DEFAULT;
        private String stringValue;
        private int intValue;

        public static CustomFacebookButton.ToolTipMode fromInt(int enumValue) {
            CustomFacebookButton.ToolTipMode[] arr$ = values();
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                CustomFacebookButton.ToolTipMode mode = arr$[i$];
                if(mode.getValue() == enumValue) {
                    return mode;
                }
            }

            return null;
        }

        ToolTipMode(String stringValue, int value) {
            this.stringValue = stringValue;
            this.intValue = value;
        }

        public String toString() {
            return this.stringValue;
        }

        public int getValue() {
            return this.intValue;
        }

        static {
            DEFAULT = AUTOMATIC;
        }
    }

    private void init(AttributeSet attrs) {
        if (typefaces == null) {
            typefaces = new HashMap<String, Typeface>();
        }

        // prevent exception in Android Studio / ADT interface builder
        if (this.isInEditMode()) {
            return;
        }

        final TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.AHTextView);
        if (array != null) {
            final String typefaceAssetPath = array.getString(
                    R.styleable.AHTextView_typefacePath);

            if (typefaceAssetPath != null) {
                Typeface typeface = null;

                if (typefaces.containsKey(typefaceAssetPath)) {
                    typeface = typefaces.get(typefaceAssetPath);
                } else {
                    AssetManager assets = getContext().getAssets();
                    typeface = Typeface.createFromAsset(assets, typefaceAssetPath);
                    typefaces.put(typefaceAssetPath, typeface);
                }

                setTypeface(typeface);
            }
            array.recycle();
        }

        Resources res = this.getResources();

        setBackgroundResource(R.drawable.shape_provider_background);
        setCompoundDrawablesWithIntrinsicBounds(null, null, res.getDrawable(R.drawable.ic_facebook), null);
        setPadding(60, 2, 8, 2);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        this.loginText = "Facebook";
    }
}
