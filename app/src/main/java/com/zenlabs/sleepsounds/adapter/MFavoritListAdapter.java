package com.zenlabs.sleepsounds.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.customview.MFavoritContainViewItem;
import com.zenlabs.sleepsounds.model.MFavorit;
import com.zenlabs.sleepsounds.model.MSound;
import com.zenlabs.sleepsounds.sqlite.FavoritDBHelper;
import com.zenlabs.sleepsounds.utils.Common;

import java.util.ArrayList;

/**
 * Created by fedoro on 5/23/16.
 */
public class MFavoritListAdapter extends BaseAdapter implements MFavoritContainViewItem.MFavoriteItemHandler {

    ArrayList<MFavorit> favorits;
    Context context;
    int views_count = 0;
    public MFavoritListHandler handler;

    @Override
    public void dataChanged(int positionInFavorite, int positionInSounds) {
        FavoritDBHelper dbHelper = new FavoritDBHelper(context);
        if (Common.favorits.get(positionInFavorite).sounds.size() == 1) {
            dbHelper.delete_model(Common.favorits.get(positionInFavorite));
            Common.favorits.remove(positionInFavorite);
        } else {
            Common.favorits.get(positionInFavorite).sounds.remove(positionInSounds);
            dbHelper.update_model(Common.favorits.get(positionInFavorite));
        }
        if (handler != null)
            handler.dataChanged();
    }

    public interface MFavoritListHandler    {

        public void dataChanged();
        public void editFavorite();
    }

    public MFavoritListAdapter(Context context, ArrayList<MFavorit> list)    {

        this.context = context;
        favorits = list;
    }

    @Override
    public int getCount() {
        return favorits.size();
    }

    @Override
    public Object getItem(int position) {
        return favorits.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        LayoutInflater vi;
        vi = LayoutInflater.from(context);
        convertView = vi.inflate(R.layout.view_favorit_item, null);
        MFavorit favorit = favorits.get(position);

        TextView title = (TextView) convertView.findViewById(R.id.favorit_title);

        Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/AvenirLTStd-Light.otf");
        title.setTypeface(custom_font);

        ImageView favorit_item = (ImageView) convertView.findViewById(R.id.background_favorit_item);

        favorit_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handler != null) {
                    Common.currentFavorit = Common.favorits.get(position);
                    handler.editFavorite();
                }
            }
        });

        TableLayout favorite_sound_views = (TableLayout) convertView.findViewById(R.id.favorit_item_view);

        title.setText(favorit.title);

        loadFavoritSongs(favorit.sounds, favorite_sound_views, position);

        return convertView;
    }

    private void loadFavoritSongs(ArrayList<MSound> sounds, TableLayout tableLayout, int positionInFavorite) {

        tableLayout.removeAllViews();
        if (sounds.size() % 2 == 0) {
            for (views_count = 0; views_count < sounds.size(); views_count += 2){

                MFavoritContainViewItem leftViewSoundView = new MFavoritContainViewItem(context);
                leftViewSoundView.updateItem(sounds.get(views_count));

                leftViewSoundView.setRemove(Common.favorits.get(positionInFavorite).isEditing, positionInFavorite, views_count);

                MFavoritContainViewItem rightViewSoundView = new MFavoritContainViewItem(context);
                rightViewSoundView.updateItem(sounds.get(views_count + 1));

                rightViewSoundView.setRemove(Common.favorits.get(positionInFavorite).isEditing, positionInFavorite, views_count + 1);

                if (Common.favorits.get(positionInFavorite).isEditing)  {
                    leftViewSoundView.sethandler(this);
                    rightViewSoundView.sethandler(this);
                }

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

                leftViewSoundView.setRemove(Common.favorits.get(positionInFavorite).isEditing, positionInFavorite, views_count);

                MFavoritContainViewItem rightViewSoundView = new MFavoritContainViewItem(context);

                if (views_count < sounds.size() - 1) {
                    rightViewSoundView.updateItem(sounds.get(views_count + 1));
                    rightViewSoundView.setRemove(Common.favorits.get(positionInFavorite).isEditing, positionInFavorite, views_count + 1);
                }

                if (Common.favorits.get(positionInFavorite).isEditing)  {
                    leftViewSoundView.sethandler(this);
                    rightViewSoundView.sethandler(this);
                }

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

    private void addSoundViews(MFavoritContainViewItem leftView, MFavoritContainViewItem rightView, TableLayout tableLayout) {

        TableRow tr = new TableRow(context);

        int width = tableLayout.getWidth();

        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        leftView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (float) 0.50));

        rightView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (float) 0.50));
        tr.addView(leftView);
        tr.addView(rightView);

        tableLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        views_count += 2;
    }
}