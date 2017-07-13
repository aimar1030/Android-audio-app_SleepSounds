package com.zenlabs.sleepsounds.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.customview.MFavoritContainViewItem;
import com.zenlabs.sleepsounds.model.MFavorit;
import com.zenlabs.sleepsounds.model.MSound;
import com.zenlabs.sleepsounds.utils.Common;

import java.util.ArrayList;

/**
 * Created by fedoro on 5/25/16.
 */
public class SoundSelectAdapter extends BaseAdapter {

    ArrayList<MFavorit> list;
    Context context;
    int views_count = 0;

    public SoundSelectAdapter(Context context, ArrayList<MFavorit> list)    {

        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater vi;
        vi = LayoutInflater.from(context);
        convertView = vi.inflate(R.layout.view_favorit_item, null);
        MFavorit favorit = list.get(position);

        TextView title = (TextView) convertView.findViewById(R.id.favorit_title);

        ImageView check_img = (ImageView) convertView.findViewById(R.id.select_category);

        if (Common.currentSelectedSound == position)
            check_img.setVisibility(View.VISIBLE);
        else
            check_img.setVisibility(View.GONE);

        TableLayout favorite_sound_views = (TableLayout) convertView.findViewById(R.id.favorit_item_view);

        title.setText(favorit.title);

        loadFavoritSongs(favorit.sounds, favorite_sound_views);

        return convertView;
    }

    private void loadFavoritSongs(ArrayList<MSound> sounds, TableLayout tableLayout) {

        tableLayout.removeAllViews();
        if (sounds.size() % 2 == 0) {
            for (views_count = 0; views_count < sounds.size(); views_count += 2){

                MFavoritContainViewItem leftViewSoundView = new MFavoritContainViewItem(context);
                leftViewSoundView.updateItem(sounds.get(views_count));

                MFavoritContainViewItem rightViewSoundView = new MFavoritContainViewItem(context);
                rightViewSoundView.updateItem(sounds.get(views_count + 1));

                TableRow tr = new TableRow(context);

                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (float) 0.50);
                params.rightMargin = 5;

                leftViewSoundView.setLayoutParams(params);
                rightViewSoundView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (float) 0.50));
                tr.addView(leftViewSoundView);
                tr.addView(rightViewSoundView);

                tableLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            }
        } else {

            for (views_count = 0; views_count < sounds.size()+1; views_count += 2) {

                MFavoritContainViewItem leftViewSoundView = new MFavoritContainViewItem(context);
                leftViewSoundView.updateItem(sounds.get(views_count));

                MFavoritContainViewItem rightViewSoundView = new MFavoritContainViewItem(context);

                if (views_count < sounds.size() - 1)
                    rightViewSoundView.updateItem(sounds.get(views_count + 1));

                TableRow tr = new TableRow(context);

                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (float) 0.50);
                params.rightMargin = 5;

                leftViewSoundView.setLayoutParams(params);

                rightViewSoundView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (float) 0.50));
                tr.addView(leftViewSoundView);
                tr.addView(rightViewSoundView);

                tableLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            }
        }
    }
}
