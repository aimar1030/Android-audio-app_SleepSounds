package com.zenlabs.sleepsounds.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.utils.Common;

/**
 * Created by fedoro on 5/25/16.
 */
public class FadeInAdapter extends BaseAdapter {

    Context context;
    String[] list;

    public FadeInAdapter(String[] list, Context context)  {
        this.list = list;
        this.context = context;
    }
    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public Object getItem(int position) {
        return list[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater vi;
        vi = LayoutInflater.from(context);
        convertView = vi.inflate(R.layout.view_fade_in_cell, null);

        TextView textView = (TextView) convertView.findViewById(R.id.times);

        Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/MyriadPro-Light.otf");
        textView.setTypeface(custom_font);

        ImageView checkStatus = (ImageView) convertView.findViewById(R.id.check_fade_in_status);

        textView.setText(list[position]);

        if (position == Common.currentSelectedFadeIn)
            checkStatus.setVisibility(View.VISIBLE);
        else
            checkStatus.setVisibility(View.GONE);

        return convertView;
    }
}
