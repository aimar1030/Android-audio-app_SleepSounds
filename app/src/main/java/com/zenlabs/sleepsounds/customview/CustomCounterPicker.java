package com.zenlabs.sleepsounds.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.zenlabs.sleepsounds.R;

/**
 * Created by fedoro on 5/24/16.
 */
public class CustomCounterPicker extends RelativeLayout{

    CustomPicker hour_picker, hour_char_picker, min_picker, min_char_picker;
    String[] hour_str, hour_char_str, min_str, min_char_str;

    public CustomCounterPicker(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.view_custom_counter_picker, this);
        initUI();
    }

    public CustomCounterPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.view_custom_counter_picker, this);
        initUI();
    }

    public CustomCounterPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.view_custom_counter_picker, this);
        initUI();
    }

    private void initUI()   {

        hour_picker = (CustomPicker) findViewById(R.id.counter_hour);
        hour_char_picker = (CustomPicker) findViewById(R.id.counter_hour_char);

        min_picker = (CustomPicker) findViewById(R.id.counter_minutes);
        min_char_picker = (CustomPicker) findViewById(R.id.counter_minutes_char);

        hour_str = new String[24];
        hour_char_str = new String[]{"hours"};
        min_str = new String[60];
        min_char_str = new String[]{"mins"};

        for (int i = 0; i < 24; i ++)   {

            hour_str[i] = i + "";
        }

        for (int i = 0; i < 60; i ++)   {

            min_str[i] = i + "";
        }

        hour_picker.setMinValue(0);
        hour_picker.setMaxValue(23);
        hour_picker.setDisplayedValues(hour_str);

        hour_char_picker.setMinValue(0);
        hour_char_picker.setMaxValue(0);
        hour_char_picker.setDisplayedValues(hour_char_str);

        min_picker.setMinValue(0);
        min_picker.setMaxValue(59);
        min_picker.setDisplayedValues(min_str);

        min_char_picker.setMinValue(0);
        min_char_picker.setMaxValue(0);
        min_char_picker.setDisplayedValues(min_char_str);
    }

    public int hours()  {

        int hour_position = hour_picker.getValue();
        String hours = hour_str[hour_position];
        return Integer.parseInt(hours);
    }

    public int minutes()    {

        int min_position = min_picker.getValue();
        String mins = min_str[min_position];

        return Integer.parseInt(mins);
    }

    public void setTime(int hour, int min)   {
        hour_picker.setValue(hour);
        min_picker.setValue(min);
    }
}
