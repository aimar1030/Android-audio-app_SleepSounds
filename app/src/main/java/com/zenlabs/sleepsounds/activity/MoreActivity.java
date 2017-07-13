package com.zenlabs.sleepsounds.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.adapter.AppAdapter;
import com.zenlabs.sleepsounds.model.App;
import com.zenlabs.sleepsounds.plistparser.Array;
import com.zenlabs.sleepsounds.plistparser.Dict;
import com.zenlabs.sleepsounds.plistparser.PList;
import com.zenlabs.sleepsounds.plistparser.PListXMLHandler;
import com.zenlabs.sleepsounds.plistparser.PListXMLParser;
import com.zenlabs.sleepsounds.utils.BitmapManager;
import com.zenlabs.sleepsounds.utils.UtilsMethods;

import java.util.ArrayList;

import us.feras.ecogallery.EcoGallery;
import us.feras.ecogallery.EcoGalleryAdapterView;

/**
 * Created by fedoro on 5/31/16.
 */
public class MoreActivity extends Activity {

    private ArrayList<App> apps = new ArrayList<>();
    private EcoGallery gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_apps);

        if (apps.size() == 0) {
            try {
                PListXMLParser parser = new PListXMLParser();
                PListXMLHandler handler = new PListXMLHandler();
                parser.setHandler(handler);

                String s = UtilsMethods.getMoreApps(MoreActivity.this);
                if (!s.equals("")) {
                    parser.parse(s);

                    PList actualPList = ((PListXMLHandler) parser.getHandler())
                            .getPlist();

                    Array apps_array = ((Dict) actualPList.getRootElement())
                            .getConfigurationArray("apps");

                    for (int i = 0; i < apps_array.size(); i++) {

                        App app = new App();
                        app.setName(((Dict) apps_array.get(i))
                                .getConfiguration("name").getValue());
                        app.setUrl(((Dict) apps_array.get(i)).getConfiguration(
                                "url").getValue());
                        app.setImage(((Dict) apps_array.get(i))
                                .getConfiguration("icon").getValue());
                        String banner = ((Dict) apps_array.get(i))
                                .getConfiguration("banner568").getValue();
                        if (banner.contains("runners_buddy_trainer")) {
                            banner = banner.replace(
                                    "runners_buddy_trainer_banner",
                                    "runners_buddy_trainer");
                        }
                        app.setPanelImage(banner);

                        apps.add(app);
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        // Setup banner view pager
        final ViewPager banner = (ViewPager) findViewById(R.id.imagePanel);
        PhotosPagerAdapter photosPagerAdapter = new PhotosPagerAdapter(MoreActivity.this, apps);
        banner.setAdapter(photosPagerAdapter);
        banner.addOnPageChangeListener(bannerPageChangeListener);

        // Setup bottom gallery
        AppAdapter myAdapter = new AppAdapter(MoreActivity.this, apps, true);
        gallery = (EcoGallery) findViewById(R.id.gallery);
        gallery.setAdapter(myAdapter);
        gallery.setOnItemSelectedListener(new EcoGallery.OnItemSelectedListener() {

            @Override
            public void onItemSelected(EcoGalleryAdapterView<?> parent,
                                       View view, int position, long id) {
                banner.setCurrentItem(position);

            }

            @Override
            public void onNothingSelected(EcoGalleryAdapterView<?> parent) {
            }
        });

//        new DownloadBannerImagesAsyncTask().execute(0);

    }

    private ViewPager.OnPageChangeListener bannerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            gallery.setSelection(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public ArrayList<App> getApps() {
        return apps;
    }

    public void setApps(ArrayList<App> apps) {
        this.apps = apps;
    }

    public class PhotosPagerAdapter extends PagerAdapter {

        private ArrayList<App> apps;

        private BitmapManager bitmapManager;

        public PhotosPagerAdapter(Context context, ArrayList<App> apps) {
            this.apps = apps;
        }

        @Override
        public int getCount() {
            return apps.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            LayoutInflater layoutInflater = (LayoutInflater) container.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ImageView view = (ImageView) layoutInflater.inflate(R.layout.more_apps_item_pager_photo, null);

            if (apps.get(position) != null) {
                view.setTag(apps.get(position).getPanelImage());
//                bitmapManager.displayImage(apps.get(position).getPanelImage(), view, null);

                UrlImageViewHelper.setUrlDrawable(view, apps.get(position).getPanelImage(), 0, new UrlImageViewCallback() {
                    @Override
                    public void onLoaded(ImageView imageView, Bitmap loadedBitmap,
                                         String url, boolean loadedFromCache) {
                        // TODO Auto-generated method stub
                        if (!loadedFromCache) {
                            ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
                            scale.setDuration(100);
                            scale.setInterpolator(new OvershootInterpolator());
                            imageView.startAnimation(scale);
                        }
                    }
                });
            } else {
                view.setImageBitmap(null);
                view.setTag(null);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(apps
                            .get(position).getUrl())));
                }
            });

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

//    private class DownloadBannerImagesAsyncTask extends AsyncTask<Integer, Void, Void> {
//
//        private int position = 0;
//
//        @Override
//        protected Void doInBackground(Integer... params) {
//            position = params[0];
//            if (position < apps.size() && apps.get(position).getPanelImage() != null) {
//                bitmapManager.downloadBitmap(apps.get(position).getPanelImage());
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//
//            if (position + 1 < apps.size())
//                new DownloadBannerImagesAsyncTask().execute(position + 1);
//            super.onPostExecute(aVoid);
//        }
//    }
}
