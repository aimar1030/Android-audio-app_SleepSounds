package com.zenlabs.sleepsounds.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.zenlabs.sleepsounds.R;

/**
 * Created by fedoro on 5/24/16.
 */
public class CustomTimerPicker extends RelativeLayout {

    CustomPicker hour_picker;
    CustomPicker minues_picker;
    CustomPicker zone_picker;

    String[] str_mins = new String[60];
    String[] str_hours = new String[12];
    String[] str_zone = {"AM", "PM"};

    public CustomTimerPicker(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.view_custom_timer_picker, this);
        initUI();
    }

    public CustomTimerPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.view_custom_timer_picker, this);
        initUI();
    }

    public CustomTimerPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.view_custom_timer_picker, this);
        initUI();
    }

    private void initUI()   {

        hour_picker = (CustomPicker) findViewById(R.id.timer_hour);
        minues_picker = (CustomPicker) findViewById(R.id.timer_minutes);
        zone_picker = (CustomPicker) findViewById(R.id.timer_zone);

        for (int i = 0; i < 60; i ++)   {

            str_mins[i] = i + "";
        }

        for (int i = 1; i < 13; i ++)   {
            str_hours[i-1] = i + "";
        }

        hour_picker.setMinValue(0);
        hour_picker.setMaxValue(11);
        hour_picker.setDisplayedValues(str_hours);

        minues_picker.setMinimumHeight(0);
        minues_picker.setMaxValue(59);
        minues_picker.setDisplayedValues(str_mins);

        zone_picker.setMinValue(0);
        zone_picker.setMaxValue(1);
        zone_picker.setDisplayedValues(str_zone);
    }

    public int hours()   {

        int hour_pos = hour_picker.getValue();
        String hour = str_hours[hour_pos];

        int zone_pos = zone_picker.getValue();

        int hours = 0;

        if (zone_pos == 0)  {
            hours = Integer.parseInt(hour);
            if (hours == 12)    {
                hours = 24;
            }
        } else {
            hours = Integer.parseInt(hour) + 12;

            if (hours == 24)
                hours = 12;
        }


        return hours;
    }

    public int minutes()    {
        int min_pos = minues_picker.getValue();
        String min = str_mins[min_pos];

        int mins = Integer.parseInt(min);

        return mins;
    }

    public void setTime(int hour, int minutes, int zone)    {

        hour_picker.setValue(hour - 1);
        minues_picker.setValue(minutes);
        zone_picker.setValue(zone);
    }
}
