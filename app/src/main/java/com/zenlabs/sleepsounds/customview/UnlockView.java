package com.zenlabs.sleepsounds.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zenlabs.sleepsounds.R;

/**
 * Created by fedoro on 5/20/16.
 */
public class UnlockView extends LinearLayout {
    public UnlockView(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.view_unlock, this);
        initViews(context);
    }

    public UnlockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.view_unlock, this);
        initViews(context);
    }

    public UnlockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.view_unlock, this);
        initViews(context);
    }

    private void initViews(Context context) {
        TextView title = (TextView) findViewById(R.id.unlock_title);
        TextView bt_title = (TextView) findViewById(R.id.unlock_bt_title);

        Typeface custom_font = Typeface.createFromAsset(getContext().getAssets(), "fonts/AvenirLTStd-Light.otf");
        title.setTypeface(custom_font);
        bt_title.setTypeface(custom_font);
    }
}
