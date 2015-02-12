package com.apphunt.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Created by nmp on 2/10/15.
 */
public class SplashActivity extends Activity {
    private Animation alphaOut, alphaIn;

    private static final int ANIM_LONG_DURATION = 800;

    private ImageView appHuntLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initUI();
        initAnimations();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                appHuntLogo.startAnimation(alphaIn);
            }
        }, 500);

    }

    private void initUI() {
        appHuntLogo = (ImageView) findViewById(R.id.apphunt_background);
    }

    private void initAnimations() {
        alphaIn = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
        alphaIn.setDuration(ANIM_LONG_DURATION);
        alphaOut = AnimationUtils.loadAnimation(this, R.anim.alpha_out);
        alphaOut.setDuration(ANIM_LONG_DURATION);

        alphaIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                appHuntLogo.setVisibility(View.VISIBLE);
                appHuntLogo.startAnimation(alphaOut);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        alphaOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                appHuntLogo.setVisibility(View.GONE);
                SplashActivity.this.setResult(RESULT_OK);
                SplashActivity.this.finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        setResult(RESULT_OK);
        super.onDestroy();
    }
}
