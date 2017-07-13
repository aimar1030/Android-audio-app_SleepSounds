package com.zenlabs.sleepsounds.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.activity.MainActivity;
import com.zenlabs.sleepsounds.model.MAlarm;
import com.zenlabs.sleepsounds.model.MFavorit;
import com.zenlabs.sleepsounds.model.MSound;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by fedoro on 5/12/16.
 */
public class UtilsMethods {

    public static boolean getBooleanFromSharedPreferences(Context context, String key, boolean defaultValue) {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        if (key.equals(UtilsValues.SHARED_PREFERENCES_TICK_SOUNDS)) {
            defaultValue = false;
        }

        boolean value = sharedPreferences.getBoolean(key, defaultValue);

        return value;
    }

    public static String getStringFromSharedPreferences(Context context, String key, String defaultValue) {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        String value = sharedPreferences.getString(key, defaultValue);

        return value;
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static void saveBooleanInSharedPreferences(Context context, String key, boolean value) {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value)
                .commit();

        LogService.Log("saveBooleanInSharedPreferences", "key: " + key + " value: " + value);
    }

    public static void saveStringInSharedPreferences(Context context, String key, String value) {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value)
                .commit();

        LogService.Log("saveBooleanInSharedPreferences", "key: " + key + " value: " + value);
    }

    public static void showTipScreenDialog(final Context context) {

        final Dialog dialog;

        dialog = new Dialog(context, R.style.Dialog_PopUpSleepSounds);
        try {

            if (isTablet(context))
                dialog.setContentView(R.layout.tip_screen_popup_tablet_layout);
            else
                dialog.setContentView(R.layout.tip_screen_popup_layout);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);

            ImageView imageView = (ImageView) dialog.findViewById(R.id.tipScreenDialogImageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        } catch (Exception e) {

        }
    }

    public static boolean getBooleanFromSharedPreferences(Context context, String key) {
        return getBooleanFromSharedPreferences(context, key, true);
    }

    public static String readFile(String path) {

        String stringText = "";

        LogService.Log("readFile", "path: " + path);

        try {
            URLConnection conn = new URL(path).openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            BufferedReader bufferReader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String StringBuffer;

            while ((StringBuffer = bufferReader.readLine()) != null) {
                stringText += StringBuffer;
            }
            bufferReader.close();

        } catch (Throwable t) {
            LogService.Log("readFile", "Throwable: " + t.toString());
            t.printStackTrace();
        }

        LogService.Log("readFile", "stringText: " + stringText);

        return stringText;
    }

    public static void setMoreApps(Context context, String more_apps) {
        SharedPreferences settings = context.getSharedPreferences(
                context.getString(R.string.PREFS), 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("more_apps", more_apps);
        editor.commit();
    }

    public static Uri getUriFromFile(String fileName, Context context) {

        String filepath = fileName;
        try {
//            File root = android.os.Environment.getExternalStorageDirectory();
//            File dir = new File (root.getAbsolutePath() + "/sleep_sounds");
//
//            if (fileName == null)
//                return null;
//
//            File outFile = new File(dir, fileName);

            InputStream is = context.getAssets().open(filepath);
//            FileOutputStream fos = new FileOutputStream(outFile);
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = is.read(bytes)) != -1) {
                fos.write(bytes, 0, read);
            }

            is.close();
            fos.close();

            File outFile = context.getFileStreamPath(fileName);

            return Uri.fromFile(outFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Drawable getAssetImage(Context context, String filename) {
        AssetManager assets = context.getResources().getAssets();
        InputStream buffer = null;
        try {
            buffer = new BufferedInputStream((assets.open("drawable/" + filename)));
            Bitmap bitmap = BitmapFactory.decodeStream(buffer);
            return new BitmapDrawable(context.getResources(), bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static MAlarm getMinumAlarm() {

        ArrayList<MAlarm> liveAlarms = new ArrayList<>();

        for (int i = 0; i < Common.alarms.size(); i ++) {
            if (Common.alarms.get(i).fire_seconds > 1 && Common.alarms.get(i).status == 1)
                liveAlarms.add(Common.alarms.get(i));
        }

        if (liveAlarms.size() > 0) {
            MAlarm minumAlarm = liveAlarms.get(0);

            for (int i = 1; i < liveAlarms.size(); i++) {

                MAlarm alarm = liveAlarms.get(i);
                if (alarm.fire_seconds < minumAlarm.fire_seconds) {
                    minumAlarm = alarm;
                }
            }

            if (minumAlarm.status != 1)
                return null;
            return minumAlarm;
        } else return null;
    }

    public static void showAlert(Context context, String title, String message) {

        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static int dpToPx(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public static boolean hasInternetConnection(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo wifiNetwork = cm
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifiNetwork != null && wifiNetwork.isConnected()) {

                return true;
            }

            NetworkInfo mobileNetwork = cm
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mobileNetwork != null && mobileNetwork.isConnected()) {

                return true;
            }

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected()) {

                return true;
            }
        }
        return false;
    }

    public static List<MSound> getSoundsFromFavoritWithID(String category_id) {

        ArrayList<MSound> sounds = new ArrayList<>();

        for (int i = 0; i < Common.favorits.size(); i ++)   {

            MFavorit favorit = Common.favorits.get(i);

            if (favorit.uniqueID.equals(category_id))   {

                sounds = favorit.sounds;
                break;
            }
        }

        return sounds;
    }

    public static void setGrayScale(ImageView v){
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0); //0 means grayscale
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        v.setColorFilter(cf);
    }

    public static boolean compareDate(Date date1, Date date2) {

        Calendar c = Calendar.getInstance();

        c.setTime(date1);

        long milionSeconds1 = c.getTimeInMillis();

        c.setTime(date2);

        long milionSeconds2 = c.getTimeInMillis();

        return milionSeconds1 < milionSeconds2 ? true : false;
    }

    public static int getMonth() {

        Calendar c = Calendar.getInstance();

        return c.get(Calendar.MONTH);
    }

    public static Date getDateWithMonth(int month) {

        Calendar c = Calendar.getInstance();
        if (month > 12) {
            month = month - 12;
            int year = c.get(Calendar.YEAR);
            int day = c.get(Calendar.DAY_OF_MONTH);
            c.set(year, month, day);

            return c.getTime();
        } else {
            c.set(Calendar.MONTH, month);
            return c.getTime();
        }
    }

    public static boolean compareDate(int year, int month, int day) {

        Calendar c = Calendar.getInstance();
        long currentMillion = c.getTimeInMillis();

        c.set(year, month, day);
        long expireMillion = c.getTimeInMillis();

        return currentMillion > expireMillion ? true : false;
    }

    public static String getIDFromSound(ArrayList<MSound> sounds) {

        String id = "";

        for (int i = 0; i < sounds.size(); i ++)    {
            MSound sound = sounds.get(i);

            id = id + sound.getUniqueId();

            if (i < sounds.size() - 1)  {
                id = id + ",";
            }
        }

        return id;
    }

    public static ArrayList<MSound> getSoundsFromId(String string) {

        ArrayList<MSound> sounds = new ArrayList<>();
        String[] ids = string.split(",");

        for (int i = 0; i < ids.length; i ++)   {

            String id = ids[i];

            for (int j = 0; j < Common.sounds.size(); j ++) {

                MSound sound = Common.sounds.get(j);
                if (sound.getUniqueId().equals(id)) {
                    sounds.add(sound);
                    break;
                }
            }
        }

        return sounds;
    }

    public static String getMoreApps(Context context) {
        SharedPreferences settings = context.getSharedPreferences(
                context.getString(R.string.PREFS), 1);
        String more_apps = settings.getString("more_apps", "");
        return more_apps;
    }
}
