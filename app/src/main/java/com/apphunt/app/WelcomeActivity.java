package com.apphunt.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.utils.FlurryWrapper;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 9/3/15.
 * *
 * * NaughtySpirit 2015
 */
public class WelcomeActivity extends Activity {
    private static final String TAG = WelcomeActivity.class.getSimpleName();

    private String senderIdStr;
    private String senderNameStr;
    private String senderAvatarStr;
    private String receiverNameStr;

    @InjectView(R.id.container)
    RelativeLayout container;

    @InjectView(R.id.sender_avatar)
    CircleImageView senderAvatar;

    @InjectView(R.id.sender_name)
    TextView senderName;

    @InjectView(R.id.welcome_message)
    TextView welcomeMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        Bundle bundle = getIntent().getExtras();
        this.senderIdStr = bundle.getString(Constants.KEY_SENDER_ID);
        this.senderNameStr = bundle.getString(Constants.KEY_SENDER_NAME);
        this.senderAvatarStr = bundle.getString(Constants.KEY_SENDER_PROFILE_IMAGE_URL);
        this.receiverNameStr = bundle.getString(Constants.KEY_RECEIVER_NAME);

        Map<String, String> params = new HashMap<>();
        params.put(Constants.KEY_SENDER_ID, senderIdStr);
        params.put(Constants.KEY_RECEIVER_NAME, receiverNameStr);
        FlurryWrapper.logEvent(TrackingEvents.UserViewedWelcomeScreen, params);

        initUI();
    }

    private void initUI() {
        ButterKnife.inject(this);

        Animation showAnim = AnimationUtils.loadAnimation(this, R.anim.vertical_flip);
        container.startAnimation(showAnim);

        Picasso.with(this)
                .load(senderAvatarStr)
                .into(senderAvatar);

        senderName.setText(senderNameStr);
        welcomeMessage.setText(String.format(getString(R.string.msg_welcome), !TextUtils.isEmpty(receiverNameStr) ? receiverNameStr : "friend"));
    }

    @OnClick(R.id.login)
    public void onLoginButtonClick() {
        setResult(Constants.SHOW_LOGIN);
        finish();
    }

    @OnClick(R.id.not_now)
    public void onContinueButtonClick() {
        finish();
    }

    @Override
    public void finish() {
        Animation slideOutRight = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
        slideOutRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                WelcomeActivity.super.finish();
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        container.startAnimation(slideOutRight);
    }
}
