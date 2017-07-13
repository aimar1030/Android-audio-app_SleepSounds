package com.zenlabs.sleepsounds.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.utils.Common;
import com.zenlabs.sleepsounds.utils.UtilsMethods;
import com.zenlabs.sleepsounds.utils.UtilsValues;

/**
 * Created by fedoro on 5/26/16.
 */
public class MenuListAdapter extends BaseAdapter {

    Context context;
    String[] nameList;

    public MenuListAdapter(Context context, String[] nameList)     {

        this.context = context;
        this.nameList = nameList;
    }

    @Override
    public int getCount() {
        return nameList.length;
    }

    @Override
    public Object getItem(int position) {
        return nameList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater vi;
        vi = LayoutInflater.from(context);
        convertView = vi.inflate(R.layout.view_menu_cell, null);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.menu_cell_ic);

        TextView textView = (TextView) convertView.findViewById(R.id.menu_cell_lb);

        Switch menu_switch = (Switch) convertView.findViewById(R.id.menu_cell_switch);

        Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/MyriadPro-Light.otf");
        textView.setTypeface(custom_font);

        if (UtilsMethods.getBooleanFromSharedPreferences(context, UtilsValues.SHARED_PREFERENCES_PAID_STATUS, false))   {

            if (position == 7) {
                textView.setTextColor(Color.GRAY);
            }
            if (position == 8) {
                textView.setTextColor(Color.GRAY);
            }
        } else {
            if (position == 7) {
                textView.setTextColor(Color.BLACK);
            }
            if (position == 8) {
                textView.setTextColor(Color.BLACK);
            }
        }

        imageView.setImageResource(UtilsValues.menuCellImageArray[position]);
        String[] titles = context.getResources().getStringArray(R.array.menu_cell_title);

        textView.setText(titles[position]);

        if (position == 3) {
            menu_switch.setVisibility(View.VISIBLE);
            boolean isChecked = UtilsMethods.getBooleanFromSharedPreferences(parent.getContext(), UtilsValues.SHARED_PREFERENCES_TIPS_SCREEN);
            menu_switch.setChecked(isChecked);

            menu_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    UtilsMethods.saveBooleanInSharedPreferences(context, UtilsValues.SHARED_PREFERENCES_TIPS_SCREEN, isChecked);
                }
            });
        }
        else
            menu_switch.setVisibility(View.GONE);

        return convertView;
    }
}
