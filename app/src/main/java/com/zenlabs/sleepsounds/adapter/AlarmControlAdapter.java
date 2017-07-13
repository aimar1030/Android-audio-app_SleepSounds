package com.zenlabs.sleepsounds.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.activity.MainActivity;
import com.zenlabs.sleepsounds.customview.AlarmTextView;
import com.zenlabs.sleepsounds.customview.CustomSwitch;
import com.zenlabs.sleepsounds.model.MAlarm;
import com.zenlabs.sleepsounds.sqlite.AlarmDBHelper;
import com.zenlabs.sleepsounds.utils.Common;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fedoro on 5/25/16.
 */
public class AlarmControlAdapter extends BaseAdapter implements CustomSwitch.CustomSwitchHandler {

    public interface AlarmControlHandler  {

        public void dataChanged();
    }
    Context context;
    AlarmControlHandler handler;

    public AlarmControlAdapter(Context context, AlarmControlHandler handler)    {
        this.context = context;
        this.handler = handler;
    }
    @Override
    public int getCount() {
        return Common.alarms.size();
    }

    @Override
    public Object getItem(int position) {
        return Common.alarms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater vi;
        vi = LayoutInflater.from(context);
        convertView = vi.inflate(R.layout.view_alarm_cell, null);

        ImageView alarm_unique_ic = (ImageView) convertView.findViewById(R.id.alarm_unique_ic);
        TextView alarm_unique_lb = (TextView) convertView.findViewById(R.id.alarm_unique_lb);
        Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/MyriadPro-Light.otf");
        alarm_unique_lb.setTypeface(custom_font);
        final AlarmTextView timer_lb = (AlarmTextView) convertView.findViewById(R.id.timer_lb);

        ImageView remove_bt = (ImageView) convertView.findViewById(R.id.remove_bt);

        remove_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timer_lb.stopAlarms();
                timer_lb.stopTimer();

                AlarmDBHelper dbHelper = new AlarmDBHelper(context);
                dbHelper.delete_model(Common.alarms.get(position));

                Common.alarms.remove(position);
                handler.dataChanged();
            }
        });

        CustomSwitch alarmSwitch = (CustomSwitch) convertView.findViewById(R.id.alarm_switch);
        alarmSwitch.addHandler(this, position);

        MAlarm alarm = Common.alarms.get(position);

        if (alarm.isEditing)
        {
            remove_bt.setVisibility(View.VISIBLE);
            alarmSwitch.setVisibility(View.GONE);
        }
        else {
            remove_bt.setVisibility(View.GONE);
            alarmSwitch.setVisibility(View.VISIBLE);
        }

        if (!alarm.isTimer)  {
            alarm_unique_ic.setImageResource(R.drawable.b_alarm);
            alarm_unique_lb.setText("Alarm");
        } else {
            alarm_unique_ic.setImageResource(R.drawable.b_volume_off);
            alarm_unique_lb.setText("Stop Sounds");
        }

        if (alarm.status == 2) {
            if (alarm.isSnooze && alarm.isCounter) {
                Common.alarms.get(position).status = 1;
                alarm.status = 1;
            }
            else
                alarmSwitch.setStatus(alarm.isSnooze);
        } else if (alarm.status == 0)  {
            alarmSwitch.setStatus(false);
        } else if (alarm.status == 1 && alarm.fire_seconds > 1)
            alarmSwitch.setStatus(true);

        AlarmDBHelper dbHelper = new AlarmDBHelper(context);
        dbHelper.update_model(Common.alarms.get(position));

        if (Common.isGoHomeView)
            timer_lb.stopTimer();
        else
            timer_lb.updateUI(alarm);

        return convertView;
    }

    @Override
    public void changeSwitch(boolean status, int position) {

        if (status)
            Common.alarms.get(position).status = 1;
        else
            Common.alarms.get(position).status = 0;

        AlarmDBHelper dbHelper = new AlarmDBHelper(context);
        dbHelper.update_model(Common.alarms.get(position));

        handler.dataChanged();
    }
}
