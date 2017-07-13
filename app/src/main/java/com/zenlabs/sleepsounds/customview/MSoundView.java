package com.zenlabs.sleepsounds.customview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.model.MSound;
import com.zenlabs.sleepsounds.sqlite.SoundDBHelper;
import com.zenlabs.sleepsounds.utils.AsyncTask;
import com.zenlabs.sleepsounds.utils.Common;
import com.zenlabs.sleepsounds.utils.ImageCache;
import com.zenlabs.sleepsounds.utils.UtilsMethods;
import com.zenlabs.sleepsounds.utils.UtilsValues;

import java.lang.ref.WeakReference;

public class MSoundView extends RelativeLayout {

    public ImageView sound_background;
    public TextView sound_title;
    public ImageView play_bt;
    public RelativeLayout greenSashView, sound_volume_panel, play_bt_action, volume_down_bt, volume_up_bt;
    public LinearLayout dotView;
    public RelativeLayout sound_main_panel;
    int repeat_count = 0;
    public MSound sound;
    Context mContext;
    int position;

    boolean isPausedByHome = false;

    MediaPlayer mediaPlayer;

    ActionHandler handler;

    public interface ActionHandler {

        public void playAction(boolean isPlaying, int position);
    }

    public MSoundView(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.view_msound, this);
    }

    public MSoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.view_msound, this);
    }

    public MSoundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.view_msound, this);
    }


    public void initUI(MSound mSound, final ActionHandler mHandler)    {

        this.sound = mSound;
        this.handler = mHandler;

        position = Common.unlockSounds.indexOf(sound);

        sound_background = (ImageView)findViewById(R.id.sound_background);
        sound_title = (TextView)findViewById(R.id.sound_title);

        Typeface custom_font = Typeface.createFromAsset(getContext().getAssets(), "fonts/MyriadPro-Light.otf");
        sound_title.setTypeface(custom_font);

        play_bt = (ImageView)findViewById(R.id.play_bt);
        volume_down_bt = (RelativeLayout) findViewById(R.id.volume_down);
        volume_up_bt = (RelativeLayout) findViewById(R.id.volume_up);
        greenSashView = (RelativeLayout)findViewById(R.id.greenSashView);
        sound_volume_panel = (RelativeLayout)findViewById(R.id.sound_volume_panel);
        play_bt_action = (RelativeLayout) findViewById(R.id.play_bt_action);
        dotView = (LinearLayout)findViewById(R.id.dot_view);
        sound_main_panel = (RelativeLayout)findViewById(R.id.sound_main_panel);

        drawDot(getContext());

        loadMusic();

        play_bt_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("status", "clicked");

                if (sound.isPlaying)    {
                    hidePlayBt();
                    if (mediaPlayer!= null)
                        mediaPlayer.pause();
                    enviromentActionForPlay();
                } else {
                    playSound();
                }
            }
        });

        volume_down_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sound.volume -= 0.1;
                if (sound.volume < 0)
                    sound.volume = 0;
                updateVolume();
            }
        });
        volume_up_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sound.volume += 0.1;
                if (sound.volume > 1)
                    sound.volume = 1;
                updateVolume();
            }
        });

        this.mContext = getContext();
        this.sound = mSound;

        if (sound.getBackground() != null)  {
            int img_position = Common.sounds.indexOf(sound);
//            sound_background.setImageResource(UtilsValues.imageArray[img_position]);
            loadBitmap(UtilsValues.imageArray[img_position], sound_background);

        }

        if (sound.getName() != null)
            sound_title.setText(sound.getName());

        if (!sound.isUsable)
        {
            play_bt.setVisibility(GONE);
            sound_volume_panel.setVisibility(GONE);
            greenSashView.setVisibility(GONE);
            volume_up_bt.setVisibility(GONE);
            volume_down_bt.setVisibility(GONE);
            dotView.setVisibility(GONE);
        }

        if (!sound.isPlaying)   {
            hideSashView();
            hideVolumeControl();
        }else {
            showSashView();
            showVolumeControl();
        }
    }

    public void updateVolume()  {

        mediaPlayer.setVolume(sound.volume, sound.volume);
        Common.unlockSounds.get(position).volume = sound.volume;

        SoundDBHelper dbHelper = new SoundDBHelper(getContext());
        dbHelper.update_model(sound);

        drawDot(getContext());
    }

    public void playSound() {
        if (mediaPlayer == null)
            loadMusic();
        if(!mediaPlayer.isPlaying())
            mediaPlayer.start();
        showSashView();
        showVolumeControl();
        play_bt.setImageResource(R.drawable.a_pause);

        sound.isPlaying = true;
        Common.unlockSounds.get(position).isPlaying = sound.isPlaying;
        handler.playAction(sound.isPlaying, position);

        SoundDBHelper dbHelper = new SoundDBHelper(getContext());
        dbHelper.update_model(sound);
    }

    private void enviromentActionForPlay() {

        sound.isPlaying = !sound.isPlaying;
        Common.unlockSounds.get(position).isPlaying = sound.isPlaying;
        handler.playAction(sound.isPlaying, position);

        SoundDBHelper dbHelper = new SoundDBHelper(getContext());
        dbHelper.update_model(sound);
    }

    public void showSashView() {

        greenSashView.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)greenSashView.getLayoutParams();;

        int height = (int) getContext().getResources().getDimension(R.dimen.sound_manager_height);

        params.setMargins(0, 0-height, 0, height);
        greenSashView.setLayoutParams(params);
        Animation animation1 = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.move_down_anim);
        greenSashView.startAnimation(animation1);
    }

    public void hideSashView()  {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)greenSashView.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        greenSashView.setLayoutParams(params);
        Animation animation1 = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.move_up_anim);
        greenSashView.startAnimation(animation1);
    }

    public void showVolumeControl() {

        sound_volume_panel.setVisibility(View.VISIBLE);
        int width = (int) getContext().getResources().getDimension(R.dimen.sound_manager_volume_panel_width);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.setMargins(width, 0, 0-width, 0);
        sound_volume_panel.setLayoutParams(params);

        ObjectAnimator objectAnimatorButton
                = ObjectAnimator.ofFloat(sound_volume_panel, "translationX", 0, 0-width);

        objectAnimatorButton.setDuration(200);
        objectAnimatorButton.start();

    }

    public void hideVolumeControl() {
        int width = (int) getContext().getResources().getDimension(R.dimen.sound_manager_volume_panel_width);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.setMargins(0, 0, 0, 0);
        sound_volume_panel.setLayoutParams(params);

        ObjectAnimator objectAnimatorButton
                = ObjectAnimator.ofFloat(sound_volume_panel, "translationX", 0, width);

        objectAnimatorButton.setDuration(200);
        objectAnimatorButton.start();
    }

    public void drawDot(Context context)   {

        final int width = 100;
        final int point_width = (int) (width * sound.volume);
        final int height = 4;
        final int point_legth = (int) (10 * sound.volume);

        final Paint paint = new Paint();
        paint.setColor(Color.WHITE);

        dotView.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bg = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                paint.setStyle(Paint.Style.FILL);
                Canvas canvas = new Canvas(bg);

                for (int i = 0; i < point_legth; i ++)  {
                    canvas.drawCircle(point_width/point_legth * i + 1, 2, 1, paint);
                }

                dotView.setBackgroundDrawable(new BitmapDrawable(bg));
            }
        });
    }

    public void hidePlayBt()    {

        play_bt.setAlpha(0.0f);
        ObjectAnimator objectAnimator
                = ObjectAnimator.ofFloat(play_bt, "alpha", 0.0f, 1.0f);
        objectAnimator.setRepeatCount(3);
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        objectAnimator.setDuration(100);
        objectAnimator.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                play_bt.setImageResource(R.drawable.a_play);
                hideSashView();
                hideVolumeControl();
            }
        }, 300);
    }

    private void loadMusic()    {

        mediaPlayer = new MediaPlayer();
        try{
            String fileName = "sounds/" + sound.getSound();
            AssetFileDescriptor descriptor = getContext().getAssets().openFd(fileName);
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength() );
            descriptor.close();
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.setVolume(sound.volume*10, sound.volume*10);
        } catch(Exception e){
            // handle error here..
            e.printStackTrace();
        }
    }

    public void pauseMusic()    {
        if (sound.isPlaying && mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void resumeMusic()   {
        if (sound.isPlaying && mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void stopPlayer() {

        if (sound != null && sound.isPlaying) {
            if (mediaPlayer != null)    {
                mediaPlayer.pause();
            }
            hidePlayBt();
            sound.isPlaying = false;
            Common.unlockSounds.get(position).isPlaying = sound.isPlaying;
            handler.playAction(sound.isPlaying, position);

            SoundDBHelper dbHelper = new SoundDBHelper(getContext());
            dbHelper.update_model(sound);
        }
    }

    @Override
    public void onDetachedFromWindow()  {

        stopPlayer();
        super.onDetachedFromWindow();
    }

    public void loadBitmap(int resId, ImageView imageView) {
        final String imageKey = String.valueOf(resId);

        ImageCache cache = new ImageCache(new ImageCache.ImageCacheParams(getContext(), "com.zenlabs.sleepsounds"));
        final BitmapDrawable bitmap = cache.mMemoryCache.get(imageKey);
        if (bitmap != null) {
            imageView.setImageDrawable(bitmap);
        } else {
            imageView.setImageResource(R.drawable.sound_img_0);
            BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            task.execute(resId);
        }
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private int data = 0;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            return UtilsMethods.decodeSampledBitmapFromResource(mContext.getResources(), data, 100, 100);
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
}