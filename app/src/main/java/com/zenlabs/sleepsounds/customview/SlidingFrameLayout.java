package com.zenlabs.sleepsounds.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * Created by fedoro on 5/24/16.
 */
public class SlidingFrameLayout extends FrameLayout
{
    private static final String TAG = SlidingFrameLayout.class.getName();

    public SlidingFrameLayout(Context context) {
        super(context);
    }

    public SlidingFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public float getXFraction()
    {
        int width = ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        return (width == 0) ? 0 : getX() / (float) width;
    }

    public void setXFraction(float xFraction) {
        int width = ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        setX((width > 0) ? (xFraction * width) : 0);
    }
}

