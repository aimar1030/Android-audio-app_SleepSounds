package com.zenlabs.sleepsounds.fragment;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.activity.MainActivity;
import com.zenlabs.sleepsounds.activity.VungleActivity;
import com.zenlabs.sleepsounds.customview.AlarmTextView;
import com.zenlabs.sleepsounds.customview.MSoundLockView;
import com.zenlabs.sleepsounds.customview.MSoundView;
import com.zenlabs.sleepsounds.customview.MVideoView;
import com.zenlabs.sleepsounds.customview.UnlockView;
import com.zenlabs.sleepsounds.model.MAlarm;
import com.zenlabs.sleepsounds.model.MSound;
import com.zenlabs.sleepsounds.sqlite.SoundDBHelper;
import com.zenlabs.sleepsounds.utils.Common;
import com.zenlabs.sleepsounds.utils.UtilsMethods;
import com.zenlabs.sleepsounds.utils.UtilsValues;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment implements MSoundView.ActionHandler {

    RelativeLayout play_bt, clear_bt, volume_bt, clock_bt, alarm_bt;
    ImageView clear_img, volume_img, alarm_img, clock_img, play_img;
    ScrollView scrollView;
    TableLayout tableLayout_unlock, tableLayout_lock;

    RelativeLayout extraView;

    UnlockView unlockView;
    MVideoView videoView;

    AlarmTextView alarmTextView;
    RelativeLayout alarmBar;

    boolean isPlaying = true;
    boolean isVolumeSeleted = false;

    SeekBar volume_contrl;

    ArrayList<MSoundView> unLockViews, lockViews;

    int animated_views = 0;
    int lock_views = 0;

    ImageView alarm_ic;

    ImageView tipScreen;

    Timer timer;

    private AudioManager audioManager = null;

    private AdView admobAdView;

    RelativeLayout mainView;

    AlarmTextView.AlarmTextViewHandler alarmTextViewHandler = new AlarmTextView.AlarmTextViewHandler() {
        @Override
        public void ThreadFinished() {
            showAlarm();
        }
    };

    MVideoView.MVideoViewHandler handler = new MVideoView.MVideoViewHandler() {
        @Override
        public void videoFinished() {
            Intent intent = new Intent(getActivity(), VungleActivity.class);
            getActivity().startActivity(intent);
            ((MainActivity)getActivity()).finish();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        animated_views = 0;
        lock_views = 0;

        play_bt = (RelativeLayout) rootView.findViewById(R.id.pause_bt);
        play_img = (ImageView) rootView.findViewById(R.id.play_img);
        clear_bt = (RelativeLayout) rootView.findViewById(R.id.clear_bt);
        clear_img = (ImageView) rootView.findViewById(R.id.clear_img);
        volume_bt = (RelativeLayout) rootView.findViewById(R.id.volume_bt);
        volume_img = (ImageView) rootView.findViewById(R.id.volume_img);
        alarm_bt = (RelativeLayout) rootView.findViewById(R.id.alarm_bt);
        alarm_img = (ImageView) rootView.findViewById(R.id.alarm_img);
        clock_bt = (RelativeLayout) rootView.findViewById(R.id.clock_bt);
        clock_img = (ImageView) rootView.findViewById(R.id.clock_img);

        mainView = (RelativeLayout) rootView.findViewById(R.id.main_view);

        extraView = (RelativeLayout) rootView.findViewById(R.id.extra_view);
        volume_contrl = (SeekBar) rootView.findViewById(R.id.volume_control);

        tipScreen = (ImageView) rootView.findViewById(R.id.tip_screen);

        tipScreen.setVisibility(View.GONE);

        tipScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipScreen.setVisibility(View.GONE);
            }
        });

        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        volume_contrl.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volume_contrl.setProgress(audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC));

        alarmBar = (RelativeLayout) rootView.findViewById(R.id.alarm_bar);

        alarm_ic = (ImageView) rootView.findViewById(R.id.alarm_alert_img);

        alarmTextView = (AlarmTextView) rootView.findViewById(R.id.alarm_alert_title);

        ImageView menu_bt = (ImageView) rootView.findViewById(R.id.menu_bt);
        menu_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).openMenu();
            }
        });

        volume_contrl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                progressChanged = progress;

                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        ImageView favorit_bt = (ImageView) rootView.findViewById(R.id.favorit_bt);

        favorit_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).openFavorit();
            }
        });


        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
        play_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("status","clicked");

                if (isPlaying)  {
                    play_img.setImageResource(R.drawable.play);
                    pauseAllMusic();
                } else {
                    play_img.setImageResource(R.drawable.pause);
                    resumeAllMusic();
                }

                isPlaying = !isPlaying;
            }
        });

        clear_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllMusic();
            }
        });

        volume_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isVolumeSeleted) {
                    extraView.setVisibility(View.VISIBLE);
                    volume_img.setImageResource(R.drawable.volume_on);
                    showVolumeView();
                }
                else    {
                    volume_img.setImageResource(R.drawable.volume);
                    hideVolumeView();
                }
                isVolumeSeleted = !isVolumeSeleted;
            }
        });

        alarm_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmTextView.stopTimer();
                ((MainActivity)getActivity()).showAlertTimerView();
            }
        });

        clock_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).showClockView();
            }
        });

        hideVolumeView();

        tableLayout_lock = (TableLayout)rootView.findViewById(R.id.tableLayout_lock);
        tableLayout_unlock = (TableLayout) rootView.findViewById(R.id.tableLayout_unlock);

        if (!Common.isStart)
            loadAllViews();
        else
            reloadAllViews();

        admobAdView = (AdView) rootView.findViewById(R.id.workoutActivityBannerAdView);

        return rootView;
    }

    public void showTipScreen() {

        tipScreen.setVisibility(View.VISIBLE);

        if (UtilsMethods.isTablet(getContext()))
            tipScreen.setImageResource(R.drawable.tip_screen_tablet);
        else
            tipScreen.setImageResource(R.drawable.tip_screen);
    }

    public void playVideoView() {

        videoView.playAnimation();
    }

    public void stopVideoView() {
        videoView.stopTimer();
    }

    public void reloadAllViews()   {

        unLockViews = new ArrayList<>();
        lockViews = new ArrayList<>();

        tableLayout_lock.removeAllViews();
        tableLayout_unlock.removeAllViews();

        if (tableLayout_unlock.getChildCount() > 0) {
            tableLayout_lock.removeAllViews();
            tableLayout_unlock.removeAllViews();
        }

        reloadUnLockViews();
    }

    @Override
    public void onResume()  {

        super.onResume();

        showAdmobBanner();

        if (Common.alarms == null || Common.alarms.size() == 0) {
            alarmBar.setVisibility(View.GONE);
        } else {
            alarmBar.setVisibility(View.VISIBLE);
            showAlarm();
        }
    }

    public void loadAllViews()  {

        unLockViews = new ArrayList<>();
        lockViews = new ArrayList<>();

        loadUnLockViews();
    }

    public void loadUnLockViews()   {

        for (int i = 0; i < Common.unlockSounds.size(); i ++)   {

            MSoundView mSoundView = new MSoundView(getContext());
            mSoundView.initUI(Common.unlockSounds.get(i), this);
            unLockViews.add(mSoundView);
        }

        Common.unlockViews = unLockViews;

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                loadAnimatedView();
            }
        }, 4000);
    }

    public void reloadUnLockViews()   {

        for (int i = 0; i < Common.unlockSounds.size(); i ++)   {

            MSoundView mSoundView = new MSoundView(getContext());
            mSoundView.initUI(Common.unlockSounds.get(i), this);
            unLockViews.add(mSoundView);
        }

        Common.unlockViews = unLockViews;

        new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    loadStaticViews();
                }
            }, 50);
    }

    public void loadAnimatedView()  {

        if(animated_views < Common.unlockSounds.size()) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (!UtilsMethods.isTablet(getContext()))
                        animatedView(unLockViews.get(animated_views));
                    else {
                        if (Common.unlockSounds.size() % 2 == 1 && animated_views == Common.unlockSounds.size() - 1) {
                            MSoundView emptyView = new MSoundView(getContext());
                            for ( int i = 0; i < emptyView.getChildCount();  i++ ){
                                View view = emptyView.getChildAt(i);
                                view.setVisibility(View.GONE); // Or whatever you want to do with the view.
                            }
                            animatedViewForTablet(unLockViews.get(animated_views), emptyView);
                        } else {
                            animatedViewForTablet(unLockViews.get(animated_views), unLockViews.get(animated_views + 1));
                        }
                    }
                }
            }, 50);
        } else {

        animated_views = 0;
        addUnlockView();
        loadLockViews();
        }
    }

    public void loadStaticViews()  {

        if(animated_views < Common.unlockSounds.size()) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (!UtilsMethods.isTablet(getContext()))
                        reloadStaticViews(unLockViews.get(animated_views));
                    else {
                        if (Common.unlockSounds.size() % 2 == 1 && animated_views == Common.unlockSounds.size() - 1) {
                            MSoundView emptyView = new MSoundView(getContext());

                            for ( int i = 0; i < emptyView.getChildCount();  i++ ){
                                View view = emptyView.getChildAt(i);
                                view.setVisibility(View.GONE); // Or whatever you want to do with the view.
                            }

                            reloadStaticViewsForTablet(unLockViews.get(animated_views), emptyView);
                        } else {
                            reloadStaticViewsForTablet(unLockViews.get(animated_views), unLockViews.get(animated_views + 1));
                        }
                    }
                }
            }, 50);
        } else {

            animated_views = 0;
            if (!UtilsMethods.getBooleanFromSharedPreferences(getContext(), UtilsValues.SHARED_PREFERENCES_PAID_STATUS, false)) {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        addUnlockView();
                        loadLockViews();
                    }
                }, 1000);
            } else
                Common.isStart = true;
        }
    }

//
    public void animatedView(MSoundView mSoundView)  {

        TableRow tr = new TableRow(getContext());

        int width = tableLayout_unlock.getWidth();

        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        TableRow.LayoutParams params = new TableRow.LayoutParams(width, TableRow.LayoutParams.WRAP_CONTENT);

        params.setMargins(0-width, 0, width, 0);
        mSoundView.setLayoutParams(params);

        tr.addView(mSoundView);

        tableLayout_unlock.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

            ObjectAnimator objectAnimatorButton
                    = ObjectAnimator.ofFloat(mSoundView, "translationX", 0, width);

            objectAnimatorButton.setDuration(200);
            objectAnimatorButton.start();

            animated_views += 1;

        loadAnimatedView();
    }

    public void animatedViewForTablet(MSoundView leftView, MSoundView rightView)  {

        TableRow tr = new TableRow(getContext());

        int width = tableLayout_unlock.getWidth();

        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        TableRow.LayoutParams params_tr = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (float) 0.50);
        params_tr.rightMargin = 5;

        leftView.setLayoutParams(params_tr);

        rightView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (float) 0.50));
        tr.addView(leftView);
        tr.addView(rightView);

        tableLayout_unlock.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        TableLayout.LayoutParams params = new TableLayout.LayoutParams(width, TableLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(0-width, 0, width, 0);

        tr.setLayoutParams(params);

        ObjectAnimator objectAnimatorButton
                = ObjectAnimator.ofFloat(tr, "translationX", 0, width);

        objectAnimatorButton.setDuration(200);
        objectAnimatorButton.start();

        animated_views += 2;

        loadAnimatedView();
    }

    public void reloadStaticViews(MSoundView mSoundView)  {

        TableRow tr = new TableRow(getContext());

        int width = tableLayout_unlock.getWidth();

        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        TableRow.LayoutParams params = new TableRow.LayoutParams(width, TableRow.LayoutParams.WRAP_CONTENT);

        params.setMargins(0, 0, 0, 0);
        mSoundView.setLayoutParams(params);

        tr.addView(mSoundView);

        tableLayout_unlock.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        animated_views += 1;

        loadStaticViews();
    }

    public void reloadStaticViewsForTablet(MSoundView leftView, MSoundView rightView)    {

        TableRow tr = new TableRow(getContext());

        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        TableRow.LayoutParams params_tr = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (float) 0.50);
        params_tr.rightMargin = 5;

        leftView.setLayoutParams(params_tr);

        rightView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (float) 0.50));
        tr.addView(leftView);
        tr.addView(rightView);

        tableLayout_unlock.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        animated_views += 2;

        loadStaticViews();
    }

    private void addUnlockView()    {

        unlockView = new UnlockView(getContext());

        TableRow tr = new TableRow(getContext());

        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
        unlockView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1));

        tr.addView(unlockView);

        tableLayout_unlock.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT, 1));

        unlockView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!UtilsMethods.getBooleanFromSharedPreferences(getContext(), UtilsValues.SHARED_PREFERENCES_PAID_STATUS, false))
                    ((MainActivity)getActivity()).showSubscribe();
            }
        });
    }

    public void loadLockViews() {

        if (UtilsMethods.isTablet(getContext()))    {

            if (Common.lockSounds.size() > lock_views) {
                if (Common.lockSounds.size() % 4 == 0) {
                    if (lock_views < Common.lockSounds.size()) {
                        ArrayList<MSoundLockView> views = new ArrayList<>();

                        for (int i = 0; i < 4; i++) {
                            MSoundLockView view = new MSoundLockView(getContext());
                            view.initViews(Common.lockSounds.get(lock_views + i));
                            views.add(view);
                        }

                        addSoundViewsForDoubleForTablet(views);
                    }
                } else {
                    if (lock_views < Common.lockSounds.size() - 1) {
                        ArrayList<MSoundLockView> views = new ArrayList<>();

                        for (int i = 0; i < 4; i++) {
                            MSoundLockView view = new MSoundLockView(getContext());
                            view.initViews(Common.lockSounds.get(lock_views + i));
                            views.add(view);
                        }

                        addSoundViewsForDoubleForTablet(views);
                    } else {
                        ArrayList<MSoundLockView> views = new ArrayList<>();

                        MSoundLockView fillView = new MSoundLockView(getContext());
                        fillView.initViews(Common.lockSounds.get(lock_views));
                        views.add(fillView);

                        for (int j = 0; j < 3; j++) {
                            MSoundLockView emptyView = new MSoundLockView(getContext());

                            for (int i = 0; i < emptyView.getChildCount(); i++) {
                                View view = emptyView.getChildAt(i);
                                view.setVisibility(View.GONE); // Or whatever you want to do with the view.
                            }
                            views.add(emptyView);
                        }

                        addSoundViewsForDoubleForTablet(views);
                    }
                }
            } else {
                animated_views = 0;
                lock_views = 0;
                Common.isStart = true;
                if (Common.videoSounds.size() > 0)
                    if (UtilsMethods.isTablet(getContext()))
                        addVideoViewForTablet();
                    else
                        addVideoView();
            }
        } else {
            if (Common.lockSounds.size() % 2 == 0) {
                if (lock_views < Common.lockSounds.size()) {

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {

                            MSoundLockView leftViewSoundView = new MSoundLockView(getContext());
                            leftViewSoundView.initViews(Common.lockSounds.get(lock_views));

                            MSoundLockView rightViewSoundView = new MSoundLockView(getContext());
                            rightViewSoundView.initViews(Common.lockSounds.get(lock_views + 1));

                            addSoundViewsForDouble(leftViewSoundView, rightViewSoundView);
                        }
                    }, 100);
                } else {
                    animated_views = 0;
                    lock_views = 0;
                    Common.isStart = true;
                    if (Common.videoSounds.size() > 0)
                        if (UtilsMethods.isTablet(getContext()))
                            addVideoViewForTablet();
                        else
                            addVideoView();
                }
            } else {

                if (lock_views < Common.lockSounds.size() + 1) {

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {

                            MSoundLockView leftViewSoundView = new MSoundLockView(getContext());
                            leftViewSoundView.initViews(Common.lockSounds.get(lock_views));

                            MSoundLockView rightViewSoundView = new MSoundLockView(getContext());
                            ;

                            if (lock_views < Common.lockSounds.size() - 1)
                                rightViewSoundView.initViews(Common.lockSounds.get(lock_views + 1));

                            addSoundViewsForDouble(leftViewSoundView, rightViewSoundView);
                        }
                    }, 50);
                } else {
                    Common.isStart = true;
                    if (Common.videoSounds.size() > 0) {

                        if (UtilsMethods.isTablet(getContext()))
                            addVideoViewForTablet();
                        else
                            addVideoView();
                    }
                    animated_views = 0;
                }
            }
        }
    }

    public void addSoundViewsForDouble(MSoundLockView leftView, MSoundLockView rightView)    {

        TableRow tr = new TableRow(getContext());

        int width = tableLayout_lock.getWidth();

        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        TableRow.LayoutParams params_tr = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (float) 0.50);
        params_tr.rightMargin = 5;

        leftView.setLayoutParams(params_tr);

        rightView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (float) 0.50));
        tr.addView(leftView);
        tr.addView(rightView);

        tableLayout_lock.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        if (!Common.isStart) {
            TableLayout.LayoutParams params = new TableLayout.LayoutParams(width, TableLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(0-width, 0, width, 0);

            tr.setLayoutParams(params);

            ObjectAnimator objectAnimatorButton
                    = ObjectAnimator.ofFloat(tr, "translationX", 0, width);

            objectAnimatorButton.setDuration(200);
            objectAnimatorButton.start();
        }

        lock_views += 2;

        loadLockViews();
    }

    public void addSoundViewsForDoubleForTablet(ArrayList<MSoundLockView> views)    {

        TableRow tr = new TableRow(getContext());

        int width = tableLayout_lock.getWidth();

        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        for (int i = 0; i < views.size(); i ++) {

            MSoundLockView view = views.get(i);
            TableRow.LayoutParams params_tr = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (float) 0.25);
            if (i < views.size() - 1)
              params_tr.rightMargin = 5;

            view.setLayoutParams(params_tr);
            tr.addView(view);
        }

        tableLayout_lock.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        if (!Common.isStart) {
            TableLayout.LayoutParams params = new TableLayout.LayoutParams(width, TableLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(0-width, 0, width, 0);

            tr.setLayoutParams(params);

            ObjectAnimator objectAnimatorButton
                    = ObjectAnimator.ofFloat(tr, "translationX", 0, width);

            objectAnimatorButton.setDuration(200);
            objectAnimatorButton.start();
        }

        lock_views += 4;

        loadLockViews();
    }

    public void addVideoView()   {

        lock_views = 0;
        animated_views = 0;

        videoView = new MVideoView(getContext(), handler);
        MSoundLockView blankView = new MSoundLockView(getContext());

        TableRow tr = new TableRow(getContext());

        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        videoView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (float) 0.5));
        blankView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (float) 0.5));

        tr.addView(videoView);
        tr.addView(blankView);

        tableLayout_lock.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }

    public void addVideoViewForTablet()   {

        lock_views = 0;
        animated_views = 0;

        videoView = new MVideoView(getContext(), handler);
        MSoundLockView blankView = new MSoundLockView(getContext());
        MSoundLockView blankView1 = new MSoundLockView(getContext());
        MSoundLockView blankView2 = new MSoundLockView(getContext());

        TableRow tr = new TableRow(getContext());

        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        videoView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (float) 0.25));
        blankView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (float) 0.25));
        blankView1.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (float) 0.25));
        blankView2.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (float) 0.25));

        TableRow.LayoutParams params_tr = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (float) 0.25);
        params_tr.rightMargin = 5;

        videoView.setLayoutParams(params_tr);
        blankView.setLayoutParams(params_tr);
        blankView1.setLayoutParams(params_tr);

        tr.addView(videoView);
        tr.addView(blankView);
        tr.addView(blankView1);
        tr.addView(blankView2);

        tableLayout_lock.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }

    private void resumeAllMusic() {

        for (int i = 0; i < unLockViews.size(); i ++)   {

            MSoundView mSoundView = unLockViews.get(i);
            mSoundView.resumeMusic();
        }
    }

    private void pauseAllMusic()    {

        for (int i = 0; i < unLockViews.size(); i ++)   {

            MSoundView mSoundView = unLockViews.get(i);
            mSoundView.pauseMusic();
        }
    }

    private void clearAllMusic()    {
        for (int i = 0; i < unLockViews.size(); i ++)   {

            MSoundView mSoundView = unLockViews.get(i);
            mSoundView.stopPlayer();
        }
    }

    @Override
    public void playAction(boolean isPlaying, int position) {
        this.isPlaying = isPlaying;

        if (isPlaying)
            play_img.setImageResource(R.drawable.pause);

    }

    private void hideVolumeView()   {

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)extraView.getLayoutParams();

        int height = (int) getContext().getResources().getDimension(R.dimen.control_view_height);
        params.setMargins(0, 0, 0, 0-height);
        extraView.setLayoutParams(params);

        ObjectAnimator objectAnimatorButton
                = ObjectAnimator.ofFloat(extraView, "translationY", 0, height);

        objectAnimatorButton.setDuration(200);
        objectAnimatorButton.start();
    }

    private void showVolumeView()   {

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)extraView.getLayoutParams();
        int height = (int) getContext().getResources().getDimension(R.dimen.control_view_height);
        params.setMargins(0, height, 0, 0);
        extraView.setLayoutParams(params);

        ObjectAnimator objectAnimatorButton
                = ObjectAnimator.ofFloat(extraView, "translationY", 0, -height);

        objectAnimatorButton.setDuration(200);
        objectAnimatorButton.start();
    }

    private void showAlarm()    {

        MAlarm mAlarm = UtilsMethods.getMinumAlarm();
        Common.currentAlarm = mAlarm;
        if (mAlarm == null) {
            alarmBar.setVisibility(View.GONE);
        } else {
            alarmBar.setVisibility(View.VISIBLE);
            alarmTextView.setHandler(alarmTextViewHandler);
            alarmTextView.setTextColor(Color.WHITE);
            alarmTextView.updateUI(mAlarm);
        }
    }

    private void showAdmobBanner() {

        if (!UtilsMethods.getBooleanFromSharedPreferences(getContext(), UtilsValues.SHARED_PREFERENCES_PAID_STATUS, false)) {
            AdRequest adRequest = new AdRequest.Builder().build();
            // Start loading the ad in the background.
            admobAdView.loadAd(adRequest);
        } else {
            admobAdView.setVisibility(View.GONE);
            ((RelativeLayout.LayoutParams) mainView.getLayoutParams()).addRule(RelativeLayout.ABOVE,0);
        }
    }
}