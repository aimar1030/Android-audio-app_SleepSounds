package com.zenlabs.sleepsounds.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Stack;

/**
 * Created by fedoro on 5/31/16.
 */
public class BitmapManager {

    private LruCache<String, Bitmap> imageMap;
    private File cacheDir;

    private ImageQueue imageQueue = new ImageQueue();
    private Thread imageLoaderThread = new Thread(new ImageQueueManager());
    private Activity activity;

    public BitmapManager(Context context) {
        this.activity = (Activity) context;

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        imageMap = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return (bitmap.getRowBytes() * bitmap.getHeight()) / 1024;
            }
        };

        String sdState = android.os.Environment.getExternalStorageState();

        if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
            File sdDir = android.os.Environment.getExternalStorageDirectory();
            cacheDir = new File(sdDir, "data/codehenge");
        } else if (context != null)
            cacheDir = context.getCacheDir();
        if (cacheDir != null && !cacheDir.exists())
            cacheDir.mkdirs();
    }

    private class ImageRef {
        public String url;
        public ImageView imageView;
        public ImageManager imageManager;

        public ImageRef(String u, ImageView i, ImageManager imageManager) {
            url = u;
            imageView = i;
            this.imageManager = imageManager;
        }
    }

    private class ImageQueue {
        private Stack<ImageRef> imageRefs = new Stack<ImageRef>();

        synchronized public void Clean(ImageView view) {
            for (int i = 0; i < imageRefs.size(); ) {
                if (i >= imageRefs.size()) {
                    break;
                }
                try {
                    if (imageRefs.get(i).imageView == view)
                        imageRefs.remove(i);
                    else
                        ++i;
                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.e("Ex", e.getMessage());
                    break;
                }
            }
        }
    }

    private void queueImage(String url, ImageView imageView, ImageManager imageManager) {
        imageQueue.Clean(imageView);
        ImageRef p = new ImageRef(url, imageView, imageManager);

        synchronized (imageQueue.imageRefs) {
            imageQueue.imageRefs.push(p);
            imageQueue.imageRefs.notifyAll();
        }

        if (imageLoaderThread.getState() == Thread.State.NEW)
            imageLoaderThread.start();
    }

    public class ImageQueueManager implements Runnable {

        @Override
        public void run() {
            try {

                while (true) {
                    if (imageQueue.imageRefs.size() == 0) {
                        synchronized (imageQueue.imageRefs) {
                            imageQueue.imageRefs.wait();
                        }
                    }

                    if (imageQueue.imageRefs.size() != 0) {
                        final ImageRef imageToLoad;

                        synchronized (imageQueue.imageRefs) {
                            imageToLoad = imageQueue.imageRefs.firstElement();
                            imageQueue.imageRefs.remove(imageToLoad);
                        }

                        if (imageToLoad != null && !imageToLoad.url.equals("")) {

                            Bitmap bmp;
                            bmp = downloadBitmap(imageToLoad.url);

                            if (imageMap != null && bmp != null) {

//                                imageMap.put(imageToLoad.url, bmp);
                                Object tag = imageToLoad.imageView.getTag();

                                if (tag != null
                                        && ((String) tag)
                                        .equals(imageToLoad.url)) {
                                    BitmapDisplayer bmpDisplayer = new BitmapDisplayer(
                                            bmp, imageToLoad.imageView,
                                            imageToLoad.url, imageToLoad.imageManager);

                                    if (activity != null)
                                        activity.runOnUiThread(bmpDisplayer);

                                }
                            } else
                                activity.runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        imageToLoad.imageView.setImageBitmap(null);
                                    }
                                });

                        }
                    }

                    if (Thread.interrupted())
                        break;
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap downloadBitmap(String urlString) {

        try {
            URL url = new URL(urlString);

            URLConnection connection = url.openConnection();
            connection.connect();
            InputStream inputStream = connection
                    .getInputStream();

            Bitmap bmp = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            if (bmp != null)
                UtilsImage.saveBitmapToFile(activity, urlString, bmp);

            if (imageMap != null && bmp != null) {
                imageMap.put(urlString, bmp);
            }
            return bmp;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface ImageManager {
        void imageLoaded();
    }

    public synchronized void displayImage(String url,
                                          ImageView imageView, ImageManager imageManager) {
        Bitmap bmp = imageMap.get(url);

        if (bmp != null) {
            BitmapDisplayer displayer = new BitmapDisplayer(bmp, imageView, url, imageManager);
//            Activity a = (Activity) imageView.getContext();
            activity.runOnUiThread(displayer);
        } else {

            Bitmap bitmap = UtilsImage.loadBitmap(activity, url);

            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                if (imageManager != null)
                    imageManager.imageLoaded();
            } else {
                imageView.setImageBitmap(null);
                queueImage(url, imageView, imageManager);
            }
        }
    }

    private class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        ImageView imageView;
        String url;
        ImageManager imageManager;

        public BitmapDisplayer(Bitmap b, ImageView i, String url, ImageManager imageManager) {

            if (bitmap != null) {
                bitmap.recycle();
            }
            this.url = url;
            bitmap = b;
            imageView = i;
            this.imageManager = imageManager;
        }

        @Override
        public void run() {
            if (bitmap != null) {

                imageView.setImageBitmap(bitmap);
                if (imageManager != null)
                    imageManager.imageLoaded();
            }
        }
    }
}
