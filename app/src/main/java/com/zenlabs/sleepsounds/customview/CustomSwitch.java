package com.zenlabs.sleepsounds.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zenlabs.sleepsounds.R;

public class CustomSwitch extends RelativeLayout implements View.OnTouchListener {

    private boolean isChecked;
    ImageView switch_bt;
    RelativeLayout switch_track;
    boolean isTapped = false;
    int position;

    float prev_x;

    CustomSwitchHandler handler;

    public interface CustomSwitchHandler    {

        public void changeSwitch(boolean status, int position);
    }

    public CustomSwitch(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.view_switch, this);
        initUI(context);
    }

    public CustomSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.view_switch, this);
        initUI(context);
    }

    public CustomSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.view_switch, this);
        initUI(context);
    }

    public void addHandler(CustomSwitchHandler handler, int position) {

        this.handler = handler;
        this.position = position;
    }

    private void changeStatus(boolean status) {

        if (handler == null)
            return;
        handler.changeSwitch(status, position);
    }

    private void initUI(Context context)    {

        switch_bt = (ImageView) findViewById(R.id.switch_bt);
        switch_track = (RelativeLayout) findViewById(R.id.switch_track);
        switch_track.setOnTouchListener(this);
        switch_bt.setOnTouchListener(this);

        updateUI();
    }

    public void updateUI()    {

        if (!isChecked)  {

            switch_track.setBackgroundResource(R.drawable.switch_off_back);
            switch_bt.setImageResource(R.drawable.switch_off);
            int valueInPixels = (int) getResources().getDimension(R.dimen.switch_bt_height);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(valueInPixels, valueInPixels);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.leftMargin = 7;
            switch_bt.setLayoutParams(params);
        } else {

            switch_bt.setImageResource(R.drawable.switch_on);
            switch_track.setBackgroundResource(R.drawable.switch_on_back);

            int valueInPixels = (int) getResources().getDimension(R.dimen.switch_bt_height);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(valueInPixels, valueInPixels);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.rightMargin = 7;
            switch_bt.setLayoutParams(params);
        }
    }

    public boolean getStatus()  {

        return isChecked;
    }

    public void setStatus(boolean status) {

        isChecked = status;

        updateUI();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (v == switch_bt || v == switch_track) {

            switch (event.getAction())  {

                case MotionEvent.ACTION_DOWN:
                    isTapped = true;
                    touchStart();
                    break;
                case MotionEvent.ACTION_MOVE:
                    moveSwitchButton(event.getX());
                    break;
                case MotionEvent.ACTION_UP:
                    touchEnded(event.getRawX());
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    private void touchStart()   {
        if (isChecked)
            prev_x = switch_bt.getX();
        else
            prev_x = 0;
    }

    private void moveSwitchButton(float fx)   {

        float difference = fx - prev_x;

        if (isTapped) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) switch_bt.getLayoutParams();

            if (difference > 10 || difference * (-1) > 10) {

                if (params.leftMargin + difference < switch_track.getWidth() - switch_bt.getWidth() && params.leftMargin + difference > switch_bt.getWidth())
                 params.leftMargin = (int) (params.leftMargin + difference);
            }

            switch_bt.setLayoutParams(params);

            prev_x  = fx;
        }
    }

    private void touchEnded(float fx)   {

        if (isTapped) {

            isTapped = false;
            setStatus(!isChecked);
            changeStatus(isChecked);
        }
    }
}
