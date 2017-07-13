package com.zenlabs.sleepsounds.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.model.App;
import com.zenlabs.sleepsounds.utils.ImageLoader;

import java.util.ArrayList;

/**
 * Created by fedoro on 5/31/16.
 */
public class AppAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<App> items = null;
    private ImageLoader imageLoader;
    private boolean isMoreApps;

    public AppAdapter(Context c, ArrayList<App> items, boolean isMoreApps) {
        context = c;
        layoutInflater = LayoutInflater.from(context);
        this.items = items;
        imageLoader = new ImageLoader(c);
        this.isMoreApps = isMoreApps;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid = null;

        grid = layoutInflater.inflate(R.layout.layout_more_apps_item, parent,
                false);
        TextView text = (TextView) grid.findViewById(R.id.app_name);
        if (isMoreApps) {
            text.setBackgroundResource(R.drawable.shadow_icons_more_apps);
        } else {
            text.setText(items.get(position).getName());
        }
        ImageView img = (ImageView) grid.findViewById(R.id.app_image);
        imageLoader.DisplayImage(items.get(position).getImage(), img, true);

        UrlImageViewHelper.setUrlDrawable(img, items.get(position).getImage(), 0, new UrlImageViewCallback() {
            @Override
            public void onLoaded(ImageView imageView, Bitmap loadedBitmap,
                                 String url, boolean loadedFromCache) {
                // TODO Auto-generated method stub
                if (!loadedFromCache) {
                    ScaleAnimation scale = new ScaleAnimation(1, 1, 1, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
                    scale.setDuration(100);
                    scale.setInterpolator(new OvershootInterpolator());
                    imageView.startAnimation(scale);
                }
            }
        });

        return grid;
    }
}
