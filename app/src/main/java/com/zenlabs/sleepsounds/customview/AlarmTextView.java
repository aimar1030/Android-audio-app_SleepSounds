package com.zenlabs.sleepsounds.customview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.activity.MainActivity;
import com.zenlabs.sleepsounds.model.MAlarm;
import com.zenlabs.sleepsounds.model.MSound;
import com.zenlabs.sleepsounds.sqlite.AlarmDBHelper;
import com.zenlabs.sleepsounds.utils.Common;
import com.zenlabs.sleepsounds.utils.UtilsMethods;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fedoro on 5/25/16.
 */
public class AlarmTextView extends RelativeLayout {

    TextView timerLabel;
    Timer timer;
    MAlarm mAlarm;
    int position;
    boolean isRunning;
    AlarmTextViewHandler handler;

    public interface AlarmTextViewHandler   {

        public void ThreadFinished();
    }

    public AlarmTextView(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.view_alarm_text, this);
        initUI(context);
    }

    public AlarmTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.view_alarm_text, this);
        initUI(context);
    }

    public AlarmTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.view_alarm_text, this);
        initUI(context);
    }

    private void initUI(Context context)   {

        timerLabel = (TextView)findViewById(R.id.time_lb);
        Typeface custom_font = Typeface.createFromAsset(getContext().getAssets(), "fonts/MyriadPro-Light.otf");
        timerLabel.setTypeface(custom_font);
    }

    public void updateUI(MAlarm alarm)  {

        if (timer != null)
        {
            timer.cancel();
            timer = null;
        }
        mAlarm = alarm;
        position = Common.alarms.indexOf(mAlarm);
        if (!alarm.isCounter) {
            String zone = "am";
            int hours = 0;
            if (alarm.hours_fire >=  12 && alarm.hours_fire < 24) {
                zone = "pm";
                hours = alarm.hours_fire - 12;

                if (hours == 0)
                    hours = 12;
            } else {
                hours = alarm.hours_fire;
                if (hours == 24)
                    hours = hours - 12;
            }

            String hours_st = "" + hours;
            if (hours < 10)
                hours_st = "0" + hours;
            String mins_st = "" + alarm.minutes_fire;
            if (alarm.minutes_fire < 10)
                mins_st = "0" + alarm.minutes_fire;

            String time = hours_st + ":" + mins_st + zone;
            timerLabel.setText(time);
        } else {

            int hours = (int) alarm.fire_seconds/3600;
            int mins = (int) alarm.fire_seconds%3600/60;
            int seconds = (int) alarm.fire_seconds%3600%60;

            String hours_st = "" + hours;
            if (hours < 10)
                hours_st = "0" + hours;
            String mins_st = "" + mins;
            if (mins < 10)
                mins_st = "0" + mins;
            String seconds_st = "" + seconds;
            if (seconds < 10)
                seconds_st = "0" + seconds;
            String time = hours_st + ":" + mins_st+ ":" + seconds_st;

            timerLabel.setText(time);
        }

        if (alarm.status == 1)    {
            isRunning = true;
            timer = new Timer();
            MyTimerTask myTimerTask = new MyTimerTask(alarm.fire_seconds, timerLabel);
            timer.schedule(myTimerTask, 0, 1000);
        }
    }

    public void setHandler(AlarmTextViewHandler handler)    {
        this.handler = handler;
    }

    public void setTextColor(int color)  {

        timerLabel.setTextColor(color);
    }

    public void setSize(float size){
        timerLabel.setTextSize(size);
    }

    public void runAlarm()  {

        if (mAlarm.isTimer) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < Common.unlockViews.size(); j++) {
                        Common.unlockViews.get(j).stopPlayer();
                    }

                    stopAlarms();
                }
            }, mAlarm.fade_in_time * 1000);
        }
        else {
            final List<MSound> alarmSounds = UtilsMethods.getSoundsFromFavoritWithID(mAlarm.category_id);

            for (int j = 0; j < alarmSounds.size(); j++) {
                MSound sound = alarmSounds.get(j);
                playAlarm(Common.unlockSounds.indexOf(sound));
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < alarmSounds.size(); j++) {
                        MSound sound = alarmSounds.get(j);
                        stopSound(Common.unlockSounds.indexOf(sound));
                    }

                    stopAlarms();
                }
            }, mAlarm.fade_in_time * 1000);
        }
    }

    public void stopAlarms()    {

        if (mAlarm.isTimer) {
            mAlarm.status = 2;
            isRunning = false;
        }
        else if (mAlarm.isCounter)
        {
            if (mAlarm.isSnooze)    {
                mAlarm.status = 1;
                mAlarm.fire_seconds = mAlarm.hours_fire * 3600 + mAlarm.minutes_fire * 60;
                isRunning = true;
            } else {
                mAlarm.status = 2;
                isRunning = false;
            }

        } else {
            mAlarm.status = 2;
            DateFormat df = new SimpleDateFormat("dd MMM yyyy");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                    mAlarm.hours_fire, mAlarm.minutes_fire, 0);
            long endtime = calendar.getTimeInMillis()/1000;
            long starttime = System.currentTimeMillis()/1000;

            mAlarm.fire_seconds = endtime - starttime;

            if (mAlarm.isSnooze)    {
                mAlarm.status = 1;
                mAlarm.fire_seconds = mAlarm.fire_seconds + 24 * 3600;
                isRunning = true;
            }
        }

        int i = Common.alarms.indexOf(mAlarm);
        if (i != -1) {
            Common.alarms.set(i, mAlarm);
            AlarmDBHelper dbHelper = new AlarmDBHelper(getContext());
            dbHelper.update_model(mAlarm);
        }

        if (mAlarm.isPower && mAlarm.isTimer) {
            ((MainActivity)getContext()).finish();
        }

        if (handler != null)
            handler.ThreadFinished();
    }

    public void playAlarm(int position) {
        Common.unlockViews.get(position).playSound();
    }

    public void stopSound(int position) {
        Common.unlockViews.get(position).stopPlayer();
    }

    class MyTimerTask extends TimerTask {

        long prev_time = 0;
        long total_seconds;
        TextView lb;

        public MyTimerTask (long total_seconds, TextView timerlb)    {

            this.total_seconds = total_seconds;
            lb = timerlb;
        }
        @Override
        public void run() {

            if (getContext() == null)
                return;
            ((MainActivity)getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Long current_time = System.currentTimeMillis()/1000;

                    if (prev_time > 0)  {
                        long diff_time = current_time - prev_time;

                        if (diff_time < total_seconds)  {
                            total_seconds = total_seconds - diff_time;

                            int hours = (int) total_seconds/3600;
                            int mins = (int) (total_seconds%3600)/60;
                            int seconds = (int) (total_seconds%3600)%60;

                            String seconds_st = "" + seconds;
                            if (seconds < 10)
                                seconds_st = "0"+seconds;

                            String mins_st = "" + mins;
                            if (mins < 10)
                                mins_st = "0"+mins;

                            String hours_st = "" + hours;
                            if (hours < 10)
                                hours_st = "0"+hours;

                            if (mAlarm.isCounter)
                                lb.setText(hours_st + ":" + mins_st + ":" + seconds_st);

                            int i = Common.alarms.indexOf(mAlarm);
                            if (i != -1) {
                                Common.alarms.get(i).fire_seconds = total_seconds;

                                AlarmDBHelper dbHelper = new AlarmDBHelper(getContext());
                                dbHelper.update_model(Common.alarms.get(i));
                            }

                        } else {
                            stopAlarms();
                            runAlarm();
                        }
                    }

                    prev_time = current_time;
                }
            });
        }
    }

    public void stopTimer() {

        if (timer != null)
            timer.cancel();
        isRunning = false;
        timer = null;
    }

    @Override
    public void onDetachedFromWindow()  {
        stopTimer();
        super.onDetachedFromWindow();
    }
}
