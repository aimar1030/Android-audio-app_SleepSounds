package com.zenlabs.sleepsounds.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.zenlabs.sleepsounds.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by fedoro on 5/31/16.
 */
public class UtilsImage {

    public static File getFolder(Context context){

        File dir = context.getApplicationContext().getDir(context.getString(R.string.app_name),Context.MODE_PRIVATE);
        return dir;

    }

    public static void saveBitmapToFile(Context context,String imageUrl,Bitmap bitmap){

        File folder = getFolder(context);
        int position = imageUrl.lastIndexOf("/");
        String name = imageUrl.substring(position + 1, imageUrl.length());
        String mFilePathExternal = folder.getPath() + File.separator + name+ ".png";
        File file = new File(mFilePathExternal);

        try{
            FileOutputStream fOut = new FileOutputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] bitmapdata = bos.toByteArray();
            fOut.write(bitmapdata);
            fOut.flush();
            fOut.close();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public static Bitmap loadBitmap(Context context, String imageUrl){

        File folder = getFolder(context);
        int position = imageUrl.lastIndexOf("/");
        String name = imageUrl.substring(position + 1, imageUrl.length());
        String mFilePathExternal = folder.getPath() + File.separator + name+ ".png";
        File file = new File(mFilePathExternal);
        Bitmap bitmap = null;

        if (file.exists()){
            bitmap = BitmapFactory.decodeFile(file.getPath());
        }

        return bitmap;

    }
}
