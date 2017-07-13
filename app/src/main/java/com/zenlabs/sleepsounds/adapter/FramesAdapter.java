package com.zenlabs.sleepsounds.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.model.App;
import com.zenlabs.sleepsounds.utils.ImageLoader;

import java.util.ArrayList;

/**
 * Created by fedoro on 5/12/16.
 */
public class FramesAdapter extends BaseAdapter {

    private int frameCount;
    private int firstFramePosition;
    private Context context;
    private ArrayList<App> items;
    private ImageLoader imageLoader;
    private Activity activity;

    public FramesAdapter(Activity activity, Context context, int frameCount,
                         int firstFramePosition, ArrayList<App> items) {
        super();
        this.activity = activity;
        this.context = context;
        this.frameCount = frameCount;
        this.firstFramePosition = firstFramePosition;
        this.items = items;
        imageLoader = new ImageLoader(context);
    }

    @Override
    public int getCount() {
        return frameCount;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int framePos = position + firstFramePosition;
        if (convertView == null) {
            convertView = ((LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.layout_more_apps_item, parent, false);
        }

        if (items != null && framePos < items.size()) {
            final App item = items.get(framePos);
            TextView text = (TextView) convertView.findViewById(R.id.app_name);
            text.setTextColor(Color.parseColor("#999999"));

            text.setText(item.getName());
            ImageView img = (ImageView) convertView
                    .findViewById(R.id.app_image);
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            Log.d("TAG_IMG", item.getImage().toString());
//            manager.displayImage(item.getImage(), img, metrics.widthPixels,
//    				metrics.heightPixels);
            imageLoader.DisplayImage(item.getImage(), img, true);
        }
        return convertView;
    }
}
