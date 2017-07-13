package com.zenlabs.sleepsounds.fragment;

import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.activity.MainActivity;
import com.zenlabs.sleepsounds.adapter.MFavoritListAdapter;
import com.zenlabs.sleepsounds.model.MSound;
import com.zenlabs.sleepsounds.utils.Common;
import com.zenlabs.sleepsounds.utils.UtilsMethods;

import java.util.ArrayList;

/**
 * Created by fedoro on 5/23/16.
 */
public class FavoritFragment extends Fragment implements MFavoritListAdapter.MFavoritListHandler {

    ListView listView;
    boolean isEditing;
    ImageView edit_bt;
    MFavoritListAdapter adapter;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_favorit, container, false);

        listView =(ListView)rootView.findViewById(R.id.favorit_list);

        edit_bt = (ImageView) rootView.findViewById(R.id.edit_favorit_bt);
        ImageView add_bt = (ImageView) rootView.findViewById(R.id.add_favorit_bt);

        add_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Common.currentPlayingSounds = new ArrayList<MSound>();

                for (int i = 0; i < Common.unlockSounds.size(); i ++)   {

                    MSound sound = Common.unlockSounds.get(i);

                    if (sound.isPlaying)
                        Common.currentPlayingSounds.add(sound);
                }

                if (Common.currentPlayingSounds.size() == 0)
                    UtilsMethods.showAlert(getContext(), "Empty Selection!", "In order to save your current selection as a favorite, you must first select some sounds.");
                else
                    ((MainActivity) getActivity()).showAddFavoriteView();

            }
        });

        edit_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditing)  {
                    edit_bt.setImageResource(R.drawable.pen);

                    for (int i = 0; i < Common.favorits.size(); i ++)   {
                        Common.favorits.get(i).isEditing = false;
                    }
                } else {
                    edit_bt.setImageResource(R.drawable.check);

                    for (int i = 0; i < Common.favorits.size(); i ++)   {
                        Common.favorits.get(i).isEditing = true;
                    }
                }

                isEditing = !isEditing;
                adapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }

    @Override
    public void onResume()  {

        super.onResume();
        adapter = new MFavoritListAdapter(getContext(), Common.favorits);
        listView.setAdapter(adapter);

        adapter.handler = this;
    }

    @Override
    public void dataChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void editFavorite()  {
        if (isEditing)
            ((MainActivity) getActivity()).showAddFavoriteView();
        else
            Common.currentFavorit = null;
    }
}
