package com.apphunt.app.smart_rate.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.apphunt.app.R;
import com.apphunt.app.smart_rate.SmartRateConstants;

public class FeedbackFragment extends BaseRateFragment implements OnClickListener {

    private ActionBarActivity activity;
    private View view;
    private RelativeLayout smartRateLayout;
    private EditText message;
    private Button send;
    private Button dismiss;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.smartrate_feedback_title);
        super.setFragmentTag(SmartRateConstants.TAG_FEEDBACK_FRAGMENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_smartrate_feedback, container, false);

        initUI();

        return view;
    }

    private void initUI() {
        smartRateLayout = (RelativeLayout) view.findViewById(R.id.smartrate);

        message = (EditText) view.findViewById(R.id.smartrate_message);

        send = (Button) view.findViewById(R.id.send);
        send.setOnClickListener(this);

        dismiss = (Button) view.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(this);
    }

    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            Animation enterAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_in);
            enterAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Animation notificationEnterAnim = AnimationUtils.loadAnimation(activity,
                            R.anim.slide_in_top_notification);
                    notificationEnterAnim.setFillAfter(true);
                    smartRateLayout.startAnimation(notificationEnterAnim);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            return enterAnim;
        } else {
            Animation outAnim = AnimationUtils.loadAnimation(activity, R.anim.alpha_out);

            smartRateLayout.startAnimation(AnimationUtils.loadAnimation(activity,
                    R.anim.slide_out_top));

            return outAnim;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                if (message.getText() != null && message.getText().length() > 0) {
                    getOnSendClickListener().onSendClick(FeedbackFragment.this, message.getText().toString());

                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(message.getWindowToken(), 0);
                } else {
                    message.setHint(R.string.smartrate_rate_hint_feedback_please_enter_text);
                    message.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.shake));
                    Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(300);
                }
                break;

            case R.id.dismiss:
                activity.getSupportFragmentManager().popBackStack();
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = (ActionBarActivity) activity;
    }
}
