package com.zenlabs.sleepsounds.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.vungle.publisher.EventListener;
import com.vungle.publisher.VunglePub;
import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.sqlite.SoundDBHelper;
import com.zenlabs.sleepsounds.utils.Common;
import com.zenlabs.sleepsounds.utils.UtilsMethods;
import com.zenlabs.sleepsounds.utils.UtilsValues;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by fedoro on 6/3/16.
 */
public class VungleActivity extends Activity {

    final VunglePub vunglePub = VunglePub.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vungle);

        // get your App ID from the app's main page on the Vungle Dashboard after setting up your app
        final String app_id = "574b92e1bca549a01e00007f";

        // initialize the Publisher SDK
        vunglePub.init(this, app_id);

        vunglePub.setEventListeners(vungleDefaultListener, vungleSecondListener);
    }

    private final EventListener vungleDefaultListener = new EventListener() {
        @Deprecated
        @Override
        public void onVideoView(boolean isCompletedView, int watchedMillis, int videoDurationMillis) {
            // This method is deprecated and will be removed. Please use onAdEnd() instead.
        }

        @Override
        public void onAdStart() {
            // Called before playing an ad.
        }

        @Override
        public void onAdUnavailable(String reason) {
            // Called when VunglePub.playAd() was called but no ad is available to show to the user.
            Log.d("available", "false");
        }

        @Override
        public void onAdEnd(boolean wasCallToActionClicked) {
            // Called when the user leaves the ad and control is returned to your application.

            Common.videoSounds.get(0).type = 0;
            Common.videoSounds.get(0).isUsable = true;
            SoundDBHelper dbHelper = new SoundDBHelper(VungleActivity.this);
            dbHelper.update_model(Common.videoSounds.get(0));
            Common.unlockSounds.add(Common.videoSounds.get(0));
            Common.videoSounds.remove(0);

            UtilsMethods.saveStringInSharedPreferences(VungleActivity.this, UtilsValues.SHARED_PREFERENCES_WATCH_TIME, getDelay());
            UtilsMethods.saveBooleanInSharedPreferences(VungleActivity.this, UtilsValues.SHARED_PREFERENCES_WATCH_VIDEO, true);
            Intent intent = new Intent(VungleActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        private String getDelay()  {

            Calendar c = Calendar.getInstance();
            Date newDate = new Date(c.getTime().getTime() - 3 * 24*60*60*1000);
            c.setTime(newDate);
            return c.get(Calendar.YEAR) + "," + c.get(Calendar.MONTH) + "," + c.get(Calendar.DAY_OF_MONTH);
        }

        @Override
        public void onAdPlayableChanged(boolean isAdPlayable) {
            // Called when ad playability changes.
            Log.d("DefaultListener", "This is a default eventlistener.");
            final boolean enabled = isAdPlayable;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Called when ad playability changes.
                }
            });
        }
    };

    private final EventListener vungleSecondListener = new EventListener() {
        // Vungle SDK allows for multiple listeners to be attached. This secondary event listener is only
        // going to print some logs for now, but it could be used to Pause music, update a badge icon, etc.
        @Deprecated
        @Override
        public void onVideoView(boolean isCompletedView, int watchedMillis, int videoDurationMillis) {}

        @Override
        public void onAdStart() {}

        @Override
        public void onAdUnavailable(String reason) {
            Log.d("available", "false");
        }

        @Override
        public void onAdEnd(boolean wasCallToActionClicked) {}

        @Override
        public void onAdPlayableChanged(boolean isAdPlayable) {
            Log.d("SecondListener", String.format("This is a second event listener! Ad playability has changed, and is now: %s", isAdPlayable));
        }
    };

    private void PlayAd() {
        vunglePub.playAd();
    }

    @Override
    protected void onPause() {
        super.onPause();
        vunglePub.onPause();
    }

    @Override
    protected void onDestroy() {
        // onDestroy(), remove eventlisteners.
        vunglePub.removeEventListeners(vungleDefaultListener, vungleSecondListener);
        super.onDestroy();
    }

    @Override
    protected void onResume()   {
        PlayAd();
        super.onResume();
    }

    @Override
    public void onBackPressed()  {
        Intent intent = new Intent(VungleActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
