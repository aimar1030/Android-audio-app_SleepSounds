package com.zenlabs.sleepsounds.fragment;

import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.activity.MainActivity;
import com.zenlabs.sleepsounds.adapter.OnSwipeTouchListener;
import com.zenlabs.sleepsounds.utils.UtilsMethods;

import java.util.Calendar;

/**
 * Created by fedoro on 5/31/16.
 */
public class ClockFragment extends Fragment  {

    private OnSwipeTouchListener detector;
    RelativeLayout touchView;
    ImageView tipImageView;
    ImageView background_1;
    ImageView background_2;
    TextView timer_text;
    boolean isDefault = true;
    Typeface custom_font1, custom_font2;
    final static float TOP_MARGIN_FOR_TIP = 200.0f;
    final static float TOP_MARGIN_FOR_TIMER = 2000.0f;
    RelativeLayout tip_layout, timer_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_clock, container, false);
        timer_text = (TextView) rootView.findViewById(R.id.current_time);
        tipImageView = (ImageView) rootView.findViewById(R.id.tip_imageview);
        background_1 = (ImageView) rootView.findViewById(R.id.background_clock1);
        background_2 = (ImageView) rootView.findViewById(R.id.background_clock2);
        touchView = (RelativeLayout) rootView.findViewById(R.id.touchView);

        custom_font1 = Typeface.createFromAsset(getContext().getAssets(), "fonts/DigitalDreamFat.otf");
        custom_font2 = Typeface.createFromAsset(getContext().getAssets(), "fonts/DIN-Light.otf");

        tip_layout = (RelativeLayout)rootView.findViewById(R.id.tip_layout);
        timer_layout = (RelativeLayout)rootView.findViewById(R.id.timer_layout);

        timer_text.setTypeface(custom_font1);

        timer_text.setText(showCurrentTime());

        if (UtilsMethods.isTablet(getContext()))    {

            timer_text.setTextSize(UtilsMethods.dpToPx(getContext(), 80));
            RelativeLayout.LayoutParams tip_img_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            tip_img_params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            tipImageView.setLayoutParams(tip_img_params);
            RelativeLayout.LayoutParams tip_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            tip_params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            tip_layout.setLayoutParams(tip_params);
            tip_layout.setY(TOP_MARGIN_FOR_TIP);

            RelativeLayout.LayoutParams timer_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            timer_params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            timer_layout.setLayoutParams(timer_params);

            timer_layout.setY(TOP_MARGIN_FOR_TIMER);
        }

        touchView.setOnTouchListener(new OnSwipeTouchListener(getContext()) {

            public void onSwipeTop() {
                float currentAlpha = touchView.getAlpha();

                if (currentAlpha > 0.2) {
                    ObjectAnimator.ofFloat(touchView, "alpha", currentAlpha, currentAlpha - 0.2f)
                            .setDuration(300)
                            .start();
                }
            }
            public void onSwipeRight() {
                changeBackground();
            }
            public void onSwipeLeft() {
                changeBackground();
            }
            public void onSwipeBottom() {
                float currentAlpha = touchView.getAlpha();
                if (currentAlpha < 0.8)   {
                    ObjectAnimator.ofFloat(touchView, "alpha", currentAlpha, currentAlpha + 0.2f)
                            .setDuration(300)
                            .start();
                }
            }
            public void onSingleTap()   {
                backAction();
            }
        });

        return rootView;
    }

    private void changeBackground()  {

        int i = 1;

        isDefault = !isDefault;

        if (isDefault)
            i = i * (-1);

        float value1 = (1 + i)/2;
        float value2 = (1 - i)/2;

        ObjectAnimator.ofFloat(background_1, "alpha", value1, value2)
                .setDuration(600)
                .start();

        ObjectAnimator.ofFloat(background_2, "alpha", value2, value1)
                .setDuration(600)
                .start();

        changeTextPosition();
    }

    private String showCurrentTime()  {

        Calendar c = Calendar.getInstance();
        int minutes = c.get(Calendar.MINUTE);
        int hours = c.get(Calendar.HOUR);
        int zone = c.get(Calendar.AM_PM);

        if (zone > 0)
            hours = hours + 12;

        String minutes_st = minutes + "";
        if (minutes < 10)
            minutes_st = "0" + minutes_st;

        String hours_st = hours + "";
        if (hours < 10)
            hours_st = "0" + hours_st;

        return hours_st + ":" + minutes_st;
    }

    public void backAction()    {

        ((MainActivity) getActivity()).backAction();
    }

    public void changeTextPosition()    {

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        int timer_size = timer_text.getHeight();
        int tip_height = tipImageView.getHeight();

        if (!isDefault) {
            if (UtilsMethods.isTablet(getContext())){
                int tip_moving_height = (int) (TOP_MARGIN_FOR_TIMER - TOP_MARGIN_FOR_TIP + timer_size);
                int timer_moving_height = (int) (height/2 - tip_height);

                changePosition(tip_layout, TOP_MARGIN_FOR_TIP, tip_moving_height);
                changePosition(timer_layout, TOP_MARGIN_FOR_TIMER, timer_moving_height);
            } else {
                int tip_moving_height = UtilsMethods.dpToPx(getContext(), 453) - UtilsMethods.dpToPx(getContext(), 25);
                int timer_moving_height = UtilsMethods.dpToPx(getContext(), 453) - height / 2 + UtilsMethods.dpToPx(getContext(), 90);

                changePosition(tip_layout, 0, tip_moving_height);
                changePosition(timer_layout, 0, 0 - timer_moving_height);
            }
            timer_text.setTypeface(custom_font2);

        } else {

            if (UtilsMethods.isTablet(getContext()))    {
                int tip_moving_height = (int) (TOP_MARGIN_FOR_TIMER - TOP_MARGIN_FOR_TIP + UtilsMethods.dpToPx(getContext(), 106));
                int timer_moving_height = (int) (height/2 - UtilsMethods.dpToPx(getContext(), 90));
                changePosition(tip_layout, tip_moving_height, TOP_MARGIN_FOR_TIP);
                changePosition(timer_layout, timer_moving_height, TOP_MARGIN_FOR_TIMER);
            } else {
                int tip_moving_height = UtilsMethods.dpToPx(getContext(), 453) - UtilsMethods.dpToPx(getContext(), 25) - UtilsMethods.dpToPx(getContext(), 106);
                int timer_moving_height = UtilsMethods.dpToPx(getContext(), 453) - height / 2 + UtilsMethods.dpToPx(getContext(), 90);

                changePosition(tip_layout, tip_moving_height, 0);
                changePosition(timer_layout, 0 - timer_moving_height, 0);
            }
            timer_text.setTypeface(custom_font1);
        }
    }

    public void changePosition(View view, float fy, float ty)    {
        ObjectAnimator objectAnimator
                = ObjectAnimator.ofFloat(view, "translationY", fy, ty);
        objectAnimator.setDuration(600);
        objectAnimator.start();
    }
}
