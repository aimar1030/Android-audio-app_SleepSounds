package com.zenlabs.sleepsounds.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.activity.MainActivity;
import com.zenlabs.sleepsounds.model.MSound;
import com.zenlabs.sleepsounds.utils.AsyncTask;
import com.zenlabs.sleepsounds.utils.Common;
import com.zenlabs.sleepsounds.utils.ImageCache;
import com.zenlabs.sleepsounds.utils.UtilsMethods;
import com.zenlabs.sleepsounds.utils.UtilsValues;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by fedoro on 5/19/16.
 */
public class MSoundLockView extends GridLayout {

    public ArrayList<MSound> arrayList;
    public Context mContext;
    RelativeLayout mainView;

    public MSoundLockView(Context context) {
        super(context);
        this.mContext = context;
    }

    public MSoundLockView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MSoundLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void initViews(MSound sound)  {

        LayoutInflater.from(getContext()).inflate(R.layout.view_msound_lock, this);

        ImageView background = (ImageView)findViewById(R.id.sound_background_lock);

        RelativeLayout showSubscribe = (RelativeLayout) findViewById(R.id.play_bt_action_lock);

        int position = Common.sounds.indexOf(sound);

        if (sound.getBackground() != null) {

//            background.setImageResource(UtilsValues.imageArray[position]);
            loadBitmap(UtilsValues.imageArray[position], background);
            UtilsMethods.setGrayScale(background);
        }
        TextView title = (TextView)findViewById(R.id.sound_title_lock);
        Typeface custom_font = Typeface.createFromAsset(getContext().getAssets(), "fonts/MyriadPro-Light.otf");
        title.setTypeface(custom_font);

        showSubscribe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UtilsMethods.getBooleanFromSharedPreferences(getContext(), UtilsValues.SHARED_PREFERENCES_PAID_STATUS, false))
                    ((MainActivity)getContext()).showSubscribe();
            }
        });

        if (sound.getName() != null)
            title.setText(sound.getName());
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
