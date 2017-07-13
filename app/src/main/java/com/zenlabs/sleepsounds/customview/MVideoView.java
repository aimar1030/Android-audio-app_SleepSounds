package com.zenlabs.sleepsounds.customview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vungle.publisher.EventListener;
import com.vungle.publisher.VunglePub;
import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.activity.MainActivity;
import com.zenlabs.sleepsounds.sqlite.AlarmDBHelper;
import com.zenlabs.sleepsounds.sqlite.SoundDBHelper;
import com.zenlabs.sleepsounds.utils.Common;
import com.zenlabs.sleepsounds.utils.UtilsMethods;
import com.zenlabs.sleepsounds.utils.UtilsValues;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fedoro on 5/20/16.
 */
public class MVideoView extends RelativeLayout {

    public interface MVideoViewHandler  {

        public void videoFinished();
    }

    int animationViews = 0;
    ImageView background;

    Timer timer;

    public MVideoView(Context context, MVideoViewHandler handler) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.view_video, this);
        initViews(context, handler);
    }

    public MVideoView(Context context, AttributeSet attrs, MVideoViewHandler handler) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.view_video, this);
        initViews(context, handler);
    }

    public MVideoView(Context context, AttributeSet attrs, int defStyleAttr, MVideoViewHandler handler) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.view_video, this);
        initViews(context, handler);
    }

    private void initViews(final Context context, final MVideoViewHandler handler) {

        animationViews = 0;

        background = (ImageView) findViewById(R.id.background_video);
        UtilsMethods.setGrayScale(background);
        int position = Common.sounds.indexOf(Common.videoSounds.get(animationViews));
        background.setImageResource(UtilsValues.imageArray[position]);

        TextView textView = (TextView) findViewById(R.id.title_video);
        Typeface custom_font = Typeface.createFromAsset(getContext().getAssets(), "fonts/MyriadPro-Light.otf");
        textView.setTypeface(custom_font);

        RelativeLayout play_video_action = (RelativeLayout) findViewById(R.id.play_video_action);

        play_video_action.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("status", "clicked");

                VunglePub vunglePub = VunglePub.getInstance();
                vunglePub.init(getContext(), "574b92e1bca549a01e00007f");
                if (!UtilsMethods.getBooleanFromSharedPreferences(getContext(), UtilsValues.SHARED_PREFERENCES_WATCH_VIDEO, false) && vunglePub.isAdPlayable()) {
                    animationViews = 0;
                    handler.videoFinished();
                }
            }
        });

        String title = "Watch video to unlock";

        textView.setText(title);

        playAnimation();
    }

    public void playAnimation()    {
        timer = new Timer();
        VideoViewTask myTimerTask = new VideoViewTask();
        timer.schedule(myTimerTask, 0, 2000);
    }

    class VideoViewTask extends TimerTask {

        public VideoViewTask ()    {
        }
        @Override
        public void run() {

            if (getContext() == null)
                return;
            ((MainActivity)getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (Common.videoSounds.size() > 1) {
                        if (animationViews == Common.videoSounds.size())
                            animationViews = 0;
                        int position = Common.sounds.indexOf(Common.videoSounds.get(animationViews));
                        background.setImageResource(UtilsValues.imageArray[position]);
                        animationViews += 1;
                    }
                }
            });
        }
    }

    public void stopTimer() {

        if (timer != null)
            timer.cancel();
        timer = null;
    }

    @Override
    public void onDetachedFromWindow()  {
        stopTimer();
        super.onDetachedFromWindow();
    }
}
