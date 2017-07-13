package com.zenlabs.sleepsounds.fragment;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.activity.MainActivity;
import com.zenlabs.sleepsounds.customview.MFavoritContainViewItem;
import com.zenlabs.sleepsounds.model.MFavorit;
import com.zenlabs.sleepsounds.model.MSound;
import com.zenlabs.sleepsounds.sqlite.FavoritDBHelper;
import com.zenlabs.sleepsounds.utils.Common;

import java.util.ArrayList;

/**
 * Created by fedoro on 5/26/16.
 */
public class AddFavoriteFragment extends Fragment {

    int views_count;
    boolean isEditableFavorit;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_favorit, container, false);

        ImageView close_bt = (ImageView) rootView.findViewById(R.id.close_bt);
        ImageView add_bt = (ImageView) rootView.findViewById(R.id.add_favorit);
        TableLayout soundViews = (TableLayout) rootView.findViewById(R.id.favorite_sound_list);
        final TextView display_title = (TextView) rootView.findViewById(R.id.favorite_item_title);
        final EditText title_txt = (EditText) rootView.findViewById(R.id.favorite_name_in);

        if (Common.currentFavorit != null && Common.currentFavorit.uniqueID.length() > 0)   {
            isEditableFavorit = true;
            display_title.setText(Common.currentFavorit.title);
            title_txt.setText(Common.currentFavorit.title);
        } else {
            isEditableFavorit = false;
        }

        title_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                display_title.setText(title_txt.getText());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        close_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).backFromAddFavoriteView();
            }
        });

        add_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = getActivity().getCurrentFocus();

                if (view != null)   {

                    InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                String title = title_txt.getText().toString();

                FavoritDBHelper dbHelper = new FavoritDBHelper(getContext());

                if(isEditableFavorit)   {
                    Common.currentFavorit.title = title;
                    for (int i = 0; i < Common.favorits.size(); i ++)   {
                        if (Common.currentFavorit.uniqueID.equals(Common.favorits.get(i).uniqueID)) {
                            Common.favorits.get(i).title = title;
                        }
                    }

                    dbHelper.update_model(Common.currentFavorit);
                    Common.currentFavorit = null;
                } else {
                    MFavorit favorit = new MFavorit();
                    favorit.title = title;
                    favorit.sounds = Common.currentPlayingSounds;
                    favorit.uniqueID = Common.favorits.size() + 1 + "";

                    dbHelper.addFavorite(favorit);
                    Common.favorits.add(favorit);
                }

                ((MainActivity)getActivity()).backFromAddFavoriteView();
            }
        });

        if (isEditableFavorit)
            loadFavoritSongs(Common.currentFavorit.sounds, soundViews);
        else
            loadFavoritSongs(Common.currentPlayingSounds, soundViews);

        return rootView;
    }

    private void loadFavoritSongs(ArrayList<MSound> sounds, TableLayout tableLayout) {

        tableLayout.removeAllViews();
        if (sounds.size() % 2 == 0) {
            for (views_count = 0; views_count < sounds.size(); views_count += 2){

                MFavoritContainViewItem leftViewSoundView = new MFavoritContainViewItem(getContext());
                leftViewSoundView.updateItem(sounds.get(views_count));

                MFavoritContainViewItem rightViewSoundView = new MFavoritContainViewItem(getContext());
                rightViewSoundView.updateItem(sounds.get(views_count + 1));

                TableRow tr = new TableRow(getContext());

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

                MFavoritContainViewItem leftViewSoundView = new MFavoritContainViewItem(getContext());
                leftViewSoundView.updateItem(sounds.get(views_count));

                MFavoritContainViewItem rightViewSoundView = new MFavoritContainViewItem(getContext());

                if (views_count < sounds.size() - 1)
                    rightViewSoundView.updateItem(sounds.get(views_count + 1));

                TableRow tr = new TableRow(getContext());

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
