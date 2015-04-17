package com.apphunt.app.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
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
import com.apphunt.app.utils.Constants;
import com.apphunt.app.utils.TrackingEvents;
import com.flurry.android.FlurryAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 2/20/15.
 */
public class SuggestFragment extends BaseFragment implements View.OnClickListener {

    private RelativeLayout suggestionLayout;
    private EditText message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.suggest_title);
        FlurryAgent.logEvent(TrackingEvents.UserViewedSuggestion);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suggest, container, false);
        suggestionLayout = (RelativeLayout) view.findViewById(R.id.suggestion_layout);
        message = (EditText) view.findViewById(R.id.suggestion_message);

        view.findViewById(R.id.suggestion_send).setOnClickListener(this);
        view.findViewById(R.id.suggestion_dismiss).setOnClickListener(this);
        return view;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            Animation enterAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_in);
            enterAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Animation notificationEnterAnim = AnimationUtils.loadAnimation(getActivity(),
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
            Animation outAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_out);
            suggestionLayout.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                    R.anim.slide_out_top));

            return outAnim;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.suggestion_send:
                Editable messageText = message.getText();
                if (TextUtils.isEmpty(messageText)) {
                    message.setHint(R.string.suggestion_empty_text_error);
                    message.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake));
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(300);
                } else {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(message.getWindowToken(), 0);
                    getSupportFragmentManager().popBackStack(Constants.TAG_SUGGEST_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    Map<String, String> params = new HashMap<>();
                    params.put("suggestion", messageText.toString());
                    FlurryAgent.logEvent(TrackingEvents.UserMadeSuggestion, params);
                    Toast.makeText(getActivity(), R.string.feedback_send, Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.suggestion_dismiss:
                getSupportFragmentManager().popBackStack();
                break;
        }
    }

    private FragmentManager getSupportFragmentManager() {
        return getActivity().getSupportFragmentManager();
    }
}
