package com.zenlabs.sleepsounds.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

/**
 * Created by fedoro on 5/24/16.
 */
public class CustomPicker extends NumberPicker {
    public CustomPicker(Context context) {
        super(context);
    }

    public CustomPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        if(child instanceof EditText) {
            ((EditText) child).setTextSize(40);
        }
    }
}
