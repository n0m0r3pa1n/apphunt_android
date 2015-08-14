package com.apphunt.app.ui.fragments.notification;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.apphunt.app.R;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.utils.SoundsUtils;
import com.apphunt.app.utils.ui.ActionBarUtils;
import com.flurry.android.FlurryAgent;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 2/20/15.
 */
public class SuggestFragment extends BaseFragment {

    private AppCompatActivity activity;

    @InjectView(R.id.suggestion_layout)
    RelativeLayout suggestionLayout;

    @InjectView(R.id.suggestion_message)
    EditText message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlurryAgent.logEvent(TrackingEvents.UserViewedSuggestion);
        ActionBarUtils.getInstance().hideActionBarShadow();

        setFragmentTag(Constants.TAG_SUGGEST_FRAGMENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suggest, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            Animation enterAnim = AnimationUtils.loadAnimation(activity, R.anim.alpha_in);
            enterAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Animation notificationEnterAnim = AnimationUtils.loadAnimation(activity,
                            R.anim.slide_in_top_notification);
                    notificationEnterAnim.setFillAfter(true);
                    suggestionLayout.startAnimation(notificationEnterAnim);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            return enterAnim;
        } else {
            Animation slideOutAnim = AnimationUtils.loadAnimation(activity,
                    R.anim.slide_out_top);
            slideOutAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    closeKeyboard(message);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            suggestionLayout.startAnimation(slideOutAnim);

            return AnimationUtils.loadAnimation(activity, R.anim.alpha_out);
        }
    }

    @OnClick(R.id.suggestion_send)
    public void onSendBtnClick() {
        Editable messageText = message.getText();
        if (TextUtils.isEmpty(messageText)) {
            message.setHint(R.string.suggestion_empty_text_error);
            message.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.shake));

            SoundsUtils.vibrate(activity);
        } else {
            Map<String, String> params = new HashMap<>();
            params.put("suggestion", messageText.toString());
            FlurryAgent.logEvent(TrackingEvents.UserMadeSuggestion, params);
            Toast.makeText(activity, R.string.feedback_send, Toast.LENGTH_LONG).show();

            activity.getSupportFragmentManager().popBackStack();
        }

        closeKeyboard(message);
    }

    @OnClick(R.id.suggestion_dismiss)
    public void onDismissBtnClick() {
        activity.getSupportFragmentManager().popBackStack();
    }

    @Override
    public int getTitle() {
        return R.string.suggest_title;
    }

    private void closeKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (AppCompatActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ActionBarUtils.getInstance().showActionBarShadow();
    }
}
