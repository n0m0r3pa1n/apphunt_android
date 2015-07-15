package com.apphunt.app.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apphunt.app.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nmp on 15-6-9.
 */
public class AlertView extends LinearLayout {
    private LayoutInflater inflater;
    @InjectView(R.id.text)
    TextView textView;
    private TypedArray array;


    public AlertView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    public AlertView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AlertView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    public void init(Context context, AttributeSet attrs) {
        View view = getLayoutInflater().inflate(R.layout.view_alert, this, true);
        ButterKnife.inject(this, view);

        array = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.AlertView, 0, 0);

        String text = array.getString(R.styleable.AlertView_text);
        textView.setText(text);
    }

    protected LayoutInflater getLayoutInflater() {
        if(inflater == null) {
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        return inflater;
    }
}
