package com.zenlabs.sleepsounds.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vungle.publisher.VunglePub;
import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.iab.IabBroadcastReceiver;
import com.zenlabs.sleepsounds.iab.IabHelper;
import com.zenlabs.sleepsounds.iab.IabResult;
import com.zenlabs.sleepsounds.iab.Inventory;
import com.zenlabs.sleepsounds.iab.Purchase;
import com.zenlabs.sleepsounds.model.App;
import com.zenlabs.sleepsounds.model.MFavorit;
import com.zenlabs.sleepsounds.model.MSound;
import com.zenlabs.sleepsounds.plistparser.Array;
import com.zenlabs.sleepsounds.plistparser.Dict;
import com.zenlabs.sleepsounds.plistparser.MyString;
import com.zenlabs.sleepsounds.plistparser.PList;
import com.zenlabs.sleepsounds.plistparser.PListXMLHandler;
import com.zenlabs.sleepsounds.plistparser.PListXMLParser;
import com.zenlabs.sleepsounds.plistparser.ProductsPlistParsing;
import com.zenlabs.sleepsounds.sqlite.AlarmDBHelper;
import com.zenlabs.sleepsounds.sqlite.FavoritDBHelper;
import com.zenlabs.sleepsounds.sqlite.SoundDBHelper;
import com.zenlabs.sleepsounds.utils.Common;
import com.zenlabs.sleepsounds.utils.LogService;
import com.zenlabs.sleepsounds.utils.Utils;
import com.zenlabs.sleepsounds.utils.UtilsMethods;
import com.zenlabs.sleepsounds.utils.UtilsValues;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import me.kiip.sdk.Kiip;

@SuppressWarnings("deprecation")
public class SplashActivity extends Activity implements TextureView.SurfaceTextureListener, MediaPlayer.OnCompletionListener {

    private App featuredApp = new App();
    private String tip = "", quote = "";
    private ArrayList<App> apps;
    private boolean isTipsDownloaded = false;
    private boolean isAnimationFinished = false;

    // Log tag.
    private static final String TAG = SplashActivity.class.getName();

    // Asset video file name.
    private static final String FILE_NAME = "opener.mp4";

    // MediaPlayer instance to control playback of video file.
    private MediaPlayer mMediaPlayer;

    final VunglePub vunglePub = VunglePub.getInstance();

    private void loadUnlockSounds() {

        Common.unlockSounds = new ArrayList<>();
        Common.lockSounds = new ArrayList<>();
        Common.videoSounds = new ArrayList<>();

        if (!UtilsMethods.getBooleanFromSharedPreferences(SplashActivity.this, UtilsValues.SHARED_PREFERENCES_PAID_STATUS, false)) {

            loadDefaultSounds();
        } else {
            String exiredate_st = UtilsMethods.getStringFromSharedPreferences(SplashActivity.this, UtilsValues.SHARED_PREFERENCES_PAID_EXPIRED, "");

            if (exiredate_st.equals("forever"))    {
                Common.currentIAPStatus = UtilsValues.USABLE_FOREVER;
                loadPaidSounds();
            } else {

                String[] date_array = exiredate_st.split(",");
                int expiredYear = Integer.parseInt(date_array[0]);
                int expiredMonth = Integer.parseInt(date_array[1]);
                int expiredDay = Integer.parseInt(date_array[2]);

                if (!UtilsMethods.compareDate(expiredYear, expiredMonth, expiredDay))
                {
                    Common.currentIAPStatus = UtilsValues.NON_PAID;
                    UtilsMethods.saveBooleanInSharedPreferences(SplashActivity.this, UtilsValues.SHARED_PREFERENCES_PAID_STATUS, false);
                    loadDefaultSounds();
                } else
                    loadPaidSounds();
            }
        }

        if (UtilsMethods.getBooleanFromSharedPreferences(SplashActivity.this, UtilsValues.SHARED_PREFERENCES_WATCH_VIDEO, false))   {
            String expireDate = UtilsMethods.getStringFromSharedPreferences(SplashActivity.this, UtilsValues.SHARED_PREFERENCES_WATCH_TIME, "");

            String[] date_array = expireDate.split(",");
            int expiredYear = Integer.parseInt(date_array[0]);
            int expiredMonth = Integer.parseInt(date_array[1]);
            int expiredDay = Integer.parseInt(date_array[2]);

            if (!UtilsMethods.compareDate(expiredYear, expiredMonth, expiredDay))
            {
                UtilsMethods.saveBooleanInSharedPreferences(SplashActivity.this, UtilsValues.SHARED_PREFERENCES_WATCH_VIDEO, false);
                UtilsMethods.saveStringInSharedPreferences(SplashActivity.this, UtilsValues.SHARED_PREFERENCES_WATCH_TIME, "");
            }
        }

        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    private void loadDefaultSounds() {
        Common.currentIAPStatus = UtilsValues.NON_PAID;
        for (int i = 0; i < Common.sounds.size(); i++) {
            MSound sound = Common.sounds.get(i);

            switch (sound.type) {
                case 0:
                    Common.unlockSounds.add(sound);
                    break;
                case 1:
                    Common.lockSounds.add(sound);
                    break;
                default:
                    Common.videoSounds.add(sound);
                    break;
            }
        }
    }

    private void loadPaidSounds()   {

        loadDefaultSounds();

        for (int i = 0; i < Common.lockSounds.size(); i++) {

            Common.lockSounds.get(i).isUsable = true;
            Common.unlockSounds.add(Common.lockSounds.get(i));
        }

        if (Common.videoSounds.size() > 0)
            for (int i = 0; i < Common.videoSounds.size(); i ++) {
                Common.videoSounds.get(i).isUsable = true;
                Common.unlockSounds.add(Common.videoSounds.get(i));
            }

        Common.lockSounds = new ArrayList<>();
        Common.videoSounds = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        startApp();
    }

    private void startApp() {

        Kiip kiip = Kiip.init(getApplication(), UtilsValues.KIIP_KEY, UtilsValues.KIIP_SECRET);
        kiip.setTestMode(false);
        Kiip.setInstance(kiip);

        new GetListOfTipsAsyncTask().execute();

        initView();

        vunglePub.init(this, UtilsValues.VUNGLE_APP_ID);
    }

    private void initView() {
        TextureView textureView = (TextureView) findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(this);
    }

    private void stopVideo()    {

        isAnimationFinished = true;

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer = null;
        }

        goToTheNextScreen();

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {

        Surface surface = new Surface(surfaceTexture);

        try {
            AssetFileDescriptor afd = getAssets().openFd(FILE_NAME);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mMediaPlayer.setSurface(surface);
            mMediaPlayer.setLooping(false);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnCompletionListener(this);

            // Play video when the media source is ready for playback.
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });

        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.getMessage());
        } catch (SecurityException e) {
            Log.d(TAG, e.getMessage());
        } catch (IllegalStateException e) {
            Log.d(TAG, e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d("status", "finished");
        stopVideo();
    }

    public class GetListOfTipsAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... arg0) {

            try {
                PListXMLParser parser = new PListXMLParser();
                PListXMLHandler handler = new PListXMLHandler();
                parser.setHandler(handler);

                String plist_url = UtilsValues.PLIST_URLS[UtilsValues.STORE];
                String s = UtilsMethods.readFile(plist_url);
                parser.parse(s);

                String moreApps = "";

                if (UtilsValues.STORE == UtilsValues.AMAZON_STORE) {
                    s = UtilsMethods.readFile(UtilsValues.AMAZON_PLIST);
                } else if (UtilsValues.STORE == UtilsValues.GOOGLE_STORE) {
                    s = UtilsMethods.readFile(UtilsValues.GOOGLE_PLIST);
                }

                moreApps = UtilsMethods.readFile(UtilsValues.PLIST_URLS[UtilsValues.STORE]);
                LogService.Log("GetListOfTipsAsyncTask", "moreApps: " + moreApps);
                UtilsMethods.setMoreApps(getBaseContext(), moreApps);
                parser.parse(moreApps);


                PList actualPList = ((PListXMLHandler) parser.getHandler())
                        .getPlist();

                Array featured_app_array = ((Dict) actualPList.getRootElement())
                        .getConfigurationArray("featured");

                featuredApp.setName(((Dict) featured_app_array.get(0))
                        .getConfiguration("name").getValue());

                featuredApp.setUrl(((Dict) featured_app_array.get(0))
                        .getConfiguration("url").getValue());

                featuredApp.setDesc(((Dict) featured_app_array.get(0))
                        .getConfiguration("description").getValue());

                featuredApp.setImage(((Dict) featured_app_array.get(0))
                        .getConfiguration("icon").getValue());

                Random rand = new Random();

                Array apps_array = ((Dict) actualPList.getRootElement())
                        .getConfigurationArray("apps");

                LogService.Log("GetListOfTipsAsyncTask", "apps_array: " + apps_array);

                Array tips_array = ((Dict) actualPList.getRootElement())
                        .getConfigurationArray("tips");

                LogService.Log("GetListOfTipsAsyncTask", "tips: " + tips_array);

                int rand_tip = rand.nextInt(tips_array.size());

                tip = ((MyString) tips_array.get(rand_tip)).getValue();

                Array quotes_array = ((Dict) actualPList.getRootElement())
                        .getConfigurationArray("quotes");

                LogService.Log("GetListOfTipsAsyncTask", "quotes_array: " + quotes_array);

                int rand_quote = rand.nextInt(quotes_array.size());

                quote = ((MyString) quotes_array.get(rand_quote)).getValue();

                apps = new ArrayList<App>();


                for (int i = 0; i < apps_array.size(); i++) {

                    App app = new App();

                    app.setName(((Dict) apps_array.get(i)).getConfiguration(
                            "name").getValue());
                    app.setUrl(((Dict) apps_array.get(i)).getConfiguration(
                            "url").getValue());

                    app.setImage(((Dict) apps_array.get(i)).getConfiguration(
                            "icon").getValue());

                    apps.add(app);
                }

                LogService.Log("GetListOfTipsAsyncTask","apps: "+apps.toString());

                return true;

            } catch (Throwable t) {
                LogService.Log("GetListOfTipsAsyncTask", "Throwable: " + t.toString());
                t.printStackTrace();
                return false;
            }

        }

        protected void onPostExecute(Boolean result) {
            if (result) {

                Type arrayListType = new TypeToken<ArrayList<App>>() {
                }.getType();
                Gson tmp = new Gson();

                SharedPreferences preferences = getSharedPreferences(
                        getString(R.string.PREFS), 1);

                preferences.edit().putString("tip", tip).commit();
                preferences.edit().putString("quote", quote).commit();

                LogService.Log("TAG", "apps size " + apps.size());
                preferences.edit()
                        .putString("apps", tmp.toJson(apps, arrayListType))
                        .commit();

            }
            isTipsDownloaded = true;

            goToTheNextScreen();

        }
    }

    private void goToTheNextScreen() {
        if (isAnimationFinished && isTipsDownloaded) {

            SoundDBHelper dbHelper = new SoundDBHelper(this);
            Common.sounds = dbHelper.getAllModels();

            if(Common.sounds == null || Common.sounds.size() == 0) {

                Common.sounds = new ArrayList<>();
                new GetListOfSongsAsyncTask().execute();
            } else {
                loadFavorte();
            }
        }
    }

    public class GetListOfSongsAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... arg0) {

            try {

                ProductsPlistParsing parser = new ProductsPlistParsing(SplashActivity.this);
                List<HashMap<String, Object>> objects = parser.getProductsPlistValues(R.xml.sounds);

                Common.sounds = new ArrayList<>();

                for (int i = 0; i < objects.size(); i ++)    {

                    HashMap<String, Object> object = objects.get(i);
                    MSound mSound = new MSound();
                    mSound.setBackground(object.get("background").toString());
                    mSound.setName(object.get("name").toString());
                    mSound.setSound(object.get("sound").toString());
                    mSound.isUsable = (Boolean) object.get("isUsable");
                    mSound.setUniqueId(object.get("uniqueId").toString());
                    mSound.type = Integer.parseInt(object.get("type").toString());
                    mSound.volume = Float.parseFloat(object.get("volume").toString());

                    Common.sounds.add(mSound);
                }

                return true;

            } catch (Throwable t) {
                LogService.Log("GetListOfSongsAsyncTask", "Throwable: " + t.toString());
                t.printStackTrace();
                return false;
            }

        }

        protected void onPostExecute(Boolean result) {
            if (result && Common.sounds != null) {

                for (int i = 0; i < Common.sounds.size(); i ++) {
                    SoundDBHelper dbHelper = new SoundDBHelper(SplashActivity.this);
                    dbHelper.addSound(Common.sounds.get(i));
                }

                loadFavorte();
            }
        }
    }

    public void loadFavorte()   {

        FavoritDBHelper dbHelper = new FavoritDBHelper(this);
        Common.favorits = dbHelper.getAllModels();

        if (Common.favorits == null || Common.favorits.size() == 0) {

            Common.favorits = new ArrayList<>();
            new GetListOfFavoritAsyncTask().execute();
        } else {
            loadAlarm();
        }
    }

    public class GetListOfFavoritAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... arg0) {

            try {

                ProductsPlistParsing parser = new ProductsPlistParsing(SplashActivity.this);
                List<HashMap<String, Object>> objects = parser.getProductsPlistValues(R.xml.favorite);

                Common.favorits = new ArrayList<>();

                for (int i = 0; i < objects.size(); i ++)    {

                    HashMap<String, Object> object = objects.get(i);
                    MFavorit favorit = new MFavorit();
                    favorit.title = object.get("name").toString();
                    favorit.uniqueID = object.get("uniqueId").toString();
                    ArrayList<String> ids = (ArrayList<String>) object.get("soundIds");

                    for (int j = 0; j < ids.size(); j ++)   {

                        String id = ids.get(j);
                        for (int k = 0; k < Common.sounds.size(); k ++) {
                            MSound sound = Common.sounds.get(k);

                            if (id.equals(sound.getUniqueId())) {
                                favorit.sounds.add(sound);
                                break;
                            }
                        }
                    }

                    Common.favorits.add(favorit);
                }

                return true;

            } catch (Throwable t) {
                LogService.Log("GetListOfSongsAsyncTask", "Throwable: " + t.toString());
                t.printStackTrace();
                return false;
            }

        }

        protected void onPostExecute(Boolean result) {
            if (result && Common.sounds != null) {

                for (int i = 0; i < Common.favorits.size(); i ++)   {
                    FavoritDBHelper dbHelper = new FavoritDBHelper(SplashActivity.this);
                    dbHelper.addFavorite(Common.favorits.get(i));
                }

                loadAlarm();
            }
        }
    }

    public void loadAlarm() {
        AlarmDBHelper dbHelper = new AlarmDBHelper(SplashActivity.this);
        Common.alarms = dbHelper.getAllModels();

        if(Common.alarms == null || Common.alarms.size() == 0)  {
            Common.alarms = new ArrayList<>();
        }

        loadUnlockSounds();
    }

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    private void insertDummyContactWrapper() {

        List<String> permissionsList = new ArrayList<String>();

        if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && !ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && !ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED && !ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)) {
            permissionsList.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (permissionsList.size() == 0)
            startApp();
        else {
            ActivityCompat.requestPermissions(this, permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            startApp();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG, "permission requested");
    }
}