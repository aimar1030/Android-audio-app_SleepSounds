package com.zenlabs.sleepsounds.fragment;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.activity.MainActivity;
import com.zenlabs.sleepsounds.customview.CustomCounterPicker;
import com.zenlabs.sleepsounds.customview.CustomSwitch;
import com.zenlabs.sleepsounds.customview.CustomTimerPicker;
import com.zenlabs.sleepsounds.model.MAlarm;
import com.zenlabs.sleepsounds.model.MFavorit;
import com.zenlabs.sleepsounds.sqlite.AlarmDBHelper;
import com.zenlabs.sleepsounds.utils.Common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by fedoro on 5/24/16.
 */
public class AlarmViewFragment extends Fragment {

    TableRow soundRow, fadeInRow, fadeOutRow, snoozeRow, powerRow;
    ImageView check_bt, close_bt;
    TextView sound_sub_title, fade_in_sub_title, fade_out_sub_title;
    TextView title;
    CustomSwitch snoozeSwitch, powerSwitch;
    RelativeLayout pickView;
    RelativeLayout select_counter, select_timer;

    CustomTimerPicker timerPicker;
    CustomCounterPicker counterPicker;

    boolean isClockable = true;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_alarm, container, false);

        snoozeRow = (TableRow) rootView.findViewById(R.id.snooze_row);
        fadeInRow = (TableRow) rootView.findViewById(R.id.fade_in_row);
        fadeOutRow = (TableRow) rootView.findViewById(R.id.fade_out_row);
        powerRow = (TableRow) rootView.findViewById(R.id.power_row);
        soundRow = (TableRow) rootView.findViewById(R.id.sound_row);

        check_bt = (ImageView) rootView.findViewById(R.id.check_bt);
        close_bt = (ImageView) rootView.findViewById(R.id.close_alarm_view_bt);

        sound_sub_title = (TextView) rootView.findViewById(R.id.sub_title_sound);
        fade_in_sub_title = (TextView) rootView.findViewById(R.id.sub_title_fade_in);
        fade_out_sub_title = (TextView) rootView.findViewById(R.id.sub_title_fade_out);

        title = (TextView) rootView.findViewById(R.id.alarm_view_title);

        snoozeSwitch = (CustomSwitch) rootView.findViewById(R.id.snooze_switch);
        powerSwitch = (CustomSwitch) rootView.findViewById(R.id.exit_switch);

        timerPicker = (CustomTimerPicker) rootView.findViewById(R.id.picker_timer);
        counterPicker = (CustomCounterPicker) rootView.findViewById(R.id.picker_counter);

        pickView = (RelativeLayout) rootView.findViewById(R.id.picker_view);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        int height = (int) getContext().getResources().getDimension(R.dimen.picker_view_height);
        params.setMargins(0, height, 0, 0-height);
        pickView.setLayoutParams(params);


        select_counter = (RelativeLayout) rootView.findViewById(R.id.counter_picker);
        select_timer = (RelativeLayout) rootView.findViewById(R.id.clock_picker);

        if (Common.currentSelectedAlarm != -1)  {

            MAlarm alarm = Common.alarms.get(Common.currentSelectedAlarm);

            for (int i = 0; i < Common.favorits.size(); i ++)   {

                MFavorit favorit = Common.favorits.get(i);

                if (favorit.uniqueID.equals(alarm.category_id)) {
                    Common.currentSelectedSound = i;
                    break;
                }
            }

            for (int i = 0; i < Common.data_value.length; i ++)  {

                if (alarm.fade_in_time == Common.data_value[i])  {
                    Common.currentSelectedFadeIn = i;
                    break;
                }
            }

            snoozeSwitch.setStatus(alarm.isSnooze);
        }

        select_counter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClockable = false;
                updatePicker();
            }
        });

        select_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClockable = true;
                updatePicker();
            }
        });

        close_bt.setColorFilter(Color.WHITE);
        check_bt.setColorFilter(Color.WHITE);

        close_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).backAlarmTimerView();
            }
        });

        fadeInRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).showFadeInView();
            }
        });

        fadeOutRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).showFadeInView();
            }
        });

        check_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                MAlarm alarm = new MAlarm();
                if (Common.currentSelectedAlarm != -1)
                    alarm = Common.alarms.get(Common.currentSelectedAlarm);
                else
                    alarm.status = 1;
                if (Common.currentSelectedFadeIn == 0)
                    alarm.fade_in_time = 0;
                else
                    alarm.fade_in_time = Common.data_value[Common.currentSelectedFadeIn];

                if (!isClockable) {
                    alarm.isCounter = true;
                    alarm.hours_fire = counterPicker.hours();
                    alarm.minutes_fire = counterPicker.minutes();

                    alarm.fire_seconds = counterPicker.hours() * 3600 + counterPicker.minutes() * 60;
                } else {

                    alarm.isCounter = false;
                    alarm.minutes_fire = timerPicker.minutes();
                    alarm.hours_fire = timerPicker.hours();

                    final DateFormat df = new SimpleDateFormat("dd MMM yyyy");
                    final Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                            timerPicker.hours(), timerPicker.minutes(), 0);
                    long endtime = calendar.getTimeInMillis()/1000;
                    long starttime = System.currentTimeMillis()/1000;

                    alarm.fire_seconds = endtime - starttime;
                }

                if (Common.currentSelection == Common.SELECT_ALARM) {
                    alarm.isTimer = false;
                    alarm.isSnooze = snoozeSwitch.getStatus();

                    if (Common.currentSelectedSound > -1)
                    alarm.category_id = Common.favorits.get(Common.currentSelectedSound + 1).uniqueID;

                } else {
                    alarm.isTimer = true;
                    alarm.isPower = powerSwitch.getStatus();
                }

                if (Common.alarms == null)  {
                    Common.alarms = new ArrayList<MAlarm>();
                }

                if (Common.currentSelectedAlarm == -1) {
                    alarm.unique_id = Common.alarms.size() + "";

                    AlarmDBHelper dbHelper = new AlarmDBHelper(getContext());
                    dbHelper.addAlarm(alarm);

                    Common.alarms.add(alarm);
                } else {
                    Common.alarms.set(Common.currentSelectedAlarm, alarm);
                    AlarmDBHelper dbHelper = new AlarmDBHelper(getContext());
                    dbHelper.update_model(alarm);
                }

                Common.currentSelectedFadeIn = 2;
                Common.currentSelectedSound = -1;
                Common.currentSelectedAlarm = -1;

                ((MainActivity)getActivity()).backAlarmTimerView();
            }
        });

        soundRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).showwSoundView();
            }
        });

        updateUI();

        setPickerWithDefault();

        return rootView;
    }


    @Override
    public void onViewStateRestored(Bundle savedInstanceState)  {

        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onResume()  {

        super.onResume();

        fade_in_sub_title.setText(Common.data[Common.currentSelectedFadeIn]);
        fade_out_sub_title.setText(Common.data[Common.currentSelectedFadeIn]);
        if (Common.currentSelectedSound != -1)
            sound_sub_title.setText(Common.favorits.get(Common.currentSelectedSound).title);
    }

    private void setPickerWithDefault() {

        Calendar c = Calendar.getInstance();
        int minutes = c.get(Calendar.MINUTE);
        int hours = c.get(Calendar.HOUR);
        int zone = c.get(Calendar.AM_PM);

        timerPicker.setTime(hours, minutes, zone);

        counterPicker.setTime(2, 0);
    }

    private void animationView()    {

        int height = (int) getContext().getResources().getDimension(R.dimen.picker_view_height);

        ObjectAnimator objectAnimatorButton
                = ObjectAnimator.ofFloat(pickView, "translationY", 0, -height);

        objectAnimatorButton.setDuration(200);
        objectAnimatorButton.start();
    }

    private void updatePicker() {

        if (isClockable)    {
            timerPicker.setVisibility(View.VISIBLE);
            counterPicker.setVisibility(View.GONE);
            select_timer.setBackgroundColor(Color.parseColor("#02B1B4"));
            select_counter.setBackgroundColor(Color.parseColor("#059EA1"));
        }
        else {
            timerPicker.setVisibility(View.GONE);
            counterPicker.setVisibility(View.VISIBLE);
            select_counter.setBackgroundColor(Color.parseColor("#02B1B4"));
            select_timer.setBackgroundColor(Color.parseColor("#059EA1"));
        }
    }

    private void updateUI() {

        switch (Common.currentSelection) {
            case Common.SELECT_ALARM:
                fadeOutRow.setVisibility(View.GONE);
                powerRow.setVisibility(View.GONE);
                title.setText("Alarm");
                fade_in_sub_title.setText("30 seconds");
                snoozeSwitch.setStatus(true);
                break;
            case Common.SELECT_TIMER:
                fadeInRow.setVisibility(View.GONE);
                snoozeRow.setVisibility(View.GONE);
                soundRow.setVisibility(View.GONE);
                title.setText("Timer");
                break;
            default:
                break;
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                animationView();
            }
        }, 500);
    }
}
